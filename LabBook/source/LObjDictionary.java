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
	objectType = DICTIONARY;

    }

    public LabObjectView getPropertyView(LObjViewContainer vc, LObjDictionary curDict)
    {
		if(hasMainObject){
	    	LObjSubDict mo = getMainObj();
	   	 	if(mo != null){
				return mo.getPropertyView(vc, curDict);
	    	} else return null;
		}
		return null;
    }
    public LabObjectView getView(LObjViewContainer vc, boolean edit, LObjDictionary curDict)
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
	    return (LObjSubDict)(getObj((LabObjectPtr)(objects.get(0))));
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
	objects.add(lBook.store(lObj));
	lBook.store(this);
    }

    public void insert(LabObjectPtr lObj, int index)
    {
	objects.insert(index, lObj);
	lBook.store(this);
    }

    public void insert(LabObject node, int index)
    {
	objects.insert(index, lBook.store(node));
	lBook.store(this);
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

    public void remove(TreeNode node)
    {
	Debug.println("Removing node");
	int index = getIndex(node);
	objects.del(index);
	lBook.store(this);
	// Should tell the labbook we don't care about this obj any more
    }

    public void remove(int index)
    {
	if(index < 0 || index >= objects.getCount()) return;
	
	objects.del(index);
	lBook.store(this);
    }

    public int getChildCount()
    {
	return objects.getCount();
    }

    public void readExternal(DataStream ds)
    {
	objects = new Vector();
	int i;
	super.readExternal(ds);
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
	super.writeExternal(ds);

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
	LabObject obj = lBook.load(ptr);
	if(obj instanceof LObjDictionary){ 
	    return (TreeNode)obj;
	} else {
	    return ptr;
	}
    }

    public static TreeNode getNode(LabObject obj)
    {

	if(obj instanceof LObjDictionary){
	    return (TreeNode)obj;
	} else {
	    LabObjectPtr ptr = lBook.store(obj);
	    ptr.name = obj.name;
	    return ptr;
	}	
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
		    LabObject obj = lBook.load(ptr);
		    if(obj instanceof LObjDictionary){
				children[i] = (TreeNode)obj;
		    } else if(obj instanceof LObjSubDict &&
			          hasMainObject){
				((LObjSubDict)obj).setDict(this);
				ptr.name = "..main_obj..";
				children[i] = ptr;
		    } else {
				if(obj == null){
			    	Debug.println("childArray: Null Object");
			    	ptr.name = "..null..";
				} else {
			    	ptr.name = obj.name;
				}
				children[i] = ptr;
		    }
		}
		return children;
     }

    public LabObject getObj(TreeNode node)
    {
	if(node instanceof LabObject){
	    return (LabObject)node;
	} else if(node instanceof LabObjectPtr){
	    LabObject obj = lBook.load((LabObjectPtr)node);
	    if(hasMainObject &&
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
	LObjDictionary me = new LObjDictionary();
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
	if(name == null) return "..Null..";
	return name;
    }

}
