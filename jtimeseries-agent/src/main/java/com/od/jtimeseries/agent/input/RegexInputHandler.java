package com.od.jtimeseries.agent.input;

import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
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

    private List<Pattern> patterns = new LinkedList<Pattern>();
    private List<RegexValueHandler> valueHandlers = new ArrayList<RegexValueHandler>();

    public RegexInputHandler(String inputPattern) {
        this(Collections.singletonList(inputPattern));
    }

    public RegexInputHandler(List<String> inputPattern) {
        for ( String p : inputPattern) {
            patterns.add(Pattern.compile(p));
        }
    }

    public void parseInput(String input) {
        for ( Pattern p : patterns) {
            Matcher m = p.matcher(input);
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
    }

    public void setRegexValueHandlers(List<RegexValueHandler> r) {
        valueHandlers = new ArrayList<RegexValueHandler>(r);
    }
}
