package org.concord.waba.extra.ui;

import waba.util.*;
public class Choice extends extra.ui.List{
	String name = null;

	public Choice(Vector options){
		super(options);
		initialYOffset = 0;
	}

	public Choice(String []options){
		super(options);
		initialYOffset = 0;
	}
	public Choice(){
		super();
		initialYOffset = 0;
	}

	public void setName(String name)
	{
		this.name = name;
		initialYOffset = 15;
	}
	
	public void onPaint(waba.fx.Graphics g)
	{
		if (fm==null)
			calcSizes();
		if (popup==null){
			g.translate(0,4);
			g.setColor(0,0,0);
			g.drawLine(0,0,7,0);
			g.drawLine(1,1,6,1);
			g.drawLine(2,2,5,2);
			g.drawLine(3,3,4,3);
			g.translate(0,-4);
			if(name != null){
				g.drawText(name, 10, 0);
			} else {
				g.drawText(getSelected(),10,0);
			}
		} else {
			drawList(g);
			g.setColor(0,0,0);
			g.drawLine(1,0,width-3,0);
			g.drawLine(0,1,0,height-3);
			g.drawLine(width-2,1,width-2,height-2);
			g.drawLine(1,height-2,width-3,height-2);
			g.drawLine(2,height-1,width-3,height-1);
			g.drawLine(width-1,2,width-1,height-3);
			if(name != null){
				g.drawLine(1,initialYOffset - 2, width-2, initialYOffset - 2);
				g.drawText(name,4,2);
			}
		}
	}
}
