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
package com.od.jtimeseries.util.logging;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 16-May-2009
 * Time: 17:51:16
 * To change this template use File | Settings | File Templates.
 *
 * Error logging which stops spamming after the first few messages
 */
public class LimitedErrorLogger {

    private LogMethods logMethods;
    private final int maxInitialErrors;
    private final int subsequentSkipRatio;
    private volatile int errors;

    public LimitedErrorLogger(LogMethods logMethods, int maxInitialErrors, int subsequentSkipRatio) {
        this.logMethods = logMethods;
        this.maxInitialErrors = maxInitialErrors;
        this.subsequentSkipRatio = subsequentSkipRatio;
    }

    public void logError(String s, Throwable t) {
        if ( errors++ < maxInitialErrors ) {
            logMethods.error(s, t);
        } else if ( errors++ % subsequentSkipRatio == 0) {
            logMethods.error(s, t);
        }
    }

    public void logError(String s) {
        if ( errors++ < maxInitialErrors ) {
            logMethods.error(s);
        } else if ( errors++ % subsequentSkipRatio == 0) {
            logMethods.error(s);
        }
    }
}
