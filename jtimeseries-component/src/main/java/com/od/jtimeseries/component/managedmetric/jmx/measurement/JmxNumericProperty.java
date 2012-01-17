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

    private JmxNumericProperty(String parentContextPath, String id, String description, JmxValue value) {
        super(parentContextPath, id, description, value);
    }

    /**
     * Creates a JmxNumericProperty which gets the Java Runtime System Property specified by attributeKey
     * @param parentContextPath
     * @param id
     * @param description
     * @param attributeKey
     * @return
     */
    static JmxNumericProperty createNumericSystemProperty(String parentContextPath, String id, String description, String attributeKey) {
        JmxValue jmxValue = new TabularDataNumericJmxValue("java.lang:type=Runtime", "SystemProperties",attributeKey);
        return new JmxNumericProperty(parentContextPath, id, description, jmxValue);
    }

    /**
         * Creates a JmxNumericProperty which gets the numeric value specified by the objectName, attribute set and attributeKey
         * e.g. objectName = java.lang:type=Runtime, attribute = SystemProperties, attributeKey = build.number
         * would get the build.number value contained in the SystemProperties Attribute stored in Runtime Container
         * @param parentContextPath
         * @param id
         * @param description
         * @param attributeKey
         * @return
         */
    static JmxNumericProperty createNumericProperty(String parentContextPath, String id, String description, String objectName, String attribute, String attributeKey) {
        JmxValue jmxValue = new TabularDataNumericJmxValue(objectName, attribute,attributeKey);
        return new JmxNumericProperty(parentContextPath, id, description, jmxValue);
    }
    
    

}
