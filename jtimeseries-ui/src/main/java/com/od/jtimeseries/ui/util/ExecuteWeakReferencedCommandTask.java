package com.od.jtimeseries.ui.util;

import swingcommand.SwingCommand;

import java.lang.ref.WeakReference;
import java.util.concurrent.ScheduledFuture;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 09-Dec-2010
* Time: 16:24:50
*/
public class ExecuteWeakReferencedCommandTask implements Runnable {

    private WeakReference<SwingCommand> command;
    private volatile ScheduledFuture future;

    //set the ScheduledFuture to cancel if the SwingCommand has been gc'd
    public void setFutureToCancel(ScheduledFuture task) {
        this.future = task;
    }

    public ExecuteWeakReferencedCommandTask(SwingCommand c) {
        command = new WeakReference<SwingCommand>(c);
    }

    public void run() {
        SwingCommand c = command.get();
        if ( c != null ) {
            c.execute();
        } else {
            if ( future != null ) {
                future.cancel(false);
            }
        }
    }
}
