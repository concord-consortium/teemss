package org.concord.waba.extra.ui;

import waba.ui.*;

public class Choice extends extra.ui.List{
	String name = null;

	public Choice(String []options){
		super(options);
		initialYOffset = 15;
	}
	public Choice(){
		super();
	}

	public void setName(String n)
	{
		name = n;
	}

	boolean justPopped = false;
	boolean dragged = false;
	int lastY, lastX;
	/**
	 * Process pen and key events to this component
	 * @param event the event to process
	 */
	public void onEvent(Event event)
	{
		if (event instanceof PenEvent) {		   
			int px=((PenEvent)event).x;
			int py=((PenEvent)event).y - initialYOffset;
			switch (event.type){
			case PenEvent.PEN_DOWN:
				if (popup==null){
					justPopped = true;
					lastX = px;
					lastY = py;
					event.type = PenEvent.PEN_UP;
					super.onEvent(event);
					event.type = PenEvent.PEN_DOWN;
					break;
				} 
			case PenEvent.PEN_DRAG:
				int diffX = lastX - px;
				int diffY = lastY - py;
				lastX = px;
				lastY = py;
				if(diffX > 3 || diffX < -3 ||
				   diffY > 3 || diffY < -3) dragged = true;
				super.onEvent(event);
				break;
			case PenEvent.PEN_UP:
				if (justPopped){
					justPopped = false;
					if(!dragged) break;
				}
				dragged = false;
				super.onEvent(event);
				break;
			}
		} else {
			super.onEvent(event);
		}
	}
	
	public void onPaint(waba.fx.Graphics g)
	{
		if(fm == null) calcSizes();
		if (!isPopup()){
			int width = this.width;
			int height = this.height;
			int x2 = width - 1;
			int y2 = height - 1;
			if (waba.sys.Vm.isColor()){
				g.setColor(130, 130, 130);
				g.drawLine(0, 0, x2 - 1, 0);
				g.drawLine(0, 0, 0, y2 - 1);
				g.setColor(0, 0, 0);
				g.drawLine(1, 1, x2 - 2, 1);
				g.drawLine(1, 1, 1, y2 - 2);
				g.setColor(255, 255, 255);
				g.drawLine(0, y2, x2, y2);
				g.drawLine(x2, y2, x2, 0);
				g.setColor(255, 255, 255);
				g.fillRect(2, 2, width - 4, height - 4);
			}else{
				g.setColor(0, 0, 0);
				g.drawDots(0, y2, x2, y2);
			}
			if (waba.sys.Vm.isColor()){
				g.setColor(200, 200, 200);
				g.fillRect(x2 - 15,1,14,initialYOffset - 3);
				g.setColor(100, 100, 100);
			}else{
				g.setColor(0, 0, 0);
			}
			g.drawRect(x2 - 15,1,14,initialYOffset - 3);
			if (waba.sys.Vm.isColor()){
				g.setColor(0, 0, 0);
				g.drawLine(x2 - 14, 1, x2 - 2, 1);
				g.setColor(255, 255, 255);
				g.drawLine(x2 - 14, 2, x2 - 14, initialYOffset - 4);
				g.drawLine(x2 - 14, 2, x2 - 3, 2);
			}
			g.setColor(0, 0, 0);
			int currL = 7;
			int currY = 6;
			int currX = x2 - 12;
			while(currL > 0){
				g.setColor(0, 0, 0);
				g.drawLine(currX, currY, currX + currL, currY);
				if (waba.sys.Vm.isColor()){
					g.setColor(70, 70, 70);
					g.drawLine(currX + currL + 1, currY, currX + currL+1, currY);
				}
				currL -= 2;
				currX++;
				currY++;
			}
		}else{
		      drawList(g);
		      g.setColor(0,0,0);
		      g.drawLine(1,0,width-3,0);
		      g.drawLine(0,1,0,height-3);
		      g.drawLine(width-2,1,width-2,height-2);
		      g.drawLine(1,height-2,width-3,height-2);
		      g.drawLine(2,height-1,width-3,height-1);
		      g.drawLine(width-1,2,width-1,height-3);
		      g.drawLine(1, initialYOffset, width - 2, initialYOffset);

		}
		g.setColor(0, 0, 0);
		if(name != null){
			g.drawText(name,4,3);
		} else {
			g.drawText(getSelected(),4,3);
		}

 	}

	public void doPopup(){
		waba.ui.Control c = getParent();
		waba.ui.Window w = null;
		while(c != null){
			if(c instanceof waba.ui.Window){
				w = (waba.ui.Window)c;
				break;
			}
			c = c.getParent();
		}
	    
		popup=new extra.ui.Popup(this,w);
		popup.popup(x,y,expandedWidth+10,textHeight*numDisplayed+3+initialYOffset);
	}

}
