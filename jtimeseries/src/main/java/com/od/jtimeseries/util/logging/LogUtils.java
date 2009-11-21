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

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 12-Jan-2009
 * Time: 10:30:43
 *
 * JTimeseries classes get a LogMethods instance through LogUtils, to decouple them from any specific logging implementation
 *
 * In general, applications using jtimeseries library may wish the logging to be done via their usual logging provider (e.g. log4j)
 * To accomplish this you need to set the LogMethodsFactory on this class on application startup, and make it create/return approprite
 * LogMethods implementations, which delegate to your logger utilities implementation.
 */
public class LogUtils {

    private static volatile LogMethods defaultLogMethods = new StandardOutputLogMethods();

    private static volatile LogMethodsFactory logMethodsFactory = new LogMethodsFactory() {
        public LogMethods getLogMethods(Class c) {
            return defaultLogMethods;
        }
    };

    public static LogMethods getLogMethods(Class c) {
        return LogUtils.logMethodsFactory.getLogMethods(c);
    }

    public static void setDefaultLogMethods(LogMethods defaultLogMethods) {
        LogUtils.defaultLogMethods = defaultLogMethods;
    }

    public static void setLogMethodFactory(LogMethodsFactory logMethodFactory) {
        LogUtils.logMethodsFactory = logMethodFactory;
    }

    public static LogMethodsFactory getLogMethodsFactory() {
        return logMethodsFactory;
    }
}
