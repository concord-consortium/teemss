package org.concord.waba.extra.util;

public interface TreeNode 
{
    public TreeNode [] childArray();

    public TreeNode [] parentArray();

    public TreeNode getChildAt(int index);

    public int getIndex(TreeNode node);

    public boolean isLeaf();

    public int getChildCount();

    public void insert(TreeNode child, int index);

    public void addParent(TreeNode parent);

    public void remove(int index);

    public void remove(TreeNode node);

    public String toString();

    public boolean equals(TreeNode node);
}
