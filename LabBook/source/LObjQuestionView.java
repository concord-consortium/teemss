package org.concord.LabBook;

import waba.ui.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import extra.ui.*;

public class LObjQuestionView extends LabObjectView
{
    LObjQuestion quest = null;

    RelativeContainer edit = new RelativeContainer();
 
    Button doneButton;

    LabObjectView doc;

    public LObjQuestionView(LObjViewContainer vc, LObjQuestion lq)
    {
	super(vc);
	quest = lq;
	lObj = (LabObject)lObj;
	Debug.println("Opening QV for: " + lq.name);
    }

    public void onEvent(Event e)
    {
	if(e.target == doneButton &&
		  e.type == ControlEvent.PRESSED){
	    if(container != null){
		container.done(this);
	    }	    
	}
    }

    Radio [] radios;
    LabObjectView [] options;
    LabObjectView essay;

    public void layout(boolean sDone)
    {
	if(didLayout) return;
	didLayout = true;

	showDone = sDone;

	if(quest.outputSet.curOutput != null){
	    // There should be some answers for this question
	    int index = ((LObjDictionary)(quest.outputSet.mainObject)).getIndex(quest.dict);
	    Debug.println("Setting answer for " + quest.dict.name +  " #" + index + " in dict: " + 
			       (LObjDictionary)(quest.outputSet.mainObject));
	    answer = (LabObject)(((LObjDictionary)(quest.outputSet.curOutput)).getChildAt(index));
	    Debug.println(" Got answer: " + answer);
	}

	doc = quest.getQuestionText().getView(null, true);
	if(doc instanceof LObjDocumentView) ((LObjDocumentView)doc).showName = false;
	doc.layout(false);
	add(doc);

	switch(quest.questionType){
	case quest.TRUE_FALSE:
	    radios = new Radio [2];
	    radios[0] = new Radio("True");
	    add(radios[0]);
	    radios[1] = new Radio("False");
	    add(radios[1]);
	    if(answer != null &&
	       answer instanceof LObjDocument){
		if(((LObjDocument)answer).text.equals("True")){
		    radios[0].setChecked(true);
		} else {
		    radios[1].setChecked(true);
		}
	    }
	    break;
	case quest.MULTIPLE_CHOICE:
	    if(quest.options != null){
		TreeNode [] answers = null;
		TreeNode [] choices = quest.options.childArray();
		if(answer != null &&
		   answer instanceof LObjDictionary){
		    answers = ((LObjDictionary)answer).childArray();
		}
		radios = new Radio[choices.length];
		options = new LabObjectView[choices.length];
		for(int i=0; i<choices.length; i++){
		    radios[i] = new Radio("");
		    add(radios[i]);
		    options[i] = ((LabObject)(choices[i])).getView(null, false);
		    if(options[i] instanceof LObjDocumentView) ((LObjDocumentView)options[i]).showName = false;
		    options[i].layout(false);
		    if(answers != null){
			for(int j=0; j<answers.length; j++){
			    if(choices[i] == answers[j]){
				radios[i].setChecked(true);
				break;
			    }
			}
		    }
		    add(options[i]);
		}
	    }
	    break;
	case quest.ESSAY:
	    if(answer != null &&
	       answer instanceof LObjDocument){
		essay = answer.getView(null, true);
	    } else {		
		essay = ((new LObjDocument()).getView(null, true));
	    }
	    ((LObjDocumentView)essay).showName = false;
	    add(essay);
	    break;
	}

	if(showDone){
	    doneButton = new Button("Done");
	    add(doneButton);
	}

    }

    public void setRect(int x, int y, int width, int height)
    {
	super.setRect(x,y,width,height);
	if(!didLayout) layout(false);

	int curY = 1;
	doc.setRect(1, curY, width, height/2);
	
	curY += height/2;

	switch(quest.questionType){
	case quest.TRUE_FALSE:
	    radios[0].setRect(1,curY, width-2, 16);
	    curY += 16;
	    radios[1].setRect(1, curY, width-2, 16);
	    curY += 16;
	    break;
	case quest.MULTIPLE_CHOICE:
	    if(quest.options != null){
		int optHeight;
		for(int i=0; i<radios.length; i++){
		    optHeight = ((LObjDocumentView)options[i]).getHeight();
		    radios[i].setRect(1,curY, 15, optHeight);
		    options[i].setRect(16, curY, width - 20, optHeight);
		    curY += optHeight;
		}
	    }
	    break;
	case quest.ESSAY:
	    essay.setRect(1,curY, width-2, height - curY - 16);
	    break;
	}

	if(showDone){
	    doneButton.setRect(width-30,height-15,30,15);
	}

    }

    LabObject answer = null;

    public void close()
    {
	LObjDictionary tmp;
	Debug.println("Got close in quest");
	// Check if answered.  maybe pull up dialog

	// create answer dict if null
	doc.close();

	if(quest.outputSet.outputDict == null){
	    tmp = new LObjDictionary();
	    tmp.name = "Answers";
	    quest.outputSet.setOutputDict(tmp);
	}

	if(quest.outputSet.curOutput == null){
	    tmp = new LObjDictionary();
	    tmp.name = "Answers" + (quest.outputSet.outputDict.getChildCount() + 1);
	    tmp.hideChildren = true;
	    quest.outputSet.newCurOutput(tmp);
	}

	switch(quest.questionType){
	case quest.TRUE_FALSE:
	    // make document for true or false
	    if(answer == null){		
		answer =  new LObjDocument();
		answer.name = "*";
		((LObjDictionary)(quest.outputSet.curOutput)).add(answer);
	    }
	    if(radios[0].getChecked()){
		((LObjDocument)answer).setText("True");
	    } else {
		((LObjDocument)answer).setText("False");
	    }
	    break;
	case quest.MULTIPLE_CHOICE:
	    if(quest.options != null){
		if(answer == null){		
		    answer =  new LObjDictionary();
		    answer.name = "*";
		    ((LObjDictionary)(quest.outputSet.curOutput)).add(answer);
		}
		
		for(int i=0; i<radios.length; i++){
		    if(radios[i].getChecked()){
			((LObjDictionary)answer).add(options[i].getLabObject());
		    }

		    options[i].close();
		    // save link to selected option
		}
	    }
	    break;
	case quest.ESSAY:
	    if(answer == null){		
		answer = (LabObject) essay.getLabObject();
		answer.name = "*";
		((LObjDictionary)(quest.outputSet.curOutput)).add(answer);
	    }	    
	    essay.close();
	    break;
	}

    }

}
