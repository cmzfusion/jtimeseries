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

import java.io.ByteArrayInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 13-Jan-2009
 * Time: 11:12:18
 */
public class UdpServer {

    public static final int MAX_PACKET_SIZE = 8192;
    private static final int RESTART_WAIT = 600000; //10 mins

    private LimitedErrorLogger limitedLogger;
    private int port;
    private final List<UdpMessageListener> udpMessageListeners = Collections.synchronizedList(new ArrayList<UdpMessageListener>());
    private MessageFactory udpMessageFactory = new MessageFactory();

    private Executor udpMessageExecutor = Executors.newSingleThreadExecutor();
    private volatile boolean stopping;
    private Thread receiveThread;

    public UdpServer(int port) {
        this(LogUtils.getLogMethods(UdpServer.class), port);
    }

    public UdpServer(LogMethods log, int port) {
        limitedLogger = new LimitedErrorLogger(log, 10, 100);
        this.port = port;
        startReceive();
    }

    public void setMessageFactory(MessageFactory m) {
        this.udpMessageFactory = m;
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

    private void fireUdpMessageReceived(Properties p) {

        UdpMessage m = udpMessageFactory.getMessage(p);
        if ( m != null) {
            List<UdpMessageListener> snapshot;
            synchronized (udpMessageListeners) {
                snapshot = new ArrayList<UdpMessageListener>(udpMessageListeners);
            }

            for ( UdpMessageListener l : snapshot) {
                l.udpMessageReceived(m);
            }
        } else {
            String message = "Unknown UDP message received with type " + p.getProperty(UdpMessage.MESSAGE_TYPE_PROPERTY);
            limitedLogger.logError(message);
        }
    }

    public static interface UdpMessageListener {
        void udpMessageReceived(UdpMessage m);
    }

    public class UdpReceiveThread extends Thread {

        public void run() {
            byte[] buffer = new byte[MAX_PACKET_SIZE];
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
                    byte[] receivedData = new byte[packet.getLength()];
                    System.arraycopy(buffer, 0, receivedData, 0, packet.getLength());
                    ByteArrayInputStream bos = new ByteArrayInputStream(receivedData);
                    final Properties p = new Properties();
                    p.loadFromXML(bos);
                    fireMessageToListeners(p);
                }
                catch (Throwable t) {
                    limitedLogger.logError("Error receiving UdpClient", t);
                }
            }
            stopping = false;
        }

        private void fireMessageToListeners(final Properties p) {
            udpMessageExecutor.execute(
                        new Runnable() {
                    public void run() {
                        fireUdpMessageReceived(p);
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
