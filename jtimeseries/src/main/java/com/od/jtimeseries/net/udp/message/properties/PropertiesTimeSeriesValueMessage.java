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
package com.od.jtimeseries.net.udp.message.properties;

import com.od.jtimeseries.net.udp.message.MessageType;
import com.od.jtimeseries.net.udp.message.TimeSeriesValueMessage;
import com.od.jtimeseries.timeseries.Item;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.util.numeric.DoubleNumeric;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 16-May-2009
 * Time: 23:22:45
 * To change this template use File | Settings | File Templates.
 */
public class PropertiesTimeSeriesValueMessage extends AbstractPropertiesUdpMessage implements TimeSeriesValueMessage {

    public static final String MESSAGE_TYPE = "TimeSeriesValueMessage";
    public static final String SERIES_PATH_KEY = "CONTEXT_PATH"; //keep CONTEXT_PATH to support legacy clients although this is series path
    public static final String TIMESTAMP_KEY = "TIMESTAMP";
    public static final String NUMERIC_VALUE_KEY = "VALUE";

    @Deprecated
    public static final String DESCRIPTION_KEY = "DESCRIPTION";

    /**
     * Create a time series value message
     *
     * The seriesDescription will be truncated if it might be too large to fit into one datagram packet
     * but for most practical purposes it can be long enough.
     */
    PropertiesTimeSeriesValueMessage(String seriesPath, TimeSeriesItem timeSeriesItem) {
        super(MESSAGE_TYPE);

        setProperty(SERIES_PATH_KEY, seriesPath);
        setProperty(TIMESTAMP_KEY, String.valueOf(timeSeriesItem.getTimestamp()));
        setProperty(NUMERIC_VALUE_KEY, timeSeriesItem.getValue().toString());
    }

    PropertiesTimeSeriesValueMessage(Properties p) {
        super(p);
    }

    public String getSeriesPath() {
        return getProperty(SERIES_PATH_KEY);
    }

    public String getDescription() {
        return getProperty(DESCRIPTION_KEY);
    }

    public TimeSeriesItem getTimeSeriesItem() {
        return new Item(
            Long.parseLong(getProperty(TIMESTAMP_KEY)),
            Double.parseDouble(getProperty(NUMERIC_VALUE_KEY))
        );
    }

    public String toString() {
        return "TimeSeriesValueMessage " + getSourceHostname() + " " + getTimeSeriesItem();
    }

    public MessageType getMessageType() {
        return MessageType.TS_VALUE;
    }
}
