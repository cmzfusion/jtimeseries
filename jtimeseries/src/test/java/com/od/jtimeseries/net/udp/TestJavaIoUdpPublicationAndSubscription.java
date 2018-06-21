package com.od.jtimeseries.net.udp;

import com.od.jtimeseries.net.udp.message.UdpMessageFactory;
import com.od.jtimeseries.net.udp.message.javaio.JavaIOMessageFactory;
import org.junit.Before;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 25/03/12
 * Time: 18:59
 */
public class TestJavaIoUdpPublicationAndSubscription extends AbstractTestUdpPublicationAndSubscription {
    
    @Override
    protected UdpMessageFactory createMessageFactory() {
        return new JavaIOMessageFactory();
    }
}
