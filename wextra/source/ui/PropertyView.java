package org.concord.waba.extra.ui;

import extra.util.*;
import org.concord.waba.extra.event.*;
import waba.ui.*;
import waba.util.*;

public class PropertyView extends Container
{
	int			currContainer = 0;

	CCButton bCancel = null;
	CCButton bOk = null;
	TabBar 	 tabBar = null;

	Label  contName = null;

	Vector propPanes = new Vector();
	PropertyPane currentPane =null;
	int 	bHeight = 20;
	int tabHeight = 0;
	int botMargin = 5;
	ActionListener listener = null;
	int widthBorder = 1;

	public final int PROPERTY_CHANGE = 5000;

	public PropertyView(){
		this(null);
	}

	public PropertyView(ActionListener al){
		listener = al;
	}

	public void setActionListener(ActionListener al){
		listener = al;
	}

	public void addContainer(PropContainer pCont)
	{
		PropertyPane pane = 
			new PropertyPane(pCont, this);
		propPanes.add(pane);
	}

	public void removeContainer(PropContainer pCont)
	{
		for(int i=0; i<propPanes.getCount(); i++){
			if(((PropertyPane)propPanes.get(i)).getContainer() == pCont){
				propPanes.del(i);
				return;
			}
		}
	}

	public void addPane(PropertyPane pane)
	{
		propPanes.add(pane);
	}

	public void setCurTab(int index)
	{
		currContainer = index;
	}

	public void setApplyEnabled(boolean flag)
	{
		// bApply.setEnabled(flag);
	}

	public void setButtons(){
		waba.fx.FontMetrics fm = getFontMetrics(MainWindow.defaultFont);

		int bWidthCancel 	= fm.getTextWidth("Cancel") + 5;
		int bWidthOk 	= fm.getTextWidth("Ok") + 5;
		
		int buttonXStart = (width - 5 - bWidthOk - bWidthCancel)/2;


		if(bCancel == null) bCancel = new CCButton("Cancel");
		else remove(bCancel);
		bCancel.setRect(buttonXStart,
						height - botMargin - bHeight,
						bWidthCancel,bHeight);
		add(bCancel);

		if(bOk == null) bOk = new CCButton("Ok");
		else remove(bOk);
		bOk.setRect(buttonXStart + 5 + bWidthCancel,
					   height - botMargin - bHeight,
					   bWidthOk,bHeight);
		add(bOk);
	}

	public void updateView()
	{
		setTabBar();
		for(int i = 0; i < propPanes.getCount(); i++){
			((PropertyPane)propPanes.get(i)).reSetup();
		}
		setPropertiesPane();
	}

	public void setTabBar(){
		MyTab curTab = null;

		if(tabBar != null){
			remove(tabBar);
			tabBar = null;
		}
		if(propPanes.getCount() > 1){
			tabBar = new TabBar();
			for(int i = 0; i < propPanes.getCount(); i++){
				MyTab tab = new MyTab(((PropertyPane)propPanes.get(i)).getName());
				if(i == currContainer) curTab = tab;
				tabBar.add(tab);
			}

			tabBar.setRect(widthBorder+2, 0, width - 2*widthBorder - 4, 20);
			if(curTab != null) tabBar.setActiveTab(curTab);
			add(tabBar);
			tabHeight = 20;
		} else {
			tabHeight = 0;
		}
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
 		if(currentPane != null){
			remove(currentPane);
 		}

		PropertyPane oldPane = currentPane;
		currentPane = (PropertyPane)propPanes.get(currContainer);
		if(!currentPane.isSetup()){
			int pHeight = height - (tabHeight + bHeight);
			int pWidth = width - 2*widthBorder;
			currentPane.setRect(widthBorder,tabHeight, pWidth ,pHeight);
			currentPane.setupPane();
		}
		if(oldPane != currentPane){
			if(oldPane != null) oldPane.setVisible(false);
			currentPane.setVisible(true);
		}

		add(currentPane);
	}
	
	public void doApply()
	{
		for(int i = 0; i < propPanes.getCount(); i++){
			((PropertyPane)propPanes.get(i)).apply();
		}
		if(listener != null){
			String message = "Apply";
			ActionEvent ae = new ActionEvent(this,null,message);
			ae.type = PROPERTY_CHANGE;
			listener.actionPerformed(ae);
		}
	}

	public void doClose()
	{
		for(int i = 0; i < propPanes.getCount(); i++){
			((PropertyPane)propPanes.get(i)).close();
		}
		if(listener != null){
			String message = "Close";
			ActionEvent ae = new ActionEvent(this,null,message);
			ae.type = PROPERTY_CHANGE;
			listener.actionPerformed(ae);
		}
	}

 	public void onEvent(waba.ui.Event event)
	{
		if (event.type == waba.ui.ControlEvent.PRESSED){
			if(event.target instanceof MyTab){
				String contName = ((MyTab)event.target).getText();
				
				int index = -1;
				for(int i = 0; i < propPanes.getCount(); i++){
					if(contName.equals(((PropertyPane)propPanes.get(i)).getName())){
						index = i;
						break;
					}
				}

				if(index >= 0){
					currContainer = index;
					setPropertiesPane();
				}
			}
			if(event.target instanceof CCButton){
				if(event.target == bOk){
					doApply();
					doClose();
				}
				if(event.target == bCancel){
					doClose();
				}

				return;
			}
		}
  	}

	class MyTab extends Tab{
		public MyTab(String text){super(text);}
		public String getText(){return text;}
	}
}
