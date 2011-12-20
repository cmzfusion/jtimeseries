package com.od.jtimeseries.net.httpd.response;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Properties;

/**
 * HTTP response.
 * Return one of these from serve().
 */
public class NanoHttpResponse {

    /**
     * Basic constructor.
     */
    public NanoHttpResponse(String status, String mimeType) {
        this.status = status;
        this.mimeType = mimeType;
    }

    /**
     * Adds given line to the header.
     */
    public void addHeader(String name, String value) {
        header.put(name, value);
    }

    /**
     * HTTP status code after processing, e.g. "200 OK", HTTP_OK
     */
    public String status;

    /**
     * MIME type of content, e.g. "text/html"
     */
    public String mimeType;

    /**
     * Headers for the HTTP response. Use addHeader()
     * to add lines.
     */
    public Properties header = new Properties();

    /**
     * Subclass should write the body of the response to the stream
     */
    public void writeResponseBody(OutputStream out, PrintWriter pw) {
    }
}
