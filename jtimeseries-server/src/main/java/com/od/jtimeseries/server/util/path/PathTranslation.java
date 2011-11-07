package com.od.jtimeseries.server.util.path;

import com.od.jtimeseries.util.logging.LogMethods;
import com.od.jtimeseries.util.logging.LogUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06/11/11
 * Time: 21:21
 *
 * Translate paths using patterns and replacements
 * Paths which do not match a pattern are passed through unchanged
 */
public class PathTranslation extends AbstractPathProcessingRule {

    private static final LogMethods logMethods = LogUtils.getLogMethods(PathTranslation.class);

    private volatile Map<String, String> patternMap = new HashMap<String, String>();
    private Map<Pattern, String> compiledPatternToReplacement = new HashMap<Pattern, String>();

    public PathTranslation() {}

    public PathTranslation(PathProcessingRule decoratedRule) {
        super(decoratedRule);
    }

    protected void doInitialize() {
        for ( Map.Entry<String,String> s : patternMap.entrySet()) {
            try {
                Pattern p = Pattern.compile(s.getKey());
                compiledPatternToReplacement.put(p, s.getValue());
            } catch (Throwable t) {
                logMethods.logError("Failed to compile pattern " + s + " will not translate any paths for this pattern");
            }
        }
    }

    protected PathMappingResult doGetPath(PathMappingResult s) {
        for (Map.Entry<Pattern, String> e : compiledPatternToReplacement.entrySet()) {
            Matcher matcher = e.getKey().matcher(s.getNewPath());
            if ( matcher.find()) {
                s.setNewPath(matcher.replaceAll(e.getValue()));
                break;
            }
        }
        return s;
    }

    /**
     * A Map of regular expressions and their replacement patterns to translate paths.
     * Any paths not matching one of these patterns will be passed through unchanged
     */
    public void setPatternMap(Map<String, String> patternMap) {
        this.patternMap = patternMap;
    }
}
