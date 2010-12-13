package com.od.jtimeseries.util.identifiable;

import com.od.jtimeseries.util.identifiable.Identifiable;

import java.util.Collections;
import java.util.List;

public class IdentifiableTreeEvent {

    private final String path;
    private final List<Identifiable> nodes;

    public IdentifiableTreeEvent(String path, Identifiable node) {
       this(path, Collections.singletonList(node));
    }
    
    /**
     * @param path to parent node containing nodes which have changed
     * @param nodes, nodes which were modified
     */
    public IdentifiableTreeEvent(String path, List<Identifiable> nodes) {
        this.path = path;
        this.nodes = nodes;
    }

    public String getPath() {
        return path;
    }

    public List<Identifiable> getNodes() {
        return nodes;
    }
}
