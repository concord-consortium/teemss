package graph;

import waba.ui.*;
import waba.fx.*;
import waba.io.*;
import waba.sys.*;
import waba.util.*;
import extra.util.*;    

public class SplitAxis extends Axis
{
	public Axis [] axisArray = new Axis [10];
	Axis lastAxis = null;
    int numAxis;
	int type;

    public final static float MIN = 0;
    public final static float MAX = 100;

	public SplitAxis(int t)
	{
		super(t);

		type = t;
		lastAxis = new Axis(type);
		lastAxis.max = (float)1E30;  // some huge number 		

		axisArray[0] = lastAxis;
		numAxis = 1;
	}

    public void free()
    {
		super.free();

		if(axisArray != null){
			for(int i=0; i<axisArray.length; i++){
				if(axisArray[i] != null){
					axisArray[i].free();
				}
			}
		}
    }

	public float getRange()
	{
		return (float)dispLen / scale;
	}

	public float getValue(int pos, Axis [] ax)
	{
		Axis curAxis;
		float time;
		int i;

		curAxis = null;
		for(i=0; i < numAxis; i++){
			curAxis = axisArray[i];	    
			if(pos*curAxis.axisDir > curAxis.drawnX*curAxis.axisDir && 
			   pos*curAxis.axisDir < curAxis.axisDir*(curAxis.drawnX + curAxis.axisDir + 
											curAxis.dispLen)){
				break;
			}
		}
		if(i != numAxis){
			time = (pos - curAxis.drawnX) / curAxis.scale + curAxis.dispMin;
			ax[0] = curAxis;
			return time;
		}

		ax[0] = null;
		return Maths.NaN;
	}

	public Axis getAxis(int i)
	{
		if((i >= 0) && (i < numAxis)){
			return axisArray[i];
		} else {
			return null;
		}

	}

    int startPos = 0;
    /* 
     * This is a very tricky piece of code where we change
     * the scale.  However we need to keep the startPos
     * in the same place
     */
    public void setScale(float newScale, boolean eScale)
    {
		if(!estimateScale && eScale) oldScale = scale;
		estimateScale = eScale;

		scale = newScale;
		axisDir = 1;
		if(scale < (float)0) axisDir = -1;

		/*
		  Do I need to watch out for this stuff:

		  axisDir = 1;
		  if(scale < (float)0) axisDir = -1;
		  setDispOffset(dispMin, 0);
		  setStepSize();
		  needCalcTics = true;       
		*/

		int i;
		int oldScaleSP = 0;
		int newXaxisStartPos = -1;
		int curStartPos = 0;
		Axis xa;

		for(i =0; i < numAxis-1; i++){
			xa = axisArray[i];
			if(newXaxisStartPos == -1 &&
			   startPos < (oldScaleSP + (int)(xa.max * xa.scale))){
				newXaxisStartPos = curStartPos + (int)(xa.dispMin * scale);
			}
			oldScaleSP += 10 + xa.dispMin * scale + xa.dispLen;
			xa.setScale(scale, eScale);
			curStartPos += 10 + (int)(xa.max * xa.scale);
		}

		xa = axisArray[i];
		xa.setScale(scale);
		if(newXaxisStartPos == -1){
			newXaxisStartPos = curStartPos + (int)(xa.dispMin * scale);
		}

		startPos = newXaxisStartPos;

		labelExp = lastAxis.labelExp;
		notifyListeners(SCALE_CHANGE);
    }

    public void scrollStartPos(int xDist)
	{
		startPos += xDist;
		if(startPos < 0){
			startPos = 0;
		}
    }

	public Axis getAxisFromBlob(int x, int y)
	{
		Axis xa;

		for(int i=0;i<numAxis;i++){
			xa = axisArray[i];
			if(xa.drawnX == -1) continue;
			if(x <= (xa.drawnX + xa.dispLen + xa.axisDir) &&
			   x >= (xa.drawnX + xa.dispLen + xa.axisDir - xa.axisDir*xa.majTicSize) &&
			   y >= drawnY &&
			   y <= (drawnY + xa.majTicSize - xa.ticDir)){
				return xa;
			}
		}
		return null;
	}

	public final static int ADDED_AXIS = 4002;

	// Return index of newAxis
    public int addAxis(float oldMax)
	{
		// Technically this should search through all the active bins
		// and get the max
		lastAxis.max = oldMax;

		if(numAxis >= axisArray.length){
			Axis [] newAxis = new Axis[(numAxis * 3)/ 2];
			Vm.copyArray(axisArray, 0, newAxis, 0, numAxis);
			axisArray = newAxis;
		} 
		axisArray[numAxis] = lastAxis = new Axis(type);
		lastAxis.max = (float)1E30;  // some huge number 
		lastAxis.gridEndOff = gridEndOff;
		lastAxis.setLength(dispLen);
		lastAxis.setDispMin(dispMin);
		lastAxis.setScale(scale);
		numAxis++;

		notifyListeners(ADDED_AXIS);
		return numAxis -1;

		/* 
		   This stuff needs to be taken care of the by the caller!!!
		
		   numBinsPerAxis[numAxis] = 0;
		   graphLayout[1][2] = xaxis;		   
		   activeBins = new Vector();		   
		   needRecalc = true;	    
		*/
    }

    public void draw(Graphics g, int xOriginOff, int yOriginOff)
	{
		int xaxisOffset = xOriginOff;
		int curStartPos = 0;
		Axis xa;
		int axisLen;
		int i;

		drawnY = yOriginOff;
		drawnX = xOriginOff;

		curStartPos = 0;

		// Find the first visible axis
		// And set all the axis drawnX to -1;
		int firstVisible = -1;
		int xaScMax;

		for(i=0;i<numAxis;i++){
			xa = axisArray[i];
			xa.drawnX = -1;
			if(xa.max > (float)1E25)
				// This is the active axis
				xaScMax = (int)0x7FFFFFF - curStartPos - 10;
			else
				xaScMax = (int)(xa.max * xa.scale);
			if(firstVisible == -1){
				if(startPos < (curStartPos + xaScMax)){
					firstVisible = i;
					// Once we found a visible one we need to leave the
					// curStartPos at the begining of this axis
					continue;
				}
				curStartPos += xaScMax + 10;
			}
		}

		if(firstVisible == -1){
			return;
		}

		int endPoint = startPos + dispLen;
		int dispOffset = 0;
		for(i=firstVisible;i<numAxis;i++){
			xa = axisArray[i];
			if(curStartPos >= endPoint){
				//our drawing work is done
				//we still need to set the remaining axis drawnX to -1
				break;
			}

			g.setColor(0,0,0);
			if(curStartPos < startPos){
				// This axis starts before the visible area so we need to offset it
				dispOffset = startPos - curStartPos;
				xa.setDispOffset(dispMin, dispOffset);
				curStartPos = startPos;
				if(xa.max > (float)1E25){
					// this is the active axis
					axisLen = dispLen;
				} else {
					axisLen = (int)(xa.max * xa.scale) - dispOffset;
				}
			} else {
				// The axis starts in the visible area
				// Need to draw the beginning line of the axis
				// draw the next axis
				g.drawLine(xaxisOffset + curStartPos - startPos, yOriginOff, 
						   xaxisOffset + curStartPos - startPos, yOriginOff + gridEndOff);

				// And set the dispMin correctly
				xa.setDispMin(MIN);
				if(xa.max > (float)1E25){
					// this is the active axis
					axisLen = dispLen;
				} else {
					axisLen = (int)(xa.max * xa.scale);
				}
			} 
	
			if(axisLen + (curStartPos - startPos) >= dispLen){
				// The axis extends beyond the visible area
				xa.dispLen = dispLen - (curStartPos - startPos);
			} else {
				// This axis ends before the end of the visible area so draw an end line
				xa.dispLen = axisLen;				
			}
	    
			xa.gridEndOff = gridEndOff;
			xa.draw(g, xaxisOffset + (curStartPos - startPos), yOriginOff);
			curStartPos += axisLen + 10;
		}
    }

	public void reset()
	{
		axisArray[0] = lastAxis;		
		lastAxis.dispLen = dispLen;
		lastAxis.setDispMin(MIN);
		numAxis = 1;
		startPos = 0;
	}

}
