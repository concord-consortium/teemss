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

public class GraphViewBar extends GraphView
{
    final static float DEFAULT_RANGE = (float)30.0;
    final static float DEFAULT_MIN = (float)10.0;

    public float range, minValue;
    public String units = null;

    int numBars;
    int length = 0;

    BarGraph bGraph;

    FloatConvert fConvert = new FloatConvert();

    public GraphViewBar(int w, int h)
    {
	super(w,h);

	fConvert.maxDigits = 4;
	fConvert.minDigits = 2;

	range = DEFAULT_RANGE;
	minValue = DEFAULT_MIN;

	graph = bGraph = new BarGraph(w, h);
	bGraph.setYRange(minValue, range);
	
	units = new String("C");
	
	numBars = 0;	
	
	// Make the popup!
	/*
	yAxisPage = new PropPage(this);
	yAxisPage.addEdit("Max", 50);
	yAxisPage.addEdit("Min", 50);
	*/
    }
    /*
    public void updateProp(PropPage pp, int action)
    {
	if(pp == yAxisPage){
	    switch(action){
	    case PropPage.UPDATE:
		minValue = fConvert.toFloat(((Edit)(pp.props.get(0))).getText());
		range = fConvert.toFloat(((Edit)(pp.props.get(1))).getText()) - minValue;
		bGraph.setYRange(minValue, range);
		length = 0;
		repaint();
		break;
	    case PropPage.REFRESH:
		((Edit)(pp.props.get(0))).setText(minValue + range + "");
		((Edit)(pp.props.get(1))).setText(minValue + "");
		break;
	    }
	}

    }
    */

    boolean barDown, yAxisDown;
    Object selBar = null;
    int downX, downY, dragX, dragY;

    public void onEvent(Event e)
    {
	PenEvent pe = null;
	int i;
	int moveX, moveY;
	float xChange;
	float yChange;

	if(e.target == this){
	    if(e instanceof PenEvent){
		pe = (PenEvent)e;
		switch(e.type){
		case PenEvent.PEN_DOWN:
		    barDown = yAxisDown = false;
		    if(pe.y < bGraph.yOriginOff && pe.x < bGraph.xOriginOff){
			yAxisDown = true;		
		    } else {
			Object oldBar = selBar;
			selBar = null;

			for(i=0; i<bGraph.barSet.nBars; i++){
			    bGraph.barSet.barSel[i] = false;
			    if(!yAxisDown && 
			       (pe.x > bGraph.barSet.barPos[i] && pe.x < 
				(bGraph.barSet.barPos[i] + bGraph.barSet.barWidth))){
				bGraph.barSet.barSel[i] = true;
				selBar = bGraph.bars.get(i);
				barDown = true;
			    }
			}
			
			if(oldBar != selBar)
			    draw();

			if(selBar != null)
			    postEvent(new ControlEvent(1000, this));

		    }
		    
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

		    if(yAxisDown && (dragY > 10 || dragX > 10)){ 
			if(bGraph.yOriginOff - pe.y > 20){
			    yChange = (float)(bGraph.yOriginOff - pe.y)/ (float)(bGraph.yOriginOff - downY);
			    
			    bGraph.yaxis.setScale(bGraph.yaxis.scale * yChange);
			    draw();
			}
		    }

		    downX = pe.x;
		    downY = pe.y;
		    if(e.type == PenEvent.PEN_UP){
			yAxisDown = false;
			barDown = false;
			postEvent(new ControlEvent(1001, this));
		    }
		    break;		    
		}
	    }
	}

    }

}












