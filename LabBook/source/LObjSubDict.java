package org.concord.LabBook;

import extra.io.*;

public abstract class LObjSubDict extends LabObject
{
    LObjDictionary dict = null;

	public LObjSubDict(int type)
	{
		super(type);
	}

	/*
	 * Only call this the first time the sub object is instantiated
	 * This should no be called when the object is de-serialized
	 */
    public void init(){
    	super.init();
		dict = DefaultFactory.createDictionary();
		dict.hideChildren = true;
    }

	/*
	 * This is a bit tricky this is called the first time the object
	 * is stored in the labBook this is the first time the object
	 * has a valid pointer
	 */
	public void firstStore(LabBookSession session)
	{
		if(dict !=  null){
			session.storeNew(dict);
			dict.setMainObj(this);
		}
	}
    
	LObjDictionary getDict(){return dict;}
    void setDict(LObjDictionary d){
		dict = d;
    }

	public LabObjectPtr getVisiblePtr()
	{ 
		LabObjectPtr ptr;
		if(dict != null){
			ptr = dict.getVisiblePtr();
			return ptr;
		} else {
			ptr =  super.getVisiblePtr(); 
			return ptr;
		}
	}

	public LabObjectView getMinimizedView()
	{
		return new LObjMinimizedView(this);
	}

    public LabObjectView getPropertyView(ViewContainer vc,LObjDictionary curDict,
										 LabBookSession session){
		return null;
    }

	public void setName(String name)
	{
		super.setName(name);
		if(dict != null){
			dict.setName(name);
		}
	}

    public void store()
    {
		if(dict != null){
			dict.setName(getName());
			dict.store();
		}
		super.store();
    }

    // Notice for this to work correctly the dictionary needs to be
    // loaded before the this object is

	public int getNumObjs()
	{
		return (dict == null)?0:dict.getChildCount() - 1;
	}

    public void setObj(LabObject obj, int id)
    {
		// This assumes the dict doesn't have a template
		id++;
		if(dict == null) return;
	
		if(dict.getChildCount() <= id){
			for(int i=dict.getChildCount(); i<id; i++){
				dict.add(null);
			}
			dict.insert(obj, id);
		} else {
			Debug.println("Removing #" + id + " from dict: " + dict.getName());
			dict.remove(id);
			dict.insert(obj, id);
		}

    }

    public LabObject getObj(int id, LabBookSession s)
    {
		// we should never be calling this if the dict 
		// or session so we won't check and if there is a 
		// a null exception then you know you need to fix it
		// if(dict == null || s == null) return null;

		// This assumes the dictionary doesn't have a template
		id++;
		LabObjectPtr ptr = dict.getChildAt(id);
		if(ptr == null){
			return null;
		}
		LabObject obj = s.getObj(ptr);
		if(obj == null) Debug.println("Got null obj from subDict");

		return obj;
    }

}
