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
		bClose.setRect(contentRect.width/2 + 5 + bWidthApply/2,contentRect.height - 5 - bHeight,bWidthClose,bHeight);
		add(bClose);
		if(bCancel == null) bCancel = new waba.ui.Button("Cancel");
		else remove(bCancel);
		bCancel.setRect(contentRect.width/2 - 5 - bWidthApply/2 - bWidthCancel,contentRect.height - 5 - bHeight,bWidthCancel,bHeight);
		add(bCancel);
		if(bApply == null) bApply = new waba.ui.Button("Apply");
		else remove(bApply);
		bApply.setRect(contentRect.width/2 - bWidthApply/2 ,contentRect.height - 5 - bHeight,bWidthApply,bHeight);
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
			int y0 = pHeight / 2  - (nProperties * 20) / 2;
			if (y0 < 0) y0 = 0;
			int x0 = 5;

			PropObject po;
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
				lName.setRect(labelStartX,y0,maxLabelWidth,16);
				if(po.prefWidth < maxPrefValWidth){
					c.setRect(labelStartX+maxLabelWidth+spaceSize,y0,po.prefWidth,16);
				} else {
					c.setRect(labelStartX+maxLabelWidth+spaceSize,y0,maxPrefValWidth,16);
				}
				y0 += 20;
				propertiesPanes[currContainer].add(lName);
				propertiesPanes[currContainer].add(c);
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