package org.concord.LabBook;

import waba.ui.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import extra.ui.*;
import extra.io.*;

public class LObjCCTextAreaPropView extends LabObjectView implements ActionListener{
CCTextArea 				tArea;
LObjCCTextArea 			doc;
Button 					doneButton;

Check					propertyModeCheck;

	public LObjCCTextAreaPropView(ViewContainer vc, LObjCCTextArea d){
		super(vc);
		doc = d;
		lObj = doc;
	}
    public void actionPerformed(ActionEvent e){
    }

	public void setTextArea(CCTextArea tArea){
		this.tArea = tArea;
		if(tArea != null && propertyModeCheck != null){
			setPropertyMode(tArea.getPropertyMode());
		}
	}
	public void setPropertyMode(boolean state){
		if(propertyModeCheck != null){
			propertyModeCheck.setChecked(state);
		}
	}
	

	public void layout(boolean sDone){
		if(didLayout) return;
		didLayout = true;
		if(propertyModeCheck == null){
			propertyModeCheck = new Check("Edit Mode");
			add(propertyModeCheck);
		}
		showDone = sDone;
		if(showDone){
			doneButton = new Button("Done");
			add(doneButton);
		} 
	}

	public void setRect(int x, int y, int width, int height){
		super.setRect(x,y,width,height);
		if(!didLayout) layout(false);
		if(propertyModeCheck != null){
			propertyModeCheck.setRect(1,5,80,15);
		}
		if(showDone){
			doneButton.setRect(width-31,height-15,30,15);
		}
	}

	public void close(){
		super.close();
	}

	public void onEvent(Event e){
		if(e.target == doneButton && e.type == ControlEvent.PRESSED){
			if(container != null){
				container.done(this);
			}	 
			if(tArea != null && propertyModeCheck != null) tArea.setPropertyMode(propertyModeCheck.getChecked());   
		}
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
