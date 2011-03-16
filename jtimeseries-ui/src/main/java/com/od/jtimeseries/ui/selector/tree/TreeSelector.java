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
import com.od.jtimeseries.ui.selector.shared.IdentifiableListActionModel;
import com.od.jtimeseries.ui.selector.shared.SelectorComponent;
import com.od.jtimeseries.ui.selector.shared.SelectorPopupMouseListener;
import com.od.jtimeseries.ui.selector.shared.NoImportsSelectorTransferHandler;
import com.od.jtimeseries.ui.timeseries.UIPropertiesTimeSeries;
import com.od.jtimeseries.util.identifiable.Identifiable;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeEvent;
import com.od.jtimeseries.util.identifiable.IdentifiableTreeListener;
import com.od.swing.progress.AnimatedIconTree;
import com.od.swing.util.AwtSafeListener;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseEvent;
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
    private static final int treeAutoExpandLevel = 1;

    private static final ExpansionRule AUTO_EXPAND_RULE = new ExpansionRule() {

        public boolean shouldExpand(AbstractSeriesSelectionTreeNode n) {
            return n.getLevel() <= treeAutoExpandLevel;
        }
    };

    private DefaultTreeModel treeModel;
    private TimeSeriesContext rootContext;
    private JTree tree;
    private Map<Identifiable, AbstractSeriesSelectionTreeNode> identifiableToNodeMap = new HashMap<Identifiable, AbstractSeriesSelectionTreeNode>();
    private SelectorTreeNodeFactory<E> nodeFactory;
    private SeriesTreeCellRenderer cellRenderer;

    public TreeSelector(IdentifiableListActionModel selectionsActionModel, TimeSeriesContext rootContext, SelectorTreeNodeFactory nodeFactory) {
        super(rootContext, selectionsActionModel);
        this.rootContext = rootContext;
        this.nodeFactory = nodeFactory;

        treeModel = new DefaultTreeModel(new DefaultMutableTreeNode());
        tree = new AnimatedIconTree();

        setupSeries();

        tree.setModel(treeModel);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        autoExpandTree();

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        cellRenderer = new SeriesTreeCellRenderer();
        tree.setCellRenderer(cellRenderer);
        tree.addTreeSelectionListener(new SeriesTreeSelectionListener());

        setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(tree);
        //add(toolbar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        addMouseListeners();
        setupDragAndDrop();
    }

    private void setupDragAndDrop() {
        tree.setDragEnabled(true);
        tree.setDropMode(DropMode.ON);
        tree.setTransferHandler(new NoImportsSelectorTransferHandler(getSelectionsActionModel()));
    }

    public void setTransferHandler(TransferHandler newHandler) {
        tree.setTransferHandler(newHandler);
    }

    public void setSeriesSelectionEnabled(boolean enabled) {
        cellRenderer.setSeriesSelectionEnabled(enabled);
    }

    private void autoExpandTree() {
        expandNodesFrom(
                (AbstractSeriesSelectionTreeNode) treeModel.getRoot(),
                AUTO_EXPAND_RULE,
                true
        );
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

                if ( newNode.getLevel() <= (treeAutoExpandLevel + 1)) {  //+1 because parent would have been a leaf before and so would not have expanded
                    expandNodesFrom(parentNode, AUTO_EXPAND_RULE, false);
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
        tree.addMouseListener(new SelectorPopupMouseListener(this, tree) {
            protected List<Identifiable> getSelectedIdentifiable(MouseEvent e) {
                //only support single selection at present
                List<Identifiable> result = Collections.emptyList();
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if (selPath != null) {
                    Object selectedNode = selPath.getLastPathComponent();
                    if (selectedNode instanceof AbstractSeriesSelectionTreeNode) {
                        result = Collections.singletonList(((AbstractSeriesSelectionTreeNode)selectedNode).getIdentifiable());
                    }
                }
                return result;
            }

            protected SelectorComponent getSelectorComponent() {
                return TreeSelector.this;
            }
        });
    }

    protected void buildView() {
        AbstractSeriesSelectionTreeNode rootNode = buildTree(rootContext);
        treeModel.setRoot(rootNode);
    }

    public void showSelections(List<Identifiable> selected) {
        tree.clearSelection();
        List<TreePath> selectionPaths = findPaths(selected);

        for (TreePath p : selectionPaths) {
            tree.expandPath(p);
            tree.addSelectionPath(p);
        }

        if ( selectionPaths.size() > 0) {
            tree.scrollPathToVisible(selectionPaths.get(0));
        }

    }

    private List<TreePath> findPaths(List<Identifiable> selected) {
        Set<Identifiable> ids = convertToIdentifiableInThisContext(selected);
        List<TreePath> selectionPaths = new LinkedList<TreePath>() ;
        for ( Identifiable i : ids) {
            AbstractSeriesSelectionTreeNode n = identifiableToNodeMap.get(i);
            if ( n != null) {
                selectionPaths.add(new TreePath(n.getPath()));
            }
        }
        return selectionPaths;
    }

    private static interface ExpansionRule {
        public boolean shouldExpand(AbstractSeriesSelectionTreeNode n);
    }

    /**
     * expand the tree to show any child nodes of startNode which satisfy the ExpansionRule
     */
    private void expandNodesFrom(AbstractSeriesSelectionTreeNode node, ExpansionRule r, boolean collapse) {
        Enumeration<AbstractSeriesSelectionTreeNode> e = node.children();
        while(e.hasMoreElements()) {
            expandNodesFrom(e.nextElement(), r, collapse);
        }

        TreePath pathToExpand = new TreePath(node.getPath());
        boolean expanded = tree.isExpanded(pathToExpand);
        boolean shouldBeExpanded = r.shouldExpand(node);
        if ( ! expanded && shouldBeExpanded) {
            tree.expandPath(pathToExpand);
        } else if ( expanded && ! shouldBeExpanded && collapse ) {
            tree.collapsePath(pathToExpand);
        }
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
        AbstractSeriesSelectionTreeNode result = nodeFactory.buildNode(identifiable, tree);
        if ( result != null ) {
            identifiableToNodeMap.put(identifiable, result);
        }
        return result;
    }

    private class SeriesTreeSelectionListener implements TreeSelectionListener {

        public void valueChanged(TreeSelectionEvent e) {
            List<Identifiable> selections = new LinkedList<Identifiable>();
            TreePath[] paths = tree.getSelectionPaths();
            if ( paths != null ) {
                for ( TreePath p : tree.getSelectionPaths()) {
                    AbstractSeriesSelectionTreeNode o = (AbstractSeriesSelectionTreeNode)p.getLastPathComponent();
                    selections.add(o.getIdentifiable());
                }
                getSelectionsActionModel().setSelected(selections);
            } else {
                getSelectionsActionModel().clearActionModelState();
            }
        }
    }

}
