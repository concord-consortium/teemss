package org.concord.LabBook;

import waba.ui.*;
import waba.util.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.probware.probs.*;
import org.concord.waba.extra.probware.*;
import extra.ui.*;
import extra.util.*;

public class LObjDictionaryProp extends LabObjectView
{
	LObjDictionary dict;
    Button doneButton;
	Label nameLabel = new Label("Name:");
	Edit nameEdit = new Edit();
	Label viewLabel = new Label("View:");
    Choice viewChoice = null;
	String [] choiceNames = {"Tree", "Paging"};

    public LObjDictionaryProp(ViewContainer vc, LObjDictionary d)
    {
		super(vc);

		dict = d;
		lObj = d;
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

	public void layout(boolean sDone)
    {
		if(didLayout) return;
		didLayout = true;

		showDone = sDone;

		add(nameLabel);
		nameEdit.setText(dict.getName());
		add(nameEdit);

		add(viewLabel);
		viewChoice = new Choice(choiceNames);
		if(dict.viewType == dict.TREE_VIEW){
			viewChoice.setSelectedIndex(0);
		} else {
			viewChoice.setSelectedIndex(1);
		}
		add(viewChoice);

		if(showDone){
			doneButton = new Button("Done");
			add(doneButton);
		} 
	}

    public void setRect(int x, int y, int width, int height)
    {
		super.setRect(x,y,width,height);
		if(!didLayout) layout(false);

		nameLabel.setRect(3,5, 45, 15);
		nameEdit.setRect(50, 5, 110, 15);

		viewLabel.setRect(3,30, 45, 15);
		viewChoice.setRect(50,30, 60, 15);

		if(showDone){
			doneButton.setRect(width-30,height-15,30,15);
		} 

	}

	public void close()
	{
		String viewStr = viewChoice.getSelected();
		if(viewStr.equals("Tree")){
			dict.viewType = dict.TREE_VIEW;
		} else {
			dict.viewType = dict.PAGING_VIEW;
		}
		dict.setName(nameEdit.getText());

		super.close();
	}

	public int getPreferredWidth(waba.fx.FontMetrics fm){
		return -1;
	}

	public int getPreferredHeight(waba.fx.FontMetrics fm){
		return -1;
	}

	public extra.ui.Dimension getPreferredSize(){
		return null;
	}
}
