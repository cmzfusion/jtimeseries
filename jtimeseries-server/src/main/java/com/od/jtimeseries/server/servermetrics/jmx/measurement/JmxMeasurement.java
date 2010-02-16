package com.od.jtimeseries.server.servermetrics.jmx.measurement;

import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunctions;
import com.od.jtimeseries.server.servermetrics.jmx.JmxValue;

import java.util.List;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 16-Feb-2010
 * Time: 17:05:38
 */
public class JmxMeasurement {

    private String parentContextPath;
    private String id;
    private String description;
    private List<JmxValue> listOfJmxValue;
    private AggregateFunction aggregateFunction;
    private double divisor = 1;

    /**
     * JMX Measurement which reads a single value from a jmx bean
     */
    public JmxMeasurement(String parentContextPath, String id, String description, JmxValue value) {
        this(parentContextPath, id, description, Arrays.asList(value), AggregateFunctions.LAST());
    }

    /**
     * JMX Measurement which reads several values from jmx bean(s) and applies an aggregate function to derive the final measurement value
     */
    public JmxMeasurement(String parentContextPath, String id, String description, List<JmxValue> listOfJmxValue, AggregateFunction aggregateFunction) {
        this.parentContextPath = parentContextPath;
        this.id = id;
        this.description = description;
        this.listOfJmxValue = listOfJmxValue;
        this.aggregateFunction = aggregateFunction;
    }

    public String getParentContextPath() {
        return parentContextPath;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public List<JmxValue> getListOfJmxValue() {
        return listOfJmxValue;
    }

    public AggregateFunction getAggregateFunction() {
        return aggregateFunction;
    }

    public double getDivisor() {
        return divisor;
    }

    public void setDivisor(double divisor) {
        this.divisor = divisor;
    }
}

