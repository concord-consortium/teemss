package org.concord.LabBook;

import waba.ui.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.probware.probs.*;
import org.concord.waba.extra.probware.*;
import extra.ui.*;

public class LObjDataControlEditView extends LabObjectView
{
    LObjDataControl dc;
    Button doneButton;
    Choice probeChoice = null;
    Edit nameEdit = null;
    Label nameLabel = null;

    public LObjDataControlEditView(LObjViewContainer vc, LObjDataControl d)
    {
	super(vc);

	dc = d;
	lObj = dc;
    }
    
    public void layout(boolean sDone)
    {
	if(didLayout) return;
	didLayout = true;

	showDone = sDone;

	nameLabel = new Label("Name");
	nameEdit = new Edit();
	nameEdit.setText("" + dc.dict.name);
	add(nameLabel);
	add(nameEdit);

	probeChoice = new Choice(ProbFactory.getProbNames());	
	String oldName = ProbFactory.getName(dc.probeId);
	if(oldName != null){
	    probeChoice.setSelectedIndex(oldName);
	}
	add(probeChoice);

	if(showDone){
	    doneButton = new Button("Done");
	    add(doneButton);
	} 
    }

    public void setRect(int x, int y, int width, int height)
    {
	super.setRect(x,y,width,height);
	if(!didLayout) layout(false);

	nameLabel.setRect(1, 2, 30, 15);
	nameEdit.setRect(35, 2, width-40, 15);

	probeChoice.setRect(3,30, width-7, 15);

	if(showDone){
	    doneButton.setRect(width-30,height-15,30,15);
	} 
    }

    public void close()
    {
	Debug.println("Got close in document");
	dc.probeId = ProbFactory.getIndex(probeChoice.getSelected());
	dc.curProbe = null;
	if(nameEdit.getText() != "" && nameEdit.getText() != null){
	    dc.dict.name = nameEdit.getText();
	}
	dc.lBook.store(dc);
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
}