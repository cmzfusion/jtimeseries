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
package com.od.jtimeseries.net.udp;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 16-Mar-2010
 * Time: 18:34:10
 * <p/>
 * <p/>
 * This is a performance optimization -
 * since the server has to deserialize many properties files in udp messages a lot of cpu was
 * being wasted in the creation and gc of temporary objects created during Properties.loadFromXml()
 * <p/>
 * The logic is the same as Properties.loadFromXml() but builderFactory etc. is cached for reuse.
 */
class PropertiesUtil {

    // The required DTD URI for exported properties
    private static final String PROPS_DTD_URI =
            "http://java.sun.com/dtd/properties.dtd";

    private static final String PROPS_DTD =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<!-- DTD for properties -->" +
                    "<!ELEMENT properties ( comment?, entry* ) >" +
                    "<!ATTLIST properties" +
                    " version CDATA #FIXED \"1.0\">" +
                    "<!ELEMENT comment (#PCDATA) >" +
                    "<!ELEMENT entry (#PCDATA) >" +
                    "<!ATTLIST entry " +
                    " key CDATA #REQUIRED>";

    /**
     * Version number for the format of exported properties files.
     */
    private static final String EXTERNAL_XML_VERSION = "1.0";

    private final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder documentBuilder;

    public PropertiesUtil() throws ParserConfigurationException {
        builderFactory.setIgnoringElementContentWhitespace(true);
        builderFactory.setValidating(true);
        builderFactory.setCoalescing(true);
        builderFactory.setIgnoringComments(true);
        documentBuilder = builderFactory.newDocumentBuilder();
        documentBuilder.setEntityResolver(new Resolver());
        documentBuilder.setErrorHandler(new EH());
    }

    public Properties loadFromXML(InputStream in) throws IOException {
        if (in == null)
            throw new NullPointerException();
        Properties p = new Properties();
        load(p, in);
        in.close();
        return p;
    }

    private void load(Properties props, InputStream in) throws IOException {
        Document doc = null;
        try {
            doc = getLoadingDoc(in);
        } catch (SAXException saxe) {
            throw new InvalidPropertiesFormatException(saxe);
        }
        Element propertiesElement = (Element) doc.getChildNodes().item(1);
        String xmlVersion = propertiesElement.getAttribute("version");
        if (xmlVersion.compareTo(EXTERNAL_XML_VERSION) > 0)
            throw new InvalidPropertiesFormatException(
                    "Exported Properties file format version " + xmlVersion +
                            " is not supported. This java installation can read" +
                            " versions " + EXTERNAL_XML_VERSION + " or older. You" +
                            " may need to install a newer version of JDK.");
        importProperties(props, propertiesElement);
    }

    private Document getLoadingDoc(InputStream in) throws SAXException, IOException {
        InputSource is = new InputSource(in);
        return documentBuilder.parse(is);
    }

    private void importProperties(Properties props, Element propertiesElement) {
        NodeList entries = propertiesElement.getChildNodes();
        int numEntries = entries.getLength();
        int start = numEntries > 0 &&
                entries.item(0).getNodeName().equals("comment") ? 1 : 0;
        for (int i = start; i < numEntries; i++) {
            Element entry = (Element) entries.item(i);
            if (entry.hasAttribute("key")) {
                Node n = entry.getFirstChild();
                String val = (n == null) ? "" : n.getNodeValue();
                props.setProperty(entry.getAttribute("key"), val);
            }
        }
    }

    private static class Resolver implements EntityResolver {

        public InputSource resolveEntity(String pid, String sid)
                throws SAXException {
            if (sid.equals(PROPS_DTD_URI)) {
                InputSource is;
                is = new InputSource(new StringReader(PROPS_DTD));
                is.setSystemId(PROPS_DTD_URI);
                return is;
            }
            throw new SAXException("Invalid system identifier: " + sid);
        }
    }

    private static class EH implements ErrorHandler {

        public void error(SAXParseException x) throws SAXException {
            throw x;
        }

        public void fatalError(SAXParseException x) throws SAXException {
            throw x;
        }

        public void warning(SAXParseException x) throws SAXException {
            throw x;
        }
    }

}

