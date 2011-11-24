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

import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 06-Feb-2010
 * Time: 11:38:11
 *
 * A pool of keyed acquirable objects which is bounded in size
 *
 * Once a keyed acquirable is acquired from the pool, subsequent attempts to acquire the instance with that key will
 * block until the instance is returned to the pool
 *
 * Once the pool is full, an attempt to create an acquirable with a key which is not currently known will cause the oldest
 * acquirable in the pool to be removed
 */
abstract class AbstractKeyedAcquirablePool<K, E extends Acquirable> {

    private LinkedHashMap<K, E> activeAcquirables = new LinkedHashMap<K, E>();
    private int maxPoolSize = 10;

    public AbstractKeyedAcquirablePool(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    /**
     * Get or create the acquirable for this key, acquiring the exclusive lock on it
     */
    public E getAcquirable(K key) throws Exception {
        E connection = getOrCreateAcquirable(key);
        connection.acquire();
        return connection;
    }

    /**
     *  Return the acquirable, and releasing the exclusive lock on it
     */
    public void returnAcquirable(E acquirable) {
        //take back the lock when returning connection to connection cache
        if ( acquirable != null) {
            //add to the start of the list of active connections (the most recently used)
            E i = (E) acquirable;
            i.release();
        }
    }


    private synchronized E getOrCreateAcquirable(K key) throws Exception {
        E result = activeAcquirables.get(key);
        if ( result == null ) {
            while(activeAcquirables.size() >= maxPoolSize) {
                closeOldestAcquirable();
            }
            result = createAcquirable(key);
            activeAcquirables.put(key, result);
        }
        return result;
    }

    protected abstract E createAcquirable(K key) throws Exception;

    private synchronized void closeOldestAcquirable() {
        Iterator<Map.Entry<K,E>> i = activeAcquirables.entrySet().iterator();
        if ( i.hasNext() ) {
            Map.Entry<K, E> next = i.next();
            removeAcquirable(next.getKey(), next.getValue());
        } else {
            throw new RuntimeException("Could not close acquirable from pool, none to close");
        }
    }

    protected void removeAcquirable(K key, E acquirable) {
        acquirable.acquire();
        activeAcquirables.remove(key);
        doRemoveAcquirable(key, acquirable);
        acquirable.release();
    }

    protected abstract void doRemoveAcquirable(K key, E acquirable);

    public LinkedHashMap<K, E> getAcquirables() {
        return activeAcquirables;
    }


    public int getMaxPoolSize() {
        return maxPoolSize;
    }
}
