package org.concord.waba.extra.ui;

import extra.util.*;
import org.concord.waba.extra.event.*;
import waba.ui.*;

public class PropertyDialog extends Dialog
	implements ActionListener
{
	PropertyView propView = null;
	PropContainer propContainer;
	ExtraMainWindow owner = null;

	public PropertyDialog(ExtraMainWindow owner,DialogListener l,String title, 
						  PropContainer propContainer)
	{
		this(owner,l,title,propContainer, 0);
	}

	public PropertyDialog(ExtraMainWindow owner,DialogListener l,String title, 
						  PropContainer propContainer, int curTab)
	{
		super(title);
		this.propContainer = propContainer;
		propView = new PropertyView(propContainer, curTab, this);
		this.owner = owner;
		addDialogListener(l);
		owner.setDialog(this);
	}

	public void setContent(){
 		waba.fx.Rect cRect = getContentPane().getRect();
		propView.setRect(0, 0, cRect.width, cRect.height);
		getContentPane().add(propView);
	}

	public void onEvent(Event e){}

	public void actionPerformed(ActionEvent ae)
	{
		if(listener != null){
			Object info = propContainer;
			int infoType = org.concord.waba.extra.event.DialogEvent.PROPERTIES;
			listener.dialogClosed(new DialogEvent(this,null,ae.getActionCommand(),propContainer,infoType));
		}

		if(!ae.getActionCommand().equals("Apply")){
			hide();
			owner.setDialog(null);
		}
		return;
	}
}
