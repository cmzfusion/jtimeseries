package com.od.jtimeseries.server.util.path;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06/11/11
 * Time: 21:26
 */
public abstract class AbstractPathProcessingRule implements PathProcessingRule {

    private PathProcessingRule decoratedRule = new NullProcessingRule();

    public AbstractPathProcessingRule(PathProcessingRule decoratedRule) {
        this.decoratedRule = decoratedRule;
    }

    public AbstractPathProcessingRule() {
    }

    public void initialize() throws Exception {
        decoratedRule.initialize();
        doInitialize();
    }

    protected abstract void doInitialize();

    public StringBuilder getPath(StringBuilder path) {
        StringBuilder s = decoratedRule.getPath(path);
        if ( s != null ) {
            s =  doGetPath(s);
        }
        return s;
    }

    protected abstract StringBuilder doGetPath(StringBuilder s);

    /**
     * A rule which simply returns the path unchanged, used to terminate a chain of decorated
     * processing rules
     */
    private static class NullProcessingRule implements PathProcessingRule {

        public void initialize() {
        }

        public StringBuilder getPath(StringBuilder path) {
            return path;
        }
    }
}
