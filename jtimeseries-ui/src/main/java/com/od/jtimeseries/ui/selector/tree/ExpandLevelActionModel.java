package com.od.jtimeseries.ui.selector.tree;

import com.od.jtimeseries.util.NamedExecutors;
import com.od.swing.action.AbstractActionModel;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
* Created by IntelliJ IDEA.
* User: nick
* Date: 30/12/10
* <p/>
*
* An action model which links its state to the depth of the tree
* This is updated at max once per second, since the depth calculation is expensive
*/
class ExpandLevelActionModel extends AbstractActionModel {

    private static ScheduledExecutorService treeDepthMonitorService = NamedExecutors.newSingleThreadScheduledExecutor("TreeDepthMonitor");
    private TreeModel model;
    private int autoExpandLevel = -1;
    private int treeLevels = -1;
    private AtomicBoolean scheduled = new AtomicBoolean();

    public ExpandLevelActionModel(TreeModel model, int i) {
        this.model = model;
        this.autoExpandLevel = i;
        model.addTreeModelListener(new UpdateDepthOnTreeChangeListener());
    }

    private void updateTreeLevels() {
        SwingUtilities.invokeLater(
            new Runnable() {
                public void run() {
                    setTreeLevels(
                        ((DefaultMutableTreeNode)getRoot()).getDepth()
                    );
                }
            }
        );
    }

    public Object getRoot() {
        return model.getRoot();
    }

    public boolean isMaximallyExpanded() {
        System.out.println("autoExpand " + autoExpandLevel + " tree " + treeLevels);
        return autoExpandLevel >= treeLevels - 1; //-1 because leaves don't get expanded
    }

    public boolean isMinimallyExpanded() {
        System.out.println("autoExpand " + autoExpandLevel + " tree " + treeLevels);
        return autoExpandLevel < 1 || treeLevels < autoExpandLevel;
    }

    public void incrementExpandLevel(int adjustment) {
        this.autoExpandLevel += adjustment;
        setValidAndFireUpdate();
    }

    public int getAutoExpandLevel() {
        return autoExpandLevel;
    }

    public void setTreeLevels(int treeLevels) {
        this.treeLevels = treeLevels;
        setValidAndFireUpdate();
    }

    private void setValidAndFireUpdate() {
        setModelValid(isLevelSet());
        fireActionStateUpdated();
    }

    private boolean isLevelSet() {
        return autoExpandLevel >= 0 && treeLevels >= 0;
    }

    protected void doClearActionModelState() {}


    private class UpdateDepthOnTreeChangeListener implements TreeModelListener {

        public void treeNodesChanged(TreeModelEvent e) {}

        public void treeNodesInserted(TreeModelEvent e) {
            queueDepthRecalc();
        }

        public void treeNodesRemoved(TreeModelEvent e) {
            queueDepthRecalc();
        }

        public void treeStructureChanged(TreeModelEvent e) {
            queueDepthRecalc();
        }

        //we don't want to do this every updated, since it is an expensive operation
        private void queueDepthRecalc() {
            if ( ! scheduled.getAndSet(true) ) {
                treeDepthMonitorService.schedule(new Runnable() {
                    public void run() {
                        scheduled.getAndSet(false);
                        updateTreeLevels();
                    }
                }, 1, TimeUnit.SECONDS);
            }
        }
    }
}
