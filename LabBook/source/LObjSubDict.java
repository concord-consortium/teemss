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
    
	public LObjDictionary getDict(){return dict;}

    public void setDict(LObjDictionary d){
		dict = d;
    }

    public LabObjectView getPropertyView(ViewContainer vc,LObjDictionary curDict){
		return null;
    }
    public void store()
    {
		if(dict != null){
			dict.name = name;
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
			Debug.println("Removing #" + id + " from dict: " + dict.name);
			dict.remove(id);
			dict.insert(obj, id);
		}

    }

    public LabObject getObj(int id)
    {
		if(dict == null){
			return null;
		}

		// This assumes the dictionary doesn't have a template
		id++;
		LabObject obj = dict.getObj(dict.getChildAt(id));
		if(obj == null) Debug.println("Got null obj from subDict");

		return obj;
    }

}
