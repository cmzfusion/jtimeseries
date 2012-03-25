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

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ! ( o instanceof SeriesDescriptionMessage)) return false;
        if (!super.equals(o)) return false;

        SeriesDescriptionMessage that = (SeriesDescriptionMessage) o;

        if (getSeriesDescription() != null ? !getSeriesDescription().equals(that.getSeriesDescription()) : that.getSeriesDescription() != null) return false;
        if (getSeriesPath() != null ? !getSeriesPath().equals(that.getSeriesPath()) : that.getSeriesPath() != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getSeriesDescription() != null ? getSeriesDescription().hashCode() : 0);
        result = 31 * result + (getSeriesPath() != null ? getSeriesPath().hashCode() : 0);
        return result;
    }

    public String toString() {
        return getClass().getSimpleName() + "{" +
                "description='" + getSeriesDescription() + '\'' +
                ", path='" + getSeriesPath() + '\'' +
                super.toString() +
                "} " + super.toString();
    }
}
