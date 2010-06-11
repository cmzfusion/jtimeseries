package com.od.jtimeseries.agent.input;

import com.od.jtimeseries.capture.function.CaptureFunction;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.util.regex.Matcher;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04-Jun-2010
 * Time: 17:34:21
 *
 * parse a numeric value and use a valueRecorder to capture the value into one or more series using the CaptureFunctions specified
 */
public class DoubleRegexValueHandler implements RegexValueHandler {

    protected static LogMethods logMethods = LogUtils.getLogMethods(DoubleRegexValueHandler.class);

    private AtomicBoolean errorLogged = new AtomicBoolean();
    private ValueRecorder valueRecorder;
    private String destinationSeriesPath;
    private int regExCaptureGroup;

    public DoubleRegexValueHandler(TimeSeriesContext rootContext, String destinationSeriesPath, String seriesDescription, int regExCaptureGroup, CaptureFunction... captureFunctions) {
        this.destinationSeriesPath = destinationSeriesPath;
        this.regExCaptureGroup = regExCaptureGroup;

        createSeries(rootContext, destinationSeriesPath, seriesDescription, captureFunctions);
    }

    private void createSeries(TimeSeriesContext rootContext, String destinationSeriesPath, String seriesDescription, CaptureFunction... captureFunctions) {
        logMethods.logInfo("Creating series for path " + destinationSeriesPath);
        if ( rootContext.findValueSources(destinationSeriesPath).getNumberOfMatches() > 0 ) {
            throw new RuntimeException("There is already a series set up with path " + destinationSeriesPath + ". Cannot set up this value handler");
        }

        valueRecorder = rootContext.createValueRecorderSeries(destinationSeriesPath, seriesDescription, captureFunctions);
        logMethods.logInfo("Creating valueRecorder for path " + destinationSeriesPath);
    }

    public void parseInputValue(Matcher m) {
        if ( m.groupCount() < regExCaptureGroup) {
            logGroupError();
        } else {
            parseAndRecordValue(m);
        }
    }

    private void parseAndRecordValue(Matcher m) {
        String value = m.group(regExCaptureGroup);
        try {
            Double d = Double.parseDouble(value);
            valueRecorder.newValue(d);
        } catch ( NumberFormatException nfe) {
            if ( ! errorLogged.getAndSet(true)) {
                //don't want to log this for every line of input
                logMethods.logError("Cannot read values for series " + destinationSeriesPath + ". The parsed value " + value +
                        " for group " + regExCaptureGroup + " appears not to be numeric");
            }
        }
    }

    private void logGroupError() {
        //don't want to log this for every line of input
        if ( ! errorLogged.getAndSet(true) ) {
            logMethods.logError("Cannot read values for series " + destinationSeriesPath + ". This regular expression matcher does not have " + regExCaptureGroup + " capture groups");
        }
    }
}
