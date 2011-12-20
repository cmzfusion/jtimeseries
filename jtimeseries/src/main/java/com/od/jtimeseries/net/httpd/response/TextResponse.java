package com.od.jtimeseries.net.httpd.response;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 20/12/11
* Time: 19:31
*/
public class TextResponse extends NoCacheResponse {

    private String text;

    public TextResponse(String status, String mimeType, String text) {
        super(status, mimeType);
        this.text = text;
    }

     /**
     * Subclass should write the body of the response to the stream
     */
    public void writeResponseBody(OutputStream out, PrintWriter pw) {
        pw.write(text);
    }
}
