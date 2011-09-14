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
package com.od.jtimeseries.util.identifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * An event generated when the tree of identifiable nodes changes
 */
public class IdentifiableTreeEvent {

    private final Identifiable rootNode;
    private final String path;
    private final List<Identifiable> nodes;

    public IdentifiableTreeEvent(Identifiable rootNode, String path, Identifiable node) {
       this(rootNode, path, Collections.singletonList(node));
    }
    
    /**
     * @param rootNode, the root node of the tree which is changing
     * @param path, path to parent node of nodes which have changed
     * @param nodes, nodes which were modified
     */
    public IdentifiableTreeEvent(Identifiable rootNode, String path, List<Identifiable> nodes) {
        this.rootNode = rootNode;
        this.path = path;
        this.nodes = new ArrayList<Identifiable>(nodes);
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
    public List<Identifiable> getNodes() {
        return nodes;
    }

    /**
     * @return the root node of the tree which is changing
     */
    public Identifiable getRootNode() {
        return rootNode;
    }
}
