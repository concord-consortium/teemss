package org.concord.LabBook;

import waba.ui.*;
import waba.util.*;
import org.concord.waba.extra.io.*;
import org.concord.waba.extra.util.*;

public abstract class LabObject
{
    private String name = null;
    LabObjectPtr ptr;
    short objectType = -1;
	public LabObjectFactory factory;
    public static LabBook lBook;
	private int refCount = 0;
	private boolean locked = false;
	private short version = 0;

	public LabObject(int type)
	{
		objectType = (short)type;
	}

	public LabObjectPtr getVisiblePtr()
	{ 
		ptr.name = name;
		return ptr; 
	}

	public String getName(){ return name; }
	public void setName(String name){ this.name = name; }

	public void init(){}

	public final static short FLAG_LOCKED = (short)0x8000;
	public void setFlags(short flags)
	{
		locked = (flags & FLAG_LOCKED) != 0;
	}

	public short getFlags()
	{
		short flags = 0;
		flags = locked?(short)(flags | FLAG_LOCKED):flags;

		return flags;
	}

	public void setVersion(short version)
	{
		this.version = version;
	}
	public short getVersion()
	{
		return version;
	}

    public abstract void readExternal(DataStream in);

    public abstract void writeExternal(DataStream out);

    public boolean equals(TreeNode node)
    {
		if(node == this) return true;
		if(node instanceof LabObject){	    
			LabObject obj = (LabObject)node;
			return obj.ptr != null && ptr != null &&
				ptr.equals(obj.ptr);
		}
		
		return false;
    }

    public LabObjectView getView(ViewContainer vc, boolean edit, 
				 				 LObjDictionary curDict, boolean embeddedState,
								 LabBookSession session){
		LabObjectView view = getView(vc,edit,curDict,session);
		if(view != null) view.setEmbeddedState(embeddedState);
		
		return view;
	}
	  
    public LabObjectView getView(ViewContainer vc, boolean edit,
								 LabBookSession session)    
    {
		return getView(vc, edit, null, session);
    }

    public LabObjectView getView(ViewContainer vc, boolean edit, 
				 				 LObjDictionary curDict, LabBookSession session){
		return null;
    }

    public LabObjectView getPropertyView(ViewContainer vc, 
										 LabBookSession session)    
    {
		return getPropertyView(vc, null, session);
    }

    public LabObjectView getPropertyView(ViewContainer vc,
										 LObjDictionary curDict,
										 LabBookSession session){
		return null;
    }

    public LabObject copy(){return null;}

	/*
	 * This is a bit tricky this is called the first time the object
	 * is stored in the labBook this is the first time the object
	 * has a valid pointer
	 */
	public void firstStore(LabBookSession session)
	{
		lBook.store(this);
	}

    public void store()
	{
		lBook.store(this);
	}

	public void storeNow()
	{
		LabObjectPtr ptr = lBook.store(this);
		if(ptr != null){
			lBook.commit(ptr);
		} 
	}

	public int incRefCount(){ return ++refCount; }
	public int getRefCount(){ return refCount; }
	public int release()
	{
		if(refCount > 0){		   
			refCount--;
			if(refCount == 0) lBook.release(this);
			return refCount;
		} else {
			// Error
			return -1;
		}
	}

	Vector objListeners = null;
	public void addLabObjListener(LabObjListener l)
	{
		if(objListeners == null) objListeners = new Vector();
		objListeners.add(l);
	}

	public void delLabObjListener(LabObjListener l)
	{
		int index = objListeners.find(l);
		if(index >= 0) objListeners.del(index);
	}

	public void notifyObjListeners(LabObjEvent e)
	{
		if(objListeners == null) return;
		for(int i=0; i<objListeners.getCount(); i++){
			LabObjListener l = (LabObjListener)objListeners.get(i);
			if(l != null) l.labObjChanged(e);
		}
	}
}
