package org.concord.LabBook;

import extra.io.*;

public abstract class LObjSubDict extends LabObject
{
    LObjDictionary dict = null;

	/*
	 * Only call this the first time the sub object is instantiated
	 * if it is instanciated from the file 
	 */
    public void initSubDict()
    {
		dict = new LObjDictionary();
		dict.setMainObj(this);
		dict.hideChildren = true;
		dict.store();
    }

    public void setDict(LObjDictionary d){
		dict = d;
    }

    public LabObjectView getPropertyView(LObjViewContainer vc,LObjDictionary curDict){
		return null;
    }
    public void store()
    {
		if(dict != null) dict.store();
		super.store();
    }

    public void writeExternal(DataStream out)
    {
		super.writeExternal(out);
    }


    // Notice for this to work correctly the dictionary needs to be
    // loaded before the this object is
    public void readExternal(DataStream in)
    {
		super.readExternal(in);
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
