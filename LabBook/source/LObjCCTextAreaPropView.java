package org.concord.LabBook;

import waba.ui.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.io.*;

public class LObjCCTextAreaPropView extends LabObjectView implements ActionListener{
LObjCCTextArea 			doc;
Button 					doneButton;

Check					editModeCheck;

	public LObjCCTextAreaPropView(ViewContainer vc, LObjCCTextArea d){
		super(vc, (LabObject)d, null);
		doc = d;
	}
    public void actionPerformed(ActionEvent e){
    }

	public void setEditMode(boolean state){
		if(editModeCheck != null){
			editModeCheck.setChecked(state);
		}
	}
	

	public void layout(boolean sDone){
		if(didLayout) return;
		didLayout = true;
		if(editModeCheck == null){
			editModeCheck = new Check("Edit Mode");
			add(editModeCheck);
			if(doc != null) editModeCheck.setChecked(doc.editMode);
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
		if(editModeCheck != null){
			editModeCheck.setRect(1,5,80,15);
		}
		if(showDone){
			doneButton.setRect(width-31,height-15,30,15);
		}
	}

	public void close(){
		if(editModeCheck != null) doc.editMode = editModeCheck.getChecked();   
		super.close();
	}

	public void onEvent(Event e){
		if(e.target == doneButton && e.type == ControlEvent.PRESSED){
			if(editModeCheck != null) doc.editMode = editModeCheck.getChecked();   
			if(container != null){
				container.done(this);
			}	 
		}
	}
	public int getPreferredWidth(waba.fx.FontMetrics fm){
		return -1;
	}

	public int getPreferredHeight(waba.fx.FontMetrics fm){
		return -1;
	}

	public Dimension getPreferredSize(){
		return null;
	}
}
