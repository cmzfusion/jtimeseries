/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.od.jtimeseries.component.managedmetric.jmx.measurement;

import com.od.jtimeseries.component.managedmetric.jmx.value.JmxValue;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunction;
import com.od.jtimeseries.timeseries.function.aggregate.AggregateFunctions;

import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 16-Feb-2010
 * Time: 17:05:38
 *
 * A value captured from JMX beans(s)
 *
 * One or more JmxValue may be read, and an AggregateFunction then applied to derive
 * a value to capture to the series at parentContextPath.id
 */
public class JmxMeasurement {

    private String parentContextPath;
    private String id;
    private String description;
    private List<JmxValue> listOfJmxValue;
    private AggregateFunction aggregateFunction;
    private double divisor = 1;
    private boolean recordNanIfFailed = true;

    /**
     * JMX Measurement which reads a single value from a jmx bean
     */
    public JmxMeasurement(String parentContextPath, String id, String description, JmxValue value) {
        this(parentContextPath, id, description, Arrays.asList(value), AggregateFunctions.LATEST());
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

    public boolean isRecordNanIfFailed() {
        return recordNanIfFailed;
    }

    public void setRecordNanIfFailed(boolean recordNanIfFailed) {
        this.recordNanIfFailed = recordNanIfFailed;
    }

    public String toString() {
        return "JmxMeasurement{" + parentContextPath + "."  + id + "}";
    }
}

