/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
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
