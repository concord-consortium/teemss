package org.concord.ProbeLib;

import extra.util.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.ui.*;
import waba.ui.*;

public class CalibrationDialog extends Dialog 
	implements ActionListener{
	ExtraMainWindow owner = null;
	CalibrationPane cPane = null;
	PropertyPane pPane = null;
	PropertyView pView = null;

	public CalibrationDialog(ExtraMainWindow owner,DialogListener l,String title, Probe probe){
		super(title);
		this.owner = owner;
		addDialogListener(l);
		owner.setDialog(this);
		pView = new PropertyView(this);
		pPane = new PropertyPane(probe, pView);
		cPane = new CalibrationPane(probe, this, pView);
		pView.addPane(pPane);
		pView.addPane(cPane);
	}

	public void setContent(){
 		waba.fx.Rect cRect = getContentPane().getRect();
		pView.setRect(0, 0, cRect.width, cRect.height);
		getContentPane().add(pView);
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
			listener.dialogClosed(new DialogEvent(this,null,ae.getActionCommand(),cPane.probe,infoType));
		}

		if(!ae.getActionCommand().equals("Apply")){
			hide();
			owner.setDialog(null);
		}
		return;
	}  	
}
