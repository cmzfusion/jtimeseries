package com.od.jtimeseries.server;

import com.od.jtimeseries.net.httpd.HttpRequestMonitor;
import com.od.jtimeseries.source.Counter;
import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.source.impl.DefaultCounter;
import com.od.jtimeseries.source.impl.DefaultValueRecorder;
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
    private static volatile ValueRecorder htpRequestTimeValueRecorder = DefaultValueRecorder.NULL_VALUE_RECORDER;
    private static Counter requestCounter = DefaultCounter.NULL_COUNTER;
    private static Counter requestErrorCounter = DefaultCounter.NULL_COUNTER;
    private static Counter requestInvalidCounter;

    private ThreadLocal<Long> requestStartTimes = new ThreadLocal<Long>();

    public void requestStarting(long requestId, Socket mySocket) {
        logMethods.logDebug("Starting HTTPD request " + requestId + " from client " + mySocket.getInetAddress());
        requestCounter.incrementCount();
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
        requestErrorCounter.incrementCount();
        logMethods.logWarning("Error processing HTTPD request " + requestId, t);
    }

    public void badRequest(long requestId, Socket mySocket, String httpErrorType, String errorDescription) {
        requestInvalidCounter.incrementCount();
        logMethods.logWarning("Processed invalid http request, " + httpErrorType + ", " + errorDescription);
    }

    public static void setHttpRequestTimeValueRecorder(ValueRecorder htpRequestTimeValueRecorder) {
        ServerHttpRequestMonitor.htpRequestTimeValueRecorder = htpRequestTimeValueRecorder;
    }

    public static void setHttpRequestCounter(Counter requestCounter) {
        ServerHttpRequestMonitor.requestCounter = requestCounter;
    }

    public static void setHttpRequestErrorCounter(Counter requestErrorCounter) {
        ServerHttpRequestMonitor.requestErrorCounter = requestErrorCounter;
    }

    public static void setHttpRequestInvalidCounter(Counter requestInvalidCounter) {
        ServerHttpRequestMonitor.requestInvalidCounter = requestInvalidCounter;
    }
}
