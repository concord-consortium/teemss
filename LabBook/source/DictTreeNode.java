package org.concord.LabBook;

import waba.util.*;
import org.concord.waba.extra.ui.*;

public class DictTreeNode
	implements TreeNode
{
	LObjDictionary dict;
	LabObjectPtr ptr;
	LabBookSession session;
	LabBook lBook;

	public DictTreeNode(LObjDictionary dict, LabBookSession session,
						LabBook lBook)
	{
		this.dict = dict;
		this.session = session;
		this.lBook = lBook;
		ptr = dict.ptr; 
	}

	public LObjDictionary getDict(){
		if(dict.ptr == null && ptr != null){
			// some how this dictionary was released
			// load it again
			dict = (LObjDictionary)session.load(ptr);
		}

		return dict;
	}

	public LabObjectPtr getPtr()
	{
		return ptr;
	}
		
	public LabObjectPtr getPtr(TreeNode node)
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

			/*
			 * this is pointless in this context
			 * however if someone unhides folders and then
			 * opens a object inside they will probably get
			 * errors.
			if(hasMainObject &&
			   obj instanceof LObjSubDict &&
			   ((LabObjectPtr)(objects.get(0))).equals(node)){
				((LObjSubDict)obj).setDict(this);
			}
			*/
			return obj;
		}
		return null;
    }

    public TreeNode getNode(LabObject obj)
    {
		if(obj instanceof LObjDictionary){
			return new DictTreeNode((LObjDictionary)obj, session, lBook);
		} else if(obj instanceof LObjSubDict){
			LObjSubDict newObj = (LObjSubDict)obj;
			
			// we'll assume this object has been initiaizled
			if(newObj.dict != null){
				return new DictTreeNode((LObjDictionary)newObj.dict, session, lBook);
			} 
		} 

		// default
		LabObjectPtr ptr = lBook.store(obj);
		ptr.name = obj.getName();
		return ptr;
    }

    public static LabObjectPtr emptyChild = new LabObjectPtr("..empty..");
    public TreeNode [] childArray()
	{
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
				LObjDictionary newDict = (LObjDictionary)session.load(ptr);
				nodes[i] = new DictTreeNode(newDict, session, lBook);
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
		LabObjectPtr ptr = dict.getChildAt(index);

		if(ptr.objType == DefaultFactory.DICTIONARY){
			LObjDictionary newDict = (LObjDictionary)session.load(ptr);
			return new DictTreeNode(newDict, session, lBook);
		} else if(dict.hasMainObject && index == 0){
			//			((LObjSubDict)obj).setDict(this);
			ptr.name = "..main_obj..: " + ptr.name;
			return ptr;
		}
		
		return ptr;
	}

    public int getIndex(TreeNode node)
	{
		LabObjectPtr ptr = getPtr(node);
		return dict.getIndex(ptr);
	}

    public boolean isLeaf()
    {
		return (dict.hideChildren && dict.globalHide);
    }

    public int getChildCount()
	{
		return dict.getChildCount();
	}

    public void insert(TreeNode node, int index)
    {
		if(node instanceof DictTreeNode){
			dict.insert(((DictTreeNode)node).getDict(), index);
		} else if(node instanceof LabObjectPtr){
			dict.insert((LabObjectPtr)node, index);
		} else {
			Debug.println("Weirdness is happening");
		}
    }

    public void addParent(TreeNode parent){}

    public void remove(TreeNode node)
    {
		LabObjectPtr ptr = getPtr(node);
		dict.remove(ptr);
    }

    public void remove(int index)
    {
		LabObjectPtr ptr = getPtr(getChildAt(index));
		dict.remove(ptr);
    }

    public String toString()
    {
		if(dict.getName() == null) return "..null_name..";
		return dict.getName();
		
    }

    public boolean equals(TreeNode node)
	{
		if(node instanceof DictTreeNode){
			return getPtr().equals(((DictTreeNode)node).getPtr());
		}
		return false;
	}
}
