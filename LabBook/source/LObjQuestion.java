package org.concord.LabBook;

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
		super(DefaultFactory.QUESTION);
    }

    public static LObjOutputSet makeNewQuestionSet()
    {
		LObjOutputSet output = DefaultFactory.createOutputSet();

		LObjDictionary qsDict = DefaultFactory.createDictionary();
		qsDict.name = "Questions";
		qsDict.viewType = qsDict.PAGING_VIEW;
		output.setMainObject((LabObject)qsDict);

		LObjQuestion me = DefaultFactory.createQuestion();
		me.setOutputSet(output);
		qsDict.setObjTemplate(me);

		return output;
    }

    public LabObjectView getView(ViewContainer vc, boolean edit, LObjDictionary curDict)
    {
		outputSet = (LObjOutputSet)getObj(0);
		questionText = (LObjDocument)getObj(1);

		if(questionType == MULTIPLE_CHOICE){
			options = (LObjDictionary)getObj(2);
		}	

		if(edit){
			return new LObjQuestionEditView(vc, this);
		} else {
			return new LObjQuestionView(vc, this);
		}
    }

    public void writeExternal(DataStream out)
    {	
		out.writeInt(questionType);
	
		// This is dangerous if we've been commited
		if(outputSet != (LObjOutputSet)getObj(0) ||
		   questionText != (LObjDocument)getObj(1) ||
		   (questionType == MULTIPLE_CHOICE &&
			options != (LObjDictionary)getObj(2))){
			Debug.println("Question Dict setup wrong");
		}	
    }

    public void readExternal(DataStream in)
    {
		questionType = in.readInt();
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
		if(options != null) setObj(options, 2);
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
			questionText = DefaultFactory.createDocument();
			questionText.name = "Text";
			setObj(questionText, 1);
		}
		return questionText;
    }
    
    public LabObject copy()
    {
		LObjQuestion me = DefaultFactory.createQuestion();
		me.outputSet = outputSet;	
		return me;
    }
}
