package graph;

import waba.ui.*;
import waba.fx.*;

public abstract class GraphView extends Container implements PropObject
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
	myG = createGraphics();
	if(myG != null){
	    graph.draw(bufG);
	    myG.copyRect(buffer, 0, 0, width, height, 0, 0); 	    
	    drawn = true;
	}
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

}
