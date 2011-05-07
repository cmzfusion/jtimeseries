package com.od.jtimeseries.ui.config;

/**
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: 05/01/11
 * Time: 22:22
 *
 * Flatten a tree structure of ConfigAware classes, and iterate the nodes
 * when saving or loading config
 */
public class ConfigAwareTreeManager {

    private CollectionClearingConfigAware rootNode;

    public ConfigAwareTreeManager(CollectionClearingConfigAware rootNode) {
        this.rootNode = rootNode;
    }

    public void prepareConfigForSave(TimeSeriousConfig config) {
        save(config, rootNode);
    }

    public void restoreConfig(TimeSeriousConfig config) {
        restore(config, rootNode);
    }

    public void clearConfig() {
        clear(rootNode);
    }

    private void clear(CollectionClearingConfigAware node) {
        for ( CollectionClearingConfigAware c : node.getConfigAwareChildren()) {
            clear(c);
        }
        node.clearConfig();
    }

    private void restore(TimeSeriousConfig config, CollectionClearingConfigAware node) {
        node.restoreConfig(config);
        for ( CollectionClearingConfigAware c : node.getConfigAwareChildren()) {
            restore(config, c);
        }
    }

    private void save(TimeSeriousConfig config, CollectionClearingConfigAware node) {
        node.prepareConfigForSave(config);
        for ( CollectionClearingConfigAware c : node.getConfigAwareChildren()) {
            save(config, c);
        }
    }


}
