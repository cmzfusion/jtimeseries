package com.od.jtimeseries.ui.timeserious.config;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 05/01/11
 * Time: 22:22
 * To change this template use File | Settings | File Templates.
 */
public class ConfigAwareTree {

    private ConfigAware rootNode;

    public ConfigAwareTree(ConfigAware rootNode) {
        this.rootNode = rootNode;
    }


    public void prepareConfigForSave(TimeSeriousConfig config) {
        List<ConfigAware> flattenedTree = getFlattenedTree();
        for ( ConfigAware c : flattenedTree) {
            c.prepareConfigForSave(config);
        }
    }


    public void restoreConfig(TimeSeriousConfig config) {
        List<ConfigAware> flattenedTree = getFlattenedTree();
        for ( ConfigAware c : flattenedTree) {
            c.restoreConfig(config);
        }
    }

    private List<ConfigAware> getFlattenedTree() {
        List<ConfigAware> flattenedTree = new LinkedList<ConfigAware>();
        addFromNode(rootNode, flattenedTree);
        return flattenedTree;
    }


    private void addFromNode(ConfigAware node, List<ConfigAware> flattenedTree) {
        flattenedTree.add(node);
        for (ConfigAware c : node.getConfigAwareChildren()) {
            addFromNode(c, flattenedTree);
        }
    }

}
