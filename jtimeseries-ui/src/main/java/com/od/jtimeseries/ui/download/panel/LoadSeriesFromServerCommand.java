package com.od.jtimeseries.ui.download.panel;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.net.udp.TimeSeriesServer;
import com.od.jtimeseries.ui.displaypattern.DisplayNameCalculator;
import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;
import swingcommand.BackgroundTask;
import swingcommand.SwingCommand;
import swingcommand.Task;
import swingcommand.TaskListenerAdapter;

import javax.swing.*;
import java.awt.*;

/**
* Created by IntelliJ IDEA.
* User: Nick
* Date: 28/12/10
* Time: 10:47
* To change this template use File | Settings | File Templates.
*/
public class LoadSeriesFromServerCommand extends SwingCommand<TimeSeriesServer, String> {

    private static LogMethods logMethods = LogUtils.getLogMethods(LoadSeriesFromServerCommand.class);
    private TimeSeriesContext destinationContext;
    private DisplayNameCalculator displayNameCalculator;

    public LoadSeriesFromServerCommand(TimeSeriesContext destinationContext, DisplayNameCalculator displayNameCalculator) {
        this(destinationContext);
        this.displayNameCalculator = displayNameCalculator;
    }

    public LoadSeriesFromServerCommand(TimeSeriesContext destinationContext) {
        this.destinationContext = destinationContext;
        addTaskListener(
            new TaskListenerAdapter<String>() {
                public void error(Task task, Throwable error) {
                    TimeSeriesServer s = (TimeSeriesServer)task.getParameters();
                    s.setConnectionFailed(true);
                }

                public void success(Task task) {
                    TimeSeriesServer s = (TimeSeriesServer)task.getParameters();
                    s.setConnectionFailed(false);
                }
            }
        );
    }

    protected Task<TimeSeriesServer, String> createTask() {
        return new BackgroundTask<TimeSeriesServer, String>() {

            protected void doInBackground() throws Exception {
                TimeSeriesServer server = getParameters();

                AddSeriesFromServerTask t = new AddSeriesFromServerTask(
                    destinationContext,
                    server,
                    displayNameCalculator
                );
                t.call();
            }

            protected void doInEventThread() {
            }
        };
    }
}