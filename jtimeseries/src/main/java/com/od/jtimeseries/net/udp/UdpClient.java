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
import com.od.jtimeseries.util.NetworkUtils;
import com.od.jtimeseries.util.TimeSeriesExecutorFactory;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.time.TimePeriod;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    
    private final ScheduledExecutorService scheduledExecutor = TimeSeriesExecutorFactory.geUdpClientScheduledExecutor(this);

    //using the set to check whether already in the list without iterating
    //want to preserve the order in which clients are sent messages so keeping the list
    private final Set<UdpClientWithSocket> configSet = Collections.synchronizedSet(new HashSet<UdpClientWithSocket>());
    private final List<UdpClientWithSocket> configs = Collections.synchronizedList(new LinkedList<UdpClientWithSocket>());

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
            UdpClientWithSocket c = new UdpClientWithSocket(config);
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
            UdpClientWithSocket cc = new UdpClientWithSocket(c);
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
        for ( UdpClientWithSocket c : getClientConfigSnapshot()) {
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
        ByteArrayOutputStream bos = new ByteArrayOutputStream(NetworkUtils.getMaxBytesPerDatagramPacket());

        while(m != null && bos.size() < (NetworkUtils.getMaxBytesPerDatagramPacket() - m.getMaxExpectedSize())) {
            ByteArrayOutputStream msgOut = new ByteArrayOutputStream(m.getMaxExpectedSize());
            m.serialize(msgOut);
            byte[] msgBytes = msgOut.toByteArray();
            checkMessageSize(m, msgBytes);
            if ( bos.size() + msgBytes.length < NetworkUtils.getMaxBytesPerDatagramPacket()) {
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
    private List<UdpClientWithSocket> getClientConfigSnapshot() {
        return new ArrayList<UdpClientWithSocket>(configs);
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
        for (UdpClientWithSocket c : configs) {
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
        if ( data.length > NetworkUtils.getMaxBytesPerDatagramPacket()) {
            throw new IOException("Cannot send UDP datagram for message " + message + " with size greater than " + NetworkUtils.getMaxBytesPerDatagramPacket() + " bytes of data, consider setting the " + NetworkUtils.MAX_BYTES_PER_DATAGRAM_PROPERTY + " system property to allow larger packets");
        }
    }

    private class SendUdpDatagramTask implements Runnable {

        private byte[] data;

        public SendUdpDatagramTask(byte[] data) {
            this.data = data;
        }

        public void run() {
            for(UdpClientWithSocket clientConfig : getClientConfigSnapshot()) {
                clientConfig.sendDatagram(data);
            }
        }
    }

}
