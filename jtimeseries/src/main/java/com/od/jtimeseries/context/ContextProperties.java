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
package com.od.jtimeseries.context;

import sun.net.ftp.FtpClient;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 02-Jun-2009
 * Time: 11:28:12
 *
 * Standard proprties which can be set on a TimeSeriesContext
 */
public class ContextProperties {

    private static final String SUMMARY_STATS_PREFIX = "ss";

    /**
     * This property determines whether captures added to a context are started immediately.
     * If not set locally, a context will inherit this property from it's ancestors in the context tree
     */
    public static final String START_CAPTURES_IMMEDIATELY_PROPERTY = "START_CAPTURES_IMMEDIATELY";

    /**
     * Time summary stats were last updated
     */
    public static final String SUMMARY_STATS_LAST_UPDATE_PROPERTY = getSummaryStatsPropertyName("lastSummaryStatsUpdate", SummaryStatsDataType.TIMESTAMP);


    public static String getSummaryStatsPropertyName(String statisticName, SummaryStatsDataType d) {
        return SUMMARY_STATS_PREFIX + ":" + statisticName + ":" + d;
    }

    public static String getSummaryStatsAsString(Properties properties) {
        StringBuilder sb = new StringBuilder();
        for ( String property : properties.stringPropertyNames()) {
            if ( isSummaryStatsProperty(property) ){
                sb.append(property).append("=").append(properties.get(property)).append(";");
            }
        }
        return sb.toString();
    }

    private static boolean isSummaryStatsProperty(String property) {
        return property.startsWith(SUMMARY_STATS_PREFIX);
    }

    public static enum SummaryStatsDataType {

        TIMESTAMP("ts"),
        LONG("l"),
        DOUBLE("d");

        String propertySuffix;

        private SummaryStatsDataType(String propertySuffix) {
            this.propertySuffix = propertySuffix;
        }

        public String getPropertySuffix() {
            return propertySuffix;
        }

        public String toString() {
            return propertySuffix;
        }
    }
}
