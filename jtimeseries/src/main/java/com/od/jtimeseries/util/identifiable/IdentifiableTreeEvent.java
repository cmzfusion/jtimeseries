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

import com.od.jtimeseries.util.identifiable.Identifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public class IdentifiableTreeEvent {

    private Identifiable source;
    private final String path;
    private final List<Identifiable> nodes;

    public IdentifiableTreeEvent(Identifiable source, String path, Identifiable node) {
       this(source, path, Collections.singletonList(node));
    }
    
    /**
     * @param source, source of event
     * @param path, relative path from source to the parent node containing nodes which have changed
     * @param nodes, nodes which were modified
     */
    public IdentifiableTreeEvent(Identifiable source, String path, List<Identifiable> nodes) {
        this.source = source;
        this.path = path;
        this.nodes = nodes;
    }

    public String getPath() {
        return path;
    }

    /**
     * @return a list of the nodes affected by the event
     */
    public List<Identifiable> getNodes() {
        return nodes;
    }

    /**
     * @return a unique list of the nodes affected by the event and all their descendants
     */
    public List<Identifiable> getNodesWithAllDescendants() {
        LinkedHashSet<Identifiable> all = new LinkedHashSet<Identifiable>();
        for ( Identifiable i : nodes) {
            all.addAll(i.findAll(Identifiable.class).getAllMatches());
        }
        return new ArrayList<Identifiable>(all);
    }

    public Identifiable getSource() {
        return source;
    }
}
