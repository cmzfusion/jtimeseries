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
package com.od.jtimeseries.identifiable;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 08/03/11
 * Time: 07:04
 */
public class IdentifiableTreeListenerAdapter implements IdentifiableTreeListener {

    public void nodeChanged(Identifiable node, Object changeDescription) {
    }

    public void descendantChanged(IdentifiableTreeEvent contextTreeEvent) {
    }

    public void descendantAdded(IdentifiableTreeEvent contextTreeEvent) {
    }

    public void descendantRemoved(IdentifiableTreeEvent contextTreeEvent) {
    }
}
