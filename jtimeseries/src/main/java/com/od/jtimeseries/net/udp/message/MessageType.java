package com.od.jtimeseries.net.udp.message;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 17/03/12
 * Time: 12:58
 */
public enum MessageType {

    TS_VALUE('V'),
    SERVER_ANNOUNCE('S'),
    CLIENT_ANNOUNCE('C'),
    SERIES_DESCRIPTION('D');

    private char acronym;

    MessageType(char acronym) {
        this.acronym = acronym;
    }

    /**
     * For messages where we want to be as efficient with data size as possible
     * @return an acronym which can passed in a message header to uniquely identify the message type
     */
    public char getAcronym() {
        return acronym;
    }
}
