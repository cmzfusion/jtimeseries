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

import java.util.*;

/**
 * An event generated when the tree of identifiable nodes changes
 */
public class IdentifiableTreeEvent {

    private final Identifiable rootNode;
    private final String path;
    private final Map<Identifiable, Collection<Identifiable>> nodes;

    public IdentifiableTreeEvent(Identifiable rootNode, String path, Identifiable node) {
       this(rootNode, path, createSingletonMap(node, Collections.<Identifiable>emptyList()));
    }

    public IdentifiableTreeEvent(Identifiable rootNode, String path, Identifiable node, Collection<Identifiable> children) {
       this(rootNode, path, createSingletonMap(node, children));
    }

    /**
     * @param rootNode, the root node of the tree which is changing
     * @param path, path to parent node of nodes which have changed
     * @param nodes, nodes which were modified
     */
    public IdentifiableTreeEvent(Identifiable rootNode, String path, Map<Identifiable, Collection<Identifiable>> nodes) {
        this.rootNode = rootNode;
        this.path = path;
        this.nodes = nodes;
    }

    /**
     * @return the path to the parent node of the nodes which have changed
     */
    public String getPath() {
        return path;
    }

    /**
     * @return a list of the nodes affected by the event, you should not modify this list
     */
    public Collection<Identifiable> getNodes() {
        return nodes.keySet();
    }

     /**
     * @return a list of the nodes affected by the event, you should not modify this list
     */
    public Map<Identifiable, Collection<Identifiable>> getNodesWithChildren() {
        return nodes;
    }

    /**
     * @return the root node of the tree which is changing
     */
    public Identifiable getRootNode() {
        return rootNode;
    }

    private static Map<Identifiable, Collection<Identifiable>> createSingletonMap(Identifiable node, Collection<Identifiable> c) {
        Map m = new HashMap<Identifiable, Collection<Identifiable>>();
        m.put(node, c);
        return m;
    }
}
