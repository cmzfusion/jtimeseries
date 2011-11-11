package com.od.jtimeseries.component.util.path;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06/11/11
 * Time: 21:21
 *
 * Rule to process a path in PathMapper
 */
public interface PathProcessingRule {

    public static final PathProcessingRule NULL_PROCESSING_RULE = new NullProcessingRule();

    /**
     * Initialize the rule, called once before the rule is first used
     */
    void initialize() throws Exception;


    /**
     * Get a processed path (may modify the supplied path, and return the modified instance)
     *
     * @param path, the path to process
     */
    PathMappingResult getPath(PathMappingResult path);



    /**
     * A rule which simply returns the path unchanged, used to terminate a chain of decorated
     * processing rules
     */
    class NullProcessingRule implements PathProcessingRule {

        public void initialize() {
        }

        public PathMappingResult getPath(PathMappingResult path) {
            return path;
        }
    }
}
