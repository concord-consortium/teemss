package org.concord.LabBook;

import waba.ui.*;
import waba.util.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;

public abstract class LabObject
{
    private String name = null;
    public LabObjectPtr ptr;
    int objectType = -1;
	public LabObjectFactory factory;
    public static LabBook lBook;
	private int refCount = 0;

	public LabObject(int type)
	{
		objectType = type;
	}

	public String getName(){ return name; }
	public void setName(String name){ this.name = name; }

	public void init(){}

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

	public LabObjectView getMinimizedView()
	{
		return new LObjMinimizedView(this);
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

    public void store(){lBook.store(this);}

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
