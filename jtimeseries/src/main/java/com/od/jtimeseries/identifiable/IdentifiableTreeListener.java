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
 * Date: 13-Dec-2010
 * Time: 20:38:44
 *
 * This listener will receive events when an identifiable/context tree changes
 *
 * Since event firing is performed asynchronously, the context tree may have undergone subsequent changes by the time
 * tree events are received. When you receive events, the affected nodes or their parents may have changed (had more
 * descendants added, for example) since the event was fired.
 *
 * When you examine the nodes referenced in the event you are looking at the current state of those nodes, rather
 * than their state at the point the event was fired. (An alternative design would involve cloning the affected nodes
 * at the time an event is fired and passing the cloned instances - but that would impose severe performance penalties)
 * It is better to make listeners responsible for syncing with the current state of the source nodes where required.)
 *
 * In general, any traversal of the context tree, or subtrees within it, including the nodes which form part of a
 * IdentifiableTreeEvent, should be done while holding the tree lock of the nodes in question, to ensure that
 * the structure does not change while traversal is taking place (Identifiable.getTreeLock())
 */
public interface IdentifiableTreeListener {

    /**
     * called when the node to which the listener was added changes
     */
    public void nodeChanged(Identifiable node, Object changeDescription);
    
    /**
     * descendant nodes changed in a way which did not affect tree structure (e.g. node description changed). The change is limited to the listed nodes in the IdentifiableTreeEvent, descendant nodes from the listed nodes are not assumed to be changed.
     */
    public void descendantChanged(IdentifiableTreeEvent contextTreeEvent);

    /**
     * descendant nodes were added.
     */
    public void descendantAdded(IdentifiableTreeEvent contextTreeEvent);

    /**
     * descendant nodes were removed
     */
    public void descendantRemoved(IdentifiableTreeEvent contextTreeEvent);

}
