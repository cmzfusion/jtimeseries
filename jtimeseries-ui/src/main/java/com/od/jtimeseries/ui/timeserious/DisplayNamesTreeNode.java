package com.od.jtimeseries.ui.timeserious;

import com.od.jtimeseries.ui.selector.tree.AbstractSeriesSelectionTreeNode;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 12/05/11
 * Time: 08:16
 */
public class DisplayNamesTreeNode extends AbstractSeriesSelectionTreeNode {

    private DisplayNamesContext settingsContext;

    public DisplayNamesTreeNode(DisplayNamesContext settingsContext) {
        this.settingsContext = settingsContext;
    }

    public Identifiable getIdentifiable() {
        return settingsContext;
    }

    protected Icon getIcon() {
        return ImageUtils.DISPLAY_NAME_16x16;
    }
}

