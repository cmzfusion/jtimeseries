package com.od.jtimeseries.net.udp;

import com.od.jtimeseries.net.udp.message.UdpMessageFactory;
import com.od.jtimeseries.net.udp.message.properties.PropertiesMessageFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 25/03/12
 * Time: 19:01
 *
 * This is a legacy message type, use one of utf-8 or javaio
 */
public class TestPropertiesUdpPublicationAndSubscription extends AbstractTestUdpPublicationAndSubscription {

    @Override
    protected UdpMessageFactory createMessageFactory() {
        return new PropertiesMessageFactory();
    }
}
