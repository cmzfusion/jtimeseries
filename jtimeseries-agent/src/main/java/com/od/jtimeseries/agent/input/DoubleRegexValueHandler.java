package com.od.jtimeseries.agent.input;

import com.od.jtimeseries.capture.function.CaptureFunction;
import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.source.ValueRecorder;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04-Jun-2010
 * Time: 17:34:21
 *
 * parse a numeric value and use a valueRecorder to capture the value into one or more series using the CaptureFunctions specified
 * The destinationSeriesPath specified may contain references to capture groups from the pattern, which will be expanded
 * to determine
 */
public class DoubleRegexValueHandler implements RegexValueHandler {

    protected static LogMethods logMethods = LogUtils.getLogMethods(DoubleRegexValueHandler.class);

    private AtomicBoolean errorLogged = new AtomicBoolean();
    private Map<String, ValueRecorder> expandedSeriesToValueRecorderMap = new HashMap<String, ValueRecorder>();
    private TimeSeriesContext rootContext;
    private String destinationSeriesPath;
    private String seriesDescription;
    private int regExCaptureGroup;
    private CaptureFunction[] captureFunctions;

    public DoubleRegexValueHandler(TimeSeriesContext rootContext, String destinationSeriesPath, String seriesDescription, int regExCaptureGroup, CaptureFunction... captureFunctions) {
        this.rootContext = rootContext;
        this.destinationSeriesPath = destinationSeriesPath;
        this.seriesDescription = seriesDescription;
        this.regExCaptureGroup = regExCaptureGroup;
        this.captureFunctions = captureFunctions;

        logMethods.logInfo("Creating DoubleRegexValueHandler for path " + destinationSeriesPath);        
    }

    public void parseInputValue(Matcher m) {
        logMethods.logDebug("initial group count: " + m.groupCount());
        if ( m.groupCount() < regExCaptureGroup) {
            logGroupError();
        } else {
            parseAndRecordValue(m);
        }
    }

    private void parseAndRecordValue(Matcher m) {
        String source = m.group(0);

        //target series path is the destinationSeriesPath with any capturing groups replaced
        String expandedSeriesPath = m.replaceAll(destinationSeriesPath);
        m.reset();
        m.find();

        String expandedDescription = m.replaceAll(seriesDescription);
        m.reset();
        m.find();

        ValueRecorder v = getOrCreateSeries(expandedSeriesPath, expandedDescription);
        String value = m.group(regExCaptureGroup);
        logMethods.logDebug("DoubleRegexValueHandler processing value [" + value + "] for series " + expandedSeriesPath);
        try {
            Double d = Double.parseDouble(value);
            v.newValue(d);
        } catch ( NumberFormatException nfe) {
            if ( ! errorLogged.getAndSet(true)) {
                //don't want to log this for every line of input
                logMethods.logError("Cannot read values for series " + destinationSeriesPath + ". The parsed value " + value +
                        " for group " + regExCaptureGroup + " appears not to be numeric");
            }
        }
    }

    private ValueRecorder getOrCreateSeries(String expandedSeriesPath, String expandedDescription) {
        ValueRecorder result = expandedSeriesToValueRecorderMap.get(expandedSeriesPath);
        if ( result == null ) {
            logMethods.logInfo("Creating series for path " + expandedSeriesPath);
            if ( rootContext.findValueSources(expandedSeriesPath).getNumberOfMatches() > 0 ) {
                throw new RuntimeException("There is already a series set up with path " + expandedSeriesPath + ". Cannot set up this value handler");
            }

            result = rootContext.createValueRecorderSeries(expandedSeriesPath, expandedDescription, captureFunctions);
            logMethods.logInfo("Creating valueRecorder for path " + expandedSeriesPath);

            expandedSeriesToValueRecorderMap.put(expandedSeriesPath, result);
        }
        return result;
    }

    private void logGroupError() {
        //don't want to log this for every line of input
        if ( ! errorLogged.getAndSet(true) ) {
            logMethods.logError("Cannot read values for series " + destinationSeriesPath + ". This regular expression matcher does not have " + regExCaptureGroup + " capture groups");
        }
    }
}
