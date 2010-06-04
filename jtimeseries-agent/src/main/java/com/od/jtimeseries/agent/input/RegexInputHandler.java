package com.od.jtimeseries.agent.input;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04-Jun-2010
 * Time: 11:41:43
 */
public class RegexInputHandler implements InputHandler {

    private String inputPattern;
    private List<RegexValueHandler> valueHandlers = new ArrayList<RegexValueHandler>();

    public RegexInputHandler(String inputPatter) {
        this.inputPattern = inputPattern;
    }

    public void parseInput(String input) {

    }

    public void addRegexValueHandler(RegexValueHandler r) {
        valueHandlers.add(r);
    }
}
