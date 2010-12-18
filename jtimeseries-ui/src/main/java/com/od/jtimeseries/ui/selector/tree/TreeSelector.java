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
import com.od.jtimeseries.ui.download.panel.TimeSeriesServerContext;
import com.od.jtimeseries.ui.selector.shared.SelectorPanel;
import com.od.jtimeseries.ui.timeseries.ChartingTimeSeries;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.ui.util.ImageUtils;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeEvent;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeListener;
import com.od.swing.action.ListSelectionActionModel;
import com.od.swing.util.AwtSafeListener;

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
public class TreeSelector<E extends UIPropertiesTimeSeries> extends SelectorPanel<E> {

    private DefaultTreeModel treeModel;
    private TimeSeriesContext rootContext;
    private List<Action> seriesActions;
    private Class seriesClass;
    private JTree tree;
    private Map<Identifiable, AbstractSeriesSelectionTreeNode> identifiableToNodeMap = new HashMap<Identifiable, AbstractSeriesSelectionTreeNode>();

    public TreeSelector(ListSelectionActionModel<E> seriesActionModel, TimeSeriesContext rootContext, java.util.List<Action> seriesActions, Class seriesClass) {
        super(rootContext, seriesActionModel);
        this.rootContext = rootContext;
        this.seriesActions = seriesActions;
        this.seriesClass = seriesClass;

        treeModel = new DefaultTreeModel(new DefaultMutableTreeNode());
        setupSeries();

        tree = new JTree();
        tree.setModel(treeModel);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setCellRenderer(new SeriesTreeCellRenderer());
        tree.addTreeSelectionListener(new SeriesTreeSelectionListener());

        setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane, BorderLayout.CENTER);
        addMouseListeners();
    }

    protected void addContextTreeListener() {
        rootContext.addTreeListener(
                AwtSafeListener.getAwtSafeListener(
                        new IdentifiableTreeListener() {
                            public void descendantChanged(IdentifiableTreeEvent contextTreeEvent) {
                                repaint();
                            }

                            public void descendantAdded(IdentifiableTreeEvent contextTreeEvent) {
                                for (Identifiable series : contextTreeEvent.getNodes()) {
                                    Identifiable parent = series.getParent();
                                    AbstractSeriesSelectionTreeNode parentNode = identifiableToNodeMap.get(parent);
                                    AbstractSeriesSelectionTreeNode newNode = buildNode(series);
                                    if ( parentNode != null && newNode != null) {
                                        int index = addChild(parentNode, newNode);
                                        treeModel.nodesWereInserted(parentNode, new int[]{index});
                                    }

                                    //the below seems to be required or we end up not showing new nodes at the server level
                                    if ( parentNode.isRoot()) {
                                        tree.expandPath(new TreePath(new Object[] {treeModel.getRoot(), parentNode }));
                                    }
                                }

                            }

                            public void descendantRemoved(IdentifiableTreeEvent contextTreeEvent) {
                                List<E> timeSeries = getAffectedSeries(seriesClass, contextTreeEvent);
                                removeSeries(timeSeries);
                            }

                            public void nodeChanged(Identifiable node, Object changeDescription) {
                            }
                        },
                        IdentifiableTreeListener.class
                )
        );
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

    protected void buildView() {
        TreeNode rootNode = buildTreeNode(rootContext);
        treeModel.setRoot(rootNode);
        ensureSelectedNodesVisible();
    }

    private void ensureSelectedNodesVisible() {
        for ( AbstractSeriesSelectionTreeNode n : identifiableToNodeMap.values()) {
            if ( n.isSelected()) {
                tree.expandPath(new TreePath(n.getPath()).getParentPath());
            }
        }
    }

    private void removeSeries(java.util.List<E> series) {
        for ( E s : series) {
            AbstractSeriesSelectionTreeNode n = identifiableToNodeMap.remove(s);
            treeModel.removeNodeFromParent(n);
        }
    }

    private AbstractSeriesSelectionTreeNode buildTreeNode(Identifiable identifiable) {

        List<AbstractSeriesSelectionTreeNode> childNodes = new LinkedList<AbstractSeriesSelectionTreeNode>();
        for ( Identifiable c : identifiable.getChildren()) {
            AbstractSeriesSelectionTreeNode childNode = buildTreeNode(c);
            if ( childNode != null) {
                childNodes.add(childNode);
            }
        }

        AbstractSeriesSelectionTreeNode n = buildNode(identifiable);
        if ( n != null ) {
            for ( AbstractSeriesSelectionTreeNode child : childNodes) {
                addChild(n, child);
            }
        }
        return n;
    }

    private int addChild(AbstractSeriesSelectionTreeNode parent, AbstractSeriesSelectionTreeNode child) {
        Enumeration<DefaultMutableTreeNode> e = parent.children();
        int index = 0;
        IdentifiableTreeComparator c = new IdentifiableTreeComparator();
        boolean inserted = false;
        while(e.hasMoreElements()) {
            AbstractSeriesSelectionTreeNode node = (AbstractSeriesSelectionTreeNode)e.nextElement();
            int comparison = c.compare(node.getIdentifiable(), child.getIdentifiable());
            if ( comparison == -1) {
                parent.insert(child, index);
                inserted = true;
                break;
            }
            index++;
        }
        if ( ! inserted) {
            parent.add(child);
        }
        return index;
    }

    private AbstractSeriesSelectionTreeNode buildNode(Identifiable identifiable) {
        AbstractSeriesSelectionTreeNode result;
        if ( identifiable instanceof TimeSeriesContext) {
            result = buildContextNode((TimeSeriesContext)identifiable);
        } else if ( seriesClass.isAssignableFrom(identifiable.getClass())) {
            result = buildSeriesNode((E)identifiable);
        } else {
            result = null;
        }
        if ( result != null ) {
            identifiableToNodeMap.put(identifiable, result);
        }
        return result;
    }

    private ContextTreeNode buildContextNode(TimeSeriesContext context) {
        return new ContextTreeNode(context);
    }

    private SeriesTreeNode buildSeriesNode(E s) {
        return new SeriesTreeNode<E>(s);
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
                    Object timeSeries = seriesNode.getTimeSeries();
                    if ( timeSeries instanceof UIPropertiesTimeSeries) {
                        seriesSelectionCheckbox.setSelected(((UIPropertiesTimeSeries)timeSeries).isSelected());
                    }
                    delegateRenderer.setText(getDisplayName(((SeriesTreeNode<E>)value).getTimeSeries()));
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
        if ( i instanceof UIPropertiesTimeSeries) {
            return ((UIPropertiesTimeSeries)i).getDisplayName();
        } else {
            return i.getId();
        }
    }

    private abstract static class AbstractSeriesSelectionTreeNode extends DefaultMutableTreeNode {
        protected abstract Identifiable getIdentifiable();

        protected abstract Icon getIcon();

        public abstract boolean isSelected();
    }

    private static class SeriesTreeNode<E extends UIPropertiesTimeSeries> extends AbstractSeriesSelectionTreeNode {

        private E series;

        public SeriesTreeNode(E series) {
            this.series = series;
        }

        public String toString() {
            return series.toString();
        }

        public E getTimeSeries() {
            return series;
        }

        @Override
        protected Identifiable getIdentifiable() {
            return series;
        }

        protected Icon getIcon() {
            return ImageUtils.REMOTE_CHART_16x16;
        }

        @Override
        public boolean isSelected() {
            return series.isSelected();
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

        @Override
        protected Identifiable getIdentifiable() {
            return context;
        }

        protected Icon getIcon() {
            return context instanceof TimeSeriesServerContext ? ImageUtils.TIMESERIES_SERVER_ICON_16x16 : ImageUtils.CONTEXT_ICON_16x16;
        }

        @Override
        public boolean isSelected() {
            return false;
        }
    }


    private class SeriesSelectionMouseListener<E extends UIPropertiesTimeSeries> extends MouseAdapter {

        private  int hotspot = new JCheckBox().getPreferredSize().width;

        public void mouseClicked(MouseEvent me){
            TreePath path = tree.getPathForLocation(me.getX(), me.getY());
            if(path==null)
                return;
            if(me.getX()>tree.getPathBounds(path).x+hotspot)
                return;

            Object o = path.getLastPathComponent();
            if ( o instanceof SeriesTreeNode) {
                E m = ((SeriesTreeNode<E>)o).getTimeSeries();
                if ( m instanceof UIPropertiesTimeSeries) {
                    UIPropertiesTimeSeries s = (UIPropertiesTimeSeries)m;
                    s.setSelected(!s.isSelected());
                }
                tree.repaint();
            }
        }
    }

    private class SeriesTreeSelectionListener implements TreeSelectionListener {

        public void valueChanged(TreeSelectionEvent e) {
            Object o = e.getPath().getLastPathComponent();
            if ( o instanceof SeriesTreeNode ) {
                getSeriesActionModel().setSelected(((SeriesTreeNode<E>)o).getTimeSeries());
                fireSelectedForDescription(((SeriesTreeNode<E>)o).getTimeSeries());
            } else if ( o instanceof ContextTreeNode ) {
                fireSelectedForDescription(((ContextTreeNode)o).getContext());
            }
        }
    }

    private class IdentifiableTreeComparator implements Comparator<Identifiable> {
        public int compare(Identifiable o1, Identifiable o2) {
            return getDisplayName(o1).compareTo(getDisplayName(o2));
        }
    }
}
