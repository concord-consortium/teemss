package org.concord.waba.extra.ui;

import org.concord.waba.extra.util.*;

public interface TreeModelListener
{
    void treeNodeInserted(TreeNode node, TreeNode parent);

    void treeModelChanged();
}
