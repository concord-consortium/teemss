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
package graph;

import waba.ui.*;
import waba.fx.*;

public abstract class GraphView extends Container
{
    Graphics myG = null;
    boolean drawn = false;

    public Graph2D graph = null;
    
    Image buffer = null;
    Graphics bufG;

    public GraphView(int w, int h)
    {
		width = w;
		height = h;

		buffer = new Image(w, h);
		bufG = new Graphics(buffer);
    }

    public void plot()
    {
		myG = createGraphics();
		if(myG == null) return;

		if(!drawn || graph.redraw){
			graph.draw(bufG);
			myG.copyRect(buffer, 0, 0, width, height, 0, 0); 	    
			drawn = true;
		} else {
			graph.plot(myG);
		}
    }

    public void draw()
    {
		drawn = false;
		plot();
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
		if(graph != null) graph.free();
    }

}
