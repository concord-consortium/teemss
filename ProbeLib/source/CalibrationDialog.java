package org.concord.waba.extra.ui;
import extra.util.*;
import org.concord.waba.extra.event.*;
import waba.ui.*;
import org.concord.waba.extra.probware.probs.CCProb;
import org.concord.waba.extra.probware.*;

public class CalibrationDialog extends Dialog 
	implements ActionListener{
	ExtraMainWindow owner = null;
	CalibrationView cView = null;
	
	public CalibrationDialog(ExtraMainWindow owner,DialogListener l,String title, CCProb probe,int interfaceManager){
		super(title);
		this.owner = owner;
		addDialogListener(l);
		owner.setDialog(this);
		cView = new CalibrationView(probe, interfaceManager, this);
	}

	public void setContent(){
 		waba.fx.Rect cRect = getContentPane().getRect();
		cView.setRect(0, 0, cRect.width, cRect.height);
		getContentPane().add(cView);
	}

	public void onEvent(Event e){}

	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getActionCommand().equals("Close")){
			hide();
			owner.setDialog(null);
			return;
		}

		if(listener != null){
			int infoType = org.concord.waba.extra.event.DialogEvent.OBJECT;
			listener.dialogClosed(new DialogEvent(this,null,ae.getActionCommand(),cView.probe,infoType));
		}

		if(!ae.getActionCommand().equals("Apply")){
			hide();
			owner.setDialog(null);
		}
		return;
	}  	
}
