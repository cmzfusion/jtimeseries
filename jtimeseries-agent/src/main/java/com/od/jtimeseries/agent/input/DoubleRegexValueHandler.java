package com.od.jtimeseries.agent.input;

import com.od.jtimeseries.capture.function.CaptureFunction;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.source.ValueRecorder;

import java.util.regex.Matcher;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04-Jun-2010
 * Time: 17:34:21
 */
public class DoubleRegexValueHandler implements RegexValueHandler {

    private String destinationSeriesPath;
    private CaptureFunction captureFunction;
    private TimeSeriesContext rootContext;
    private String seriesDescription;
    private int captureGroup;

    public DoubleRegexValueHandler(TimeSeriesContext rootContext, String destinationSeriesPath, String seriesDescription, CaptureFunction captureFunction, int captureGroup) {
        this.rootContext = rootContext;
        this.seriesDescription = seriesDescription;
        this.captureGroup = captureGroup;
        this.destinationSeriesPath = destinationSeriesPath;
        this.captureFunction = captureFunction;
    }

    public void parseInputValue(Matcher m) {
        String value = m.group(captureGroup);
        Double d = Double.parseDouble(value);
    }

    public String getDestinationSeries() {
        return "";
    }

    public CaptureFunction getCaptureFunction() {
        return captureFunction;
    }
}
