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
import waba.io.*;
import waba.sys.*;
import waba.util.*;
import extra.util.*;    
import org.concord.waba.extra.event.*;

public class LineGraph extends Graph2D
	implements ActionListener
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
    public int dwWidth, dwHeight;
    public SplitAxis xaxis = null;
    public Axis yaxis = null;

    int platform = 0;

    int width, height;

    int [][] lineColors = { {255,  0,  0},   // red
							{0,  255,  0},   // green
							{0,  0,  255},   // blue
							{255, 255, 0}, // yellow
							{255, 0, 255}, // purple
							{0, 255, 255},}; // turquois

    protected int length = 0;

    // The active bins
    Vector activeBins = new Vector();
    int numBins = 0;
    int binStorSize = 0;

    public Vector annots = new Vector();
    Bin curBin;
    Bin binArray [];

    boolean axisFlipped = false;
    Object [][] graphLayout = new Object[3][3];
    Object annotSection = new Object();

    // These hold the top left corner of the dataWin in 
    // screen coord.
    int dwX, dwY;

    public boolean profile = false;

    public LineGraph(int w, int h, int xOrigin, int yOrigin,
					 SplitAxis xAx, Axis yAx)
	{ 
		int i;
		width = w;
		height = h;

		dwX = xOrigin;
		dwY = yOrigin;

		dwWidth = w - dwX - 10;
		if(profile)
			dwHeight = h - dwY - 40;
		else
			dwHeight = h - dwY - 30;

		yOriginOff = dwHeight + dwY;
		xOriginOff = dwX;

		xaxis = xAx;
		xaxis.setLength(dwWidth);
		xaxis.gridEndOff = -dwHeight+1;
		xaxis.addActionListener(this);

		yaxis = yAx;
		yaxis.setLength(-dwHeight);
		yaxis.gridEndOff = dwWidth-1;
		yaxis.addActionListener(this);

		int newSize = DEFAULT_STOR_SIZE;
		binStorSize = newSize;
		numBins = 0;
		binArray = new Bin [newSize];

		reset();

		graphLayout[1][0] = graphLayout[2][0] = annotSection;
		graphLayout[0][1] = graphLayout[0][0] = yaxis;
		graphLayout[1][1] = this;
		graphLayout[1][2] = graphLayout[2][2] = xaxis;
		graphLayout[2][1] = graphLayout[0][2] = null;

    }

	public void free(){}

    // need to find correct axis
    public Annotation addAnnot(String label, int pos)
    {
		Axis [] aPtr = new Axis [1];
		float time = xaxis.getValue(pos, aPtr);

		if(aPtr[0] != null){
			return addAnnot(label, time, aPtr[0]);
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

		int xaIndex = xaxis.getIndex(xa);
		if(xaIndex < 0) return false; 

		i = 0;
		for(k=0; k<numBins; k++){
			bin = binArray[k];

			if(xaIndex == bin.xaIndex){
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

	public static int FIXED_PT_DENOM = 1000;

    // return the maximum x offset plotted
    public int plotEst(Graphics g)
    {
		int xNum = (int)(xaxis.scale*(float)FIXED_PT_DENOM/oldXscale); 
		int yNum = (int)(yaxis.scale*(float)FIXED_PT_DENOM/oldYscale);
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
			//	    System.out.println("Getting axis " + bin.xaIndex + " for bin " + k);
			xa = xaxis.getAxis(bin.xaIndex);

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
			} else {
				i = bin.lastPlottedPoint*3;
			}
				
			lastX = binPoints[i++] * xNum / FIXED_PT_DENOM;
			lastY = binPoints[i++] * yNum / FIXED_PT_DENOM;
			i++;
		
			if(yaxis.scale == oldYscale ){
				for(; i<lastOffset;){
					curX = binPoints[i++] * xNum / FIXED_PT_DENOM;
					curY = binPoints[i++];
					i++;
					
					if(curX > (xOffset - 1) && curX <= (xOffset + xa.dispLen))
						g.drawLine(lastX, lastY, curX, curY);
					
					lastY = curY;
					lastX = curX;
				}
			} else if(xaxis.scale == oldXscale){
				for(; i<lastOffset;){
					curX = binPoints[i++];
					curY = binPoints[i++] * yNum / FIXED_PT_DENOM;
					i++;
					
					if(curX > (xOffset - 1) && curX <= (xOffset + xa.dispLen))
						g.drawLine(lastX, lastY, curX, curY);
					
					lastY = curY;
					lastX = curX;
				}
			} else {
				for(; i<lastOffset;){
					curX = binPoints[i++] * xNum / FIXED_PT_DENOM;
					curY = binPoints[i++] * yNum / FIXED_PT_DENOM;
					i++;
					
					if(curX > (xOffset - 1) && curX <= (xOffset + xa.dispLen))
						g.drawLine(lastX, lastY, curX, curY);
					
					lastY = curY;
					lastX = curX;
				}
			}
	    
			g.translate(-xTrans, -yTrans);

			bin.lastPlottedPoint = bin.numPoints - 1;

		}

		g.clearClip();
		return 0;
    }

	public float maxVisY;
	public float minVisY;

    public boolean calcVisibleRange()
    {
		int i,j,k;
		int lastOffset, lastPlottedOffset;
		int [] binPoints;
		int curX, curMinY, curMaxY;
		int minY, maxY;
		float minYf, maxYf;
		Bin bin;
		Axis xa;
		boolean setRanges = false;

	    maxVisY = (float)-(0x7FFFFFF);
		minVisY = (float)(0x7FFFFFF);

		for(k=0; k<numBins; k++){
			bin = binArray[k];
			//	    System.out.println("Getting axis " + bin.xaIndex + " for bin " + k);
			xa = xaxis.getAxis(bin.xaIndex);

			if(xa.drawnX == -1 || bin.numPoints <= 1) continue;
	    
			
			binPoints = bin.points;
			lastOffset = bin.numPoints*3;
	    
			minY = (0x7FFFFFF);
			maxY = -(0x7FFFFFF);

			int xOffset = (int)((xa.dispMin - bin.refX) * xa.scale);
		
			for(i=0; i<lastOffset;){
				curX = binPoints[i++];
				curMinY = binPoints[i++] - (binPoints[i] & 0xFFFF);					
				curMaxY = binPoints[i-1] + (binPoints[i] >> 16);
				i++;
		
				if(curX > (xOffset - 1) && curX <= (xOffset + xa.dispLen)){
					if(curMaxY > maxY) maxY = curMaxY;
					if(curMinY < minY) minY = curMinY;
				}		
			}	    
			
			minYf = ((float)minY / yaxis.scale + bin.refY);
			maxYf = ((float)maxY / yaxis.scale + bin.refY);
			float temp;
			if(minYf > maxYf){
				temp = minYf;
				minYf = maxYf;
				maxYf = temp;
			}

			if(minYf < minVisY) minVisY = minYf;
			if(maxYf > maxVisY) maxVisY = maxYf;

			setRanges = true;
		}		

		
		return setRanges;
    }

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
			//	    System.out.println("Getting axis " + bin.xaIndex + " for bin " + k);
			xa = xaxis.getAxis(bin.xaIndex);

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
			} else {
				// Start 1 before the lastPlottedPoint, because the lastpp might have
				// moved
				i = bin.lastPlottedPoint*3 - 3;
			}
				
			lastX = binPoints[i++];
			lastY = binPoints[i++];
			i++;
		
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

    boolean needRecalc = false;
	public void actionPerformed(ActionEvent e)
	{
		if(e.type == Axis.SCALE_CHANGE) needRecalc = true;
	}

    // Set the min for the specified axis
    // Set the size of the rest of the axis
    // ickkkkk.
	/*
    public void setXRange(int col, float min, float range)
    {
		int i;
		int curStartPos;
		float max;

		// We'll just blow this off for now
		xaxis.setRange(min, range);
    }
	*/

	boolean needPlotEst = false;
	float oldXscale = 0f;
	float oldYscale = 0f;

	public void setXscaleEst(float newScale)
	{
		boolean oldRecalc = needRecalc;
		if(!needPlotEst && !needRecalc){
			oldXscale = xaxis.scale;
			oldYscale = yaxis.scale;
		}
		xaxis.setScale(newScale);
		needRecalc = oldRecalc;
		if(!needRecalc){
			needPlotEst = true;
		}
	}

	public void setYscaleEst(float scale)
	{
		boolean oldRecalc = needRecalc;
		if(!needPlotEst){
			oldXscale = xaxis.scale;
			oldYscale = yaxis.scale;
		}
		yaxis.setScale(scale);
		needRecalc = oldRecalc;
		if(!needRecalc){			
			needPlotEst = true;
		}		
	}

    public void scroll(int xDist, int yDist)
    {
		xaxis.scrollStartPos(xDist);

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
    

    public boolean addXaxis()
    {
		if(curBin == null) return false;

		// This is a hack 
		if(curBin.maxX < 0 || curBin.getNumVals() < 3){
			curBin.reset();
			return false;
		}

		// Technically this should search through all the active bins
		// and get the max
		int newAxIndex = xaxis.addAxis(curBin.maxX);

		activeBins = new Vector();
		needRecalc = true;	    
		return true;
    }

    public void addBin(Bin bin)
    {
			   
		// need to add xAxis as necessary
		// this will only work if the bins are add incrementally
		if(bin.xaIndex >= xaxis.numAxis){
			if(!addXaxis()){
				//		Debug.println("Failed adding xAxis");
			}
		}

		curBin = bin;

		if(numBins >= binStorSize){
			incBinStor();
		}

		binArray[numBins] = bin;
		bin.color = lineColors[0];

		numBins++;
    }

    // return a Object linked to this location
    // we are ignoring location for now
    public Bin addBin(int location, String label, boolean newCollection)
    {
		Bin bin;
		boolean needNewBin = true;

		// We need to add a new xaxis
		if(newCollection){
			if(!addXaxis()){
				needNewBin = false;
		
			}
		}

		if(needNewBin){
			// setup points, reset to the begining of the graph
			curBin = new Bin(xaxis.numAxis-1);	

			//	curBin.minX = 0f;
			curBin.label = label;

			// This is a hack
			// curBin.minY = -100f;
			addBin(curBin);
			activeBins.add(curBin);
		}

		return curBin;
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
		yaxis.drawAxisLabel(g, 0);
      
		if(profile){
			endTime = Vm.getTimeStamp();
			g.drawText(endTime - startTime + "", xText, yText);
			startTime = endTime;
			xText += 20;
		}

		xaxis.draw(g, xOriginOff, yOriginOff);
		xaxis.drawAxisLabel(g, height-1);
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
		int k;

		if(needRecalc){
			for(k=0; k<numBins; k++){
				binArray[k].recalc(xaxis.scale, yaxis.scale);		
			}
			needRecalc = false;
			needPlotEst = false;
		} 

		if(profile){
			endTime = Vm.getTimeStamp();
			g.drawText(endTime - startTime + "", xText, yText);
			startTime = endTime;
			xText += 20;
		}

		if(!needPlotEst){
			plot(g);
		} else {
			plotEst(g);
		}

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
			xaxis.setScale((float)dwWidth / 1f);
			xaxis.dispLen = dwWidth;
			xaxis.gridEndOff = -dwHeight+1;
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

		xaxis.reset();

		for(i=0; i<numBins; i++){
			binArray[i] = null;
		}

		Object [] binObjs = activeBins.toObjectArray();
		numBins = binObjs.length;

		Bin bin;
		for(i=0; i<numBins; i++){
			bin = (Bin)binObjs[i];
			binArray[i] = bin;
			bin.reset();
			bin.xaIndex = 0;
		}
	

		// remove annotations
		annots = new Vector();
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

		if(x <= dwX) xPos = 0;
		else if(x < (dwX  + dwWidth)) xPos = 1;
		else xPos = 2;

		if(y <= dwY) yPos = 0;
		else if(y < (dwY + dwHeight)) yPos = 1;
		else yPos = 2;

		return graphLayout[xPos][yPos];
    }

	Axis getAxisBlobAtPoint(Axis curAxis, int x, int y)
	{

		// use axis.dispLen
		if(curAxis == yaxis){
			if(x >= (xOriginOff - curAxis.majTicSize) &&
			   x <= xOriginOff &&
			   y >= (yOriginOff + curAxis.dispLen + curAxis.axisDir) &&
			   y <= (yOriginOff + curAxis.dispLen + curAxis.axisDir - curAxis.axisDir*curAxis.majTicSize)){
				return yaxis;
			}
			return null;
		} 

		if(curAxis == xaxis){
			return xaxis.getAxisFromBlob(x, y);
		}
		return null;
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

