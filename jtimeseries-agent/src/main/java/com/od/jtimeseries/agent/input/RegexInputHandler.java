package com.od.jtimeseries.agent.input;

import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04-Jun-2010
 * Time: 11:41:43
 */
public class RegexInputHandler implements InputHandler {

    protected static LogMethods logMethods = LogUtils.getLogMethods(InputHandler.class);

    private Pattern pattern;
    private List<RegexValueHandler> valueHandlers = new ArrayList<RegexValueHandler>();

    public RegexInputHandler(String inputPattern) {
        pattern = Pattern.compile(inputPattern);
    }

    public void parseInput(String input) {
        Matcher m = pattern.matcher(input);
        if ( m.matches() ) {
            for ( RegexValueHandler h : valueHandlers ) {
                try {
                    h.parseInputValue(m);
                } catch ( Throwable t) {
                    logMethods.logError("RegexValueHandler " + h + " failed to process value from input " + input, t);
                }
            }
        }
    }

    public void addRegexValueHandler(RegexValueHandler r) {
        valueHandlers.add(r);
    }
}
