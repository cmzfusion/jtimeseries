package com.od.jtimeseries.server.serialization;

import com.od.jtimeseries.source.ValueRecorder;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 18/10/11
* Time: 18:44
*/
class AuditedRandomAccessWriter {

    private RandomAccessFile rw;
    private ValueRecorder bytesWrittenValueRecorder;
    private ValueRecorder bytesReadValueRecorder;
    private int bytesRead;
    private int bytesWritten;

    public AuditedRandomAccessWriter(RandomAccessFile rw, ValueRecorder bytesWrittenValueRecorder, ValueRecorder bytesReadValueRecorder) {
        this.rw = rw;
        this.bytesWrittenValueRecorder = bytesWrittenValueRecorder;
        this.bytesReadValueRecorder = bytesReadValueRecorder;
    }

    public void seek(int bytes) throws IOException {
        rw.seek(bytes);
    }

    public int readInt() throws IOException {
        bytesRead += 4;
        return rw.readInt();
    }

    public void writeInt(int i) throws IOException {
        bytesWritten += 4;
        rw.writeInt(i);
    }

    public void writeLong(long l) throws IOException {
        bytesWritten += 8;
        rw.writeLong(l);
    }

    public void writeDouble(double v) throws IOException {
        bytesWritten += 8;
        rw.writeDouble(v);
    }

    public void close() throws IOException {
        bytesWrittenValueRecorder.newValue(bytesWritten);
        bytesReadValueRecorder.newValue(bytesRead);
        rw.close();
    }
}
