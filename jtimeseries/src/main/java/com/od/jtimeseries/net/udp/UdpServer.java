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
import com.od.jtimeseries.net.udp.message.properties.PropertiesMessageFactory;
import com.od.jtimeseries.util.NamedExecutors;
import com.od.jtimeseries.util.logging.LimitedErrorLogger;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 13-Jan-2009
 * Time: 11:12:18
 *
 */
public class UdpServer {

    private static LogMethods logMethods = LogUtils.getLogMethods(UdpServer.class);

    private static final int RESTART_WAIT = 600000; //10 mins

    private LimitedErrorLogger limitedLogger;
    private int port;
    private final List<UdpMessageListener> udpMessageListeners = Collections.synchronizedList(new ArrayList<UdpMessageListener>());

    private Executor udpMessageExecutor = NamedExecutors.newSingleThreadExecutor("UdpServer");
    private volatile boolean stopping;
    private Thread receiveThread;

    private PropertiesMessageFactory propertiesMessageFactory = new PropertiesMessageFactory();

    public UdpServer(int port) {
        limitedLogger = new LimitedErrorLogger(logMethods, 10, 100);
        this.port = port;
    }

    public void setMessageFactory(PropertiesMessageFactory m) {
        this.propertiesMessageFactory = m;
    }

    public synchronized void startReceive() {
        if ( receiveThread == null || ! receiveThread.isAlive()) {
            receiveThread = new UdpReceiveThread();
            receiveThread.start();
        }
    }

    public synchronized void stop() {
        this.stopping = true;
    }

    public void addUdpMessageListener(UdpMessageListener l) {
        udpMessageListeners.add(l);
    }

    public void removeUdpMessageListener(UdpMessageListener l) {
        udpMessageListeners.remove(l);
    }

    private void fireUdpMessageReceived(UdpMessage m) {
            List<UdpMessageListener> snapshot;
            synchronized (udpMessageListeners) {
                snapshot = new ArrayList<UdpMessageListener>(udpMessageListeners);
            }

            for ( UdpMessageListener l : snapshot) {
                l.udpMessageReceived(m);
            }
    }

    private void logUnknownMessage(String messageType) {
        String message = "Received UDP message with unknown type " + messageType;//p.getProperty(UdpMessage.MESSAGE_TYPE_PROPERTY);
        limitedLogger.logError(message);
    }

    public int getPort() {
        return port;
    }

    public static interface UdpMessageListener {
        void udpMessageReceived(UdpMessage m);
    }

    public class UdpReceiveThread extends Thread {

        public UdpReceiveThread() {
            setName("JTimeSeriesUDPSocketReceive");
            setDaemon(true);
        }

        public void run() {
            byte[] buffer = new byte[UdpMessage.MAX_PACKET_SIZE_BYTES];
            try {
                DatagramSocket server = new DatagramSocket(port);
                processMessages(buffer, server);
            } catch (SocketException e) {
                limitedLogger.logError("Error creating UdpServer socket, will try again later", e);
                restartUdpReceive();
            }
        }

        private void processMessages(byte[] buffer, DatagramSocket server) {
            while (! stopping) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    server.receive(packet);

                    String messageXml = new String(buffer, 0, packet.getLength(), "UTF-8");
                    UdpMessage m = propertiesMessageFactory.getMessage(messageXml);
                    if ( m != null ) {
                        fireMessageToListeners(m);
                    } else {
                        logUnknownMessage(propertiesMessageFactory.getLastMessageType());
                    }
                }
                catch (Throwable t) {
                    limitedLogger.logError("Error receiving UdpClient", t);
                }
            }
            stopping = false;
        }

        private void fireMessageToListeners(final UdpMessage m) {
            udpMessageExecutor.execute(
                        new Runnable() {
                    public void run() {
                        fireUdpMessageReceived(m);
                    }
                }
            );
        }

        private void restartUdpReceive() {
            try {
                sleep(RESTART_WAIT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startReceive();
        }
    }
}
