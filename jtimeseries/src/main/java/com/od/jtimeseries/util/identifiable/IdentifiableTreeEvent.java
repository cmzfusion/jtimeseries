package com.od.jtimeseries.util.identifiable;

import com.od.jtimeseries.util.identifiable.Identifiable;

import java.util.Collections;
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

    public List<Identifiable> getNodes() {
        return nodes;
    }

    public Identifiable getSource() {
        return source;
    }
}
