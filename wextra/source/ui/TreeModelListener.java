package org.concord.waba.extra.ui;

public interface TreeModelListener
{
    void treeNodeInserted(TreeNode node, TreeNode parent);

    void treeModelChanged();
}
