/**
 * Copyright (C) 2009 (nick @ objectdefinitions.com)
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
package com.od.jtimeseries.ui.selector.tree;

import com.od.jtimeseries.context.TimeSeriesContext;
import com.od.jtimeseries.timeseries.IdentifiableTimeSeries;
import com.od.jtimeseries.ui.timeseries.ChartingTimeSeries;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.ui.selector.shared.SelectorPanel;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.swing.action.ListSelectionActionModel;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 07-Jan-2009
* Time: 10:56:35
*/
public class TreeSelector extends SelectorPanel {

    private DefaultTreeModel treeModel;
    private TimeSeriesContext rootContext;
    private List<Action> seriesActions;
    private JTree tree;
    private Map<IdentifiableTimeSeries, SeriesTreeNode> seriesToNodeMap = new HashMap<IdentifiableTimeSeries, SeriesTreeNode>();

    public TreeSelector(ListSelectionActionModel<ChartingTimeSeries> seriesActionModel, TimeSeriesContext rootContext, java.util.List<Action> seriesActions) {
        super(seriesActionModel);
        this.rootContext = rootContext;
        this.seriesActions = seriesActions;

        treeModel = new DefaultTreeModel(new DefaultMutableTreeNode());
        tree = new JTree();
        tree.setModel(treeModel);

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setCellRenderer(new SeriesTreeCellRenderer());
        tree.addTreeSelectionListener(new SeriesTreeSelectionListener());

        refreshSeries();

        setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane, BorderLayout.CENTER);
        addMouseListeners();
    }

    private void addMouseListeners() {

        tree.addMouseListener(new SeriesSelectionMouseListener());

        //add a listener for mouse clicks on the tree, to populate the fileSelectionModel
        //this is done as a mouse listener rather than a tree selection listener so that we still get an event even if the selection is not changed
        tree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                showMenuIfPopupTrigger(e);
            }

            public void mouseClicked(MouseEvent e) {
                showMenuIfPopupTrigger(e);

            }

            public void mouseReleased(MouseEvent e) {
                showMenuIfPopupTrigger(e);
            }

            private void showMenuIfPopupTrigger(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    java.util.List<JMenuItem> menuItems = getMenuItems(e);
                    if ( menuItems != null) {
                        JPopupMenu menu = new JPopupMenu();
                        for ( JMenuItem i : menuItems) {
                            menu.add(i);
                        }
                        menu.show(tree, e.getX() + 3, e.getY() + 3);
                    }
                }
            }
        });
    }

    private java.util.List<JMenuItem> getMenuItems(MouseEvent e) {
        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
        java.util.List<JMenuItem> menuItems = new ArrayList<JMenuItem>();
        if (selPath != null ) {
            Object selectedNode = selPath.getLastPathComponent();
            if ( selectedNode instanceof SeriesTreeNode) {
                for ( Action a : seriesActions) {
                    menuItems.add(new JMenuItem(a));
                }
            }
        }
        return menuItems;
    }

    public void refreshSeries() {
        TreeNode rootNode;
        seriesToNodeMap.clear();

        //hold the context tree lock so that the context tree doesn't change while we are constructing the nodes
        try {
            rootContext.getContextLock().readLock().lock();
            rootNode = buildTreeNode(rootContext);
        } finally {
            rootContext.getContextLock().readLock().unlock();
        }
        treeModel.setRoot(rootNode);

        ensureSelectedNodesVisible();
        validate();
        repaint();
    }

    private void ensureSelectedNodesVisible() {
        for ( SeriesTreeNode n : seriesToNodeMap.values()) {
            if ( n.getTimeSeries().isSelected()) {
                tree.expandPath(new TreePath(n.getPath()).getParentPath());
            }
        }
    }

    public void removeSeries(java.util.List<ChartingTimeSeries> series) {
        for ( ChartingTimeSeries s : series) {
            SeriesTreeNode n = seriesToNodeMap.get(s);
            treeModel.removeNodeFromParent(n);
        }
    }

    private DefaultMutableTreeNode buildTreeNode(TimeSeriesContext context) {
        DefaultMutableTreeNode n = new ContextTreeNode(context);

        List<TimeSeriesContext> childContexts = sort(context.getChildContexts());
        for ( TimeSeriesContext c : childContexts) {
            n.add(buildTreeNode(c));
        }

        List<IdentifiableTimeSeries> timeSeries = sort(context.getTimeSeries());
        for ( IdentifiableTimeSeries s : timeSeries) {
            SeriesTreeNode node = new SeriesTreeNode((ChartingTimeSeries)s);
            seriesToNodeMap.put(s, node);
            n.add(node);
        }
        return n;
    }

    //sort child series by display name
    private <E extends Identifiable> List<E> sort(List<E> identifiables) {
        Collections.sort(identifiables, new Comparator<Identifiable>() {
            public int compare(Identifiable o1, Identifiable o2) {
                return getDisplayName(o1).compareTo(getDisplayName(o2));
            }
        });
        return identifiables;
    }

    /**
     * @return ContextTimeSeries selected in the tree, or null if no time series is selected
     */
    public IdentifiableTimeSeries getSelectedSeries() {
        IdentifiableTimeSeries result = null;
        TreePath p = tree.getSelectionPath();
        if ( p != null ) {
            Object o = p.getLastPathComponent();
            if ( o instanceof SeriesTreeNode) {
                result = ((SeriesTreeNode)o).getTimeSeries();
            }
        }
        return result;
    }

    public class SeriesTreeCellRenderer extends JPanel implements TreeCellRenderer {

        DefaultTreeCellRenderer delegateRenderer = new DefaultTreeCellRenderer();
        private JCheckBox seriesSelectionCheckbox = new JCheckBox();

        public SeriesTreeCellRenderer() {
            setLayout(new BorderLayout());
            add(delegateRenderer, BorderLayout.CENTER);
            seriesSelectionCheckbox.setOpaque(false);
            setOpaque(false);
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            delegateRenderer.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if ( value instanceof AbstractSeriesSelectionTreeNode) {
                delegateRenderer.setIcon(((AbstractSeriesSelectionTreeNode) value).getIcon());
                removeAll();
                if ( value instanceof SeriesTreeNode ) {
                    SeriesTreeNode seriesNode = (SeriesTreeNode)value;
                    seriesSelectionCheckbox.setSelected(seriesNode.getTimeSeries().isSelected());
                    delegateRenderer.setText(getDisplayName(((SeriesTreeNode)value).getTimeSeries()));
                    add(seriesSelectionCheckbox, BorderLayout.WEST);
                    add(delegateRenderer, BorderLayout.CENTER);
                } else {
                    delegateRenderer.setText(getDisplayName(((ContextTreeNode)value).getContext()));
                    add(delegateRenderer, BorderLayout.CENTER);
                }
                return this;
            } else {
                return delegateRenderer;
            }
        }
    }

    //get the display name for an identifiable in the context tree
    private String getDisplayName(Identifiable i) {
        if ( i instanceof ChartingTimeSeries) {
            return ((ChartingTimeSeries)i).getDisplayName();
        } else {
            return i.getId();
        }
    }

    private abstract static class AbstractSeriesSelectionTreeNode extends DefaultMutableTreeNode {

        protected abstract Icon getIcon();
    }

    private static class SeriesTreeNode extends AbstractSeriesSelectionTreeNode {

        private ChartingTimeSeries series;

        public SeriesTreeNode(ChartingTimeSeries series) {
            this.series = series;
        }

        public String toString() {
            return series.toString();
        }

        public ChartingTimeSeries getTimeSeries() {
            return series;
        }

        protected Icon getIcon() {
            return ImageUtils.REMOTE_CHART_16x16;
        }
    }

    private static class ContextTreeNode extends AbstractSeriesSelectionTreeNode {

        private TimeSeriesContext context;

        public ContextTreeNode(TimeSeriesContext context) {
            this.context = context;
        }

        public TimeSeriesContext getContext() {
            return context;
        }

        public String toString() {
            return context.toString();
        }

        protected Icon getIcon() {
            return ImageUtils.CONTEXT_ICON_16x16;
        }
    }


    private class SeriesSelectionMouseListener extends MouseAdapter {

        private  int hotspot = new JCheckBox().getPreferredSize().width;

        public void mouseClicked(MouseEvent me){
            TreePath path = tree.getPathForLocation(me.getX(), me.getY());
            if(path==null)
                return;
            if(me.getX()>tree.getPathBounds(path).x+hotspot)
                return;

            Object o = path.getLastPathComponent();
            if ( o instanceof SeriesTreeNode) {
                ChartingTimeSeries m = ((SeriesTreeNode)o).getTimeSeries();
                m.setSelected(! m.isSelected());
                tree.repaint();
            }
        }
    }

    private class SeriesTreeSelectionListener implements TreeSelectionListener {

        public void valueChanged(TreeSelectionEvent e) {
            Object o = e.getPath().getLastPathComponent();
            if ( o instanceof SeriesTreeNode ) {
                getSeriesActionModel().setSelected(((SeriesTreeNode)o).getTimeSeries());
                fireSelectedForDescription(((SeriesTreeNode)o).getTimeSeries());
            } else if ( o instanceof ContextTreeNode ) {
                fireSelectedForDescription(((ContextTreeNode)o).getContext());
            }
        }
    }

}
