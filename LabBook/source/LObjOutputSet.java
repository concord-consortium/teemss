import waba.util.*;
import waba.ui.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;
import extra.ui.*;

public class LObjOutputSet extends LObjSubDict
{
    LabObject curOutput = null;
    LabObject mainObject = null;
    LObjDictionary outputDict = null;
    
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
	me.dict = new LObjDictionary();
	me.dict.mainObject = me;
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

    public LabObjectView getView(boolean edit)
    {
	if(mainObject == null) return null;

	if(outputDict == null ||
	   outputDict.getChildCount() == 0 ||
	   edit || 
	   skipChoice){
	    skipChoice = false;
	    return mainObject.getView(edit);
	}

	return new LObjOutputSetChoice(this);
		
    }

    public void writeExternal(DataStream out)
    {
	super.writeExternal(out);
	
    }

    public void readExternal(DataStream in)
    {
	super.readExternal(in);
	curOutput = getObj(0);
	mainObject = getObj(1);
	outputDict = (LObjDictionary)getObj(2);
    }

}
