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

public class AnnotView extends Container
{
    public GraphView curView = null;
    public GraphViewLine lgView = null;
    GraphViewBar bgView = null;
    
    BarGraph bGraph;
    public LineGraph lGraph;

    Bin lgBins [] = new Bin [1];
    Object bgBins [] = new Object [1];

    int buttonSpace = 20;

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

	lGraph = lgView.lGraph;
	bGraph = bgView.bGraph;
	
	add(curView);

	annotButton.setRect(0, butStart, w/3, 17);
	add(annotButton);

	delButton.setRect(w/3, butStart, w/3, 17);
	add(delButton);

	viewButton.setRect(2*w/3, butStart, w/3, 17);
	add(viewButton);

    }

    public void addBin(Bin b)
    {
	lgBins[0] = b;
	lgView.lGraph.addBin(b);
	bGraph.addBar(0, lgBins[0]);
    }

    public Bin getBin()
    {
	lgBins[0] = lgView.lGraph.addBin(0, "Probe", false);
	bGraph.addBar(0, lgBins[0]);
	return lgBins[0];
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

	bGraph.setYRange(minY, maxY - minY);
	
	this.minX = minX;
	this.maxX = maxX;

    }

    public void setPos(int x, int y)
    {
	setRect(x,y,width,height);
    }

    /*
    public void updateProp(PropPage pp, int action)
    {
	if(pp == annotPage){
	    if(bgView.selBar == null)
		return;

	    if(!(bgView.selBar instanceof Annotation)) return;

	    Annotation a = (Annotation)bgView.selBar;
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
    */

    public void reset()
    {
	lgView.lGraph.reset();
	bGraph.removeAllBars();
	lgView.curChar = 'A';

	if(lgBins[0] != null){
	    bGraph.addBar(0, lgBins[0]);
	}

	// repaint
	repaint();
    }

    public Bin pause()
    {
	// Watch out here because we need to fix this bin in the 
	// bar graph on start we'll need to save a pointer to this 
	// old bin
       
	
	Annotation a = lGraph.addAnnot("" + lgView.curChar, lgBins[0].getCurX());
	if(a != null){
	    lgView.curChar++;
	    bGraph.addBar(bGraph.numBars - 1, a);
	}
	
	//	  Hack to save memory on palm
	bGraph.removeBar(lgBins[0]);
	lgBins[0] = lGraph.addBin(0, "Probe", true);
	bGraph.addBar(bGraph.numBars, lgBins[0]);
	
	curView.draw();

	return lgBins[0];
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
		    if(lgBins[0] != null){
			a = lGraph.addAnnot("" + lgView.curChar, lgBins[0].getCurX());
			// Add bar to bargraph
			if(a != null){
			    bGraph.addBar(bGraph.numBars - 1, a);
			    lgView.curChar++;

			    repaint();
			}
		    }
		}	    
	    } else if(e.target == viewButton){
		if(curView == lgView){
		    viewButton.setText("View Line");

		    // copy settings from lineView to barview
		    bGraph.yaxis.setDispMin(lgView.lGraph.yaxis.dispMin);
		    bGraph.yaxis.setScale(lgView.lGraph.yaxis.scale);
		    
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

		if(bgView.selBar instanceof Annotation){
		    a = (Annotation)bgView.selBar;
		    lgView.lGraph.annots.del(lgView.lGraph.annots.find(a));
		    bgView.selBar = null;

		    bGraph.removeBar(a);
		    repaint();
		}
	    }

	} else if(e.type == 1000 && e.target == bgView) {
	    // Bar down event 

	    timer = addTimer(750);
	    barDown = true;

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
	    bGraph.addBar(bGraph.numBars - 1, a);
	} else if(e.type == 1003 && e.target == lgView) {
	    // Annotation moved event
	    // This should now be taken care of automagically :)

	} else if(e.type == ControlEvent.TIMER && e.target == this){
	    if(timer != null){
		removeTimer(timer);
		timer = null;
	    }
	    if(barDown &&
	       (bgView.selBar instanceof Annotation)){
		//	       annotPage.showProp();
	    }
	    barDown = false;
	}

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
