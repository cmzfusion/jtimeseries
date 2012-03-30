package com.od.jtimeseries.timeseries.function.aggregate;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 30/03/12
 * Time: 08:36
 *
 * A marker interface for Aggregate functions which support chaining - in which values from the current function
 * are used to initialize the next function instance
 *
 * In cases such as measuring change over a time period, this is helpful. Each subsequent function takes the latest
 * value from the previous as its starting value
 */
public interface ChainedFunction {
}
