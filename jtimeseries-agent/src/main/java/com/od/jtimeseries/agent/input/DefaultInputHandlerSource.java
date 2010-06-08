package com.od.jtimeseries.agent.input;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 04-Jun-2010
 * Time: 17:21:28
 *
 * Just supplies InputHandler from a list
 */
public class DefaultInputHandlerSource implements InputHandlerSource {

    private List<InputHandler> inputHandlers;

    public DefaultInputHandlerSource(List<InputHandler> inputHandlers) {
        this.inputHandlers = inputHandlers;
    }

    public List<InputHandler> getInputHandlers() {
        return inputHandlers;
    }
}
