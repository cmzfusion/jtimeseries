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
import java.util.StringTokenizer;

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
    private static final String SUMMARY_STATS_SEPARATOR_TOKEN = ";";

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

    public static String getSummaryStatsPropertyString(Properties properties) {
        StringBuilder sb = new StringBuilder();
        for ( String property : properties.stringPropertyNames()) {
            if ( isSummaryStatsProperty(property) ){
                sb.append(property).append("=").append(properties.get(property)).append(SUMMARY_STATS_SEPARATOR_TOKEN);
            }
        }
        return sb.toString();
    }

    public static Properties getSummaryStatsProperties(String summaryStatsPropertyString) {
        Properties p = new Properties();
        StringTokenizer st = new StringTokenizer(summaryStatsPropertyString, SUMMARY_STATS_SEPARATOR_TOKEN);
        String property;
        String[] nameAndValue;
        while(st.hasMoreTokens()) {
            property = st.nextToken();
            nameAndValue = property.split("=");
            p.setProperty(nameAndValue[0], nameAndValue[1]);
        }
        return p;
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
