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
	 * if it is instanciated from the file 
	 */
    public void init(){
    	super.init();
		dict = DefaultFactory.createDictionary();
		dict.setMainObj(this);
		dict.hideChildren = true;
		dict.store();
    }
    
	LObjDictionary getDict(){return dict;}

    void setDict(LObjDictionary d){
		dict = d;
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
		if(dict == null){
			return null;
		}

		if(s == null){
			return null;
			//			throw new RuntimeException("null session in getObj");
		}

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
