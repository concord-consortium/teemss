package org.concord.waba.extra.ui;

import waba.ui.*;
import waba.fx.*;
import waba.util.*;

public class ScrollContainer extends Container
{
    int offX = 0, offY = 0;
    Container sub;
    Vector controls = new Vector();

    /*
     * Need to set the x and y correctly 
     * for the sub-components.
     * when they ask for create graphics
     */
    public Graphics createGraphics()
    {
	return null;
    }

    public void add(Control c)
    {
	controls.add(c);
    }

    public void onEvent(Event e)
    {
	
    }

    public void paintChildren(Graphics g, int x, int y, int width, int height)
    {

    }

    public void scroll(int down, int right)
    {
	offX += x;
	offY += y;
	sub.setRect(-offX, -offY, width, height);     	
    }

    public void setRect(int x, int y, int width, int height)
    {
	sub.setRect(-offX, -offY, width, height);     
    }
}
