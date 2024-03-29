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

import waba.ui.*;
import waba.fx.*;

public abstract class GraphView extends Container
{
    boolean drawn = false;

    public Graph2D graph = null;
    
    Image buffer = null;
    Graphics bufG;

    public GraphView(int w, int h)
    {
		width = w;
		height = h;

    }

	public void makeActive(boolean flag)
	{
		if(flag && buffer==null){
			buffer = new Image(width, height);
			bufG = new Graphics(buffer);
		} else if(!flag && buffer != null){
			if(buffer != null) buffer.free();
			if(bufG != null) bufG.free();
			buffer = null;
			bufG = null;
		}
	}

	public void plot()
	{
		Graphics g = createGraphics();
		if(g == null) return;
		plot(g);
		g.free();
	}

    public void plot(Graphics myG)
    {
		if(myG == null) return;

		if(!drawn || graph.redraw){
			if(bufG != null){
				graph.draw(bufG);
				myG.copyRect(buffer, 0, 0, width, height, 0, 0); 	    
			} else {
				graph.draw(myG);
			}
			drawn = true;
		} else {
			graph.plot(myG);
		}
    }

	public void draw(Graphics g)
	{
		drawn = false;
		plot(g);
	}

    public void draw()
    {
		Graphics g = createGraphics();
		draw(g);
		g.free();
    }

    public void setPos(int x, int y)
    {
		setRect(x,y,width,height);
    }

    public void onPaint(Graphics g)
    {
		// redraw graph with latest data
		graph.draw(bufG);
		g.copyRect(buffer, 0, 0, width, height, 0, 0); 	    
		drawn = true;
    }

    public void free()
    {
		if(buffer != null) buffer.free();
		if(bufG != null) bufG.free();
		buffer = null;
		bufG = null;

		if(graph != null) graph.free();
    }

}
