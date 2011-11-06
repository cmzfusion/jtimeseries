package com.od.jtimeseries.server.util.path;

import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06/11/11
 * Time: 21:22
 *
 * Disallow all paths, apart from those which match a regular expressions in the list
 */
public class PathRestriction extends AbstractPathProcessingRule {

    private static final LogMethods logMethods = LogUtils.getLogMethods(PathRestriction.class);

    private volatile List<String> permittedPaths = new LinkedList<String>();
    private List<Pattern> patterns = new LinkedList<Pattern>();

    public PathRestriction(PathProcessingRule decoratedRule) {
        super(decoratedRule);
    }

    protected void doInitialize() {
        for ( String s : permittedPaths) {
            try {
                Pattern p = Pattern.compile(s);
                patterns.add(p);
            } catch (Throwable t) {
                logMethods.logError("Failed to compile pattern " + s + " will permit any paths for this pattern");
            }
        }
    }

    protected StringBuilder doGetPath(StringBuilder s) {
        boolean result = false;
        for (Pattern p : patterns) {
            if ( p.matcher(s).matches()) {
                result = true;
                break;
            }
        }
        return result ? s : null;
    }

    /**
     * A list of paths to permit, which may be regular expressions.
     * Any paths not matching one of these patterns will be disallowed
     */
    public void setPermittedPaths(List<String> permittedPaths) {
        this.permittedPaths = permittedPaths;
    }
}
