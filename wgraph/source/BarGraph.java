package graph;

import waba.util.*;
import waba.fx.*;
import waba.ui.*;

public class BarGraph implements Graph2D
{
    final static float DEFAULT_RANGE = (float)30.0;
    final static float DEFAULT_MIN = (float)10.0;

    BarSet barSet = null;
    Axis yaxis = null;
    public float range  = (float)30.0;
    public float minValue = (float)10.0;
    public String units = null;

    int width, height;
    int numBars;
    Vector bars = new Vector();
    FontMetrics	fontMet = 
	MainWindow.getMainWindow().getFontMetrics(MainWindow.defaultFont);

    public BarGraph(int w, int h)
    {
	width = w;
	height = h;

	range = DEFAULT_RANGE;
	minValue = DEFAULT_MIN;

	yaxis = new Axis(minValue, minValue + range, 
			 Axis.LEFT);
	yaxis.ticsInside = false;
	yaxis.drawgrid = true;
	yaxis.use_exponent = false;

	barSet = new BarSet(yaxis, 1, Axis.BOTTOM);	

	units = new String("C");
	
	numBars = 0;	
	
    }

    public void resize(int w, int h){}

    public void setRange(float min, float range)
    {
	minValue = min;
	this.range = range;

	yaxis = new Axis(minValue, minValue + range, Axis.LEFT);
	yaxis.ticsInside = false;
	yaxis.drawgrid = true;
	yaxis.use_exponent = false;

	barSet.axis = yaxis;
    }

    public Object addBin(int location, String label)
    {
	Object [] objArray;

	numBars++;
	// need to update list of probes
	bars.add(label);

	// need to add a new bar to the graph
	barSet = new BarSet(yaxis, numBars, Axis.BOTTOM);

	objArray = bars.toObjectArray();
	for(int i=0; i < numBars; i++){
	    barSet.labels[i].text = (String)objArray[i];
	}

	curValues = new float[numBars];

	return label;
    }

    public boolean removeBin(Object id)
    {
	Object [] objArray;
	numBars--;
	
	int index = bars.find(id);
	if(index == -1){
	    return false;
	}

	bars.del(index);
	// need to add a new bar to the graph
	// need to reset labels as well
	if(numBars == 0){
	    barSet = new BarSet(yaxis, 1, Axis.BOTTOM);
	} else {
	    barSet = new BarSet(yaxis, numBars, Axis.BOTTOM);
	    objArray = bars.toObjectArray();
	    for(int i=0; i < numBars; i++){
		barSet.labels[i].text = (String)objArray[i];
	    }

	}

	return true;
    }

    public void draw(JGraphics g, int x, int y)
    {
	int w = width;
	int h = height;

	barSet.reset();

	g.setColor(255,255,255);
	g.fillRect(x,y,w,h);
	
	g.setColor(0,0,0);

	// Calculate data window
	// This should be a bit of an iteration
	// attempting to arrive at the approx
	int widthSpace = yaxis.getWidth(h);
	int heightSpace = barSet.getHeight(w);
	int xOriginOff, yOriginOff;
	int dwWidth, dwHeight;
	int topPadding = 10;

	widthSpace = yaxis.getWidth(h-heightSpace);
	heightSpace = barSet.getHeight(w-widthSpace);

	dwWidth = w - widthSpace;
	dwHeight = h - heightSpace - topPadding;

	xOriginOff = widthSpace;
	yOriginOff = h - heightSpace;

	// DrawAxis
	yaxis.setSize(-dwHeight, dwWidth);
	yaxis.draw(g,x+xOriginOff,y+yOriginOff-1);

	barSet.draw(g,x+xOriginOff+1,y+yOriginOff,
		   dwWidth, dwHeight);

	plot(g);


    }

    float [] curValues;

    public int plot(JGraphics g)
    {
	float x = 0;
	float []y;
	Object [] objArray;
	int i;
	String label;

	if(g != null && numBars > 0){
	    // Plot data
	    g.setColor(0,0,0);
	    barSet.addColorPoint(g, x, curValues);
	}
	
	return 0;
    }

    public void reset()
    {
	barSet.reset();
    }

    public int getNextBin()
    {
	return 1;
    }

    public boolean addPoint(int confId, int x, float values[])
    {
	curValues[0] = 0;
	for(int i=0; i<values.length; i++){
	    curValues[i] = values[i];
	}
	return true;
    }

    public boolean addPoint(int confId, int locId, int x, float value)
    {
	return false;
    }

    public int transLocId(int confId, int locId)
    {
	return 1;
    }

}










