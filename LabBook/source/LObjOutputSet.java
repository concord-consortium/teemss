package org.concord.LabBook;

import waba.util.*;
import waba.ui.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;

public class LObjOutputSet extends LObjSubDict
    implements LObjViewContainer
{
    LabObject curOutput = null;
    LabObject mainObject = null;
    LObjDictionary outputDict = null;
    
    LObjViewContainer mObjCont = null;

    public boolean skipChoice = false;


    /*
     * need to figure out object loading and storing :(
     */
    public LObjOutputSet()
    {
	objectType = OUTPUT_SET;
    }

    public void setOutputDict(LObjDictionary outDict)
    {
	outputDict = outDict;
	setObj(outDict, 2);
    }

    public static LObjOutputSet makeNew()
    {
	LObjOutputSet me = new LObjOutputSet();
	LObjDictionary dict = new LObjDictionary();
	dict.setMainObj(me);
	dict.name = "OutputSet";
	me.name = "OutputSet_obj";
	dict.hideChildren = true;
	return me;
    }
    public LObjOutputSet(boolean dud){
	this();
	
    }

    public void setMainObject(LabObject obj)
    {
	mainObject = obj;
	setObj(obj, 1);
    }

    public void setCurOutput(LabObject obj)
    {
	curOutput = obj;
	setObj(obj, 0);
    }

    public void newCurOutput(LabObject obj)
    {
	curOutput = obj;
	setObj(obj, 0);
	if(outputDict != null){
	    outputDict.add(obj);
	}
    }

    public LabObjectView getView(LObjViewContainer vc, boolean edit, LObjDictionary curDict)
    {
	if(mainObject == null) return null;

	if(outputDict == null ||
	   outputDict.getChildCount() == 0 ||
	   edit || 
	   skipChoice){
	    skipChoice = false;
	    mObjCont = vc;
	    return mainObject.getView(this, edit);
	}

	return new LObjOutputSetChoice(vc, this);
		
    }

    public void addMenu(LabObjectView source, Menu menu){mObjCont.addMenu(source, menu);}

    public void delMenu(LabObjectView source, Menu menu){mObjCont.delMenu(source, menu);}

    public void done(LabObjectView source)
    {
	// popup save output menu
	mObjCont.done(source);
    }
    
    public void reload(LabObjectView source){mObjCont.reload(source);}

    public LObjDictionary getDict(){return mObjCont.getDict();}

    public void writeExternal(DataStream out)
    {
	super.writeExternal(out);
	
    }

    public void readExternal(DataStream in)
    {
	super.readExternal(in);
    }

    public void setDict(LObjDictionary dict)
    {
	super.setDict(dict);
	curOutput = getObj(0);
	mainObject = getObj(1);
	outputDict = (LObjDictionary)getObj(2);
    }

}
