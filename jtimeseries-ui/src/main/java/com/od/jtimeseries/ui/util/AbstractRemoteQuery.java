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
package com.od.jtimeseries.ui.util;

import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 12-Jan-2009
 * Time: 11:41:44
 */
public abstract class AbstractRemoteQuery {

    private static final LogMethods logMethods = LogUtils.getLogMethods(AbstractRemoteQuery.class);    

    private URL url;

    public URL getQueryUrl() {
        return url;
    }

    public AbstractRemoteQuery(URL url) {
        this.url = url;
    }

    public void runQuery() throws Exception {
        long time = System.currentTimeMillis();
        doBeforeRun();
        XMLReader parser = XMLReaderFactory.createXMLReader();
        String id    = "http://apache.org/xml/properties/input-buffer-size";
        Object value = 20000000;
        try {
            parser.setProperty(id, value);
        }
        catch (SAXException e) {
            System.err.println("could not set parser property");
        }
        parser.setContentHandler(getContentHandler());
        parser.parse(url.toString());
        long timeTaken = System.currentTimeMillis() - time;
        LocalJmxMetrics.getInstance().getQueryTimesRecorder().newValue(timeTaken);
        LocalJmxMetrics.getInstance().getQueryCounter().incrementCount();
        logMethods.debug(getClass().getName() + " query took " + timeTaken + " millis");
    }

    protected void doBeforeRun() {
    }

    public abstract ContentHandler getContentHandler();

    public abstract String getQueryDescription();
}
