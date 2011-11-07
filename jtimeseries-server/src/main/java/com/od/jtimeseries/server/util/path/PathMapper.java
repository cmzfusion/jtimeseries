package com.od.jtimeseries.server.util.path;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06/11/11
 * Time: 21:00
 */
public class PathMapper {

    private ConcurrentHashMap<String,PathMappingResult> pathMappings = new ConcurrentHashMap<String, PathMappingResult>();
    private PathProcessingRule rule;

    /**
     * Create a PathMapper with a rule (which may be last rule in a chain of decorated rules)
     */
    public PathMapper(PathProcessingRule rule) {
        this.rule = rule;
    }

    public synchronized PathMappingResult getPathMapping(String path) {
        PathMappingResult result = pathMappings.get(path);
        if ( result == null) {
            result = rule.getPath(new PathMappingResult(path));
            pathMappings.put(path, result);
        }
        return result;
    }


    public synchronized void initialize() throws Exception {
        rule.initialize();
    }
}
