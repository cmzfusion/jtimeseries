package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.selector.tree.AbstractSeriesSelectionTreeNode;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick
 * Date: 09/05/11
 * Time: 09:20
 */
public class SettingsTreeNode extends AbstractSeriesSelectionTreeNode {

    private SettingsContext settingsContext;

    public SettingsTreeNode(SettingsContext settingsContext) {
        this.settingsContext = settingsContext;
    }

    public Identifiable getIdentifiable() {
        return settingsContext;
    }

    protected Icon getIcon() {
        return ImageUtils.SETTINGS_16x16;
    }
}
