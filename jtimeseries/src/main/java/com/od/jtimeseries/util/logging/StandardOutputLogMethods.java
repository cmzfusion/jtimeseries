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
package com.od.jtimeseries.util.logging;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 03-Dec-2008
 * Time: 12:20:08
 */
public class StandardOutputLogMethods implements LogMethods {

    private static volatile boolean isDebugLoggingOn = false;

    public void logInfo(String s) {
        System.out.println("JTIMESERIES INFO--> " + new Date() + " " + s);
    }

    public void logDebug(String s) {
        if ( isDebugLoggingOn) {
            System.out.println("JTIMESERIES DEBUG--> " + new Date() + " " + s);
        }
    }

    public void logDebug(String s, Throwable t) {
        System.out.println("JTIMESERIES ERROR--> " + new Date() + " " + s);
        t.printStackTrace();
    }

    public void logWarning(String s) {
        System.out.println("JTIMESERIES WARN--> " + new Date() + " " + s);
    }

    public void logWarning(String s, Throwable t) {
        System.out.println("JTIMESERIES WARN--> " + new Date() + " " + s);
        t.printStackTrace();
    }

    public void logError(String s) {
        System.out.println("JTIMESERIES ERROR--> " + new Date() + " " + s);        
        System.err.println("JTIMESERIES ERROR--> " + new Date() + " " + s);
    }

    public void logError(String s, Throwable t) {
        System.out.println("JTIMESERIES ERROR--> " + new Date() + " " + s);
        System.err.println("JTIMESERIES ERROR--> " + new Date() + " " + s);
        t.printStackTrace();
    }

    public static void setDebugLoggingOn(boolean debugLoggingOn) {
        isDebugLoggingOn = debugLoggingOn;
    }
}
