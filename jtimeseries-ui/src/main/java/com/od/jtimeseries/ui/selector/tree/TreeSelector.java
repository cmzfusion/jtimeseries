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
import com.od.jtimeseries.ui.selector.shared.SelectorComponent;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeEvent;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeListener;
import com.od.swing.action.ListSelectionActionModel;
import com.od.swing.progress.AnimatedIconTree;
import com.od.swing.util.AwtSafeListener;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
* User: Nick Ebbutt
* Date: 07-Jan-2009
* Time: 10:56:35
*/
public class TreeSelector<E extends UIPropertiesTimeSeries> extends SelectorComponent<E> {

    //auto expand to this depth

    private DefaultTreeModel treeModel = new DefaultTreeModel(new DefaultMutableTreeNode());
    private TimeSeriesContext rootContext;
    private List<Action> seriesActions;
    private JTree tree = new AnimatedIconTree(treeModel);
    private Map<Identifiable, AbstractSeriesSelectionTreeNode> identifiableToNodeMap = new HashMap<Identifiable, AbstractSeriesSelectionTreeNode>();
    private ContextNodeFactory<E> nodeFactory;
    private SeriesTreeCellRenderer cellRenderer;
    private JToolBar toolbar = new JToolBar();
    private ExpandLevelActionModel expandLevelModel = new ExpandLevelActionModel(treeModel, 2);
    private TreeAutoExpandAction.ContractTreeAction contractTreeAction = new TreeAutoExpandAction.ContractTreeAction(tree, expandLevelModel);
    private TreeAutoExpandAction.ExpandTreeAction expandTreeAction = new TreeAutoExpandAction.ExpandTreeAction(tree, expandLevelModel);

    public TreeSelector(ListSelectionActionModel<E> seriesActionModel, TimeSeriesContext rootContext, java.util.List<Action> seriesActions, Class seriesClass) {
        super(rootContext, seriesActionModel);
        this.rootContext = rootContext;
        this.seriesActions = seriesActions;

        nodeFactory = new ContextNodeFactory<E>(tree, seriesClass);

        setupSeries();

        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);

        createToolbar();

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        cellRenderer = new SeriesTreeCellRenderer();
        tree.setCellRenderer(cellRenderer);
        tree.addTreeSelectionListener(new SeriesTreeSelectionListener());

        setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(tree);
        add(toolbar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        addMouseListeners();
    }


    private void createToolbar() {
        toolbar.add(contractTreeAction);
        toolbar.add(expandTreeAction);
    }

    public void setSeriesSelectionEnabled(boolean enabled) {
        cellRenderer.setSeriesSelectionEnabled(enabled);
    }

    protected void addContextTreeListener() {
        rootContext.addTreeListener(
            AwtSafeListener.getAwtSafeListener(
                    new ContextTreeUpdaterListener(),
                    IdentifiableTreeListener.class
            )
        );
    }

    private class ContextTreeUpdaterListener implements IdentifiableTreeListener {

        public void descendantChanged(IdentifiableTreeEvent contextTreeEvent) {
            repaint();
            for (Identifiable i : contextTreeEvent.getNodes()) {
                fireChangeEvents(i);
            }
        }

        public void descendantAdded(IdentifiableTreeEvent contextTreeEvent) {
            for (Identifiable i : contextTreeEvent.getNodes()) {
                addNodeAndAllDescendants(i);
            }
        }

        public void descendantRemoved(IdentifiableTreeEvent contextTreeEvent) {
            for ( Identifiable i : contextTreeEvent.getNodes()) {
                removeNodeAndAllDescendants(i);
            }
        }

        public void nodeChanged(Identifiable node, Object changeDescription) {
            fireChangeEvents(node);
        }
    }

    private void fireChangeEvents(Identifiable i) {
        AbstractSeriesSelectionTreeNode changedNode = identifiableToNodeMap.get(i);
        if ( i != null ) {
            treeModel.nodeChanged(changedNode);
        }
    }

    private void addNodeAndAllDescendants(Identifiable i) {
        if ( ! identifiableToNodeMap.containsKey(i)) {
            Identifiable parent = i.getParent();
            AbstractSeriesSelectionTreeNode parentNode = identifiableToNodeMap.get(parent);

            //build and add the node tree for each added item and any descendant nodes
            AbstractSeriesSelectionTreeNode newNode = buildTree(i);
            if ( parentNode != null && newNode != null) {
                int index = addChild(parentNode, newNode);
                treeModel.nodesWereInserted(parentNode, new int[]{index});

                if ( parentNode.getLevel() <= expandLevelModel.getAutoExpandLevel()) {
                    expandTreeAction.autoExpandNodesFrom(parentNode, true, false);
                }
            }
        }
    }

    private void removeNodeAndAllDescendants(Identifiable node) {
        AbstractSeriesSelectionTreeNode n = identifiableToNodeMap.remove(node);
        treeModel.removeNodeFromParent(n);

        List<Identifiable> allDescendants = new LinkedList<Identifiable>();
        addAllDescendants(allDescendants, node);
        for ( Identifiable i : allDescendants) {
            identifiableToNodeMap.remove(i);
        }
    }

    private void addAllDescendants(List<Identifiable> allDescendants, Identifiable node) {
        for ( Identifiable i : node.getChildren()) {
            addAllDescendants(allDescendants, i);
        }
        allDescendants.add(node);
    }

    private void addMouseListeners() {
        tree.addMouseListener(new SeriesSelectionMouseListener(tree));

        //add a listener for mouse clicks on the tree, to populate the fileSelectionModel
        //this is done as a mouse listener rather than a tree selection listener so that we still get an event even if the selection is not changed
        tree.addMouseListener(new PopupMenuMouseListener(tree, seriesActions));
    }

    protected void buildView() {
        AbstractSeriesSelectionTreeNode rootNode = buildTree(rootContext);
        treeModel.setRoot(rootNode);
    }


    private AbstractSeriesSelectionTreeNode buildTree(Identifiable identifiable) {

        List<AbstractSeriesSelectionTreeNode> childNodes = new LinkedList<AbstractSeriesSelectionTreeNode>();
        for ( Identifiable c : identifiable.getChildren()) {
            AbstractSeriesSelectionTreeNode childNode = buildTree(c);
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
            if ( comparison < 0) {
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
        AbstractSeriesSelectionTreeNode result = nodeFactory.buildNode(identifiable);
        if ( result != null ) {
            identifiableToNodeMap.put(identifiable, result);
        }
        return result;
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

}
