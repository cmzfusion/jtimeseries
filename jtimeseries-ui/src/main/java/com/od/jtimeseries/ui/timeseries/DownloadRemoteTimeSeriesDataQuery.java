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
package com.od.jtimeseries.ui.timeseries;

import com.od.jtimeseries.context.ContextProperties;
import com.od.jtimeseries.net.httpd.AttributeName;
import com.od.jtimeseries.net.httpd.ElementName;
import com.od.jtimeseries.timeseries.DefaultTimeSeriesItem;
import com.od.jtimeseries.timeseries.TimeSeriesItem;
import com.od.jtimeseries.ui.util.AbstractRemoteQuery;
import com.od.jtimeseries.util.numeric.DoubleNumeric;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
* User: nick
* Date: 31-May-2009
* Time: 15:30:14
* To change this template use File | Settings | File Templates.
*/
public class DownloadRemoteTimeSeriesDataQuery extends AbstractRemoteQuery {

    private List<TimeSeriesItem> itemsToAdd = new ArrayList<TimeSeriesItem>();
    private RemoteHttpTimeSeries destinationSeries;

    public DownloadRemoteTimeSeriesDataQuery(RemoteHttpTimeSeries destinationSeries, URL url) {
        super(url);
        this.destinationSeries = destinationSeries;
    }

    public ContentHandler getContentHandler() {
        return new DefaultHandler(){
            public void startElement (String uri, String localName, String qName, Attributes attributes) {
                if ( localName.equals(ElementName.series.name())) {
                    refreshSummaryStatsProperties(attributes);
                }

                if ( localName.equals(ElementName.seriesItem.name())) {
                    parseTimeSeriesItem(attributes);
                }
            }

            public void endDocument() {
                destinationSeries.addAll(itemsToAdd);
                itemsToAdd.clear();
            }
        };
    }

    private void refreshSummaryStatsProperties(Attributes attributes) {
        String stats = attributes.getValue(AttributeName.summaryStats.name());
        Properties p = ContextProperties.createSummaryStatsProperties(stats);
        destinationSeries.updateSummaryStats(p);
    }

    private void parseTimeSeriesItem(Attributes attributes) {
        String timestamp = attributes.getValue(AttributeName.timestamp.name());
        String value = attributes.getValue(AttributeName.value.name());

        if ( timestamp != null && value != null ) {
            addTimeSeriesItemIfNew(timestamp, value);
        }
    }

    private void addTimeSeriesItemIfNew(String timestamp, String value) {
        long longTimestamp = Long.parseLong(timestamp);
        double doubleValue = value.equals("?") ? Double.NaN : Double.parseDouble(value);

        //only add latest items
        if ( destinationSeries.size() == 0 || longTimestamp > destinationSeries.getLatestTimestamp()) {
            TimeSeriesItem newItem = new DefaultTimeSeriesItem(longTimestamp, DoubleNumeric.valueOf(doubleValue));
            itemsToAdd.add(newItem);
        }
    }

    public String getQueryDescription() {
        return "DownloadRemoteTimeSeriesDataQuery";
    }
}
