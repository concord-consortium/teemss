package graph;

import waba.ui.*;
import waba.fx.*;
import waba.io.*;
import waba.sys.*;
import waba.util.*;
    
public class LineGraph extends Graph2D
{
    public final static int DEFAULT_STOR_SIZE = 5;

    public final static float X_MIN = 0;
    public final static float X_MAX = 100;
    public final static float Y_MIN = -200;
    public final static float Y_MAX = 200;

    boolean palm = false;
    public int annotTopY = 3;
    public int annotPadding = 1;
    public int topPadding = 5 + Annotation.height + annotPadding;
    public int rightPadding = 8;

    int xOriginOff, yOriginOff;
    int dwWidth, dwHeight;
    public Axis xaxis = null;
    public Axis [] xaxisArray = new Axis [10];
    Axis firstXaxis = null;
    int numXaxis;
    float xRange;
    float xScale;
    float yScale;

    public ColorAxis yaxis = null;
    int platform = 0;

    int width, height;

    int [][] lineColors = { {255, 0, 0},   // red
			    {0, 255, 0},   // green
			    {0, 0, 255},   // blue
			    {255, 255, 0}, // yellow
			    {255, 0, 255}, // purple
			    {0, 255, 255},}; // turquois

    protected int length = 0;

    // The active bins
    Vector activeBins = new Vector();
    int numBins = 0;
    int binStorSize = 0;

    Vector annots = new Vector();
    Bin curBin;
    Bin binArray [];

    TextLine yExpLabel = new TextLine("", TextLine.UP);

    boolean axisFlipped = false;
    Object [][] graphLayout = new Object[3][3];
    Object annotSection = new Object();

    // These hold the top left corner of the dataWin in 
    // screen coord.
    int dwX, dwY;

    public boolean profile = false;

    public LineGraph(int w, int h)
    {
	int i;
	width = w;
	height = h;
	dwX = 36;
	dwY = 10;

	dwWidth = w - dwX - 10;
	if(profile)
	    dwHeight = h - dwY - 30;
	else
	    dwHeight = h - dwY - 20;

	xaxis = new Axis(X_MIN, X_MAX, dwWidth, Axis.BOTTOM);
	xOriginOff = 35;
	xaxis.gridEndOff=-dwHeight+1;
	xaxis.max = (float)1E30;  // some huge number 

	xaxisArray[0] = xaxis;
	numXaxis = 1;
	xRange = X_MAX - X_MIN;
	xScale = xaxis.scale;
	firstXaxis = xaxis;

	yaxis = new ColorAxis(Y_MIN, Y_MAX, -dwHeight, Axis.LEFT);
	yOriginOff = dwHeight + dwY;
	yaxis.gridEndOff=dwWidth-1;

	int newSize = DEFAULT_STOR_SIZE;
	binStorSize = newSize;
	numBins = 0;
	binArray = new Bin [newSize];

	reset();

	graphLayout[0][0] = graphLayout[1][0] = graphLayout[2][0] = annotSection;
	graphLayout[0][1] = yaxis;
	graphLayout[1][1] = this;
	graphLayout[1][2] = xaxis;
	graphLayout[2][1] = graphLayout[0][2] = graphLayout[2][2] = null;

    }

    // need to find correct axis
    public Annotation addAnnot(String label, int pos)
    {
	Bin bin;
	Axis xa;
	float time;
	int i;

	xa = null;
	for(i=0; i < numXaxis; i++){
	    xa = xaxisArray[i];	    
	    if(pos*xa.axisDir > xa.drawnX*xa.axisDir && 
	       pos*xa.axisDir < xa.axisDir*(xa.drawnX + xa.axisDir + 
					    xa.dispLen)){
		break;
	    }
	}
	if(i != numXaxis){
	    time = (pos - xa.drawnX) / xa.scale + xa.dispMin;
	    return addAnnot(label, time, xa);
	}

	return null;
    }

    public Annotation addAnnot(String label, float time)
    {
	return addAnnot(label, time, xaxis);
    }

    public boolean getValue(float time, Axis xa, float []value)
    {
	int i,k;
	Bin bin = null;
	boolean valid = true;
	boolean atLeastOne = false;
	Annotation a = null;
	float [] binValue = new float [1];
	
	i = 0;
	for(k=0; k<numBins; k++){
	    bin = binArray[k];

	    if(xa == bin.xaxis){
		valid = valid && bin.getValue(time, binValue);
		value [i] = binValue[0];
		i++;
	    }
	}

	return valid && (i > 0);
    }


    float []  tempVal = new float[1];
    public Annotation addAnnot(String label, float time, Axis xa)
    {
	Annotation a = null;
	boolean valid;

	valid = getValue(time, xa, tempVal);

	if(valid){
	    a = new Annotation(label, time, tempVal[0], xa);
	    annots.add(a);	
	}
	
	return a;
    }

    public void drawAnnots(Graphics g)
    {
	Object [] annotObj = annots.toObjectArray();
	int i;
	Annotation a;
	int pos;
	int xPos;
	int valPos;

	for(i=0; i<annotObj.length; i++){
	    a = (Annotation)annotObj[i];
	    if(a.xaxis.drawnX != -1){
		pos = (int)((a.time - a.xaxis.dispMin) * a.xaxis.scale);
		if((pos*a.xaxis.axisDir >= 0) && 
		   (pos*a.xaxis.axisDir < a.xaxis.axisDir*a.xaxis.dispLen)){
		    xPos = pos + a.xaxis.drawnX + a.xaxis.axisDir;
		    a.draw(g, xPos- a.width/2, annotTopY);
		    if(a.selected){
			g.setColor(0,0,0);
			g.drawLine(xPos, dwY, xPos, dwY + dwHeight - 1);
			valPos = (int)((a.value - yaxis.min) * yaxis.scale) + yaxis.drawnOffset;
			g.drawLine(dwX, valPos, dwX + dwWidth - 1, valPos);
		    }
		}
	    }
	}
    }

    public void resize(int w, int h){}

    // return the maximum x offset plotted
    public int plot(Graphics g)
    {
	int i,j,k;
	int lastOffset, lastPlottedOffset;
	int [] binPoints;
	int lastX, lastY, curX, curY;
	Bin bin;
	Axis xa;

	// set the clipping region
	g.setClip(xOriginOff+1, yOriginOff-dwHeight, dwWidth, dwHeight);

	for(k=0; k<numBins; k++){
	    bin = binArray[k];
	    xa = bin.xaxis;

	    if(xa.drawnX == -1) continue;

	    bin.update();

	    if(bin.numPoints < 2) continue;
	    
	    binPoints = bin.points;
	    lastOffset = bin.numPoints*3;
	    
	    g.setColor(bin.color[0], bin.color[1], bin.color[2]);
	    int xOffset = (int)((xa.dispMin - bin.refX) * xa.scale);
	    int xTrans = xa.drawnX - xOffset + xa.axisDir;
	    int yTrans = (int)((bin.refY - yaxis.dispMin) * yaxis.scale) + yaxis.drawnY + yaxis.axisDir;
	    g.translate(xTrans, yTrans);

	    if(bin.lastPlottedPoint == -1){
		i=0;
		lastX = binPoints[i++];
		lastY = binPoints[i++];
		i++;
		
	    } else {
		lastY = bin.lastPlottedY;
		lastX = bin.lastPlottedX;
		i=bin.lastPlottedPoint*3 + 3;
		
	    }

	    for(; i<lastOffset;){
		curX = binPoints[i++];
		curY = binPoints[i++];
		i++;
		
		if(curX > (xOffset - 1) && curX <= (xOffset + xa.dispLen))
		    g.drawLine(lastX, lastY, curX, curY);
		
		lastY = curY;
		lastX = curX;
	    }
	    
	    g.translate(-xTrans, -yTrans);

	    bin.lastPlottedPoint = bin.numPoints - 1;
	    bin.lastPlottedY = lastY;
	    bin.lastPlottedX = lastX;

	}

	g.clearClip();
	return 0;
    }


    // This is the yucky one
    public boolean removeBin(Object id)
    {
	// need to remove the arrays
	// need to shift the rest of the arrays down
	// need to go through the vector of bins and trash the 
	// deleted one and update the rest of them.
	
	/*
	int newSize = binStorSize * 3 / 2;

	int newNumPoints [] = new int [newSize];
	int newLastPoint [] = new int [newSize];
	int newPoints [] = new int [newSize][];
	int newValues [] = new int [newSize][];
	
	Vm.copyArray(points, 0, newPoints, 0, numBins);
	Vm.copyArray(values, 0, newValues, 0, numBins);
	Vm.copyArray(numPoints, 0, newNumPoints, 0, numBins);
	Vm.copyArray(lastPlottedPoint, 0, newLastPoint, 0, numBins);
	binStorSize = newSize;

	*/
	return true;
    }

    public void setYRange(float min, float range)
    {       
	yaxis.setDispMin(min);

	if(yaxis.scale != range){
	    yaxis.setScale((yaxis.dispLen - yaxis.axisDir)/ range);
	    needRecalc = true;
	}
    }  

    public void setYMin(float min)
    {
	yaxis.setDispMin(min);
    }
	

    boolean needRecalc = false;
    boolean startPosChanged = true;

    public void setXRange(float min, float range)
    {
	xaxis.setDispMin(min);
	setXRange(range);
    }

    public void setXRange(float range)
    {
	if(range != xRange){
	    setXscale(dwWidth / range);
	    needRecalc = true;
	    xRange = range;
	}
    }

    // Set the min for the specified axis
    // Set the size of the rest of the axis
    // ickkkkk.
    public void setXRange(int col, float min, float range)
    {
	int i;
	int curStartPos;
	float max;

	// We'll just blow this off for now
	setXRange(min, range);
    }

    int xaxisStartPos = 0;
    /* 
     * This is a very tricky piece of code where we change
     * the xScale.  However we need to keep the xaxisStartPos
     * in the same place
     */
    public void setXscale(float newScale)
    {
	int i;
	int oldScaleSP = 0;
	int newXaxisStartPos = -1;
	int curStartPos = 0;
	Axis xa;

	xScale = newScale;
	needRecalc = true;
	for(i =0; i < numXaxis-1; i++){
	    xa = xaxisArray[i];
	    if(newXaxisStartPos == -1 &&
	       xaxisStartPos < (oldScaleSP + (int)(xa.max * xa.scale))){
		newXaxisStartPos = curStartPos + (int)(xa.dispMin * xScale);
	    }
	    oldScaleSP += 10 + xa.dispMin * xScale + xa.dispLen;
	    xa.setScale(xScale);
	    curStartPos += 10 + (int)(xa.max * xa.scale);
	}

	xa = xaxisArray[i];
	xa.setScale(xScale);
	if(newXaxisStartPos == -1){
	    newXaxisStartPos = curStartPos + (int)(xa.dispMin * xScale);
	}

	xaxisStartPos = newXaxisStartPos;
    }

    public void setYscale(float scale)
    {
	yaxis.setScale(scale);
	yScale = scale;
	needRecalc = true;
    }

    public void scroll(int xDist, int yDist)
    {

	xaxisStartPos += xDist;
	if(xaxisStartPos < 0){
	    xaxisStartPos = 0;
	}

	if(yDist != 0){
	    yaxis.setDispMin(yaxis.dispMin + yDist / yaxis.scale);
	}
    }

    public void incBinStor()
    {
	int newSize = binStorSize * 3 / 2;

	Bin newBinArray [] = new Bin [newSize];
	
	Vm.copyArray(binArray, 0, newBinArray, 0, numBins);
	binArray = newBinArray;

	binStorSize = newSize;
    }

    // return a Object linked to this location
    // we are ignoring location for now
    public Bin addBin(int location, String label, boolean newCollection)
    {
	Bin bin;

	// We need to add a new xaxis
	if(newCollection){

	    // This is a hack 
	    if(curBin.maxX < 0 || curBin.numPoints < 3){
		curBin.reset();
		return curBin;
	    }

	    // Technically this should search through all the active bins
	    // and get the max
	    xaxis.max = curBin.maxX;

	    if(numXaxis >= xaxisArray.length){
		Axis [] newAxis = new Axis[(numXaxis * 3)/ 2];
		Vm.copyArray(xaxisArray, 0, newAxis, 0, numXaxis);
		xaxisArray = newAxis;
	    } 
	    xaxisArray[numXaxis] = xaxis = new Axis(X_MIN, dwWidth, xaxis.scale, Axis.BOTTOM);
	    xaxis.gridEndOff=-dwHeight+1;
	    xaxis.max = (float)1E30;  // some huge number 

	    graphLayout[1][2] = xaxis;
	    numXaxis++;

	    activeBins = new Vector();

	    startPosChanged = true;
	    needRecalc = true;	    
	}

	if(numBins >= binStorSize){
	    incBinStor();
	}

	// setup points, reset to the begining of the graph
	binArray[numBins] = curBin = new Bin(this);	
	curBin.xaxis = xaxis;
	curBin.yaxis = yaxis;
	curBin.color = lineColors[activeBins.getCount()];
	curBin.minX = 0f;

	// This is a hack
	curBin.minY = -100f;

	numBins++;
	activeBins.add(curBin);

	return curBin;
    }

    void drawXaxis(Graphics g)
    {
	int xaxisOffset = xOriginOff;
	int curStartPos = 0;
	Axis xa;
	int axisLen;
	int i;

	// System.out.println("Drawing x axis");
	xRange = dwWidth / xScale;	
	curStartPos = 0;

	// Find the first visible axis
	// And set all the axis drawnX to -1;
	int firstVisible = -1;
	int xaScMax;

	for(i=0;i<numXaxis;i++){
	    xa = xaxisArray[i];
	    xa.drawnX = -1;
	    if(xa.max > (float)1E25)
		// This is the active axis
		xaScMax = (int)0x7FFFFFF - curStartPos - 10;
	    else
		xaScMax = (int)(xa.max * xa.scale);
	    if(firstVisible == -1){
		if(xaxisStartPos < (curStartPos + xaScMax)){
		    firstVisible = i;
		    // Once we found a visible one we need to leave the
		    // curStartPos at the begining of this axis
		    continue;
		}
		curStartPos += xaScMax + 10;
	    }
	}

	if(firstVisible == -1){
	    //	    System.out.println("No visible xaxis??");
	    return;
	}

	int endPoint = xaxisStartPos + dwWidth;
	int dispOffset = 0;
	for(i=firstVisible;i<numXaxis;i++){
	    xa = xaxisArray[i];
	    if(curStartPos >= endPoint){
		//our drawing work is done
		//we still need to set the remaining axis drawnX to -1
		break;
	    }

	    g.setColor(0,0,0);
	    if(curStartPos < xaxisStartPos){
		// This axis starts before the visible area so we need to offset it
		dispOffset = xaxisStartPos - curStartPos;
		xa.setDispOffset(X_MIN, dispOffset);
		curStartPos = xaxisStartPos;
		if(xa.max > (float)1E25){
		    // this is the active axis
		    axisLen = dwWidth;
		} else {
		    axisLen = (int)(xa.max * xa.scale) - dispOffset;
		}
	    } else {
		// The axis starts in the visible area
		// Need to draw the beginning line of the axis
		// draw the next axis
		g.drawLine(xaxisOffset + curStartPos - xaxisStartPos, yOriginOff, 
			   xaxisOffset + curStartPos - xaxisStartPos, yOriginOff - dwHeight);
		// And set the dispMin correctly
		xa.setDispMin(X_MIN);
		if(xa.max > (float)1E25){
		    // this is the active axis
		    axisLen = dwWidth;
		} else {
		    axisLen = (int)(xa.max * xa.scale);
		}
	    } 
	
	    if(axisLen + (curStartPos - xaxisStartPos) >= dwWidth){
		// The axis extends beyond the visible area
		xa.dispLen = dwWidth - (curStartPos - xaxisStartPos);
	    } else {
		// This axis ends before the end of the visible area so draw an end line
		xa.dispLen = axisLen;
		g.drawLine(xaxisOffset + curStartPos - xaxisStartPos + axisLen + 1, yOriginOff, 
			   xaxisOffset + curStartPos - xaxisStartPos + axisLen + 1, yOriginOff - dwHeight);
	    }
	    
	    xa.draw(g, xaxisOffset + (curStartPos - xaxisStartPos), yOriginOff);
	    curStartPos += axisLen + 10;
	}

	/*
	endTime = Vm.getTimeStamp();
	g.drawText(endTime - startTime + "", xText, yText);
	startTime = endTime;
	xText += 20;
	*/		

    }
	

    public void drawAxis(Graphics g)
    {
	g.setColor(255,255,255);
	g.fillRect(0,0,width,height);
	
	g.setColor(0,0,0);

	if(profile)
	    yaxis.lGraph = this;
	else 
	    yaxis.lGraph = null;

	yaxis.draw(g,xOriginOff,yOriginOff);
       
	if(yaxis.labelExp != 0){
	    yExpLabel.setText("10^" + yaxis.labelExp);
	    yExpLabel.draw(g, 1, 10);
	} 
      
	if(profile){
	    endTime = Vm.getTimeStamp();
	    g.drawText(endTime - startTime + "", xText, yText);
	    startTime = endTime;
	    xText += 20;
	}

	drawXaxis(g);

    }

    public int yText = 0;
    int xText =0;
    int beginTime, startTime,endTime;

    public void draw(Graphics g)
    {
	yText = height-10;
	xText =0;
	boolean dataWinChanged;
	int i;
	int curStartPos;
       
	if(profile){
	    g.setColor(255,255,255);
	    g.fillRect(0, yText, 200, 30);
	    g.setColor(0,0,0);
	    startTime = beginTime = Vm.getTimeStamp();
	}

	drawAxis(g);

	for(i=0; i<numBins; i++){
	    binArray[i].lastPlottedPoint = -1;
	}

	if(profile){
	    endTime = Vm.getTimeStamp();
	    g.drawText(endTime - startTime + "", xText, yText);
	    startTime = endTime;
	    xText += 20;
	}

	Bin bin;
	Axis xa;

	if(needRecalc){
	    for(int k=0; k<numBins; k++){
		binArray[k].recalc();		
	    }
	    needRecalc = false;
	}

	if(profile){
	    endTime = Vm.getTimeStamp();
	    g.drawText(endTime - startTime + "", xText, yText);
	    startTime = endTime;
	    xText += 20;
	}

	plot(g);

	drawAnnots((Graphics)g);

	if(profile){
	    endTime = Vm.getTimeStamp();
	    g.drawText(endTime - startTime + "", xText, yText);
	    startTime = endTime;
	    xText += 20;

	    g.drawText(endTime - beginTime + "", xText, yText);
	    startTime = endTime;
	    xText += 20;      
	}

	redraw = false;
    }

    public boolean calcDataWin(Graphics g, int w, int h)
    {	
	// This should be a bit of an iteration
	// attempting to arrive at the approx
	int widthSpace = -1*(yaxis.getOutsideSize());
	int heightSpace = xaxis.getOutsideSize();
	int bottomAxisSpace = h - yOriginOff;
	while((widthSpace + 1) > xOriginOff || (heightSpace + 1) > bottomAxisSpace){
	    xOriginOff = widthSpace + 1;
	    bottomAxisSpace = heightSpace + 1;
	    dwWidth = width - rightPadding - widthSpace + 1;
	    dwHeight = height - topPadding - bottomAxisSpace;
	    yaxis.setScale(((float)dwHeight * yaxis.scale) / (float)(yaxis.dispLen*yaxis.axisDir));
	    yaxis.dispLen = -dwHeight;
	    yaxis.gridEndOff = dwWidth;
	    xaxis.setScale((float)dwWidth / xRange);
	    xaxis.dispLen = dwWidth;
	    xaxis.gridEndOff = -dwHeight;
	    widthSpace = -1*(yaxis.getOutsideSize());
	    heightSpace = xaxis.getOutsideSize();
	}
	yOriginOff = h - bottomAxisSpace - 1;

	return true;
    }


    public void reset()
    {
	int i;

	length = 0;

	xaxisArray[0] = xaxis;
	xaxis.dispLen = dwWidth;
	xaxis.setDispMin(X_MIN);
	numXaxis = 1;
	xaxisStartPos = 0;

	Object [] binObjs = activeBins.toObjectArray();
	numBins = binObjs.length;

	Bin bin;
	for(i=0; i<numBins; i++){
	    bin = (Bin)binObjs[i];
	    binArray[i] = bin;
	    bin.reset();
	}
	

	// remove annotations
	annots = new Vector();
    }

    /*
     * This doesn't make since in the new 
     * form of stuff 
     */
    public boolean addPoint(float x, float newValues[])
    {
	int k;
	int offset;
	Bin bin;

	for(k=0; k<numBins; k++){
	    binArray[k].addPoint(x, newValues[k]);
	}

	return true;
    }

    /*
     * There are 9 sections that we can be in:
     *  1 2 3
     *  4 5 6
     *  7 8 9
     * The Data Window  will always be in section 5
     * The axis could be in 4,2,6,8
     * The annotations can be in three neighboring sections on
     *   any of the sides.
     * We won't do any bounds checking.
     * We use the array graphLayout that represents the grid above.
     */
    Object getObjAtPoint(int x, int y)
    {
	int xPos, yPos;
	Object obj = null;

	if(x < dwX) xPos = 0;
	else if(x < (dwX  + dwWidth)) xPos = 1;
	else xPos = 2;

	if(y < dwY) yPos = 0;
	else if(y < (dwY + dwHeight)) yPos = 1;
	else yPos = 2;

	return graphLayout[xPos][yPos];
    }

    /*
     *  We need to check the bounds or only take
     * one direction or something
     */
    Annotation getAnnotAtPoint(int x, int y){
	
	// Track down which annotation
	Object [] annotObj = annots.toObjectArray();
	int i;
	Annotation a;
	int pos;
	
	// We draw them forward, so we search backwards
	//   because they might overlap
	for(i=annotObj.length-1; i>=0; i--){
	    a = (Annotation)annotObj[i];
	    if(a.xaxis.drawnX != -1){
		pos = (int)((a.time - a.xaxis.dispMin) * a.xaxis.scale);
		if((pos*a.xaxis.axisDir >= 0) && 
		   (pos*a.xaxis.axisDir < a.xaxis.axisDir*a.xaxis.dispLen)){ 
		    // Need to make this layout independent
		    
		    if(x >= (pos + a.xaxis.drawnX + a.xaxis.axisDir - a.width/2) &&
		       x < (pos + a.xaxis.drawnX + a.xaxis.axisDir + a.width/2))
			return a;
		}
	    }
	}
	
	// We didn't find any anotations
	return null;			
    }

}











