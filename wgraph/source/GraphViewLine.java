package graph;

import waba.ui.*;
import waba.fx.*;
import waba.util.*;
import waba.sys.*;

public class GraphViewLine extends GraphView
{
    final static float Y_MAX = (float)50.0;
    final static float Y_MIN = (float)-5.0;
    final static float X_MAX = (float)60.0;
    final static float X_MIN = (float)0.0;

    final static char DRAG_MODE = 'D';
    final static char ZOOM_MODE = 'Z';
    final static char ANNOT_MODE = 'A';

    public char mode = 'D';

    public char curChar = 'A';

    public float maxY, minY, maxX, minX;
    public String units = null;

    PropPage yAxisPage;
    PropPage xAxisPage;

    LineGraph lGraph;
    Annotation curAnnot = null;

    FloatConvert fConvert = new FloatConvert();

    public GraphViewLine(int w, int h)
    {
	super(w,h);

	fConvert.minDigits = 2;
	fConvert.maxDigits = 4;

	minY = Y_MIN;
	maxY = Y_MAX;
	minX = X_MIN;
	maxX = X_MAX;

	graph = lGraph = new LineGraph(w, h);
	lGraph.setYRange(minY, maxY - minY);
	lGraph.setXRange(0, minX, maxX - minX);

	units = new String("C");
	
	// Make the popups!
	yAxisPage = new PropPage(this);
	yAxisPage.addEdit("Max", 50);
	yAxisPage.addEdit("Min", 50);

	xAxisPage = new PropPage(this);
	xAxisPage.addEdit("Max", 50);
	xAxisPage.addEdit("Min", 50);
    }

    public void updateProp(PropPage pp, int action)
    {
	if(pp == yAxisPage){
	    switch(action){
	    case PropPage.UPDATE:
		maxY = fConvert.toFloat(((Edit)(pp.props.get(0))).getText());
		minY = fConvert.toFloat(((Edit)(pp.props.get(1))).getText());
		lGraph.setYRange(minY, maxY - minY);
		repaint();
		break;
	    case PropPage.REFRESH:
		maxY = lGraph.yaxis.min + (float)lGraph.yaxis.length/lGraph.yaxis.scale;
		minY = lGraph.yaxis.min;
		((Edit)(pp.props.get(0))).setText(fConvert.toString(maxY));
		((Edit)(pp.props.get(1))).setText(fConvert.toString(minY));
		break;
	    }
	} else if(pp == xAxisPage){
	    switch(action){
	    case PropPage.UPDATE:
		maxX = fConvert.toFloat(((Edit)(pp.props.get(0))).getText());
		minX = fConvert.toFloat(((Edit)(pp.props.get(1))).getText());
		lGraph.setXRange(minX, maxX - minX);
		repaint();
		break;
	    case PropPage.REFRESH:
		maxX = lGraph.xaxis.min + (float)lGraph.xaxis.dispLen/lGraph.xaxis.scale;
		minX = lGraph.xaxis.min;
		((Edit)(pp.props.get(0))).setText(fConvert.toString(maxX));
		((Edit)(pp.props.get(1))).setText(fConvert.toString(minX));
		break;
	    }
	} 
    }

    boolean autoScroll = true;
    float scrollFract = (float)0.25;
    float scrollStepSize = (float)0.15;
    int scrollSteps = 5;

    public void plot()
    {
	Bin bin = lGraph.curBin;
	float range;
	float scrollEnd;
	int myScrollStep = (int)(lGraph.dwWidth * scrollStepSize);


	if(bin.maxX > (lGraph.xaxis.dispMin + (float)lGraph.xaxis.dispLen / lGraph.xScale ) ||
	   lGraph.xaxis.drawnX == -1){
	    // scroll
	    range = lGraph.xRange;
	    scrollEnd = bin.maxX - range * scrollFract;
	    //		System.out.println("xRange: " + lGraph.xRange + ", scrollEnd: " + scrollEnd);
	    if(scrollEnd < (float)0)
		scrollEnd = (float)0;
	    if((scrollEnd - lGraph.xaxis.dispMin) * lGraph.xScale > (10 * myScrollStep)){
		myScrollStep = (int)((scrollEnd - lGraph.xaxis.dispMin) * lGraph.xScale + 2);
	    }
	    while((lGraph.xaxis.dispMin < scrollEnd) || 
		  (lGraph.xaxis.drawnX > (lGraph.xOriginOff + 4)) ||
		  (lGraph.xaxis.drawnX == -1)){
		lGraph.scroll(myScrollStep, 0);
		draw();
	    }
	} else {
	    super.plot();
	}
    }


    public boolean addPoint(Bin bin, float x, float y, boolean plot)
    {	    
	// Plot data
	if(!bin.addPoint(x, y)){
	    return false;
	}

	if(plot) plot();
        	    
	return true;
    }

    

    int downX, downY, dragX, dragY;
    boolean xAxisDown, yAxisDown, graphDown, barDown, annotDown;
    Timer timer = null;

    public Annotation selAnnot = null;
    
    float [] tempVal = new float [1];
    public void onEvent(Event e)
    {
	PenEvent pe;
	int moveX, moveY;
	float xChange;
	float yChange;
	int i;

	if(e.target == this){
	    if(e instanceof PenEvent){
		pe = (PenEvent)e;
		switch(e.type){
		case PenEvent.PEN_DOWN:
		    xAxisDown = yAxisDown = graphDown = annotDown = false;
		    Object obj = lGraph.getObjAtPoint(pe.x, pe.y);
		    if(obj == lGraph.xaxis){
			xAxisDown = true;
		    } else if(obj == lGraph.yaxis) {
			yAxisDown = true;
		    } else if(obj != null){
			Annotation oldAnnot = selAnnot;
			if(selAnnot != null){
			    selAnnot.selected = false;
			}
			if(mode == ANNOT_MODE){
			    curAnnot = lGraph.addAnnot("" + curChar, pe.x);
			    curChar++;
			    draw();
			    if(curAnnot != null)
				postEvent(new ControlEvent(1002, this));
			    return;
			} else {
			    if(obj == lGraph){
				selAnnot = null;
				graphDown = true;
			    } else {
				selAnnot = lGraph.getAnnotAtPoint(pe.x, pe.y);
				if(selAnnot != null){
				    selAnnot.selected = true;
				    lGraph.annots.del(lGraph.annots.find(selAnnot));
				    lGraph.annots.add(selAnnot);
				    annotDown = true;
				} 
				draw();
				    
			    }
			    if(oldAnnot != selAnnot) postEvent(new ControlEvent(1003, this));
			}
		    }

		    if(timer == null)
			timer = addTimer(750);
		    
		    downX = pe.x;
		    downY = pe.y;
		    dragY = 0;
		    dragX = 0;
		    break;
		case PenEvent.PEN_DRAG:
		case PenEvent.PEN_UP:
		    moveX = pe.x - downX;
		    moveY = pe.y - downY;
		    if(moveX < 0)
			dragX -= moveX;
		    else
			dragX += moveX;
		    if(moveY < 0)
			dragY -= moveY;
		    else
			dragY += moveY;

		    if(graphDown){
			if(timer != null){
			    removeTimer(timer);
			    timer = null;
			}
			lGraph.scroll(-moveX, -moveY);
			draw();
		    } else if(annotDown){
			if(timer != null){
			    removeTimer(timer);
			    timer = null;
			}
			if(selAnnot != null){
			    Axis xa = selAnnot.xaxis;
			    float newTime = (moveX + (selAnnot.time - xa.dispMin) * xa.scale)/
				xa.scale + xa.dispMin;
			    if(lGraph.getValue(newTime, xa, tempVal)){
				selAnnot.time = newTime;
				selAnnot.value = tempVal[0];
				postEvent(new ControlEvent(1003, this));
				draw();
			    }
			}

		    } else if(dragY > 10 || dragX > 10){ 
			if(timer != null){
			    removeTimer(timer);
			    timer = null;
			}
			if(yAxisDown){
			    if(lGraph.yOriginOff - pe.y > 20){
				yChange = (float)(lGraph.yOriginOff - pe.y)/ (float)(lGraph.yOriginOff - downY);
				
				lGraph.setYscale(lGraph.yaxis.scale * yChange);
				
				draw();
			    }
			}else if(xAxisDown && graph == lGraph){
			    if(pe.x - lGraph.xOriginOff > 20){
				xChange = (float)(lGraph.xOriginOff - pe.x)/ (float)(lGraph.xOriginOff - downX);
				lGraph.setXscale(lGraph.xScale * xChange);
				draw();
			    }
			}
		    }
		     
		    downX = pe.x;
		    downY = pe.y;
		    if(e.type == PenEvent.PEN_UP){
			graphDown = false;
			xAxisDown = false;
			yAxisDown = false;
			barDown = false;
			if(timer != null){
			    removeTimer(timer);
			    timer = null;
			}
		    }
		    break;
		}		
	    } else if(e.type == ControlEvent.TIMER){
		if(timer != null){
		    removeTimer(timer);
		    timer = null;
		} else {
		    // We have already cleared the timer so ignore this 
		    return;
		}
		if(xAxisDown)
		    xAxisPage.showProp();
		if(yAxisDown)
		    yAxisPage.showProp();
	    }
	}
    }
	



}












