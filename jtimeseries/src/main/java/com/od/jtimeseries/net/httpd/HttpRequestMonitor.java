package com.od.jtimeseries.net.httpd;

import java.net.Socket;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 24/06/11
 * Time: 09:16
 *
 * Interface to receive notifications when an http request is handled
 *
 * Requests may be handled by separate subthreads which call the methods on this interface,
 * so implementations of HttpRequestMonitor should be threadsafe
 */
public interface HttpRequestMonitor {

    public static final HttpRequestMonitor DUMMY_REQUEST_MONITOR = new HttpRequestMonitor() {

        public void requestStarting(long requestId, Socket clientSocket) {}

        public void servingRequest(long requestId, Socket mySocket, String uri, String method, Properties header, Properties params) {}

        public void finishedRequest(long requestId, Socket mySocket) {}

        public void exceptionDuringProcessing(long requestId, Socket mySocket, Throwable t) {}

        public void badRequest(long requestId, Socket mySocket) {}
    };

    void requestStarting(long requestId, Socket mySocket);

    void servingRequest(long requestId, Socket mySocket, String uri, String method, Properties header, Properties params);

    void finishedRequest(long requestId, Socket mySocket);

    void exceptionDuringProcessing(long requestId, Socket mySocket, Throwable t);

    void badRequest(long requestId, Socket mySocket);
}
