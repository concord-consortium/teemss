package org.concord.LabBook;

import waba.util.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;

public class LObjDictionary extends LabObject
	implements TreeNode
{
    public final static int TREE_VIEW = 0;
    public final static int PAGING_VIEW = 1;

    public Vector objects = new Vector();
    public int viewType = TREE_VIEW;

    boolean hasMainObject = false;
    boolean hasObjTemplate = false;
    int objTemplateIndex = 0;


    public LObjDictionary()
    {       
		super(DefaultFactory.DICTIONARY);
    }

    public LabObjectView getPropertyView(ViewContainer vc, LObjDictionary curDict)
    {
		if(hasMainObject){
	    	LObjSubDict mo = getMainObj();
	   	 	if(mo != null){
				return mo.getPropertyView(vc, curDict);
	    	} else return null;
		} else {
			return new LObjDictionaryProp(vc, this);
		}
    }

    public LabObjectView getView(ViewContainer vc, boolean edit, LObjDictionary curDict)
    {
		if(hasMainObject){
	    	LObjSubDict mo = getMainObj();
	   	 	if(mo != null){
				return mo.getView(vc, edit, curDict);
	    	} else return null;
		}

		if(viewType == TREE_VIEW)
			return new LObjDictionaryView(vc, this);
		if(viewType == PAGING_VIEW)
			return new LObjDictPagingView(vc, this, edit);

		return null;
    }

    public LObjSubDict getMainObj()
    {
		if(hasMainObject & objects.getCount() > 0){
			LabObject obj = (LabObject)getObj((LabObjectPtr)(objects.get(0)));
			LObjSubDict mainObj = 
				(LObjSubDict)getObj((LabObjectPtr)(objects.get(0)));
			mainObj.setDict(this);
			return mainObj;
		}

		return null;
    }
    
    public void setMainObj(LObjSubDict mainObj)
    {
		if(hasMainObject){	    
			objects.set(0, lBook.store(mainObj));
		} else {
			objects.insert(0, lBook.store(mainObj));
		} 

		mainObj.setDict(this);
		hasMainObject = true;
		objTemplateIndex = 1;
	
    }

    public void setObjTemplate(LabObject template)
    {
		if(hasObjTemplate){
			objects.set(objTemplateIndex, 
						lBook.store(template));
		} else if(!hasObjTemplate){
			objects.insert(objTemplateIndex,
						   lBook.store(template));
		}
		hasObjTemplate = true;
    }

    public LabObject getObjTemplate()
    {
		if(hasObjTemplate){
			return getObj((LabObjectPtr)(objects.get(0)));
		}

		return null;
    }
    
    public void add(LabObject lObj)
    {
		insert(lObj, objects.getCount());
    }

    public void insert(LabObjectPtr lObjPtr, int index)
    {
		LabObject newObj = lBook.load(lObjPtr);
		if(newObj instanceof LObjDictionary){
			objects.insert(index, lObjPtr);
		} else if(newObj instanceof LObjSubDict){
			LObjSubDict newSD = (LObjSubDict)newObj;
			if(newSD.dict == null){
				// we might just have to create the dictionary here.
				// but theorectially this shouldn't happen.
				// but maybe the user doesn't want a dictionary 
				// for this sub dict (see LObjDataSet)
				// so in this case just insert the subdict
				objects.insert(index, lObjPtr);
			} else {
				objects.insert(index, lBook.store(newSD.dict));
			}
		} else {
			objects.insert(index, lObjPtr);
		}
		lBook.store(this);
    }

    public void insert(LabObject node, int index)
    {
		insert(lBook.store(node), index);
    }

    public void insert(TreeNode node, int index)
    {
		if(node instanceof LObjDictionary){
			insert((LabObject)node, index);
		} else if(node instanceof LabObjectPtr){
			insert((LabObjectPtr)node, index);
		} else {
			Debug.println("Weirdness is happening");
		}
    }

	public void removeAll()
	{
		objects = new Vector();
		store();
	}

    public void remove(TreeNode node)
    {
		Debug.println("Removing node");
		int index = getIndex(node);
		if(index >= 0 && index < objects.getCount()){
			objects.del(index);
			store();
			// Should tell the labbook we don't care about this obj any more
		}
    }

    public void remove(int index)
    {
		if(index < 0 || index >= objects.getCount()) return;
	
		objects.del(index);
		store();
    }

    public int getChildCount()
    {
		return objects.getCount();
    }

    public void readExternal(DataStream ds)
    {
		objects = new Vector();
		int i;
		short flags = ds.readShort();

		viewType = (flags & 0x0F);

		hideChildren = ((flags & 0x010) == 0x010?true:false);
		hasMainObject = ((flags & 0x020) == 0x020?true:false);
		hasObjTemplate = ((flags & 0x040) == 0x040?true:false);

		short size = ds.readShort();
	
		for(i=0; i<size; i++){
			LabObjectPtr ptr = LabObjectPtr.readExternal(ds);
			objects.add(ptr);
			Debug.println(" Reading: " + ptr.debug());
		}

    }

    public void writeExternal(DataStream ds)
    {
		int i;
		int size = objects.getCount();

		short flags = (short)viewType;
		flags = hideChildren?(short)(flags|0x010):flags;
		flags = hasMainObject?(short)(flags|0x020):flags;
		flags = hasObjTemplate?(short)(flags|0x040):flags;
		ds.writeShort(flags);

		ds.writeShort(size);
		for(i=0; i<size; i++){
			LabObjectPtr ptr = (LabObjectPtr)objects.get(i);
			ptr.writeExternal(ds);
			Debug.println(" Writing: " + ptr.debug());
		}
    }

    public TreeNode getChildAt(int index)
    {
		if(index < 0 || index >= objects.getCount()) return null;

		LabObjectPtr ptr = (LabObjectPtr)(objects.get(index));
		if(!lBook.readHeader(ptr)) return null;

		if(ptr.objType == DefaultFactory.DICTIONARY){
			LObjDictionary newDict = (LObjDictionary)lBook.load(ptr);
			return (TreeNode)newDict;
		} else if(hasMainObject && index == 0){
			//			((LObjSubDict)obj).setDict(this);
			ptr.name = "..main_obj..: " + ptr.name;
		}

		return ptr;
    }

    public static TreeNode getNode(LabObject obj)
    {

		if(obj instanceof LObjDictionary){
			return (TreeNode)obj;
		} else if(obj instanceof LObjSubDict){
			LObjSubDict newObj = (LObjSubDict)obj;
			
			// we'll assume this object has been initiaizled
			if(newObj.dict != null){
				return newObj.dict;
			} 
		} 

		// default
		LabObjectPtr ptr = lBook.store(obj);
		ptr.name = obj.getName();
		return ptr;
    }

    public boolean hideChildren = false;
    public static boolean globalHide = true;
    public static LabObjectPtr emptyChild = new LabObjectPtr("..empty..");

    public boolean isLeaf()
    {
		return (hideChildren && globalHide);
    }

    public TreeNode [] childArray()
    {
		TreeNode [] children;
		int numObjs = objects.getCount();

		if(numObjs == 0){
		    children = new TreeNode [1];
		    children[0] = emptyChild;
		    return children;
		}
	
		children = new TreeNode [numObjs];
		for(int i=0; i<numObjs; i++){
		    LabObjectPtr ptr = ((LabObjectPtr)objects.get(i));		
			if(!lBook.readHeader(ptr)){
				// This is a null object
				ptr.name = "..null_object..";
				children[i] = ptr;
				continue;
			} 

			if(ptr.objType == DefaultFactory.DICTIONARY){
				LObjDictionary newDict = (LObjDictionary)lBook.load(ptr);
				children[i] = (TreeNode)newDict;
		    } else if(hasMainObject && 
					  i == 0){
				ptr.name = "..main_obj..: " + ptr.name;
				children[i] = ptr;
		    } else {
				children[i] = ptr;
		    }
		}
		return children;
	}

    public LabObject getObj(TreeNode node)
    {
		if(node instanceof LObjDictionary){
			LObjDictionary newDict = (LObjDictionary)node;
			if(newDict.hasMainObject){
				LObjSubDict mainObj = newDict.getMainObj();
				return mainObj;
			}
			return (LabObject)node;
		} else if(node instanceof LabObjectPtr){
			LabObject obj = lBook.load((LabObjectPtr)node);
			if(hasMainObject &&
			   obj instanceof LObjSubDict &&
			   ((LabObjectPtr)(objects.get(0))).equals(node)){
				((LObjSubDict)obj).setDict(this);
			}
			return obj;
		}
		return null;
    }

    /*
     * Very inefficient right now
     */
    public int getIndex(TreeNode node)
    {
		LabObjectPtr ptr;
		LabObjectPtr curPtr;

		if(node instanceof LabObjectPtr){
			ptr = (LabObjectPtr)node;
		} else if(node instanceof LObjDictionary){
			ptr = ((LObjDictionary)node).ptr;
		} else{
			Debug.println("Weirdness in getIndex");
			return -1;
		}
	
		int numObjs = objects.getCount();
	
		Debug.println("getIndex searching " + numObjs + " objects");
		for(int i=0; i<numObjs; i++){
			curPtr = (LabObjectPtr)objects.get(i);
			Debug.println(" Checking node: " + curPtr.debug());
			if(ptr.equals(curPtr)){
				return i;
			} 
		}
	
		return -1;
    }

    public LabObject copy()
    {
		Debug.println("Copying a dictionary");
		LObjDictionary me = DefaultFactory.createDictionary();
		if(hasMainObject){
			Debug.println("  adding mainObject");
			LObjSubDict oldMO = getMainObj();
			LObjSubDict newMO = (LObjSubDict)oldMO.copy();
			// the order of the mainObj and setDict seems to matter???
			me.setMainObj(newMO);
	    
		}
		me.viewType = viewType;
		return me;
    }

    public TreeNode [] parentArray(){return null;}

    public void addParent(TreeNode parent){}

    public String toString()
    {
		if(getName() == null) return "..null_name..";
		return getName();
		
    }
}
