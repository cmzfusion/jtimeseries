package com.od.jtimeseries.net.udp.message.utf8;

import com.od.jtimeseries.net.udp.message.MessageType;
import com.od.jtimeseries.net.udp.message.SeriesDescriptionMessage;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19/03/12
 * Time: 17:44
 */
public class Utf8DescriptionMessage extends AbstractUtf8Message implements SeriesDescriptionMessage {

    public static final String PATH_FIELD_KEY ="PATH";
    public static final String DESCRIPTION_FIELD_KEY ="DESCRIPTION";

    private String path;
    private String seriesDescription;

    Utf8DescriptionMessage() {}

    Utf8DescriptionMessage(String hostname, String path, String seriesDescription) {
        super(hostname);
        this.path = path;
        this.seriesDescription = seriesDescription;
    }

    protected void writeBodyFieldsToOutputStream(Writer writer, StringBuilder sb) throws IOException {
        appendValue(writer, PATH_FIELD_KEY, path, sb);
        appendValue(writer, DESCRIPTION_FIELD_KEY, seriesDescription, sb);
    }

    protected void doPostDeserialize() throws IOException {
        if ( path == null ) {
            throw new IOException(PATH_FIELD_KEY + " field not set in message " + getMessageType());
        } else if ( seriesDescription == null ) {
            throw new IOException(DESCRIPTION_FIELD_KEY + " field not set in message " + getMessageType());
        }
    }

    protected void doSetProperties(String key, String value) throws IOException {
        if ( PATH_FIELD_KEY.equals(key)) {
            path = value;
        } else if ( DESCRIPTION_FIELD_KEY.equals(key)) {
            seriesDescription = value;
        }
    }

    public String getSeriesPath() {
        return path;
    }

    public String getSeriesDescription() {
        return seriesDescription;
    }

    public MessageType getMessageType() {
        return MessageType.SERIES_DESCRIPTION;
    }
}
