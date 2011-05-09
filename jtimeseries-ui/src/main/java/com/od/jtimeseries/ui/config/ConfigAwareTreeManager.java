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

    private ConfigAware rootNode;

    public ConfigAwareTreeManager(ConfigAware rootNode) {
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

    private void clear(ConfigAware node) {
        for ( ConfigAware c : node.getConfigAwareChildren()) {
            clear(c);
        }
        node.clearConfig();
    }

    private void restore(TimeSeriousConfig config, ConfigAware node) {
        node.restoreConfig(config);
        for ( ConfigAware c : node.getConfigAwareChildren()) {
            restore(config, c);
        }
    }

    private void save(TimeSeriousConfig config, ConfigAware node) {
        node.prepareConfigForSave(config);
        for ( ConfigAware c : node.getConfigAwareChildren()) {
            save(config, c);
        }
    }


}
