package com.od.jtimeseries.component.managedmetric.jmx.measurement;

import com.od.jtimeseries.component.managedmetric.jmx.value.JmxValue;
import com.od.jtimeseries.component.managedmetric.jmx.value.TabularDataNumericJmxValue;

/**
 * Created by IntelliJ IDEA.
 * User: ga2adaz
 * Date: 19/12/11
 * Time: 15:01
 * To change this template use File | Settings | File Templates.
 */
public class JmxNumericProperty extends JmxMeasurement {

    public JmxNumericProperty(String parentContextPath, String id, String description, JmxValue value) {
        super(parentContextPath, id, description, value);
    }

    static JmxNumericProperty createNumericSystemProperty(String parentContextPath, String id, String description, String attributeKey) {
        JmxValue jmxValue = new TabularDataNumericJmxValue("java.lang:type=Runtime", "SystemProperties",attributeKey);
        return new JmxNumericProperty(parentContextPath, id, description, jmxValue);
    }
    
    static JmxNumericProperty createNumericProperty(String parentContextPath, String id, String description, String objectName, String attribute, String attributeKey) {
        JmxValue jmxValue = new TabularDataNumericJmxValue(objectName, attribute,attributeKey);
        return new JmxNumericProperty(parentContextPath, id, description, jmxValue);
    }
    
    

}
