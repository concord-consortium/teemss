package graph;

import waba.ui.*;
import waba.fx.*;
import waba.io.*;
import waba.sys.*;

public class RTGraph extends Object
{
    boolean palm = false;
    public int topPadding = 10;

    int xOriginOff, yOriginOff;
    int dwWidth, dwHeight;
    public Axis xaxis = null;
    public Axis yaxis = null;
    int platform = 0;

    int numSets = 0;

    public RTGraph(Axis x, Axis y)
    {	
	xaxis = x;
	yaxis = y;
    }

    public void draw(JGraphics g, int x, int y,
		     int w, int h)
    {
	g.setColor(255,255,255);
	g.fillRect(x,y,w,h);
	
	g.setColor(0,0,0);

	// Calculate data window
	calcDataWin(g, w, h);

	// DrawAxis
	yaxis.draw(g,x+xOriginOff,y+yOriginOff-1,-dwHeight, dwWidth);
	xaxis.draw(g,x+xOriginOff+1,y+yOriginOff,dwWidth, dwHeight);

    }

    public void calcDataWin(JGraphics g, int w, int h)
    {	
	// This should be a bit of an iteration
	// attempting to arrive at the approx
	int widthSpace = yaxis.getWidth(g,h);
	int heightSpace = xaxis.getHeight(g,w);
	widthSpace = yaxis.getWidth(g,h-heightSpace);
	heightSpace = xaxis.getHeight(g,w-widthSpace);

	dwWidth = w - widthSpace;
	dwHeight = h - heightSpace - topPadding;

	xOriginOff = widthSpace;
	yOriginOff = h - heightSpace;

    }

}


















