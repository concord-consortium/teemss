package org.concord.LabBook;

import org.concord.waba.extra.io.*;
import org.concord.waba.extra.ui.*;


public class LabObjectPtr implements TreeNode
{
	static LabBook lBook;
	LabBookDB db;

    int devId;
    int objId;
    String name;
    short objType = -1;
	short version = -1;
	short flags = 0;

    LabObject obj = null;

    LabObjectPtr(int dId, int oId, LabObject o, LabBookDB db)
    {
		this.db = db;
		devId = dId;
		objId = oId;
		obj = o;

    }

    LabObjectPtr(String name, LabBookDB db)
    {
		this(-1, -1, null, db);
		this.name = name;
    }

    LabObjectPtr(LabBookDB db)
    {
		this("", db);
    }

	LabObjectPtr(DataStream in, LabBookDB db)
	{
		this(db);
		devId = in.readInt();
		objId = in.readInt();
	}

	public LabBookSession getSession()
	{
		return lBook.getSession(db);		
	}

    void writeExternalP(DataStream out)
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

    public String toString()
	{
		if(name == null) return "..null_name..";
		return name;
	}

    public String debug(){return "devId " + devId + " objId " + objId;};

    public boolean equals(TreeNode node){
		if(node == this) return true;
		if(node != null &&
		   node instanceof LabObjectPtr){
			LabObjectPtr ptr = (LabObjectPtr)node;
			return devId == ptr.devId &&
				objId == ptr.objId;

		}

		return false;
    }
	  
}
