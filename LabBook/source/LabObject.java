package org.concord.LabBook;

import waba.ui.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;

public abstract class LabObject
{
    final public static int DICTIONARY =1;
    final public static int FORM = 2;
    final public static int DATA_SET = 3;
    final public static int GRAPH = 4;
    final public static int DATA_CONTROL = 5;
    final public static int DOCUMENT = 6;
    final public static int OUTPUT_SET = 7;
    final public static int QUESTION = 8;
    final public static int DRAWING = 9;
    final public static int UCONVERTOR = 10;
    final public static int IMAGEOBJ = 11;
    final public static int DATASOURCEOBJ = 12;

    public static String [] typeNames = {
	"Zero", "Dict", "Form", "DataSet", "Graph", "DataControl", "Doc", "OutputSet", "Quest","Drawing","UnitConvertor","Image","DataSource"};

    public static LabObject getNewObject(int objectType)
    {
	switch(objectType){
	case DICTIONARY:
	    return new LObjDictionary();
	case FORM:
	    return new LObjForm();
	case DATA_SET:
	    return new LObjDataSet();
	case GRAPH:
	    return new LObjGraph();
	case DATA_CONTROL:
	    return new LObjDataControl();
	case DOCUMENT:
	    return new LObjDocument();
	case OUTPUT_SET:
	    return new LObjOutputSet();
	case QUESTION:
	    return new LObjQuestion();
	case DRAWING:
	    return new LObjDrawing();
	case UCONVERTOR:
	    return new LObjUConvertor();
	case IMAGEOBJ:
	    return new LObjImage();
	case DATASOURCEOBJ:
		return new LObjDataSource();
	}

	return null;
    }

    public String type;
    public String name = null;
    public LabObjectPtr ptr;
    int objectType = -1;
    public static LabBook lBook;

    public void readExternal(DataStream in)
    {
	name = in.readString();
	if(name.equals("_null_name_")) name = null;
	Debug.println("Reading " + name + " " + typeNames[objectType]);

    }

    public void writeExternal(DataStream out)
    {
	if(name == null){
	    out.writeString("_null_name_");
	    Debug.println("Writing noname " + typeNames[objectType]);
	} else {
	    Debug.println("Writing " + name + " " + typeNames[objectType]);
	    out.writeString(name);
	}

    }

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
	  
    public LabObjectView getView(LObjViewContainer vc, boolean edit)    
    {
	return getView(vc, edit, null);
    }

    public LabObjectView getView(LObjViewContainer vc, boolean edit, 
				 LObjDictionary curDict){
	return null;
    }

    public LabObject copy(){return null;}

    public void store(){lBook.store(this);}
}
