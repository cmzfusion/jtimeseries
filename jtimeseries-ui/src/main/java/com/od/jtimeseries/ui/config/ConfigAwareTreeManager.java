/**
 * Copyright (C) 2011 (nick @ objectdefinitions.com)
 *
 * This file is part of JTimeseries.
 *
 * JTimeseries is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimeseries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JTimeseries.  If not, see <http://www.gnu.org/licenses/>.
 */
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

    public void clearAndRestoreConfig(TimeSeriousConfig config) {
        clear(rootNode);
        restoreConfig(config);
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
