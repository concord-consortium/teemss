package org.concord.waba.extra.ui;

public class TreeModel 
{
    TreeModelListener listener;

    /*
      I need to add events to all this so the "view" can be updated
      when the "model" changes
    */

    TreeNode root;

    public TreeModel(TreeNode node)
    {
		root = node;

    }

    public void addTreeModelListener(TreeModelListener l)
    {
		listener = l;
    }

    //    public TreeNode getChild(TreeNode parent, int index){}

    //    public TreeNode [] getChildArray(TreeNode parent){}

    public TreeNode getRoot(){return root;}

    public void insertNodeInto(TreeNode newChild, TreeNode parent, int index)
    {
		parent.insert(newChild, index);
		if(listener != null) listener.treeNodeInserted(newChild, parent);
    }

    public void addParent(TreeNode curNode, TreeNode newParent)
    {
		curNode.addParent(newParent);
		if(listener != null) listener.treeModelChanged();
    }

    public void removeNodeFromParent(TreeNode node, TreeNode parent)
    {
		parent.remove(node);
		if(listener != null) listener.treeModelChanged();
    }

    //    public void isLeaf(TreeNode node){}

    public void setRoot(TreeNode root)
    {
		this.root = root;
    }

    public void reload()
    {	
		if(listener != null) listener.treeModelChanged();
    }
}
