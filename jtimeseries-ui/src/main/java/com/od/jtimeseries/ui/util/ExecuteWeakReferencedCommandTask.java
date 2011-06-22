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
