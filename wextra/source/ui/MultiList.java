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
   
	public MultiList(String [] opts)
	{
		options = opts;
		checks = new Check[options.length];
		for(int i=0; i < checks.length; i++){
			checks[i] = new Check(options[i]);
			add(checks[i]);
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

}
