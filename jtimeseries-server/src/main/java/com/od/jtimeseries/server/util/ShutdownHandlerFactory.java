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
package com.od.jtimeseries.server.util;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.httpd.HttpHandler;
import com.od.jtimeseries.net.httpd.NanoHTTPD;
import com.od.jtimeseries.net.httpd.TimeSeriesContextHandlerFactory;
import com.od.jtimeseries.util.logging.LogDefaults;
import com.od.jtimeseries.util.logging.LogMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 25-Jun-2009
 * Time: 14:25:58
 */
public class ShutdownHandlerFactory extends TimeSeriesContextHandlerFactory {

    private LogMethods logMethods = LogDefaults.getDefaultLogMethods(ShutdownHandlerFactory.class);
    private List<ShutdownListener> shutdownListeners = Collections.synchronizedList(new ArrayList<ShutdownListener>());
    private ShutdownListener finalListener;

    public ShutdownHandlerFactory(TimeSeriesContext rootContext, ShutdownListener finalListener) {
        super(rootContext);
        this.finalListener = finalListener;
    }

    /**
     * Add a shutdown listener to the list of listeners.
     * These listeners are fired first, followed by a sleep of SHUTDOWN_SLEEP millis, before the final listener is fired
     */
    public void addShutdownListener(ShutdownListener l) {
        shutdownListeners.add(l);
    }

    public void removeShutdownListener(ShutdownListener l) {
        shutdownListeners.remove(l);
    }

    public HttpHandler getHandler(String uri, String method, Properties header, Properties params) {
        HttpHandler result;
        if ( uri.endsWith("shutdown")) {
            result = new ShutdownHandler();
        } else {
             result = super.getHandler(uri, method, header, params);
        }
        return result;
    }

    private void fireShutdownEvent() {
        List<ShutdownListener> l = new ArrayList<ShutdownListener>(shutdownListeners);
        for ( ShutdownListener sl : l) {
            try {
                sl.shutdownNow();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public static interface ShutdownListener {
        void shutdownNow();
    }

    private class ShutdownHandler implements HttpHandler {

        public NanoHTTPD.Response createResponse(String uri, String method, Properties header, Properties parms) {
            String response = "Shutting down now";
            new Thread(new ShutdownRunnable()).start();
            return new NanoHTTPD.Response(NanoHTTPD.HTTP_OK, "text/plain", response);
        }
    }

    private class ShutdownRunnable implements Runnable {

        private static final long SHUTDOWN_SLEEP = 3000;

        public void run() {
            logMethods.logInfo("Received Shutdown http request - about to start shutdown");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            fireShutdownEvent();

            try {
                Thread.sleep(SHUTDOWN_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finalListener.shutdownNow();
        }
    }

    /**
     * Can be used as the final listener to shut the system down
     */
    public static class SystemExitShutdownListener implements ShutdownListener {

        private LogMethods logMethods = LogDefaults.getDefaultLogMethods(ShutdownHandlerFactory.class);

        public void shutdownNow() {
            logMethods.logInfo("Shutdown complete");

            //this is just in the hope that 500ms is enough for that log statement to make it into the logs
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
    }
}
