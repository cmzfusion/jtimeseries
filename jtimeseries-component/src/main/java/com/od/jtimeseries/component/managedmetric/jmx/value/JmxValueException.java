package com.od.jtimeseries.component.managedmetric.jmx.value;

/**
 *
 * Create a JmxValueException when is not possible to read a value from a jmx connection
 * because, for example, the jmx bean does not exist in the remote server,
 * or exists but does not support the require attribute.
 */
public class JmxValueException extends Exception {

    public JmxValueException(String message) {
        super(message);
    }

    public JmxValueException(String s, Throwable t) {
        super(s, t);
    }
}
