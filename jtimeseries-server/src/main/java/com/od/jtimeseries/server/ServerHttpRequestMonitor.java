package com.od.jtimeseries.server;

import com.od.jtimeseries.net.httpd.HttpRequestMonitor;
import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.net.Socket;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 27/06/11
 * Time: 06:37
 */
public class ServerHttpRequestMonitor implements HttpRequestMonitor {

    private static LogMethods logMethods = LogUtils.getLogMethods(ServerHttpRequestMonitor.class);
    private static volatile ValueRecorder htpRequestTimeValueRecorder;

    private ThreadLocal<Long> requestStartTimes = new ThreadLocal<Long>();

    public void requestStarting(long requestId, Socket mySocket) {
        logMethods.logDebug("Starting HTTPD request " + requestId + " from client " + mySocket.getInetAddress());
        requestStartTimes.set(System.currentTimeMillis());
    }

    public void servingRequest(long requestId, Socket mySocket, String uri, String method, Properties header, Properties params) {
        logMethods.logInfo("Serving HTTPD request " + requestId + " from client " + mySocket.getInetAddress() + ", URI " + uri);
    }

    public void finishedRequest(long requestId, Socket mySocket) {
        logMethods.logDebug("Fnished HTTPD request " + requestId);
        logQueryTime();
    }

    private void logQueryTime() {
        long startTime = requestStartTimes.get();
        long timeTaken = System.currentTimeMillis() - startTime;
        if ( htpRequestTimeValueRecorder != null) {
            htpRequestTimeValueRecorder.newValue(timeTaken);
        }
    }

    public void exceptionDuringProcessing(long requestId, Socket mySocket, Throwable t) {
        logMethods.logWarning("Error processing HTTPD request " + requestId, t);
    }

    public void badRequest(long requestId, Socket mySocket) {
        logMethods.logWarning("Processed invalid http request");
    }

    public static void setHtpRequestTimeValueRecorder(ValueRecorder htpRequestTimeValueRecorder) {
        ServerHttpRequestMonitor.htpRequestTimeValueRecorder = htpRequestTimeValueRecorder;
    }
}
