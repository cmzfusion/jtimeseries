package com.od.jtimeseries.server.servermetrics.jmx;

/**
 * Created by IntelliJ IDEA.
* User: nick
* Date: 06-Dec-2009
* Time: 12:30:35
* To change this template use File | Settings | File Templates.
*/
public class JmxValue {

    private final String objectName;
    private final String attribute;
    private String compositeDataKey;

    public JmxValue(String objectName, String attribute, String compositeDataKey) {
        this.objectName = objectName;
        this.attribute = attribute;
        this.compositeDataKey = compositeDataKey;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getAttribute() {
        return attribute;
    }

    public String getCompositeDataKey() {
        return compositeDataKey;
    }
}
