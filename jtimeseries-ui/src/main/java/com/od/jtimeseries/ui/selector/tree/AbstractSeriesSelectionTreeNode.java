package com.od.jtimeseries.ui.selector.tree;

import com.od.jtimeseries.ui.util.IdentifiableSource;
import com.od.jtimeseries.util.identifiable.Identifiable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
* Created by IntelliJ IDEA.
* User: nick
* Date: 20/12/10
* <p/>
* To change this template use File | Settings | File Templates.
*/
public abstract class AbstractSeriesSelectionTreeNode extends DefaultMutableTreeNode implements IdentifiableSource {

    public abstract Identifiable getIdentifiable();

    protected abstract Icon getIcon();
}
