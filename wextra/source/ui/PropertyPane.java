package org.concord.waba.extra.ui;

import extra.util.*;
import org.concord.waba.extra.event.*;
import waba.ui.*;

public class PropertyPane extends Container
{
	PropContainer propContainer;
	protected boolean setup = false;
	PropertyView pView = null;
	boolean forceSetup = false;

	public PropertyPane(PropertyView pV)
	{
		pView = pV;
	}

	public PropertyPane(PropContainer pCont, PropertyView pView)
	{
		this(pView);
		propContainer = pCont;
	}

	public String getName(){return propContainer.getName();}

	public boolean isSetup(){return setup;}

	public void setVisible(boolean flag)
	{
		if(flag){
			setupPane();
		}
	}

	public boolean saveVisValue(PropObject po)
	{
		forceSetup = false;
		Control c = (Control)po.getCookie();

		if(c instanceof Edit){
			if(po.setVisValue(((Edit)c).getText())){
				// at this point the container can cause an update of the pane
				// for instance if this change affects other values
				// the container can update those values
				forceSetup = propContainer.visValueChanged(po);
				return true;
			} else {
				// The new visible value was rejected
				((Edit)c).setText(po.getVisValue());
			}
		}else if(c instanceof Choice){
			if(po.setVisValue(((Choice)c).getSelected())){
			   forceSetup = propContainer.visValueChanged(po);
			   return true;
			} else {
				((Choice)c).setSelectedIndex(po.getVisValue());
			}
		} else if(c instanceof MultiList){
			MultiList mList = (MultiList)c;
			boolean [] checkedValues = mList.getCheckedValues();
			if(po.setVisCheckedValues(checkedValues)){
				forceSetup = propContainer.visValueChanged(po);
				return true;
			} else {
				for(int k=0; k<checkedValues.length; k++){
					mList.setCheck(k, po.getVisCheckedValue(k));
				}
			}
		}
		return false;
	}

	boolean recursive = false;

	public void onEvent(Event e)
	{
		if((e.target instanceof Edit && e.type == waba.ui.ControlEvent.FOCUS_OUT) ||
		   ((e.target instanceof Choice || e.target instanceof Check) &&
			e.type == waba.ui.ControlEvent.PRESSED && !recursive)){
			recursive = true;

			// find which of our children this focus change belongs to
			Control child = (Control)e.target;
			if(child == this) return;
			while(child != null && child.getParent() != this){
				child = child.getParent();
			}

			if(child == null) return;
			
			// if we are here the child's parent should be us
			// try to find the child
			PropObject po = propContainer.findPropObjWithCookie(child);
			if(po == null) return;

			if(saveVisValue(po)){
				// the value was saved and the container had a chance
				// to update the pane
				// we might like to notify our listener at this point
				// and we should setup the pane
			} else {
				// the value wasn't saved, however the control 
				// linked to this po might have changed and changed back
				// we probably don't need to notify our listener
			}			

			if(forceSetup) setupPane();
			forceSetup = false;
			recursive = false;
		}

		if(e.target instanceof Button && 
		   e.type == waba.ui.ControlEvent.PRESSED){
			if(pView != null && pView.listener != null){
				String message = ((Button)e.target).getText();
				ActionEvent ae = new ActionEvent(this,null,message);
				ae.type = pView.PROPERTY_CHANGE;
				pView.listener.actionPerformed(ae);				
			}
		}
	}

	public void apply()
	{
		propContainer.apply();
	}

	public void close()
	{
		propContainer.close();
	}

 	public void setupPane()
	{
		while(children != null){
			remove(children);
		}

		waba.fx.FontMetrics fm = getFontMetrics(MainWindow.defaultFont);

		waba.util.Vector prop = propContainer.getProperties();
		if(prop == null) return;
		int nProperties = prop.getCount();

		PropObject po;
		int totalPropHeight = 0;
		for(int i=0; i< nProperties; i++){
			po = (PropObject)prop.get(i);
			if(po.getType() == po.MULTIPLE_SEL_LIST){
				totalPropHeight += 44;
			} else {
				totalPropHeight += 20;
			}
		}
		int y0 = height / 2  - totalPropHeight / 2;
		if (y0 < 0) y0 = 0;
		int x0 = 5;

		int maxLabelWidth = 0;
		int curLabelWidth = 0;
		int maxPrefValWidth = 0;

		for(int i=0; i< nProperties; i++){
			po = (PropObject)prop.get(i);
			curLabelWidth = fm.getTextWidth(po.getName());
			if(curLabelWidth > maxLabelWidth) maxLabelWidth = curLabelWidth;
			if(po.prefWidth > maxPrefValWidth) maxPrefValWidth = po.prefWidth;
		}

		int labelStartX;
		int spaceSize = 2;
		if((maxLabelWidth + 2 + maxPrefValWidth) > width - 2){
			int diff = (maxLabelWidth + 2 + maxPrefValWidth) - width + 2;
			maxLabelWidth -= diff/2;
			maxPrefValWidth -= diff/2;
			labelStartX = 1;
			spaceSize = 2;
		} else {
			int diff = width - (maxLabelWidth + 2 + maxPrefValWidth) - 2;
			if(diff > 10) spaceSize = 10;
			else spaceSize = diff;
			labelStartX = (width - (maxLabelWidth + spaceSize + maxPrefValWidth))/2; 
		}
							
		for(int i = 0; i < nProperties; i++){
			po = (PropObject)prop.get(i);

			String name = po.getName();
			String value = po.getVisValue();
			Label lName = new Label(name);
			Control c1 = null;
			Control c2 = null;
			String []possibleValues = po.getVisPossibleValues();
			int type = po.getType();
			int poHeight = 16;
			
			if(type == po.EDIT){
				Edit   eValue = new Edit();
				if(value == null) value = "";
				eValue.setText(value);
				c1 = eValue;
			} else if (type == po.CHOICE || type == po.CHOICE_SETTINGS){
				int index = po.getVisIndex();
				Choice ch = new Choice(possibleValues);
				if(index >= 0){
					ch.setSelectedIndex(index);
				}
				c1 = ch;
				if(type == po.CHOICE_SETTINGS){
					c2 = new Button(po.getSettingsButtonName());
				}
			} else if (type == po.MULTIPLE_SEL_LIST){
				MultiList mList = new MultiList(possibleValues);
				poHeight = mList.getPrefHeight();
				for(int j = 0; j < possibleValues.length; j++){
					mList.setCheck(j, po.getVisCheckedValue(j));
				}					
				c1 = mList;
			}
			po.setCookie(c1);
			lName.setRect(labelStartX,y0,maxLabelWidth,16);
			int prefWidth = po.prefWidth;
			if(po.prefWidth > maxPrefValWidth){
				prefWidth = maxPrefValWidth;
			} 
			if(c2 == null){
				c1.setRect(labelStartX+maxLabelWidth+spaceSize,y0,prefWidth,poHeight);
			} else {
				int c2width = fm.getTextWidth(po.getSettingsButtonName()) + 4;
				c1.setRect(labelStartX+maxLabelWidth+spaceSize,y0,prefWidth-c2width-2,poHeight);
				c2.setRect(labelStartX+maxLabelWidth+spaceSize+prefWidth-c2width+2,y0,c2width,poHeight);
			}

			y0 += poHeight+4;
			add(lName);
			add(c1);
			if(c2 != null){
				add(c2);
			}
		}
		setup = true;
	}
	
}
