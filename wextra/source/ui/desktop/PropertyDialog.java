package org.concord.waba.extra.ui;

import extra.util.*;
import org.concord.waba.extra.event.*;
import waba.ui.*;

public class PropertyDialog extends Dialog{
PropContainer propContainer;
int			nContainers = 0;
int			currContainer = 0;
waba.ui.Button bClose = null;
waba.ui.Button bCancel = null;
waba.ui.Button bApply = null;
waba.ui.Label  contName = null;
Container []propertiesPanes =null;
Container   currentPane =null;
int 	bHeight = 20;
ExtraMainWindow owner = null;


	public PropertyDialog(ExtraMainWindow owner,DialogListener l,String title, PropContainer propContainer){
		super();
  		this.title = title;
		this.propContainer = propContainer;
		this.owner = owner;
  		font = new waba.fx.Font("Helvetica", waba.fx.Font.BOLD, 12);
		nContainers = propContainer.getNumbPropContainers();
		currContainer = 0;
		addDialogListener(l);
		owner.setDialog(this);

		java.awt.Window awtWindow = (java.awt.Window)getAWTCanvas().getParent();
		if(awtWindow instanceof java.awt.Dialog){
			((java.awt.Dialog)awtWindow).setTitle(title);
			((java.awt.Dialog)awtWindow).setResizable(false);
		}else if(awtWindow instanceof java.awt.Frame){
			((java.awt.Frame)awtWindow).setTitle(title);
			((java.awt.Frame)awtWindow).setResizable(false);
		}
	}
	public void addDialogListener(org.concord.waba.extra.event.DialogListener l){
		if(listener == null){
			listener = l;
		}
	}
	public void removeDialogListener(org.concord.waba.extra.event.DialogListener l){
		if(listener == l){
			listener = null;
		}
	}
  	public waba.fx.Font getFont(){return font;}
	public void setButtons(){
		waba.fx.FontMetrics fm = getFontMetrics(getFont());
		int bWidthClose	= fm.getTextWidth("Close") + 5;
		int bWidthCancel 	= fm.getTextWidth("Cancel") + 5;
		int bWidthApply 	= fm.getTextWidth("Apply") + 5;
		bClose = new waba.ui.Button("Close");
		bClose.setRect(width/2 + 5 + bWidthApply/2,height - 5 - bHeight,bWidthClose,bHeight);
		add(bClose);
		bCancel = new waba.ui.Button("Cancel");
		bCancel.setRect(width/2 - 5 - bWidthApply/2 - bWidthCancel,height - 5 - bHeight,bWidthCancel,bHeight);
		add(bCancel);
		bApply = new waba.ui.Button("Apply");
		bApply.setRect(width/2 - bWidthApply/2 ,height - 5 - bHeight,bWidthApply,bHeight);
		add(bApply);
	}
	public void setTabBar(){
		TabBar tabBar = new TabBar();
		for(int i = 0; i < nContainers; i++){
			MyTab tab = new MyTab(propContainer.getPropertiesContainerName(i));
			tabBar.add(tab);
		}
		tabBar.setRect(widthBorder+2, 0, width - 2*widthBorder - 4, 20);
		add(tabBar);
	}
	public void setContent(){
		setButtons();
		setTabBar();
		setPropertiesPane();
	}
 	public void setPropertiesPane(){
 		if(propertiesPanes == null){
 			propertiesPanes = new Container[nContainers];
 		}
 		if(currentPane != null){
			remove(currentPane);
 		}
		waba.util.Vector prop = propContainer.getProperties(currContainer);
		if(prop == null) return;
		int nProperties = prop.getCount();
 		if(propertiesPanes[currContainer] == null){
			propertiesPanes[currContainer] = new Container();
			int pHeight = height - (25 + bHeight);
			int pWidth = width - 2*widthBorder;
			propertiesPanes[currContainer].setRect(widthBorder,20, pWidth ,pHeight);
			int y0 = pHeight / 2  - (nProperties * 20) / 2;
			if (y0 < 0) y0 = 0;
			int x0 = 5;
			for(int i = 0; i < nProperties; i++){
				PropObject po = (PropObject)prop.get(i);
				String name = po.getName();
				String value = po.getValue();
				waba.ui.Label lName = new waba.ui.Label(name);
				waba.ui.Control c = null;
				String []possibleValues = po.getPossibleValues();
				if(possibleValues == null){
					waba.ui.Edit   eValue = new waba.ui.Edit();
					eValue.setText(value);
					c = eValue;
				}else{
					int index = -1;
					for(int j = 0; j < possibleValues.length; j++){
						if(value.equals(possibleValues[j])){
							index = j;
							break;
						}
					}
					Choice ch = new Choice(possibleValues);
					if(index >= 0){
						ch.setSelectedIndex(index);
					}
					c = ch;
				}
				po.setValueKeeper(c);
				lName.setRect(width/2 - 65,y0,60,16);
				c.setRect(width/2 + 5,y0,60,16);
				y0 += 20;
				propertiesPanes[currContainer].add(lName);
				propertiesPanes[currContainer].add(c);
			}
		}
		add(propertiesPanes[currContainer]);
		currentPane = propertiesPanes[currContainer];
 	}

/*
 	public void onPaint(waba.fx.Graphics g){
 		super.onPaint(g);
 		g.setColor(0,0,0);
 		if(propertiesPanes[currContainer] == null) return;
 		waba.fx.Rect r = propertiesPanes[currContainer].getRect();
		g.drawRect(r.x,r.y,r.width,r.height);

 	}
*/
	public void updateProperties(boolean clearKeepers){
    		int nContainers = propContainer.getNumbPropContainers();
		for(int i = 0; i < nContainers; i++){
			waba.util.Vector prop = propContainer.getProperties(i);
			if(prop == null) continue;
			int nProperties = prop.getCount();
			for(int j = 0; j < nProperties; j++){
				PropObject po = (PropObject)prop.get(j);
				Control c = po.getValueKeeper();
				if(c instanceof waba.ui.Edit){
					po.setValue(((waba.ui.Edit)c).getText());
				}else if(c instanceof Choice){
					po.setValue(((Choice)c).getSelected());
				}
				if(clearKeepers) po.setValueKeeper(null);
			}
		}
	}

 	public void onEvent(waba.ui.Event event){
		if (event.type == waba.ui.ControlEvent.PRESSED){
			if(event.target instanceof MyTab){
				String contName = ((MyTab)event.target).getText();
		    		int nContainers = propContainer.getNumbPropContainers();
		    		int index = -1;
				for(int i = 0; i < nContainers; i++){
					if(contName.equals(propContainer.getPropertiesContainerName(i))){
						index = i;
						break;
					}
				}
				if(index >= 0){
					currContainer = index;
					setPropertiesPane();
				}
			}
			if(event.target instanceof waba.ui.Button){
				if(listener != null){
					if(event.target != bCancel) updateProperties(event.target == bClose);
					String message = ((waba.ui.Button)event.target).getText();
					Object info = propContainer;
					int infoType = org.concord.waba.extra.event.DialogEvent.PROPERTIES;
					listener.dialogClosed(new org.concord.waba.extra.event.DialogEvent(this,null,message,info,infoType));
				}
				if(event.target != bApply){
					hide();
					owner.setDialog(null);
				}
				return;
			}
		}
  	}
	class MyTab extends waba.ui.Tab{
		public MyTab(String text){super(text);}
		public String getText(){return text;}
	}
}
