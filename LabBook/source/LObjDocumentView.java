package org.concord.LabBook;

import waba.ui.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import extra.ui.*;

public class LObjDocumentView extends LabObjectView
{
    TextArea tArea;
    Edit nameEdit;
    RelativeContainer edit = new RelativeContainer();
 
    LObjDocument doc;
    Button doneButton;

    public LObjDocumentView(LObjViewContainer vc, LObjDocument d)
    {
	super(vc);

	doc = d;
	lObj = doc;
    }

    public boolean showName = true;
    
    public void layout(boolean sDone)
    {
	if(didLayout) return;
	didLayout = true;

	showDone = sDone;

	if(showName){
	    if(doc.name == null) doc.name = "";
	    nameEdit = new Edit();
	    nameEdit.setText(doc.name);
	    edit.add(new Label("Name"), 1, 1, 30, 15);
	    edit.add(nameEdit, 30, 1, 50, 15);
	} 
	tArea = new TextArea();
	if(doc.text != null)  tArea.setText(doc.text);
	edit.add(tArea, 1, RelativeContainer.BELOW, 
		 RelativeContainer.REST, RelativeContainer.REST);
	add(edit);
	if(showDone){
	    doneButton = new Button("Done");
	    add(doneButton);
	} 
    }

    public int getHeight()
    {
	return (tArea.getFontMetrics().getHeight() + 2) * tArea.getNumLines() + tArea.spacing*2 + 3;
    }

    public void setRect(int x, int y, int width, int height)
    {
	super.setRect(x,y,width,height);
	if(!didLayout) layout(false);

	if(showDone){
	    edit.setRect(0,0,width,height-15);
	    doneButton.setRect(width-30,height-15,30,15);
	} else {
	    edit.setRect(0,0,width,height);
	}
    }

    public void close()
    {
	Debug.println("Got close in document");
	if(showName){
	    doc.name = nameEdit.getText();
	}
	doc.text = tArea.getText();
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
