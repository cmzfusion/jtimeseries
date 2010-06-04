package com.od.jtimeseries.agent.input;

import com.od.jtimeseries.capture.function.CaptureFunction;
import com.od.jtimeseries.util.numeric.Numeric;
import com.od.jtimeseries.util.numeric.DoubleNumeric;
import com.od.jtimeseries.context.TimeSeriesContext;

import java.util.regex.Matcher;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04-Jun-2010
 * Time: 17:34:21
 */
public class DoubleRegexValueHandler implements RegexValueHandler {

    private String destinationSeries;
    private CaptureFunction captureFunction;
    private TimeSeriesContext rootContext;
    private int captureGroup;

    public DoubleRegexValueHandler(TimeSeriesContext rootContext, String destinationSeries, CaptureFunction captureFunction, int captureGroup) {
        this.rootContext = rootContext;
        this.captureGroup = captureGroup;
        this.destinationSeries = destinationSeries;
        this.captureFunction = captureFunction;
    }

    public void parseInputValue(Matcher m) {
        String value = m.group(captureGroup);
        Double d = Double.parseDouble(value);
    }

    public String getDestinationSeries() {
        return destinationSeries;
    }

    public CaptureFunction getCaptureFunction() {
        return captureFunction;
    }
}
