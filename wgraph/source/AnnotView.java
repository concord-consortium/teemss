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
import extra.ui.*;
import extra.util.*;
import org.concord.waba.extra.ui.*;

public class AnnotView extends Container
{
    public GraphView curView = null;
    public GraphViewLine lgView = null;
    GraphViewBar bgView = null;
    
    BarGraph bGraph;
    public LineGraph lGraph;

    Bin lgBins [] = new Bin [1];
    Object bgBins [] = new Object [1];

	SplitAxis xaxis = null;
	ColorAxis yaxis = null;

    public AnnotView(int w, int h, 
					 SplitAxis xAx, ColorAxis yAx)
    {
		setRect(0,0,w,h);

		xaxis = xAx;
		yaxis = yAx;

		curView = lgView = new GraphViewLine(w, h, xaxis, yaxis);
		bgView = new GraphViewBar(w, h);	
		lgView.setPos(0,0);
		bgView.setPos(0,0);

		lGraph = lgView.lGraph;
		bGraph = bgView.bGraph;
	
		add(curView);
    }

	public void setYLabel(String label, CCUnit unit)
	{
		yaxis.setAxisLabel(label, unit);
	}

	public void setXLabel(String label, CCUnit unit)
	{
		xaxis.setAxisLabel(label, unit);
	}

    public Bin getBin()
    {
		lgBins[0] = new Bin(xaxis.lastAxis, yaxis);
		lgBins[0].label = "Probe";
	
		lgView.lGraph.addBin(lgBins[0]);
		bGraph.addBar(0, lgBins[0]);
		return lgBins[0];
    }

    public float getXmin()
    {
		return xaxis.dispMin;
    }
    public float getXmax()
    {
		return xaxis.getDispMax();
    }
    public float getYmin()
    {
		return yaxis.dispMin;
    }
    public float getYmax()
    {
		return yaxis.getDispMax();
    }

    float minX, maxX;

    public void setRange(float minX, float maxX, float minY, float maxY)
    {
		yaxis.setRange(minY, maxY - minY);
		
		// This is a hack hmmm :)  I need to figure out which axis I'm setting
		// This probably won't work anyhow because I need to change the min in
		// the SplitAxis
		xaxis.setRange(minX, maxX - minX);

		bGraph.setYRange(minY, maxY - minY);
	
		this.minX = minX;
		this.maxX = maxX;

    }

    public void setPos(int x, int y)
    {
		setRect(x,y,width,height);
    }

    public void reset()
    {
		lgView.lGraph.reset();
		bGraph.removeAllBars();
		lgView.curChar = 'A';

		if(lgBins[0] != null){
			// This is a hack need to figure out
			// about reseting the curBin
			lgBins[0].reset();
			lgBins[0].xaxis = xaxis.lastAxis;
			lgBins[0].label = "Probe";
			lGraph.addBin(lgBins[0]);

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

		// need to pass in the curBin or track it so
		Bin curBin = lgBins[0];

		if(curBin.maxX < 0 || curBin.getNumVals() < 3){
			curBin.reset();
			curBin.xaxis = xaxis.lastAxis;
			curBin.label = "Probe";
		} else {
			xaxis.addAxis(curBin.maxX);
			curBin = new Bin(xaxis.lastAxis, yaxis);
			curBin.label = "Probe";
			lGraph.addBin(curBin);
			lgBins[0] = curBin;
		}

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
			if(false) {
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

	public void setViewType(int type)
	{
		if(type == 0){
			if(curView instanceof GraphViewBar) return;
			
			// copy settings from lineView to barview
			bGraph.yaxis.setDispMin(lgView.lGraph.yaxis.dispMin);
			bGraph.yaxis.setScale(lgView.lGraph.yaxis.scale);
		    
			remove(curView);
			curView = bgView;
			add(curView);			
		} else if(type == 1){
			if(curView instanceof GraphViewLine) return;
			
			remove(curView);
			curView = lgView;
			add(curView);			
		}

			repaint();
	}

	public void setViewMode(char mode)
	{
		if(curView instanceof GraphViewLine){
			lgView.mode = mode;
		}
	}

	public void addAnnot()
	{
		Annotation a = null;

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
