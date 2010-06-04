/**
 * Copyright (C) 2009 (nick @ objectdefinitions.com)
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
package com.od.jtimeseries.component.jmx;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXServiceURL;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 06-Feb-2010
 * Time: 13:20:26
 * To change this template use File | Settings | File Templates.
 */
public interface JmxExecutorTask {

    /**
     * @return the JMXServiceURL used to create the connection required by this task
     */
    JMXServiceURL getServiceURL();

    /**
     * Carry out a task using a jmxConnection
     *
     * The jmxConnection instance supplied should not be reused outside the context of the task execution
     * (do not keep a reference to it)
     */
    void executeTask(MBeanServerConnection jmxConnection) throws Exception;

}
