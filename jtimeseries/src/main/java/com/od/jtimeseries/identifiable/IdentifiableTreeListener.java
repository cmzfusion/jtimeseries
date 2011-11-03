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
 * tree events are received. This means that by the time you receive events, the affected nodes, their descendants or
 * their parents may have undergone further changes since the event was fired (had descendant nodes added or removed,
 * for example).
 *
 * When you examine the nodes referenced in the event you are looking at the current state of those nodes, rather
 * than their state at the point the event was fired. However, for add and remove events, the descendants of the nodes
 * removed or added are captured into snapshot collections at the point the event was fired, and these are accessible
 * via event.getNodesWithDescendants - this makes the implementation for certain IdentifiableTreeListener classes much
 * simpler
 *
 * n.b in general, any traversal of the identifiable tree, or subtrees within it, including the added and removed nodes
 * which form part of a IdentifiableTreeEvent, should be done while holding the tree lock of the nodes in question,
 * to ensure that the structure does not change while traversal is taking place (Identifiable.getTreeLock())
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
