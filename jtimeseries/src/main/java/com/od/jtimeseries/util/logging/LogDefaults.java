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
 */
public class LogDefaults {

    private static volatile LogMethods defaultLogMethods = new StandardOutputLogMethods();

    private static volatile LogMethodsFactory logMethodsFactory = new LogMethodsFactory() {
        public LogMethods getLogMethods(Class c) {
            return defaultLogMethods;
        }
    };

    public static LogMethods getDefaultLogMethods(Class c) {
        return LogDefaults.logMethodsFactory.getLogMethods(c);
    }

    public static void setDefaultLogMethods(LogMethods defaultLogMethods) {
        LogDefaults.defaultLogMethods = defaultLogMethods;
    }

    public static void setLogMethodFactory(LogMethodsFactory logMethodFactory) {
        LogDefaults.logMethodsFactory = logMethodFactory;
    }

    public static LogMethodsFactory getLogMethodsFactory() {
        return logMethodsFactory;
    }
}
