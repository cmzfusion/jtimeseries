package com.od.jtimeseries.server.serialization;

import com.od.jtimeseries.source.Counter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 20/10/11
 * Time: 13:49
 *
 * A wrapper around FileChannel which records number of bytes written and read from the channel
 */
public class AuditedFileChannel {

    private FileChannel channel;
    private Counter bytesWrittenValueRecorder;
    private Counter bytesReadValueRecorder;
    private int bytesRead;
    private int bytesWritten;

    public AuditedFileChannel(FileChannel channel, Counter bytesWrittenValueRecorder, Counter bytesReadValueRecorder) {
        this.channel = channel;
        this.bytesWrittenValueRecorder = bytesWrittenValueRecorder;
        this.bytesReadValueRecorder = bytesReadValueRecorder;
    }

    /**
     * Write the entire ByteBuffer to channel and set ByteBuffer position back to zero
     */
    public void writeCompletely(ByteBuffer b) throws IOException {
        b.position(0);
        int nwritten;
        do {
            nwritten = channel.write(b);
        } while (nwritten != -1 && b.hasRemaining());

        bytesWritten += b.position();
        if ( b.position() != b.limit()) { //should never happen?
            throw new IOException("Failed to write whole ByteBuffer to FileChannel");
        }
        b.position(0);
    }

    /**
     * Populate the entire ByteBuffer from channel, and set ByteBuffer position back to zero
     */
    public void readCompletely(ByteBuffer b) throws IOException {
        b.position(0);
        int nread;
        do {
            nread = channel.read(b);
        } while (nread != -1 && b.hasRemaining());

        bytesRead += b.position();
        if ( b.position() != b.limit()) {
            throw new IOException("Failed to read whole ByteBuffer from FileChannel");
        }
        b.position(0);
    }

    public void close() throws IOException {
        bytesWrittenValueRecorder.incrementCount(bytesWritten);
        bytesReadValueRecorder.incrementCount(bytesRead);
        channel.close();
    }

    public long getPosition() throws IOException {
        return channel.position();
    }

    public long size() throws IOException {
        return channel.size();
    }

    public long position() throws IOException {
        return channel.position();
    }

    public void position(int headerLength) throws IOException {
        channel.position(headerLength);
    }
}
