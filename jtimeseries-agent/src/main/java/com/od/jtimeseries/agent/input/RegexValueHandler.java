package com.od.jtimeseries.agent.input;

import java.util.regex.Matcher;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04-Jun-2010
 * Time: 17:45:25
 */
public interface RegexValueHandler {

    void parseInputValue(Matcher m);
}
