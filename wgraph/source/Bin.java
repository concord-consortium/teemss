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
import org.concord.waba.extra.event.*;
import extra.util.*;

public class Bin
    implements DecoratedValue
{
    public static int START_DATA_SIZE = 10000;

    int [] points;
    float [] values;
    int numPoints;
    int numValues;
    int lastPlottedPoint;
    int c;
    int collection;
    int lastPlottedY;
    int lastPlottedX;
    int curX;
    int sumY;
    int numXs;
    int minPtY;
    int maxPtY;
    float refX = 0f;
    float refY = 0f;
    public float minX, minY, maxX, maxY;
    int [] color = {255,0,0};
    public int xaIndex = 0;
    
    public String label;

    public Bin(int xIndex)
    {
	xaIndex = xIndex;

	// We store three ints for each point
	// (x),(avgY),(maxOff << 16 | -minOff)
	points = new int [START_DATA_SIZE*3];
	values = new float [START_DATA_SIZE*2];

	// System.out.println("Creating bin with size:" + START_DATA_SIZE);

	reset();
    }

    public String getLabel()
    {
	return label;
    }

    public float getValue()
    {
	if(numValues == 0) return 0f;
	return values[(numValues-1)*2 + 1] + refY;
    }

    public Color getColor()
    {
	return null;
    }

    public float getCurX()
    {
	if(numValues == 0) return 0f;
	return values[(numValues-1)*2];
    }

    public void recalc(float xscale, float yscale)
    {
	int i;
	int lastOffset = numValues*2;
	int newX, newY;
	int curPtPos = 0;
	int avgY;

	numPoints = 0;
	numXs = 0;
	
	if(numValues < 2){
	    return;
	}

	i=0;
	curX = (int)(values[i++]* xscale);
	minPtY = maxPtY = sumY = (int)(values[i++]* yscale);
	numXs = 1;

	// Set the last value to some non valid x
	// This will cause the inner loop to break out
	values[numValues*2] = -100000f;
	
	newX = (int)(values[i] * xscale);
	i++;
	newY = (int)(values[i] * yscale);
	i++;		

	while(true){
	    while(newX == curX){
		sumY += newY;
		numXs++;
		if(newY > maxPtY) maxPtY = newY;
		else if(newY < minPtY) minPtY = newY;

		newX = (int)(values[i] * xscale);
		i++;
		newY = (int)(values[i] * yscale);
		i++;		
	    }
	    points[curPtPos++] = curX;
	    avgY = sumY / numXs;
	    points[curPtPos++] = avgY;
	    points[curPtPos++] = (maxPtY - avgY) << 16 | (avgY - minPtY);
	    if(i >= lastOffset) break;
	    curX = newX;
	    numXs = 0;
	    sumY = 0;
	    maxPtY = minPtY = newY;
	}

	numPoints = curPtPos / 3;
	lastCalcValue = i-2;
    }

    int lastCalcValue = 0;

    public boolean update(float xscale, float yscale)
    {
	int i;
	int lastOffset = numValues*2;
	int newX, newY;
	int curPtPos = (numPoints-1)*3;
	int avgY;

	if(numPoints < 2){
	    recalc(xscale, yscale);
	    return true;
	}

	if(numValues - lastCalcValue/2 < 1){
	    return false;
	}

	i=lastCalcValue;

	// Set the last value to some non valid x
	// This will cause the inner loop to break out
	values[numValues*2] = -100000f;
	
	newX = (int)(values[i] * xscale);
	i++;
	newY = (int)(values[i] * yscale);
	i++;		

	while(true){
	    while(newX == curX){
		sumY += newY;
		numXs++;
		if(newY > maxPtY) maxPtY = newY;
		else if(newY < minPtY) minPtY = newY;

		newX = (int)(values[i] * xscale);
		i++;
		newY = (int)(values[i] * yscale);
		i++;		
	    }
	    points[curPtPos++] = curX;
	    avgY = sumY / numXs;
	    points[curPtPos++] = avgY;
	    points[curPtPos++] = (maxPtY - avgY) << 16 | (avgY - minPtY);
	    if(i >= lastOffset) break;
	    curX = newX;
	    numXs = 0;
	    sumY = 0;
	    maxPtY = minPtY = newY;
	}

	numPoints = curPtPos / 3;
	lastCalcValue = i-2;

	return true;
    }


    public boolean addPoint(float x, float value)
    {
	int offset = numValues*2;

	// should check the current config
	if(offset >= (values.length - 2)){
	    // x is out of bounds
	    // **Need to have calling funct do this*** 
	    // endCollection();

	    return false;
	}
	
	if(offset == 0){
	    refY = value;
	}

	if(maxX < x) maxX = x;
	if(minX > x) minX = x;
	values[offset] = x - refX;
	offset++;
	
	values[offset] = value - refY;
	if(maxY < value) maxY = value;
	if(minY > value) minY = value;

	numValues++;

	return true;
    }

    public boolean addPoints(int num, float [] data)
    {
	int offset = numValues*2;
	int i;
	float x, value;
	int endPos = num*2;
	
	if(offset == 0){
	    refY = data[1];
	}

	for(i=0; i<endPos; i+=2){
	    offset= numValues*2;

	    // should check the current config
	    if(offset >= (values.length - 2)){
		// x is out of bounds
		// **Need to have calling funct do this*** 
		// endCollection();
		
		return false;
	    }
	
	    x = data[i];
	    if(maxX < x) maxX = x;
	    if(minX > x) minX = x;
	    values[offset] = x - refX;
	    offset++;
	
	    value = data[i+1];
	    values[offset] = value - refY;
	    if(maxY < value) maxY = value;
	    if(minY > value) minY = value;

	    numValues++;
	}

	return true;
    }

    float dT = 0f;
    int sampSize = 1;

    public boolean dataReceived(DataEvent dataEvent)
    {
	int offset = numValues*2;
	int i;
	float x, value;
	float [] data = dataEvent.data;

	if(offset == 0){
	    dT = dataEvent.getDataDesc().getDt();
	    sampSize = dataEvent.getDataDesc().getChPerSample();
	    refY = data[dataEvent.dataOffset];
	}

	int endPos = dataEvent.numbData*sampSize;
	float curX = dataEvent.time;

	for(i=0; i<endPos; i+=sampSize){
	    offset= numValues*2;

	    // should check the current config
	    if(offset >= (values.length - 2)){
		// x is out of bounds
		// **Need to have calling funct do this*** 
		// endCollection();
		
		return false;
	    }
	
	    x = curX;
	    curX += dT;
	    if(maxX < x) maxX = x;
	    if(minX > x) minX = x;
	    values[offset] = x - refX;
	    offset++;
	
	    value = data[i];
	    values[offset] = value - refY;
	    if(maxY < value) maxY = value;
	    if(minY > value) minY = value;

	    numValues++;
	}

	return true;

    }

    void reset()
    {
	int i;

	minX = minY = 1;
	maxY = -1;
	maxX = 0f;

	for(i=0; i<100; i++){
	    minX *= (float)10;
	    minY *= (float)10;
	    maxY *= (float)10;
	}

	numPoints = 0;
	numValues = 0;
	lastPlottedPoint = -1;
	lastCalcValue = 0;
    }

    public boolean getValue(float time, float [] value)
    {
	int i;

	for(i = 0; i < numValues; i++){
	    if(time - values[i*2] < (float)0.01){
		value[0] = values[i*2+1] + refY;
		return true;
	    }
	}

	return false;
    }

    public int getValues(int start, float [] values, int off, int count)
    {
	int i;
	
	if(off + count > values.length) count = values.length - off;

	if(count + start > numValues) count = numValues - start;

	if(count < 0) return -1;

	for(i = 0; i < count; i++){
	    values[i+off] = this.values[start*2 + i*2];
	}

	return count;
    }

    public int numDataChunks(){return 1;}

    public DataEvent getDataChunk(int index)
    {
	DataEvent dEvent;
	DataDesc dDesc = new DataDesc(dT, 1);

	float [] data = new float [numValues];
	for(int i=0; i<numValues; i++){
	    data[i] = values[i*2 + 1] + refY;
	}

	dEvent = new DataEvent(DataEvent.DATA_RECEIVED, 
			       0f, data , dDesc);
	dEvent.dataOffset = 0;
	dEvent.numbData = numValues;

	return dEvent;
    }
	    
}


