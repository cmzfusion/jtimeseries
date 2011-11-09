package com.od.jtimeseries.server.util.path;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 06/11/11
 * Time: 23:37
 */
public class TestPathMapper extends TestCase{

    private PathMapper mapper;

    public void setUp() throws Exception {

        PathMigration pathTranslation = new PathMigration();
        HashMap<String,String> patternMap = new HashMap<String,String>();
        patternMap.put("root\\.child1", "root\\.child2");
        pathTranslation.setPatternMap(patternMap);

        PathRestriction pathRestriction = new PathRestriction(pathTranslation);
        List<String> premittedPaths = new LinkedList<String>();
        premittedPaths.add("root\\.child2");
        pathRestriction.setPermittedPaths(premittedPaths);

        mapper = new PathMapper(pathRestriction);
        mapper.initialize();
    }

     public void testMapperForPermittedPath() {
        PathMappingResult result = mapper.getPathMapping("root.child2.grandchild1");
        PathMappingResult expected = getExpectedResult("root.child2.grandchild1", PathMappingResult.ResultType.PERMIT);
        assertEquals(expected, result);
    }

    public void testMapperForMigratedPath() {
        PathMappingResult result = mapper.getPathMapping("root.child1.grandchild2");
        PathMappingResult expected = getExpectedResult("root.child2.grandchild2", PathMappingResult.ResultType.MIGRATE);
        assertEquals(expected, result);
    }

    public void testRestriction() {
        PathMappingResult result = mapper.getPathMapping("root.child3.grandchild2");
        PathMappingResult expected = getExpectedResult("root.child3.grandchild2", PathMappingResult.ResultType.DENY);
        assertEquals(expected, result);
    }

    private PathMappingResult getExpectedResult(String p, PathMappingResult.ResultType t) {
        return new PathMappingResult(
                    t,
                    p
            );
    }
}
