import waba.ui.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;

public abstract class LabObject implements TreeNode
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

    public static String [] typeNames = {
	"Zero", "Dict", "Form", "DataSet", "Graph", "DataControl", "Doc", "OutputSet", "Quest","Drawing"};

    public static LabObject getNewObject(int objectType)
    {
	switch(objectType){
	case DICTIONARY:
	    return (LabObject)new LObjDictionary();
	case FORM:
	    return (LabObject)new LObjForm();
	case DATA_SET:
	    return (LabObject)new LObjDataSet();
	case GRAPH:
	    return (LabObject)new LObjGraph();
	case DATA_CONTROL:
	    return (LabObject)new LObjDataControl();
	case DOCUMENT:
	    return (LabObject)new LObjDocument();
	case OUTPUT_SET:
	    return (LabObject)new LObjOutputSet();
	case QUESTION:
	    return (LabObject)new LObjQuestion();
	case DRAWING:
	    return (LabObject)new LObjDrawing();
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

    public TreeNode [] childArray()
    {
	return null;
    }

    public TreeNode [] parentArray(){return null;}

    public int getIndex(TreeNode node){return -1;}

    public TreeNode getChildAt(int index){return null;}

    public boolean isLeaf(){return true;}

    public void insert(TreeNode child, int index){}

    public void addParent(TreeNode parent){}

    public void remove(int index){}

    public void remove(TreeNode node){}

    public int getChildCount(){return 0;}

    public String toString()
    {
	if(name == null) return "..Null..";

	return name;
    }

    public LabObjectView getView(LObjViewContainer vc, boolean edit){return null;}

    public LabObject copy(){return null;}
}
