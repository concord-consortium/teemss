package org.concord.LabBook;

import graph.*;
import waba.ui.*;
import waba.util.*;

public class LObjGraphView extends LabObjectView
{
    LObjGraph graph;
    AnnotView av = null;

    Button doneButton = null;
    Vector bins = null;

    public LObjGraphView(LObjViewContainer vc, LObjGraph g)
    {
	super(vc);

	graph = g;
	lObj = g;	
    }

    public void addBins(Vector bs)
    {
	bins = bs;
    }

    public void layout(boolean sDone)
    {
	if(didLayout) return;
	didLayout = true;

	showDone = sDone;

	if(showDone){
	    doneButton = new Button("Done");
	    add(doneButton);
	} 
    }


    public void setRect(int x, int y, int width, int height)
    {
	super.setRect(x,y,width,height);
	if(!didLayout) layout(false);

	int curY = 1;
	int gHeight = height;

	if(showDone){
	    doneButton.setRect(width-30,height-15,30,15);
	    gHeight -= 16;
	}

	if(av != null){ remove(av); }
	
	av = new AnnotView(width-2, gHeight);
	av.setPos(1,curY);
	av.setRange(graph.xmin, graph.xmax, graph.ymin, graph.ymax);
	if(bins != null){
	    for(int i = 0; i < bins.getCount(); i++){
		av.addBin((Bin)bins.get(i));
	    }
	    av.lgView.autoScroll = false;
	}
	add(av);
    }

    public void close()
    {
	Debug.println("Got close in graph");
	graph.ymin = av.getYmin();
	graph.ymax = av.getYmax();
	graph.xmin = av.getXmin();
	graph.xmax = av.getXmax();
    }

    public void onEvent(Event e)
    {
	if(e.target == doneButton &&
	   e.type == ControlEvent.PRESSED){
	    if(container != null){
		container.done(this);
	    }	    
	}
    }

}
