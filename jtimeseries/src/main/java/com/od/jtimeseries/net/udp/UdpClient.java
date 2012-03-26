/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.od.jtimeseries.net.udp;

import com.od.jtimeseries.net.udp.message.UdpMessage;
import com.od.jtimeseries.util.NamedExecutors;
import com.od.jtimeseries.util.logging.LimitedErrorLogger;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.time.TimePeriod;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 13-Jan-2009
 * Time: 09:50:55
 */
public class UdpClient {

    private static final LogMethods logMethods = LogUtils.getLogMethods(UdpClient.class);
    
    private final LimitedErrorLogger limitedLogger = new LimitedErrorLogger(logMethods, 5, 100);
    private final ScheduledExecutorService scheduledExecutor = NamedExecutors.newSingleThreadScheduledExecutor("UdpClient");

    //using the set to check whether already in the list without iterating
    //want to preserve the order in which clients are sent messages so keeping the list
    private final Set<ConfigWithSocket> configSet = Collections.synchronizedSet(new HashSet<ConfigWithSocket>());
    private final List<ConfigWithSocket> configs = Collections.synchronizedList(new LinkedList<ConfigWithSocket>());

    /**
     * Create a UdpClient, but this won't send any messages until at least one client
     * config is added
     */
    public UdpClient() {}

    public UdpClient(UdpClientConfig config) {
        this(new ArrayList<UdpClientConfig>(Arrays.asList(config)));
    }

    public UdpClient(List<UdpClientConfig> configs) {
        addClientConfigs(configs);
    }

    public void addClientConfig(UdpClientConfig... configs) {
        addClientConfigs(Arrays.asList(configs));
    }

    public void addClientConfigs(List<UdpClientConfig> clientConfigs) {
        for ( UdpClientConfig config : clientConfigs) {
            addClientConfig(config);
        }
    }

    public boolean addClientConfig(UdpClientConfig config) {
        synchronized (configs) {
            ConfigWithSocket c = new ConfigWithSocket(config);
            boolean added = configSet.add(c);
            if (added) {
                configs.add(c);
            }
            return added;
        }
    }

    public void removeClientConfig(UdpClientConfig... configs) {
        removeClientConfig(Arrays.asList(configs));
    }


    public void removeClientConfig(List<UdpClientConfig> clientConfigs) {
        for ( UdpClientConfig c : clientConfigs) {
            doRemoveConfig(c);
        }
    }

    private boolean doRemoveConfig(UdpClientConfig c) {
        synchronized (configs) {
            ConfigWithSocket cc = new ConfigWithSocket(c);
            boolean removed = configSet.remove(cc);
            if ( removed ) {
                configs.remove(cc);
            }
            return removed;
        }
    }

    /**
     * @return A snapshot of current client configs
     */
    public List<UdpClientConfig> getClientConfigs() {
        List<UdpClientConfig> l = new ArrayList<UdpClientConfig>();
        for ( ConfigWithSocket c : getClientConfigSnapshot()) {
            l.add(c.getUdpClientConfig());
        }
        return l;
    }

    /**
     * Send a single message as a datagram
     */
    public void sendMessage(UdpMessage message) {
        try {
            byte[] data = createDatagram(message);
            scheduledExecutor.execute(new SendUdpDatagramTask(data));
        } catch (IOException e) {
            logMethods.error("Could not send UDP datagram", e);
        }
    }

    /**
     * Send one or more messages from the message queue packaged into a single datagram
     * When used with a message type which supports message streaming, this method will attempt to package
     * as many messages as possible into a single UDP datagram, until the max datagram size limit is reached
     */
    public void sendMessages(Queue<UdpMessage> messageQueue) {
        try {
            UdpMessage m = messageQueue.peek();
            if ( m != null) {
                if ( m.isMessageStreamingSupported()) {
                    sendMultiple(messageQueue, m);
                } else {
                    sendMessage(m);
                    messageQueue.remove();
                }
            }
        } catch (IOException e) {
            logMethods.error("Could not send UDP datagram", e);
        }
    }

    private void sendMultiple(Queue<UdpMessage> messageQueue, UdpMessage m) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(UdpMessage.MAX_PACKET_SIZE_BYTES);

        while(m != null && bos.size() < (UdpMessage.MAX_PACKET_SIZE_BYTES - m.getMaxExpectedSize())) {
            ByteArrayOutputStream msgOut = new ByteArrayOutputStream(m.getMaxExpectedSize());
            m.serialize(msgOut);
            byte[] msgBytes = msgOut.toByteArray();
            checkMessageSize(m, msgBytes);
            if ( bos.size() + msgBytes.length < UdpMessage.MAX_PACKET_SIZE_BYTES) {
                bos.write(msgBytes);
                UdpMessage removed = messageQueue.poll();
                assert(removed == m); //only not the case if we have multiple queue consumers?
                m = messageQueue.peek(); //try the next message
            } else {
                //we can't fit the serialized messages into the packet
                //so don't remove the message from the incoming message queue, we will try again next time
                break;
            }
        }

        byte[] data = bos.toByteArray();
        scheduledExecutor.execute(new SendUdpDatagramTask(data));
    }

    //get a snapshot of the current configs to iterate over
    private List<ConfigWithSocket> getClientConfigSnapshot() {
        return new ArrayList<ConfigWithSocket>(configs);
    }

    public void sendRepeatedMessage(UdpMessage message, TimePeriod period) {
        try {
            byte[] data = createDatagram(message);
            scheduledExecutor.scheduleAtFixedRate(
                new SendUdpDatagramTask(data),
                0,
                period.getLengthInMillis(),
                TimeUnit.MILLISECONDS
            );
        } catch (IOException e) {
            logMethods.error("Could not create UdpClient ", e);
        }
    }

    public synchronized void stop() {
        scheduledExecutor.shutdown();
        for (ConfigWithSocket c : configs) {
            c.closeSocket();
        }
    }

    private byte[] createDatagram(UdpMessage message) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(message.getMaxExpectedSize());
        message.serialize(bos);
        byte[] data = bos.toByteArray();
        checkMessageSize(message, data);
        return data;
    }

    private void checkMessageSize(UdpMessage message, byte[] data) throws IOException {
        if ( data.length > UdpMessage.MAX_PACKET_SIZE_BYTES) {
            throw new IOException("Cannot send UDP datagram for message " + message + " with size greater than " + UdpMessage.MAX_PACKET_SIZE_BYTES + " bytes of data");
        }
    }

    private class SendUdpDatagramTask implements Runnable {

        private byte[] data;

        public SendUdpDatagramTask(byte[] data) {
            this.data = data;
        }

        public void run() {
            for(ConfigWithSocket clientConfig : getClientConfigSnapshot()) {
                doSendDatagram(clientConfig, data);
            }
        }
    }

    private void doSendDatagram(ConfigWithSocket config, byte[] data) {
        DatagramPacket datagramPacket = new DatagramPacket(data, data.length, config.getInetAddress(), config.getPort());
        DatagramSocket socket = config.getDatagramSocket();

        try {
            if ( socket == null || socket.isClosed()) {
                socket = new DatagramSocket();
                config.setDatagramSocket(socket);
            }
            socket.send(datagramPacket);

        } catch (Throwable t) {
            limitedLogger.logError("Failed to send datagram", t);
            logMethods.debug("Failed to send datagram ");

            //close socket if there was an error
            if ( socket != null ) {
                try {
                    socket.close();
                } catch (Throwable th) {
                    limitedLogger.logError("Failed to close datagramSocket", th);
                }
            }
        }
    }

    /**
     * A wrapper around UdpClientConfig which also maintains a  DatagramSocket
     */
    private class ConfigWithSocket {

        private UdpClientConfig c;
        private DatagramSocket datagramSocket;

        private ConfigWithSocket(UdpClientConfig c) {
            this.c = c;
        }

        public int getPort() {
            return c.getPort();
        }

        public InetAddress getInetAddress() {
            return c.getInetAddress();
        }

        public DatagramSocket getDatagramSocket() {
            return datagramSocket;
        }

        public void setDatagramSocket(DatagramSocket datagramSocket) {
            this.datagramSocket = datagramSocket;
        }

        public UdpClientConfig getUdpClientConfig() {
            return c;
        }

        public void closeSocket() {
            if ( datagramSocket != null) {
                try {
                    datagramSocket.close();
                } catch (Throwable t) {
                    System.err.println("Failed to close datagram socket");
                    t.printStackTrace();
                }
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ConfigWithSocket that = (ConfigWithSocket) o;

            if (c != null ? !c.equals(that.c) : that.c != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = c != null ? c.hashCode() : 0;
            return result;
        }

    }

}
