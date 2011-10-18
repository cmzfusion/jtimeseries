package com.od.jtimeseries.server.serialization;

import com.od.jtimeseries.source.ValueRecorder;

import java.io.DataInputStream;
import java.io.IOException;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 18/10/11
* Time: 18:46
*/
class AuditedInputStream {

    private DataInputStream dataInputStream;
    private ValueRecorder bytesReadValueRecorder;
    private int bytesRead;

    public AuditedInputStream(DataInputStream dataInputStream, ValueRecorder bytesReadValueRecorder) {
        this.dataInputStream = dataInputStream;
        this.bytesReadValueRecorder = bytesReadValueRecorder;
    }

    public long readLong() throws IOException {
        bytesRead += 8;
        return dataInputStream.readLong();
    }

    public double readDouble() throws IOException {
        bytesRead += 8;
        return dataInputStream.readDouble();
    }

    public int readInt() throws IOException {
        bytesRead += 4;
        return dataInputStream.readInt();
    }

    public long skip(long n) throws IOException {
        long skipped = dataInputStream.skip(n);
        bytesRead += skipped;   //treat this as a read, probably the underlying input stream does read but discards
        return skipped;
    }

    public int read(byte[] bytes, int offset, int i) throws IOException {
        int read = dataInputStream.read(bytes, offset, i);
        bytesRead += read;
        return read;
    }

    public void close() throws IOException {
        bytesReadValueRecorder.newValue(bytesRead);
        dataInputStream.close();
    }
}
