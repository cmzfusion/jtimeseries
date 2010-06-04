/**
 * Copyright (C) 2009 (nick @ objectdefinitions.com)
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

import com.od.jtimeseries.util.logging.LimitedErrorLogger;
import com.od.jtimeseries.util.logging.LogUtils;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.time.TimePeriod;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.Executors;
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
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    //using the set to check whether already in the list without iterating
    //want to preserve the order in which clients are sent messages so keeping the list
    private final Set<ClientConfig> configSet = Collections.synchronizedSet(new HashSet<ClientConfig>());
    private final List<ClientConfig> configs = Collections.synchronizedList(new LinkedList<ClientConfig>());

    /**
     * Create a UdpClient, but this won't send any messages until at least one client
     * config is added
     */
    public UdpClient() {}

    public UdpClient(ClientConfig config) {
        this(new ArrayList<ClientConfig>(Arrays.asList(config)));
    }

    public UdpClient(List<ClientConfig> configs) {
        addClientConfigs(configs);
    }

    public void addClientConfig(ClientConfig... configs) {
        addClientConfigs(Arrays.asList(configs));
    }

    public void addClientConfigs(List<ClientConfig> clientConfigs) {
        for ( ClientConfig config : clientConfigs) {
            addClientConfig(config);
        }
    }

    public boolean addClientConfig(ClientConfig config) {
        synchronized (configs) {
            boolean added = configSet.add(config);
            if (added) {
                configs.add(config);
            }
            return added;
        }
    }

    public void removeClientConfig(ClientConfig... configs) {
        removeClientConfig(Arrays.asList(configs));
    }


    public void removeClientConfig(List<ClientConfig> clientConfigs) {
        for ( ClientConfig c : clientConfigs) {
            doRemoveConfig(c);
        }
    }

    private boolean doRemoveConfig(ClientConfig c) {
        synchronized (configs) {
            boolean removed = configSet.remove(c);
            if ( removed ) {
                configs.remove(c);
            }
            return removed;
        }
    }

    /**
     * @return A snapshot of current client configs
     */
    public List<ClientConfig> getClientConfigs() {
        return getClientConfigSnapshot();
    }

    public void sendMessage(Properties propertiesForDatagram) {
        try {
            byte[] data = createDatagram(propertiesForDatagram);
            scheduledExecutor.execute(new SendUdpDatagramTask(data));
        } catch (IOException e) {
            logMethods.logError("Could not create UdpClient ", e);
        }
    }

    //get a snapshot of the current configs to iterate over
    private List<ClientConfig> getClientConfigSnapshot() {
        return new ArrayList<ClientConfig>(configs);
    }

    public void sendRepeatedMessage(Properties propertiesForDatagram, TimePeriod period) {
        try {
            byte[] data = createDatagram(propertiesForDatagram);
            scheduledExecutor.scheduleAtFixedRate(
                new SendUdpDatagramTask(data),
                0,
                period.getLengthInMillis(),
                TimeUnit.MILLISECONDS
            );
        } catch (IOException e) {
            logMethods.logError("Could not create UdpClient ", e);
        }
    }

    public synchronized void stop() {
        scheduledExecutor.shutdown();
        for (ClientConfig c : configs) {
            c.closeSocket();
        }
    }

    private byte[] createDatagram(Properties propertiesForDatagram) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
        propertiesForDatagram.storeToXML(bos, null);
        byte[] data = bos.toByteArray();
        if ( data.length > UdpServer.MAX_PACKET_SIZE ) {
            throw new IOException("Cannot send UdpClient with properties consuming more than " + UdpServer.MAX_PACKET_SIZE + " bytes of data");
        }
        return data;
    }

    public static class ClientConfig {
        private InetAddress inetAddress;
        private int port;
        private DatagramSocket datagramSocket;

        public ClientConfig(InetAddress inetAddress, int port) {
            this.inetAddress = inetAddress;
            this.port = port;
        }

        public InetAddress getInetAddress() {
            return inetAddress;
        }

        public int getPort() {
            return port;
        }

        DatagramSocket getDatagramSocket() {
            return datagramSocket;
        }

        void setDatagramSocket(DatagramSocket datagramSocket) {
            this.datagramSocket = datagramSocket;
        }

        void closeSocket() {
            if ( datagramSocket != null) {
                try {
                    datagramSocket.close();
                } catch (Throwable t) {
                    System.err.println("Failed to close datagram socket");
                    t.printStackTrace();
                }
            }
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ClientConfig that = (ClientConfig) o;

            if (port != that.port) return false;
            if (inetAddress != null ? !inetAddress.equals(that.inetAddress) : that.inetAddress != null) return false;

            return true;
        }

        public int hashCode() {
            int result = inetAddress != null ? inetAddress.hashCode() : 0;
            result = 31 * result + port;
            result = 31 * result + (datagramSocket != null ? datagramSocket.hashCode() : 0);
            return result;
        }

        public String toString() {
            return inetAddress.getHostName() + ":" + port;
        }
    }

    private class SendUdpDatagramTask implements Runnable {

        private byte[] data;

        public SendUdpDatagramTask(byte[] data) {
            this.data = data;
        }

        public void run() {
            for(ClientConfig clientConfig : getClientConfigSnapshot()) {
                doSendDatagram(clientConfig, data);
            }
        }
    }

    private void doSendDatagram(ClientConfig config, byte[] data) {
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
            logMethods.logDebug("Failed to send datagram ");

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

}
