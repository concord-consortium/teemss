package org.concord.LabBook;

import waba.util.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;

public class LObjDictionary extends LabObject
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

    public LabObjectView getPropertyView(ViewContainer vc, LObjDictionary curDict,
										 LabBookSession session)
    {
		if(hasMainObject){
	    	LObjSubDict mo = getMainObj(session);
	   	 	if(mo != null){
				return mo.getPropertyView(vc, curDict,session);
	    	} else return null;
		} else {
			return new LObjDictionaryProp(vc, this);
		}
    }

    public LabObjectView getView(ViewContainer vc, boolean edit, LObjDictionary curDict,
								 LabBookSession session)
    {
		if(hasMainObject){
	    	LObjSubDict mo = getMainObj(session);
	   	 	if(mo != null){
				return mo.getView(vc, edit, curDict, session);
	    	} else return null;
		}

		if(viewType == TREE_VIEW)
			return new LObjDictionaryView(vc, this, session);
		if(viewType == PAGING_VIEW)
			return new LObjDictPagingView(vc, this, edit, session);

		return null;
    }

    public LObjSubDict getMainObj(LabBookSession session)
    {
		if(hasMainObject & objects.getCount() > 0){
			LabObject obj = session.load((LabObjectPtr)objects.get(0));
			LObjSubDict mainObj = (LObjSubDict)obj;
			mainObj.setDict(this);
			return mainObj;
		}

		return null;
    }
    
    public void setMainObj(LObjSubDict mainObj)
    {
		// Assertion to check for "invaild call to setMainObj"
		LabObject assertion = mainObj.ptr.obj;

		if(hasMainObject){	    
			objects.set(0, mainObj.ptr);
		} else {
			objects.insert(0, mainObj.ptr);
		} 

		// this probably isn't necessary
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

    public LabObject getObjTemplate(LabBookSession session)
    {
		if(hasObjTemplate){
			return session.getObj((LabObjectPtr)objects.get(0));
		}

		return null;
    }
    
    public void add(LabObject lObj)
    {
		LabObjectPtr lObjPtr;
		if(lObj == null) lObjPtr = lBook.getNullObjPtr();
		else lObjPtr = lObj.getVisiblePtr();
		insert(lObjPtr, objects.getCount());
    }

    public void insert(LabObjectPtr lObjPtr, int index)
    {
		// assertion to check for invalid "null pointer insertion"
		LabObject assertion = lObjPtr.obj;

		objects.insert(index, lObjPtr);
		lBook.store(this);
    }

    public void insert(LabObject lObj, int index)
    {
		LabObjectPtr lObjPtr;
		if(lObj == null) lObjPtr = lBook.getNullObjPtr();
		else lObjPtr = lObj.getVisiblePtr();
		insert(lObjPtr, index);
    }

	public void removeAll()
	{
		objects = new Vector();
		store();
	}

    public void remove(LabObjectPtr ptr)
    {
		Debug.println("Removing ptr");
		int index = getIndex(ptr);
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

	public LabObjectPtr getChildAt(int index)
	{
		if(index < 0 || index >= objects.getCount()){
			return null;
		}
		LabObjectPtr ptr = (LabObjectPtr)(objects.get(index));
		if(!lBook.readHeader(ptr)){
			return null;
		}
		return ptr;
	}

    public boolean hideChildren = false;
    public static boolean globalHide = true;

    public LabObjectPtr [] childArray()
    {
		LabObjectPtr [] children;
		int numObjs = objects.getCount();
		if(numObjs <= 0) return null;

		children = new LabObjectPtr[numObjs];

		for(int i=0; i<numObjs; i++){
		    children[i] = ((LabObjectPtr)objects.get(i));		
			if(!lBook.readHeader(children[i])){
				// This is a null object
				ptr.name = "..null_object..";
				children[i] = ptr;
				continue;
			} 
		}
		return children;
	}


	/*
	 * this does the sub dict translation
	 */
    public int getIndex(LabObject obj)
    {
		LabObjectPtr curPtr;

		if(obj == null) return -1;

		if(obj instanceof LObjSubDict &&
		   ((LObjSubDict)obj).dict != null){
			curPtr = ((LObjSubDict)obj).dict.ptr;
		} else {
			curPtr = obj.ptr;
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

    /*
     * Very inefficient right now
     */
    public int getIndex(LabObjectPtr ptr)
    {
		LabObjectPtr curPtr;

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

			/*
			 *  This will be a bug but this stuff
			 * isn't being used right now so I think we are OK
			LObjSubDict oldMO = getMainObj();
			LObjSubDict newMO = (LObjSubDict)oldMO.copy();
			// the order of the mainObj and setDict seems to matter???
			me.setMainObj(newMO);
			*/
		}
		me.viewType = viewType;
		return me;
    }

}
