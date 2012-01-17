package com.od.jtimeseries.component.managedmetric.jmx.value;

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.*;

/**
 * Created by IntelliJ IDEA.
 * User: ga2adaz
 * Date: 19/12/11
 * Time: 15:25
 * To change this template use File | Settings | File Templates.
 */
public class TabularDataNumericJmxValue extends SimpleJmxValue {

    private String tabularDataKey;

    /**
     * @param objectName, String representation of a JMX ObjectName, identifying one or more mbeans from which we want to read an attribute value
     * @param attribute,  of mbeans to read
     * @param tabularDataKey, key within tabular data structure idenfitying the value to read
     */
    public TabularDataNumericJmxValue(String objectName, String attribute, String tabularDataKey) {
        super(objectName, attribute);
        this.tabularDataKey = tabularDataKey;
    }

    /**
     * Reads in the numeric value of mapped to the tabularDataKey value specified when this object was created
     * @param jmxConnection
     * @param f
     * @param beanName
     * @throws Exception
     */
    protected void readValueFromBean(MBeanServerConnection jmxConnection, AggregateFunction f, ObjectName beanName) throws Exception {
        checkBeanExists(jmxConnection, beanName);
        Object attribute = safelyGetAttribute(jmxConnection, beanName);

        if ( ! (attribute instanceof TabularData)) {
            raiseValueException("The attribute was not of type TabularData");
        }

        TabularData tabularData = (TabularData) attribute;
        Object[] key = {tabularDataKey};

        if ( ! tabularData.containsKey(key)) {
            raiseValueException("No value is available for the key Object[] {" + tabularDataKey + "}");
        }

        CompositeData compositeData = null;
        try {
            compositeData = tabularData.get(key);
        } catch ( InvalidKeyException k) {
            raiseValueException("The TabularDataKey " + tabularDataKey + " was invalid", k);
        }

        if ( compositeData == null) {
            raiseValueException("Even after checking the TabularDataKey " + tabularDataKey + " existed, we were still return a null value");
            //key asynchronously removed on remote server?, probably possible, but never seen it
        }

        Object value = getValueFromCompositeData(compositeData, "value");
        if ( value == null ) {
            raiseValueException("The value attribute of the CompositeDataItem with tabular key " + tabularDataKey + " was null");
        }

        Double v = convertToDouble(value);

        //yippee! after all this checking we actually have a value!
        f.addValue(v);
    }



    
}
