package org.concord.LabBook;

import extra.io.*;
import org.concord.waba.extra.ui.*;


public class LabObjectPtr implements TreeNode
{
    int devId;
    int objId;
    String name;
    int objType;

    LabObject obj = null;

    public LabObjectPtr(int dId, int oId, LabObject o)
    {
	devId = dId;
	objId = oId;
	obj = o;

    }

    public LabObjectPtr(String name)
    {
	devId = -1;
	objId = -1;
	obj = null;
	this.name = name;
    }

    public LabObjectPtr()
    {
    }

    public static LabObjectPtr readExternal(DataStream in)
    {
	LabObjectPtr me = new LabObjectPtr();
	me.devId = in.readInt();
	me.objId = in.readInt();
	return me;
    }
    
    public void writeExternal(DataStream out)
    {
	out.writeInt(devId);
	out.writeInt(objId);
    }

    public TreeNode [] childArray(){return null;}

    public TreeNode [] parentArray(){return null;}

    public TreeNode getChildAt(int index){return null;}

    public int getIndex(TreeNode node){return -1;}

    public boolean isLeaf(){return true;}

    public int getChildCount(){return 0;}

    public void insert(TreeNode child, int index){}

    public void addParent(TreeNode parent){}

    public void remove(int index){}

    public void remove(TreeNode node){}

    public String toString(){return name;}

}
