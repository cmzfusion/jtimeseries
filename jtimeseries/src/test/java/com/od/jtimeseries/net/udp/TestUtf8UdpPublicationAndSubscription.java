package com.od.jtimeseries.net.udp;

import com.od.jtimeseries.net.udp.message.UdpMessageFactory;
import com.od.jtimeseries.net.udp.message.utf8.Utf8MessageFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 25/03/12
 * Time: 19:01
 */
public class TestUtf8UdpPublicationAndSubscription extends AbstractTestUdpPublicationAndSubscription {

    protected UdpMessageFactory createMessageFactory() {
        return new Utf8MessageFactory();
    }
}
