package org.concord.waba.extra.ui;

public interface TreeNode 
{
    public TreeNode [] childArray();

    TreeNode [] parentArray();

    TreeNode getChildAt(int index);

    int getIndex(TreeNode node);

    boolean isLeaf();

    void insert(TreeNode child, int index);

    void addParent(TreeNode parent);

    void remove(int index);

    void remove(TreeNode node);

    String toString();
}
