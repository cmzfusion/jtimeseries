package com.od.jtimeseries.agent.input;

import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 11-Jun-2010
 * Time: 10:06:21
 */
public class InputProcessor {

    protected static LogMethods logMethods = LogUtils.getLogMethods(InputProcessor.class);

    private InputHandlerSource inputHandlerSource;

    public InputProcessor(InputHandlerSource inputHandlerSource) {
        this.inputHandlerSource = inputHandlerSource;
    }

    public void start() {
        logMethods.logInfo("Starting InputProcessor");
        new Thread(new Runnable() {
            public void run() {
                Thread.currentThread().setName("InputProcessor");
                logMethods.logInfo("InputProcessor started");
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                try {
                    while (true) {
                        processLine(in);
                    }
                } catch (Throwable t) {
                    logMethods.logError("Failed while trying to read from standard in", t);
                }
            }
        }).start();
    }

    private void processLine(BufferedReader in) throws IOException {
        String s = in.readLine();
        if ( s != null && s.length() > 0) {
            for (InputHandler h : inputHandlerSource.getInputHandlers()) {
                try {
                    h.parseInput(s);
                } catch (Throwable t) {
                    logMethods.logError("Error processing input " + s + " using handler " + h, t);
                }
            }
        }
    }

}
