package org.concord.waba.extra.ui;

import extra.util.*;
import org.concord.waba.extra.event.*;
import waba.ui.*;
import waba.fx.*;

public class MultiList extends Container
{
	String [] options;
	Check [] checks;
	public int chHeight = 12;
	public int chSpace = 3;
	boolean radio = false;
	int curIndex = -1;

	public MultiList(String [] opts)
	{
		options = opts;
		checks = new Check[options.length];
		for(int i=0; i < checks.length; i++){
			checks[i] = new Check(options[i]);
			add(checks[i]);
		}

	}

	public void setRadio(boolean r)
	{ 
		radio = r; 
		curIndex = -1;
		for(int i=0; i< checks.length; i++){
			if(curIndex == -1){
				if(checks[i].getChecked() == true){
					curIndex = i;
				}
			} else {
				checks[i].setChecked(false);
			}				
		}
		if(curIndex == -1){
			curIndex = 0;
			checks[0].setChecked(true);
		}
	}

	public int getPrefHeight()
	{
		int prefHeight = 3;
		for(int i=0; i < options.length; i++){
			prefHeight += chHeight+chSpace;
		}
		prefHeight += 3-chSpace;
		return prefHeight;
	}

	public void setRect(int x, int y, int width, int height)
	{
		super.setRect(x,y,width,height);

		for(int i=0; i < checks.length; i++){
			checks[i].setRect(3,i*(chHeight+chSpace)+ 3, width-6, chHeight);
		}
	}

	public void onPaint(Graphics g)
	{
		g.setColor(0,0,0);
		g.drawRect(0,0,width,height);
	}

	public void setCheck(int index, boolean on)
	{
		if(radio && index != curIndex){
			if(on){
				checks[curIndex].setChecked(false);
				if(index >=0 && index < checks.length){
					checks[index].setChecked(true);
				}
				curIndex = index;
			}			
		} else {
			if(index >=0 && index < checks.length){
				checks[index].setChecked(on);
			}
		}
	}
	public boolean getCheck(int index)
	{
		if(index >=0 && index < checks.length){
			return checks[index].getChecked();
		}
		return false;
	}

	public boolean [] getCheckedValues()
	{
		boolean [] cdValues  = new boolean [checks.length];
		for(int i=0; i<checks.length; i++){
			cdValues[i] = checks[i].getChecked();
		}
		return cdValues;
	}

	boolean recursive = false;
	public void onEvent(Event e)
	{
		if(!recursive && e.target instanceof Check && 
		   e.type == ControlEvent.PRESSED){
			if(radio){
				for(int i=0; i<checks.length; i++){
					if(checks[i] == (Check)e.target){
						recursive = true;
						if(i != curIndex){
							checks[curIndex].setChecked(false);
							curIndex = i;
						} else {
							checks[i].setChecked(true);
						}
						recursive = false;
					}
				}
			}
		}
	}
}
