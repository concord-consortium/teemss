import waba.util.*;
import waba.ui.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;
import extra.ui.*;

public class LObjQuestion extends LObjSubDict
{
    public final static int TRUE_FALSE = 0;
    public final static int MULTIPLE_CHOICE = 1;
    public final static int ESSAY = 2;

    public int questionType = 0;

    LObjOutputSet outputSet;
    LObjDocument questionText = null;

    LObjDictionary options;

    public LObjQuestion()
    {
	objectType = QUESTION;
    }

    public static LObjDictionary makeNewQuestionSet()
    {
	LObjQuestion me = new LObjQuestion();
	me.dict = new LObjDictionary();
	me.dict.mainObject = me;

	LObjOutputSet output = LObjOutputSet.makeNew();
	output.name = "OutputSet_obj";

	LObjDictionary qsDict = new LObjDictionary();
	qsDict.name = "Questions";
	qsDict.viewType = qsDict.PAGING_VIEW;
	output.setMainObject((LabObject)qsDict);
	me.setOutputSet(output);
	qsDict.newObjectTemplate = me.dict;

	return me.outputSet.dict;
    }

    public LabObjectView getView(LObjViewContainer vc, boolean edit)
    {
	if(edit){
	    return new LObjQuestionEditView(vc, this);
	} else {
	    return new LObjQuestionView(vc, this);
	}
    }

    public void writeExternal(DataStream out)
    {	
	super.writeExternal(out);
	out.writeInt(questionType);
	
	if(outputSet != (LObjOutputSet)dict.getChildAt(0) ||
	   questionText != (LObjDocument)dict.getChildAt(1) ||
	   (questionType == MULTIPLE_CHOICE &&
	    options != (LObjDictionary)dict.getChildAt(2))){
	    Debug.println("Question Dict setup wrong");
	}	
    }

    public void readExternal(DataStream in)
    {
	super.readExternal(in);
	questionType = in.readInt();

	outputSet = (LObjOutputSet)getObj(0);
	questionText = (LObjDocument)getObj(1);

	if(questionType == MULTIPLE_CHOICE){
	    options = (LObjDictionary)getObj(2);
	}	
    }

    public void setDict(LObjDictionary d)
    {
	Debug.println("Setting quest dict");
	super.setDict(d);
	if(outputSet != null){
	    Debug.println(" Set non-null outputSet");
	    setObj(outputSet, 0);
	} 
	if(questionText != null) setObj(questionText, 1);
	if(options != null) setObj(outputSet, 2);
    }

    public void setOutputSet(LObjOutputSet os)
    {
	outputSet = os;
	setObj(os, 0);

    }

    public void setOptions(LObjDictionary opts)
    {
	options = opts;
	opts.name = "Choices";
	setObj(opts, 2);
    }

    // This is based on the questionSets curOutput object
    public LObjDocument getAnswerDoc()
    {
	return null;
    }

    public LObjDocument getQuestionText()
    {
	if(questionText == null){
	    // Need
	    questionText = new LObjDocument();
	    questionText.name = "Text";
	    setObj(questionText, 1);
	}
	return questionText;
    }
    
    public LabObject copy()
    {
	LObjQuestion me = new LObjQuestion();
	me.outputSet = outputSet;	
	return (LabObject)me;
    }
}
