package org.concord.waba.extra.ui;

import extra.util.*;
import org.concord.waba.extra.event.*;
import waba.ui.*;

public class PropertyView extends Container
{
	PropContainer propContainer;
	int			nContainers = 0;
	int			currContainer = 0;
	waba.ui.Button bClose = null;
	waba.ui.Button bCancel = null;
	waba.ui.Button bApply = null;
	TabBar 	 tabBar = null;

	waba.ui.Label  contName = null;

	Container []propertiesPanes =null;
	Container   currentPane =null;
	int 	bHeight = 20;
	int botMargin = 5;
	ActionListener listener = null;
	int widthBorder = 1;

	public final int PROPERTY_CHANGE = 5000;

	public PropertyView(PropContainer propContainer, ActionListener al){
		this(propContainer, 0, al);
	}

	public PropertyView(PropContainer propContainer, int curTab, ActionListener al){
		this.propContainer = propContainer;
		nContainers = propContainer.getNumbPropContainers();
		currContainer = 0;
		currContainer = curTab;
		listener = al;
	}

	

	public void setButtons(){
		waba.fx.Rect contentRect = getRect();
		waba.fx.FontMetrics fm = getFontMetrics(MainWindow.defaultFont);
		int bWidthClose	= fm.getTextWidth("Close") + 5;
		int bWidthCancel 	= fm.getTextWidth("Cancel") + 5;
		int bWidthApply 	= fm.getTextWidth("Apply") + 5;
		if(bClose == null) 	bClose = new Button("Close");
		else 				remove(bClose);
		bClose.setRect(contentRect.width/2 + 5 + bWidthApply/2,contentRect.height - botMargin - bHeight,bWidthClose,bHeight);
		add(bClose);
		if(bCancel == null) bCancel = new waba.ui.Button("Cancel");
		else remove(bCancel);
		bCancel.setRect(contentRect.width/2 - 5 - bWidthApply/2 - bWidthCancel,contentRect.height - botMargin - bHeight,bWidthCancel,bHeight);
		add(bCancel);
		if(bApply == null) bApply = new waba.ui.Button("Apply");
		else remove(bApply);
		bApply.setRect(contentRect.width/2 - bWidthApply/2 ,contentRect.height - botMargin - bHeight,bWidthApply,bHeight);
		add(bApply);
	}

	public void setTabBar(){
		MyTab curTab = null;

		if(tabBar == null){
			tabBar = new TabBar();
			for(int i = 0; i < nContainers; i++){
				MyTab tab = new MyTab(propContainer.getPropertiesContainerName(i));
				if(i == currContainer) curTab = tab;
				tabBar.add(tab);
			}
		}else{
			remove(tabBar);
		}
		tabBar.setRect(widthBorder+2, 0, width - 2*widthBorder - 4, 20);
		if(curTab != null) tabBar.setActiveTab(curTab);
		add(tabBar);
	}

	public void setRect(int x, int y, int width, int height)
	{
		super.setRect(x, y, width, height);
		if(height <= 160){
			bHeight = 13;
			botMargin = 2;
		}
		setButtons();
		setTabBar();
		setPropertiesPane();
	}

 	public void setPropertiesPane(){
		waba.fx.FontMetrics fm = getFontMetrics(MainWindow.defaultFont);
 		waba.fx.Rect contentRect = getRect();
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
			int y0 = pHeight / 2  - totalPropHeight / 2;
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
			if((maxLabelWidth + 2 + maxPrefValWidth) > pWidth - 2){
				int diff = (maxLabelWidth + 2 + maxPrefValWidth) - pWidth + 2;
				maxLabelWidth -= diff/2;
				maxPrefValWidth -= diff/2;
				labelStartX = 1;
				spaceSize = 2;
			} else {
				int diff = pWidth - (maxLabelWidth + 2 + maxPrefValWidth) - 2;
				if(diff > 10) spaceSize = 10;
				else spaceSize = diff;
				labelStartX = (pWidth - (maxLabelWidth + spaceSize + maxPrefValWidth))/2; 
			}
							
			for(int i = 0; i < nProperties; i++){
				po = (PropObject)prop.get(i);

				String name = po.getName();
				String value = po.getValue();
				waba.ui.Label lName = new waba.ui.Label(name);
				waba.ui.Control c1 = null;
				waba.ui.Control c2 = null;
				String []possibleValues = po.getPossibleValues();
				int type = po.getType();
				int poHeight = 16;
				if(type == po.EDIT){
					waba.ui.Edit   eValue = new waba.ui.Edit();
					eValue.setText(value);
					c1 = eValue;
				} else if (type == po.CHOICE || type == po.CHOICE_SETTINGS){
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
					c1 = ch;
					if(type == po.CHOICE_SETTINGS){
						c2 = new Button(po.getSettingsButtonName());
					}
				} else if (type == po.MULTIPLE_SEL_LIST){
					MultiList mList = new MultiList(possibleValues);
					poHeight = mList.getPrefHeight();
					for(int j = 0; j < possibleValues.length; j++){
						mList.setCheck(j, po.getCheckedValue(j));
					}					
					c1 = mList;
				}
				po.setValueKeeper(c1);
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
				propertiesPanes[currContainer].add(lName);
				propertiesPanes[currContainer].add(c1);
				if(c2 != null){
					propertiesPanes[currContainer].add(c2);
				}
			}
		}
		add(propertiesPanes[currContainer]);
		currentPane = propertiesPanes[currContainer];
	}
	
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
				} else if(c instanceof MultiList){
					MultiList mList = (MultiList)c;
					String [] possibleValues = po.getPossibleValues();

					for(int k=0; k<possibleValues.length; k++){
						po.setCheckedValue(k, mList.getCheck(k));
					}
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
					ActionEvent ae = new ActionEvent(this,null,message);
					ae.type = PROPERTY_CHANGE;
					listener.actionPerformed(ae);
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
