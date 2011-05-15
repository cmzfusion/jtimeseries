package com.od.jtimeseries.ui.util;

import swingcommand.SwingCommand;

import java.lang.ref.WeakReference;

/**
* Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 09-Dec-2010
* Time: 16:24:50
*
* Hold a weak reference to the command, so that we don't hold on to the
* http series and prevent it being collected
*/
public class ExecuteWeakReferencedCommandTask implements Runnable {

    private WeakReference<SwingCommand> command;

    public ExecuteWeakReferencedCommandTask(SwingCommand c) {
        command = new WeakReference<SwingCommand>(c);
    }

    public void run() {
        SwingCommand c = command.get();
        if ( c != null ) {
            c.execute();
        }
    }
}
