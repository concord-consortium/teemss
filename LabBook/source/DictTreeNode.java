package org.concord.LabBook;

import waba.util.*;
import org.concord.waba.extra.ui.*;

public class DictTreeNode
	implements TreeNode
{
	LabObjectPtr ptr;
	LabBookSession session;
	LabBook lBook;

	public DictTreeNode(LabObjectPtr dictPtr, LabBookSession session,
						LabBook lBook)
	{
		ptr = dictPtr;
		this.session = session;
		this.lBook = lBook;
	}

	public LObjDictionary getDict(){
		return (LObjDictionary)session.load(ptr);
	}

	public LabObjectPtr getPtr()
	{
		return ptr;
	}
		
	public static LabObjectPtr getPtr(TreeNode node)
	{
		if(node instanceof DictTreeNode){
			return ((DictTreeNode)node).getPtr();
		} else if(node instanceof LabObjectPtr){
			return (LabObjectPtr)node;
		} else {
			return null;
		}
	}

    public LabObject getObj(TreeNode node)
    {
		if(node instanceof DictTreeNode){
			LObjDictionary newDict = (LObjDictionary)((DictTreeNode)node).getDict();
			if(newDict.hasMainObject){
				LObjSubDict mainObj = newDict.getMainObj(session);
				return mainObj;
			}
			return newDict;
		} else if(node instanceof LabObjectPtr){
			LabObject obj = session.load((LabObjectPtr)node);
			return obj;
		}
		return null;
    }

    public TreeNode getNode(LabObject obj)
    {
		if(obj instanceof LObjDictionary){
			return new DictTreeNode(obj.getVisiblePtr(), session, lBook);
		} else if(obj instanceof LObjSubDict){
			LObjSubDict newObj = (LObjSubDict)obj;
			
			// we'll assume this object has been initiaizled
			if(newObj.dict != null){
				return new DictTreeNode(newObj.dict.getVisiblePtr(), session, lBook);
			} 
		} 

		// default
		LabObjectPtr ptr = lBook.store(obj);
		ptr.name = obj.getName();
		return ptr;
    }

    public static LabObjectPtr emptyChild = new LabObjectPtr("..empty..", null);

    public TreeNode [] childArray()
	{
		LObjDictionary dict = getDict();

		LabObjectPtr [] ptrs = dict.childArray();
		TreeNode [] nodes;
		if(ptrs == null || ptrs.length == 0){
		    nodes = new TreeNode [1];
		    nodes[0] = emptyChild;
		    return nodes;
		}	

		nodes = new TreeNode [ptrs.length];
		
		for(int i=0; i<nodes.length; i++){
			LabObjectPtr ptr = ptrs[i];

			if(ptr.objType == DefaultFactory.DICTIONARY){
				nodes[i] = new DictTreeNode(ptr, session, lBook);
			} else if(dict.hasMainObject && i == 0){
				//			((LObjSubDict)obj).setDict(this);
				ptr.name = "..main_obj..: " + ptr.name;
				nodes[i] = ptr;
			} else {
				nodes[i] = ptr;
			}
		}

		return nodes;
	}

    public TreeNode [] parentArray(){return null;}

    public TreeNode getChildAt(int index)
	{
		LObjDictionary dict = getDict();

		LabObjectPtr ptr = dict.getChildAt(index);

		if(ptr.objType == DefaultFactory.DICTIONARY){
			return new DictTreeNode(ptr, session, lBook);
		} else if(dict.hasMainObject && index == 0){
			//			((LObjSubDict)obj).setDict(this);
			ptr.name = "..main_obj..: " + ptr.name;
			return ptr;
		}
		
		return ptr;
	}

    public int getIndex(TreeNode node)
	{
		LObjDictionary dict = getDict();

		LabObjectPtr ptr = getPtr(node);
		return dict.getIndex(ptr);
	}

    public boolean isLeaf()
    {
		boolean hideChildren = ((ptr.flags & 0x010) == 0x010?true:false);
		return (hideChildren && LObjDictionary.globalHide);
    }

    public int getChildCount()
	{
		LObjDictionary dict = getDict();

		return dict.getChildCount();
	}

    public void insert(TreeNode node, int index)
    {
		LObjDictionary dict = getDict();

		if(node instanceof DictTreeNode){
			dict.insert(((DictTreeNode)node).getDict(), index);
		} else if(node instanceof LabObjectPtr){
			LabObjectPtr lObjPtr = (LabObjectPtr)node;
			dict.insert((LabObjectPtr)node, index);
		} else {
			Debug.println("Weirdness is happening");
		}
    }

    public void addParent(TreeNode parent){}

    public void remove(TreeNode node)
    {
		LObjDictionary dict = getDict();

		LabObjectPtr ptr = getPtr(node);
		dict.remove(ptr);
    }

    public void remove(int index)
    {
		LObjDictionary dict = getDict();

		LabObjectPtr ptr = getPtr(getChildAt(index));
		dict.remove(ptr);
    }

    public String toString()
	{
		if(ptr.name == null) return "..null_name..";
		return ptr.name;
	}

    public boolean equals(TreeNode node)
	{
		if(node instanceof DictTreeNode){
			return getPtr().equals(((DictTreeNode)node).getPtr());
		}
		return false;
	}
}
