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
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.util.*;
import org.concord.waba.extra.event.*;
import extra.util.*;

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

    public float minY = Y_MIN;
    public float maxY = Y_MAX;
    public float minX = X_MIN;
    public float maxX = X_MAX;

    LineGraph lGraph;
    Annotation curAnnot = null;

    FloatConvert fConvert = new FloatConvert();

    public GraphViewLine(int w, int h, 
						 SplitAxis xaxis, Axis yaxis)
	{
		super(w,h);

		fConvert.minDigits = 2;
		fConvert.maxDigits = 4;

		int dwX, dwY;
		if(yaxis.maxDigits <= 3){
			dwX = 35;
		} else {
			if(waba.sys.Vm.getPlatform().equals("PalmOS")){
				dwX = 10 + 5 + 5*yaxis.maxDigits;
			} else {
				dwX = 10 + 5 + 7*yaxis.maxDigits;
			}
		} 
		dwY = 10;

		graph = lGraph = new LineGraph(w, h, dwX, dwY, 
									   xaxis, yaxis);
		yaxis.setRange(minY, maxY - minY);

		// This is a hack see setRange in AnnotView
		xaxis.setRange(minX, maxX - minX);

    }

	public void setAxis(SplitAxis xaxis, ColorAxis yaxis)
	{
		lGraph.switchXAxis(xaxis);
		lGraph.switchYAxis(yaxis);
		yaxis.setRange(minY, maxY - minY);

		// This is a hack see setRange in AnnotView
		xaxis.setRange(minX, maxX - minX);
	}

    public boolean autoScroll = true;
    float scrollFract = (float)0.25;
    float scrollStepSize = (float)0.15;
    int scrollSteps = 5;

    public void plot()
    {
		Bin bin = lGraph.curBin;
		float range;
		float scrollEnd;
		int myScrollStep = (int)(lGraph.dwWidth * scrollStepSize);
		Axis xaxis = lGraph.xaxis.lastAxis;

		if(autoScroll &&
 		   bin != null &&
		   bin.xaxis == xaxis &&
		   (bin.getNumVals() > 0) && 
		   (bin.maxX > (xaxis.dispMin + (float)xaxis.dispLen / xaxis.scale ) ||
			xaxis.drawnX == -1)){
			// scroll
			range = lGraph.xaxis.getRange();
			scrollEnd = bin.maxX - range * scrollFract;
			//		System.out.println("xRange: " + lGraph.xRange + ", scrollEnd: " + scrollEnd);
			if(scrollEnd < (float)0)
				scrollEnd = (float)0;
			if((scrollEnd - xaxis.dispMin) * xaxis.scale > (10 * myScrollStep)){
				myScrollStep = (int)((scrollEnd - xaxis.dispMin) * xaxis.scale + 2);
			}
			while((xaxis.dispMin < scrollEnd) || 
				  (xaxis.drawnX > (lGraph.xOriginOff + 4)) ||
				  (xaxis.drawnX == -1)){
				lGraph.scroll(myScrollStep, 0);
				drawn = false;
				super.plot();
			}
			postEvent(new ControlEvent(1006, this));
		} else {
			myG = createGraphics();
			if(myG == null) return;

			if(!drawn || graph.redraw){
				graph.draw(bufG);
				myG.copyRect(buffer, 0, 0, width, height, 0, 0);
				if(mode == ZOOM_MODE && selection){
					drawSelector(myG);
				}
				drawn = true;
			} else {
				graph.plot(myG);
			}  
		}
    }

	int selectLeftX, selectRightX;
	int selectTopY, selectBotY;
	boolean selection = false;

	public void drawSelector(Graphics g)
	{
		g.drawDots(selectLeftX, selectTopY, selectRightX, selectTopY);
		g.drawDots(selectLeftX, selectTopY, selectLeftX, selectBotY);
		g.drawDots(selectLeftX, selectBotY, selectRightX, selectBotY);
		g.drawDots(selectRightX, selectTopY, selectRightX, selectBotY);
	}

	public void zoomSelect()
	{
		if(!selection) return;

		// This is dependent on axis directions
		lGraph.scroll(selectLeftX - lGraph.xOriginOff, selectBotY - lGraph.yOriginOff);
		draw();

		float yChange = (float)(lGraph.dwHeight)/(float)(selectBotY - selectTopY);
		lGraph.yaxis.setScale(lGraph.yaxis.scale * yChange);

		float xChange = (float)(lGraph.dwWidth)/(float)(selectRightX - selectLeftX);
		lGraph.xaxis.setScale(lGraph.xaxis.scale * xChange);
		
		selection = false;
		draw();
		postEvent(new ControlEvent(1006, this));
	}
    
    int downX, downY, dragX, dragY;
    boolean xAxisDown, yAxisDown, graphDown, barDown, annotDown;

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
						Axis axisBlob = lGraph.getAxisBlobAtPoint(lGraph.xaxis, pe.x, pe.y);
						if(axisBlob != null){
							postEvent(new ControlEvent(1004, this));
						}
						xAxisDown = true;
						if(pe.x < lGraph.xOriginOff + lGraph.dwWidth/20){
							pe.x = lGraph.xOriginOff + lGraph.dwWidth/20;
						}
					} else if(obj == lGraph.yaxis) {
						Axis axisBlob = lGraph.getAxisBlobAtPoint(lGraph.yaxis, pe.x, pe.y);
						if(axisBlob != null){
							postEvent(new ControlEvent(1005, this));
						}
						yAxisDown = true;
						if(pe.y > lGraph.yOriginOff - lGraph.dwHeight/20){
							pe.y = lGraph.yOriginOff - lGraph.dwHeight/20;
						}
					} else if(obj == lGraph){
						Annotation oldAnnot = selAnnot;
						if(selAnnot != null){
							selAnnot.selected = false;
							selAnnot = null;
						}

						switch(mode){
						case ANNOT_MODE:
							curAnnot = lGraph.addAnnot("" + curChar, pe.x);
							curChar++;
							if(curAnnot != null){
								postEvent(new ControlEvent(1002, this));
								curAnnot.selected = true;
								selAnnot = curAnnot;
								postEvent(new ControlEvent(1003, this));								
							}
							draw();
							break;
						case DRAG_MODE:
							selAnnot = null;
							graphDown = true;
							if(oldAnnot != null) postEvent(new ControlEvent(1003, this));								
							break;
						case ZOOM_MODE:
							selection = true;
							graphDown = true;
							selectLeftX = selectRightX = pe.x;
							selectTopY = selectBotY = pe.y;
							break;
						}
					} else {
						// This should be the annotion section
						Annotation oldAnnot = selAnnot;
						if(selAnnot != null){
							selAnnot.selected = false;
						}
						selAnnot = lGraph.getAnnotAtPoint(pe.x, pe.y);
						if(selAnnot != null){
							lGraph.setSelectedAnnot(selAnnot);
							annotDown = true;
						} 
						draw();

						if(oldAnnot != selAnnot) postEvent(new ControlEvent(1003, this));
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

					if(graphDown){
						switch(mode){
						case ANNOT_MODE:
						case DRAG_MODE:
							lGraph.scroll(-moveX, -moveY);
							draw();
							postEvent(new ControlEvent(1006, this));
							break;
						case ZOOM_MODE:
							if(lGraph == lGraph.getObjAtPoint(pe.x, pe.y)){
								selectRightX = pe.x;
								selectBotY = pe.y;
							}

							if(e.type == PenEvent.PEN_UP){
								int temp;
								if(selectRightX < selectLeftX){
									temp = selectRightX;
									selectRightX = selectLeftX;
									selectLeftX = temp;
								}
								if(selectBotY < selectTopY){
									temp = selectBotY;
									selectBotY = selectTopY;
									selectTopY = temp;
								}
							}
							draw();
							break;
						}
					} else if(annotDown){
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

					} else if(dragY > 2 || dragX > 2){ 
						if(yAxisDown){
							if(pe.y > lGraph.yOriginOff - lGraph.dwHeight/20) pe.y = lGraph.yOriginOff - lGraph.dwHeight/20;
							yChange = (float)(lGraph.yOriginOff - pe.y)/ (float)(lGraph.yOriginOff - downY);
				
							if(e.type == PenEvent.PEN_DRAG){
								lGraph.yaxis.setScale(lGraph.yaxis.scale * yChange, true);
							} else {
								lGraph.yaxis.setScale(lGraph.yaxis.scale * yChange);
								postEvent(new ControlEvent(1006, this));
							}				
							draw();
						}else if(xAxisDown && graph == lGraph){
							if(pe.x < lGraph.xOriginOff + lGraph.dwWidth/20) pe.x = lGraph.xOriginOff + lGraph.dwWidth/20;
							xChange = (float)(lGraph.xOriginOff - pe.x)/ (float)(lGraph.xOriginOff - downX);

							if(e.type == PenEvent.PEN_DRAG){
								lGraph.xaxis.setScale(lGraph.xaxis.scale * xChange, true);
							} else {
								lGraph.xaxis.setScale(lGraph.xaxis.scale * xChange);
								postEvent(new ControlEvent(1006, this));
							}
							draw();
						}
					}
		     
					downX = pe.x;
					downY = pe.y;
					if(e.type == PenEvent.PEN_UP){
						graphDown = false;
						xAxisDown = false;
						yAxisDown = false;
						barDown = false;
					}
					break;
				}		
			} 
		}
    }


	


}












