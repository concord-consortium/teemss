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
import waba.util.*;
import waba.sys.*;

public class AnnotView extends Container implements PropObject
{
    public GraphView curView = null;
    public GraphViewLine lgView = null;
    GraphViewBar bgView = null;

    Bin lgBins [] = new Bin [1];
    Object bgBins [] = new Object [1];

    int buttonSpace = 20;

    PropPage annotPage;

    Button annotButton = new Button("Add Mark");
    Button delButton = new Button("Del Mark");
    Button viewButton = new Button("View Bars");

    public boolean active = false;

    public AnnotView(int w, int h)
    {
	width = w;
	height = h;

	int butStart = h - buttonSpace + 1;

	curView = lgView = new GraphViewLine(w, h - buttonSpace);
	bgView = new GraphViewBar(w, h - buttonSpace);	
	lgView.setPos(0,0);
	bgView.setPos(0,0);
	
	lgBins [0] = lgView.lGraph.addBin(0, "Temp", false);
	bgBins [0] = bgView.bGraph.addBin(0, "Probe");
	
	add(curView);

	annotButton.setRect(0, butStart, w/3, 17);
	add(annotButton);

	delButton.setRect(w/3, butStart, w/3, 17);
	add(delButton);

	viewButton.setRect(2*w/3, butStart, w/3, 17);
	add(viewButton);

	annotPage = new PropPage(this);
	annotPage.addEdit("Label", 30);
	annotPage.addEdit("Notes", 60);

    }

    public float getXmin()
    {
	return lgView.lGraph.xaxis.dispMin;
    }
    public float getXmax()
    {
	return lgView.lGraph.xaxis.dispMin+lgView.lGraph.dwWidth/lgView.lGraph.xaxis.scale;
    }
    public float getYmin()
    {
	return lgView.lGraph.yaxis.dispMin;
    }
    public float getYmax()
    {
	return lgView.lGraph.yaxis.dispMin+lgView.lGraph.yaxis.dispLen/lgView.lGraph.yaxis.scale;
    }

    float minX, maxX;

    public void setRange(float minX, float maxX, float minY, float maxY)
    {
	lgView.lGraph.setYRange(minY, maxY - minY);
	lgView.lGraph.setXRange(0, minX, maxX - minX);

	bgView.bGraph.setYRange(minY, maxY - minY);
	
	this.minX = minX;
	this.maxX = maxX;

    }

    public void setPos(int x, int y)
    {
	setRect(x,y,width,height);
    }

    public void updateProp(PropPage pp, int action)
    {
	if(pp == annotPage){
	    if(bgView.selBar == null)
		return;

	    Annotation a = (Annotation)lgView.lGraph.annots.get(bgView.selBar.index);
	    if(a == null)
		return;

	    switch(action){
	    case PropPage.UPDATE:
		a.label = ((Edit)(pp.props.get(0))).getText();
		a.text = ((Edit)(pp.props.get(1))).getText();
		((Bar)(a.bin)).setLabel(a.label);

		repaint();
		break;
	    case PropPage.REFRESH:
		((Edit)(pp.props.get(0))).setText(a.label);
		if(a.text != null)
		    ((Edit)(pp.props.get(1))).setText(a.text);
		else 
		    ((Edit)(pp.props.get(1))).setText("");		    
		break;
	    }
	}
    }

    public void reset()
    {
	lgView.lGraph.reset();
	bgView.bGraph.removeAllBins();
	lgView.curChar = 'A';

	bgBins [0] = bgView.bGraph.addBin(0, "Probe");
	bgView.bGraph.addPoint(bgBins[0], 1, curY);

	lastX = (float)0;

	// repaint
	repaint();
    }

    public void pause()
    {
	lgBins[0] = lgView.lGraph.addBin(0, "Temp", true);
	lastX = (float)0.0;
	curView.draw();
    }

    boolean barDown = false;
    Timer timer = null;

    public void onEvent(Event e)
    {
	int i;
	Annotation a = null;

	if((e.type == ControlEvent.PRESSED)){
	    if(e.target == annotButton){
		if(curView == lgView){
		    lgView.mode = lgView.ANNOT_MODE;
		} else {
		    a = lgView.lGraph.addAnnot("" + lgView.curChar, lastX);
		    // Add bar to bargraph
		    if(a != null){
			a.bin = bgView.bGraph.addBin(bgView.bGraph.numBars - 1, "" + lgView.curChar);
			bgView.bGraph.addPoint(a.bin, 1, a.value);
			lgView.curChar++;

			repaint();
		    }
		}	    
	    } else if(e.target == viewButton){
		if(curView == lgView){
		    viewButton.setText("View Line");

		    // copy settings from lineView to barview
		    bgView.bGraph.yaxis.setDispMin(lgView.lGraph.yaxis.dispMin);
		    bgView.bGraph.yaxis.setScale(lgView.lGraph.yaxis.scale);
		    
		    remove(curView);
		    curView = bgView;
		    add(curView);

		    repaint();
		} else {
		    viewButton.setText("View Bars");
		    
		    remove(curView);
		    curView = lgView;
		    add(curView);

		    repaint();
		}

	    } else if(e.target == delButton) {
		if(bgView.selBar == null)
		    return;

		a = (Annotation)bgView.selBar.ptr;
		lgView.lGraph.annots.del(lgView.lGraph.annots.find(a));
		bgView.selBar = null;

		bgView.bGraph.removeBin(a.bin);
		repaint();
	    }

	} else if(e.type == 1000 && e.target == bgView) {
	    // Bar down event 

	    if(bgView.selBar != null && bgView.selBar.index == (bgView.bGraph.numBars - 1)){
		bgView.bGraph.barSet.barSel[bgView.bGraph.numBars - 1] = false;
		bgView.selBar = null;
		
		repaint();
	    } else {
		timer = addTimer(750);
		barDown = true;
	    }
	} else if(e.type == 1001 && e.target == bgView) {
	    // Bar up event
	    barDown = false;
	    if(timer != null){
		removeTimer(timer);
		timer = null;
	    }
	} else if(e.type == 1002 && e.target == lgView) {
	    // Annotation added event
	    lgView.mode = lgView.DRAG_MODE;
	    a = lgView.curAnnot;
	    if(a == null) return;

	    // Add bar to bargraph
	    a.bin = bgView.bGraph.addBin(bgView.bGraph.numBars - 1, a.label);
	    bgView.bGraph.addPoint(a.bin, 1, a.value);
	    ((Bar)a.bin).ptr = a;
	} else if(e.type == 1003 && e.target == lgView) {
	    // Annotation moved event
	    if((a = lgView.selAnnot ) != null){
		bgView.bGraph.addPoint(a.bin, 1, a.value);
	    }

	} else if(e.type == ControlEvent.TIMER && e.target == this){
	    if(timer != null){
		removeTimer(timer);
		timer = null;
	    }
	    if(barDown)
	       annotPage.showProp();

	    barDown = false;
	}

    }

    float lastX = 0f;
    float curY = (float)0;

    public boolean addPoint(int bin, float x, float y)
    {	
	if(bin == 0){
	    curY = y;
	    lastX = x;
	}

	bgView.bGraph.addPoint(bgBins[bin], x, y);
	if(!lgBins[bin].addPoint(x,y)){
	    // If it returned false then we are out of space
	    lgBins[bin] = lgView.lGraph.addBin(0, "Temp", true);
	    return false;
	}

	return true;
    }

    public boolean addPoints(int bin, int num, float [] data)
    {
	int lastPos = (num -1)*2;

	if(bin == 0){
	    curY = data[lastPos + 1];
	    lastX = data[lastPos];
	}

	bgView.bGraph.addPoint(bgBins[bin], data[lastPos], data[lastPos+1]);
	
	if(!lgBins[bin].addPoints(num, data)){
	    // If it returned false then we are out of space
	    lgBins[bin] = lgView.lGraph.addBin(0, "Temp", true);
	    return false;
	}

	return true;
	
    }

    public boolean addPoints(int bin, int num, int dOff, int size, float [] data, float sTime, float dT)
    {
	int lastPos = (num -1)*size + dOff;

	if(bin == 0){
	    curY = data[lastPos];
	    lastX = sTime + dT*(num-1);
	}

	bgView.bGraph.addPoint(bgBins[bin], lastX, curY);
	
	if(!lgBins[bin].addPoints(num, dOff, size, data, sTime, dT)){
	    // If it returned false then we are out of space
	    lgBins[bin] = lgView.lGraph.addBin(0, "Temp", true);
	    return false;
	}

	return true;
	
    }


    public void update()
    {
	curView.plot();
    }
    
    public void free()
    {
	if(lgView != null) lgView.free();
	if(bgView != null) bgView.free();
    }

}
