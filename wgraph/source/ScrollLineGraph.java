package graph;

import waba.fx.*;

public class ScrollLineGraph extends LineGraph
{
    Image buffer = null;
    boolean autoScroll = true;
    int scrollOffset = 0;
    int maxScrollOffset = 0;
    JGraphics bufG;
    int visibleWidth;

    public ScrollLineGraph(int w, int h, int scrollWidth)
    {
	super(scrollWidth, h);
	visibleWidth = w;
	buffer = new Image(scrollWidth, h);
	bufG = new JGraphics(buffer);
    }

    public int plot(JGraphics g)
    {
	int maxXpos = super.plot(bufG);
	if(autoScroll){
	    scrollOffset = maxXpos - visibleWidth;
	    if(scrollOffset < 0){
		scrollOffset = 0;
	    }
	}
	g.copyRect(buffer, scrollOffset + xOriginOff+1, topPadding, 
		   visibleWidth - xOriginOff+1, height-topPadding, 
		   myX+xOriginOff+1, myY+topPadding);

	return 0;
    }

    public void plot(JGraphics g, int scrollOffset)
    {
	this.scrollOffset = scrollOffset;
	g.copyRect(buffer, scrollOffset + xOriginOff+1, topPadding, 
		   visibleWidth - xOriginOff+1, height-topPadding, 
		   myX+xOriginOff+1, myY+topPadding);

    }

    int myX = -1;
    int myY = -1;

    public void draw(JGraphics g, int x, int y)
    {
	if(myX == -1 || myY == -1){
	    super.draw(bufG, 0, 0);
	    myX = x;
	    myY = y;
	}

	// first copy the yaxis part (it never moves)
	g.copyRect(buffer, 0, 0, xOriginOff+1, height, myX, myY); 

	// Then copy the scrolling part
	g.copyRect(buffer, scrollOffset + xOriginOff+1, topPadding, 
		   visibleWidth - xOriginOff+1, height-topPadding, 
		   x+xOriginOff+1, y+topPadding);

    }

    public void setRange(float min, float range)
    {
	super.setRange(min, range);
	super.draw(bufG, 0, 0);
    }


}








