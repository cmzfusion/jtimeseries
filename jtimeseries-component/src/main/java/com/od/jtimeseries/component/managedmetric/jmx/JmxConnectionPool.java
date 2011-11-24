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
package com.od.jtimeseries.component.managedmetric.jmx;

import javax.management.remote.JMXServiceURL;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 06-Feb-2010
 * Time: 11:39:08
 *
 * A service for pooling jmx connections
 */
public interface JmxConnectionPool {

    /**
     * get a JmxConnectionWrapper for this serviceURL, aquiring exclusive access while this connection is held
     * The connection will not be closed, or made available to another thread, until returned to the pool
     *
     * Calling classes should always call returnConnection in a finally{} block to ensure this connection is returned,
     * or a resource leak or liveliness issue may occur.
     *
     * This method may block, if another thread has already acquired the connection, until the connection is returned
     */
    JmxConnectionWrapper getConnection(JMXServiceURL serviceUrl) throws Exception;

    /**
     * Return a connection to the pool
     */
    void returnConnection(JmxConnectionWrapper connection);

}
