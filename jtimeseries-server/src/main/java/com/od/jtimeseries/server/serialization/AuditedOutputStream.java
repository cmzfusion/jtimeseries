package com.od.jtimeseries.server.serialization;

import com.od.jtimeseries.source.ValueRecorder;

import java.io.DataOutputStream;
import java.io.IOException;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 18/10/11
* Time: 18:47
*/
class AuditedOutputStream {

    private DataOutputStream dataOutputStream;
    private ValueRecorder bytesWrittenValueRecorder;
    private int bytesWritten = 0;

    public AuditedOutputStream(DataOutputStream dataOutputStream, ValueRecorder bytesWrittenValueRecorder) {
        this.dataOutputStream = dataOutputStream;
        this.bytesWrittenValueRecorder = bytesWrittenValueRecorder;
    }

    public void writeInt(int v) throws IOException {
        dataOutputStream.writeInt(v);
        bytesWritten += 4;
    }

    public void write(byte[] b) throws IOException {
        dataOutputStream.write(b);
        bytesWritten += b.length;
    }

    public void writeBytes(String s) throws IOException {
        dataOutputStream.writeBytes(s);
        bytesWritten += s.length();
    }

    public void writeLong(long v) throws IOException {
        dataOutputStream.writeLong(v);
        bytesWritten += 8;
    }

    public void writeDouble(double v) throws IOException {
        dataOutputStream.writeDouble(v);
        bytesWritten += 8;
    }

    public void flush() throws IOException {
        dataOutputStream.flush();
    }

    public void close() throws IOException {
        bytesWrittenValueRecorder.newValue(bytesWritten);
        dataOutputStream.close();
    }
}
