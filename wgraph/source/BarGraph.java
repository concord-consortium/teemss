package graph;

import waba.util.*;
import waba.fx.*;
import waba.ui.*;

public class BarGraph extends Graph2D
{
    final static float DEFAULT_RANGE = (float)30.0;
    final static float DEFAULT_MIN = (float)10.0;

    BarSet barSet = null;
    public ColorAxis yaxis = null;
    public float range  = (float)30.0;
    public float minValue = (float)10.0;
    public String units = null;

    int width, height;
    int numBars;
    Vector bars = new Vector();
    FontMetrics	fontMet = 
	MainWindow.getMainWindow().getFontMetrics(MainWindow.defaultFont);

    int xOriginOff, yOriginOff;
    int dwWidth, dwHeight;



    public BarGraph(int w, int h)
    {
	width = w;
	height = h;

	range = DEFAULT_RANGE;
	minValue = DEFAULT_MIN;

	dwWidth = w - 50;
	dwHeight = h - 40;

	xOriginOff = 40;
	yOriginOff = h - 30;

	yaxis = new ColorAxis(minValue, minValue + range, -dwHeight, Axis.LEFT);
	yaxis.gridEndOff=dwWidth-1;

	barSet = new BarSet(yaxis, 1, BarSet.BOTTOM);	

	units = new String("C");
	
	numBars = 0;	
	
    }

    public void resize(int w, int h){}

    public void setXRange(float min, float range){}

    public void setYRange(float min, float range)
    {
	minValue = min;
	this.range = range;

	yaxis.dispMin = min;
	yaxis.setScale((yaxis.dispLen - yaxis.axisDir)/range);
    }

    public Object addBin(int location, String label)
    {
	Object [] objArray;
	Bar bar = new Bar();
	float oldValues [];
	int i;

	bar.index = numBars;
	bar.label = label;

	numBars++;
	// need to update list of probes
	// need to check that location is valid
	bars.insert(location, bar);

	// need to add a new bar to the graph
	barSet = new BarSet(yaxis, numBars, BarSet.BOTTOM);

	objArray = bars.toObjectArray();
	for(i=0; i < numBars; i++){
	    barSet.labels[i].setText(((Bar)objArray[i]).label);
	    ((Bar)objArray[i]).index = i;
	}

	oldValues = curValues;
	curValues = new float[numBars];
	if(oldValues != null){
	    for(i = 0; i<location; i++){
		curValues[i] = oldValues[i];
	    }
	    curValues[i++] = (float)0;
	    for(; i < numBars; i++){
		curValues[i] = oldValues[i-1];
	    }
	}

	bar.barGraph = this;
	return bar;
    }

    public void removeAllBins()
    {
	bars = new Vector();
	barSet = new BarSet(yaxis, 1, BarSet.BOTTOM);
	numBars = 0;
	redraw = true;
    }

    public boolean removeBin(Object id)
    {
	Object [] objArray;
	int i;
	
	int index = ((Bar)id).index;
	if(index == -1){
	    return false;
	}

	numBars--;
	bars.del(index);
	// need to add a new bar to the graph
	// need to reset labels as well
	if(numBars == 0){
	    barSet = new BarSet(yaxis, 1, BarSet.BOTTOM);
	} else {
	    barSet = new BarSet(yaxis, numBars, BarSet.BOTTOM);
	    objArray = bars.toObjectArray();
	    for(i=0; i < numBars; i++){
		barSet.labels[i].setText(((Bar)objArray[i]).label);
		((Bar)objArray[i]).index = i;
	    }

	}

	for(i = index; i < numBars; i++){
	    curValues[i] = curValues[i+1];
	}

	redraw = true;

	return true;
    }

    public void draw(Graphics g)
    {
	//	System.out.println("Redrawing bGraph");
	int w = width;
	int h = height;

	barSet.reset();

	g.setColor(255,255,255);
	g.fillRect(0,0,w,h);
	
	g.setColor(0,0,0);

	// DrawAxis
	yaxis.draw(g,xOriginOff,yOriginOff-1);

	barSet.draw(g,xOriginOff+1,yOriginOff,
		   dwWidth, dwHeight);
	
	needUpdate = true;
	plot(g);

	redraw = false;
    }

    float [] curValues;
    boolean needUpdate = true;

    public int plot(Graphics g)
    {
	float x = 0;
	float []y;
	Object [] objArray;
	int i;
	String label;


	if(g != null && numBars > 0 && needUpdate){
	    // Plot data
	    g.setColor(0,0,0);
	    barSet.addColorPoint(g, x, curValues);
	    needUpdate = false;
	}
	
	return 0;
    }

    public void reset()
    {
	needUpdate = true;
	barSet.reset();
    }

    public boolean addPoint(float x, float values[])
    {
	curValues[0] = 0;
	for(int i=0; i<values.length; i++){
	    if(curValues[i] != values [i]){
		needUpdate = true;		
		curValues[i] = values[i];
	    }
	}
	return true;
    }

    public boolean addPoint(Object binID, float x, float value)
    {
	int index = ((Bar)binID).index;
	if(curValues[index] != value){
	    curValues[index] = value;
	    needUpdate = true;
	}

	return true;
    }

}










