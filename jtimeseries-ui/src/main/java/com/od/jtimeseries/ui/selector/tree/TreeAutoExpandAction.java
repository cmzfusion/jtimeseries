package com.od.jtimeseries.ui.selector.tree;

import com.od.swing.action.ModelDrivenAction;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.util.Enumeration;

/**
* Created by IntelliJ IDEA.
* User: nick
* Date: 30/12/10
* <p/>
*
* autoExpandLevel is the level to which nodes are automatically expanded when added to the tree.
 *
* Expand actions increase this and also expand any nodes not already expanded to the new level
* Contract actions decrease this, and also collapse any nodes not collapsed to the new level
*/
public class TreeAutoExpandAction extends ModelDrivenAction<ExpandLevelActionModel> {

    private ExpansionRule autoExpandRule;
    private JTree tree;
    private int increment;

    public TreeAutoExpandAction(JTree tree, final ExpandLevelActionModel m, int increment) {
        super(m);
        this.tree = tree;
        this.increment = increment;

        autoExpandRule = new ExpansionRule() {
            public boolean shouldExpand(DefaultMutableTreeNode n) {
                return n.getLevel() <= m.getAutoExpandLevel();
            }
        };
    }

    public void actionPerformed(ActionEvent e) {
        getActionModel().incrementExpandLevel(increment);
        boolean isExpanding = increment > 0;

        //when the user contracting the tree, we never expand any nodes, even if they should be
        //expanded according to the current autoexpand depth. The same is true in reverse for expanding the tree
        autoExpandNodesFrom((DefaultMutableTreeNode)tree.getModel().getRoot(), isExpanding, !isExpanding);
    }

    public void autoExpandNodesFrom(DefaultMutableTreeNode node, boolean isExpanding, boolean isCollapsing) {
        expandNodesFrom(tree, node, autoExpandRule, isExpanding, isCollapsing);
    }

    /**
     * change the expansion state of any child nodes of startNode, according to the ExpansionRule
     * @param isCollapsing, if true collapse any nodes which should be collapsed according to the rule
     * @param isExpanding, if true expand any nodes which should be expanded according to the rule
     */
    public static void expandNodesFrom(JTree tree, DefaultMutableTreeNode node, ExpansionRule r, boolean isExpanding, boolean isCollapsing) {
        Enumeration<DefaultMutableTreeNode> e = node.children();
        while(e.hasMoreElements()) {
            expandNodesFrom(tree, e.nextElement(), r, isCollapsing, isExpanding);
        }

        TreePath pathToExpand = new TreePath(node.getPath());
        boolean expanded = tree.isExpanded(pathToExpand);
        boolean shouldBeExpanded = r.shouldExpand(node);
        if ( ! expanded && shouldBeExpanded && isExpanding ) {
            tree.expandPath(pathToExpand);
        } else if ( expanded && ! shouldBeExpanded && isCollapsing ) {
            tree.collapsePath(pathToExpand);
        }
    }

    public static class ExpandTreeAction extends TreeAutoExpandAction {

        public ExpandTreeAction(JTree tree, ExpandLevelActionModel m) {
            super(tree, m, 1);
            putValue(Action.NAME, "+");
        }

        protected boolean isModelStateActionable() {
            return ! getActionModel().isMaximallyExpanded();
        }
    }

    public static class ContractTreeAction extends TreeAutoExpandAction {

        public ContractTreeAction(JTree tree, ExpandLevelActionModel m) {
            super(tree, m, -1);
            putValue(Action.NAME, "-");
        }

        protected boolean isModelStateActionable() {
            return ! getActionModel().isMinimallyExpanded();
        }
    }

    /**
* Created by IntelliJ IDEA.
* User: nick
* Date: 30/12/10
* <p/>
* To change this template use File | Settings | File Templates.
*/
    static interface ExpansionRule {
    public boolean shouldExpand(DefaultMutableTreeNode n);
}
}
