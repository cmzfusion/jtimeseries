package com.od.jtimeseries.net.udp.message.properties;

import com.od.jtimeseries.net.udp.message.MessageType;
import com.od.jtimeseries.net.udp.message.SeriesDescriptionMessage;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19/03/12
 * Time: 17:54
 */
public class PropertiesDescriptionMessage extends AbstractPropertiesUdpMessage implements SeriesDescriptionMessage {

    public static final String MESSAGE_TYPE = "SeriesDescriptionMessage";
    public static final String PATH_KEY = "PATH";
    public static final String DESCRIPTION_KEY = "DESCRIPTION";

    public PropertiesDescriptionMessage(Properties p) {
        super(p);
    }

    public PropertiesDescriptionMessage(String seriesPath, String description) {
        super(MESSAGE_TYPE);
        setProperty(PATH_KEY, seriesPath);
        setProperty(DESCRIPTION_KEY, description);
    }

    public MessageType getMessageType() {
        return MessageType.SERIES_DESCRIPTION;
    }

    public String getSeriesDescription() {
        return getProperty(DESCRIPTION_KEY);
    }

    public String getSeriesPath() {
        return getProperty(PATH_KEY);
    }
}
