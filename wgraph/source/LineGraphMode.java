/*
  Copyright (C) 2001 Concord Consortium

  This program is free software; you can redistribute it and/or
  modify it under the terms of the GNU General Public License
  as published by the Free Software Foundation; either version 2
  of the License, or (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package org.concord.waba.graph;

import waba.fx.*;
import waba.ui.*;

public class LineGraphMode extends Control
{
	int selIndex = 0;

	public int ARROW_SIZE = 1;

	public int getSelectedIndex(){return selIndex;}
	public void setSelectedIndex(int i)
	{
		selIndex = i;
		repaint();
	}

	public void onEvent(Event e)
	{
		if(e.type == PenEvent.PEN_DOWN){
			PenEvent pe = (PenEvent)e;
			if(pe.x < width/3){
				selIndex = 0;
				repaint();
			} else if(pe.x < 2*width/3){
				selIndex = 1;
				repaint();
			} else {
				selIndex = 2;
				repaint();				
			}
			postEvent(new ControlEvent(ControlEvent.PRESSED,this));
		}
	}

	public void onPaint(Graphics g)
	{
		int iWidth;

		g.setColor(0,0,0);
		int iHeight = height - 4;
		g.drawRect(0,0,width,height);
		g.drawLine(width/3,0,width/3,height);
		g.drawLine(2*width/3,0,2*width/3,height);

		switch(selIndex){
		case 0:
			g.fillRect(0,0,width/3,height);
			break;
		case 1:
			g.fillRect(width/3,0,width/3,height);
			break;
		case 2:
			g.fillRect(2*width/3,0,width/3,height);
			break;
		}

		// Draw translate
		if(selIndex == 0) g.setColor(255,255,255);
		else g.setColor(0,0,0);

		iWidth = width/3 - 3;
		g.translate(2,2);
		g.drawLine(iWidth/2,0,iWidth/2,iHeight - 1);
		g.drawLine(0,iHeight/2,iWidth - 1, iHeight/2);
		g.translate(iWidth/2,0);
		g.drawLine(0,0,-ARROW_SIZE,ARROW_SIZE);
		g.drawLine(0,0,ARROW_SIZE, ARROW_SIZE);
		
		g.translate(0,iHeight - 1);
		g.drawLine(0,0,-ARROW_SIZE, -ARROW_SIZE);
		g.drawLine(0,0,ARROW_SIZE, -ARROW_SIZE);

		g.translate(-iWidth/2,1-iHeight+iHeight/2);
		g.drawLine(0,0,ARROW_SIZE, ARROW_SIZE);
		g.drawLine(0,0,ARROW_SIZE, -ARROW_SIZE);

		g.translate(iWidth-1,0);
		g.drawLine(0,0,-ARROW_SIZE, ARROW_SIZE);
		g.drawLine(0,0,-ARROW_SIZE, -ARROW_SIZE);
		// End draw translate

		g.translate(1-iWidth + width/3,-iHeight/2);

		// Draw Selection
		if(selIndex == 1) g.setColor(255,255,255);
		else g.setColor(0,0,0);

		iWidth = 2*width/3 - width/3 - 3;
		g.drawDots(0,0, iWidth-1, 0);
		g.drawDots(0,0, 0, iHeight-1);
		g.drawDots(iWidth-1,iHeight-1, iWidth-1, 0);
		g.drawDots(iWidth-1,iHeight-1, 0, iHeight-1);
		// end draw selection

		g.translate(2*width/3 - width/3, 0);

		// Draw add mark
		if(selIndex == 2) g.setColor(255,255,255);
		else g.setColor(0,0,0);

		iWidth = width - 2*width/3 - 4;
		g.drawLine(iWidth/2,0,iWidth/2,iHeight-1);
		g.fillRect(0,0,iWidth/2,iHeight/2);
		// end draw mark

		g.translate(-2*width/3 - 2, -2);
		

	}


}
