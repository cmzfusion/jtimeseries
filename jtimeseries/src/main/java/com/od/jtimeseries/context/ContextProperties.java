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
package com.od.jtimeseries.context;

import java.util.*;

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
    public static final String SUMMARY_STATS_LAST_UPDATE_PROPERTY = createSummaryStatsPropertyName("lastSummaryStatsUpdate", SummaryStatsDataType.TIMESTAMP);


    public static String createSummaryStatsPropertyName(String statisticName, SummaryStatsDataType d) {
        return SUMMARY_STATS_PREFIX + ":" + statisticName + ":" + d;
    }

    public static String parseStatisticName(String summaryStatsProperty) {
        StringTokenizer st = new StringTokenizer(summaryStatsProperty, ":");
        st.nextToken();
        return st.nextToken();
    }

    public static String getSummaryStatsStringRepresentation(Properties properties) {
        StringBuilder sb = new StringBuilder();
        for ( Object property : properties.keySet()) {
            if ( property instanceof String && isSummaryStatsProperty((String)property) ){
                sb.append(property).append("=").append(properties.getProperty((String)property)).append(SUMMARY_STATS_SEPARATOR_TOKEN);
            }
        }
        return sb.toString();
    }

    public static Properties createSummaryStatsProperties(String summaryStatsStringRepresentation) {
        Properties p = new Properties();
        StringTokenizer st = new StringTokenizer(summaryStatsStringRepresentation, SUMMARY_STATS_SEPARATOR_TOKEN);
        String property;
        String[] nameAndValue;
        while(st.hasMoreTokens()) {
            property = st.nextToken();
            nameAndValue = property.split("=");
            //we have many instances of each summary stats property, so interning saves a lot of memory
            p.setProperty(nameAndValue[0].intern(), nameAndValue[1]);
        }
        return p;
    }

    public static void addStatsProperties(Properties p, Properties destination) {
        for (Object key : p.keySet()) {
            String propertyName = (String) key;
            if ( ContextProperties.isSummaryStatsProperty(propertyName)) {
                destination.setProperty(propertyName, p.getProperty(propertyName));
            }
        }
    }

    /**
     * @return a sorted list of summary stats properties from the Properties map
     */
    public static List<String> getSummaryStatsPropertyNames(Properties properties) {
        List<String> p = new ArrayList<String>();
        for ( Map.Entry e : properties.entrySet()) {
            if ( isSummaryStatsProperty((String)e.getKey())) {
                p.add((String)e.getKey());
            }
        }
        Collections.sort(p);
        return p;
    }

    public static boolean isSummaryStatsProperty(String property) {
        return property.startsWith(SUMMARY_STATS_PREFIX);
    }

    public static SummaryStatsDataType getSummaryStatsDataType(String statsProperty) {
        SummaryStatsDataType result = SummaryStatsDataType.UNKNOWN;
        for ( SummaryStatsDataType s : SummaryStatsDataType.values())  {
            if ( statsProperty.endsWith(s.getPropertySuffix())) {
                result = s;
                break;
            }
        }
        return result;
    }

    public static enum SummaryStatsDataType {

        UNKNOWN("unknown"),
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
