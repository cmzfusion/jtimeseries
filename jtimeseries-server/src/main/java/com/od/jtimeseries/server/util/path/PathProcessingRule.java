package com.od.jtimeseries.server.util.path;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06/11/11
 * Time: 21:21
 *
 * Rule to process a path in PathMapper
 */
public interface PathProcessingRule {

    /**
     * Initialize the rule, called once before the rule is first used
     */
    void initialize() throws Exception;


    /**
     * Get a processed path (may modify the supplied path, and return the modified instance)
     *
     * @param path, the path to process
     * @return the processed path, or null if the processed path is invalid
     */
    StringBuilder getPath(StringBuilder path);

}
