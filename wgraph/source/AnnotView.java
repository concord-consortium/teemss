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

import org.concord.waba.extra.util.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;

public class AnnotView extends Container
	implements ActionListener
{
    public GraphView curView = null;
    public GraphViewLine lgView = null;
    public GraphViewBar bgView = null;
    
    public BarGraph bGraph;
    public LineGraph lGraph;
	
	Vector lineBins = new Vector();
	Bin curLineBin = null;

	SplitAxis xaxis = null;
	ColorAxis yaxis = null;

    float minX, maxX;

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

		curView.makeActive(true);
		add(curView);
    }

	public void setAxis(SplitAxis xAx, ColorAxis yAx)
	{	   
		xaxis = xAx;
		yaxis = yAx;
		lgView.setAxis(xAx, yAx);
	}

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

    public void addBin(Bin curBin)
    {
		lineBins.add(curBin);
		curLineBin = curBin;
		curBin.addActionListener(this);

		lgView.lGraph.addBin(curBin);
		bGraph.addBar(0, curBin);
		return;
    }

	public void removeBin(Bin curBin)
	{
		int index = lineBins.find(curBin);
		if(index < 0) return;

		lineBins.del(index);
		curBin.removeActionListener(this);
		if(curLineBin == curBin) curLineBin = null;

		//	  Hack to save memory on palm
		bGraph.removeBar(curBin);		
	}

	public void closeBin(Bin curBin)
	{
		//	  Hack to save memory on palm
		bGraph.removeBar(curBin);		
	}

	public void actionPerformed(ActionEvent e)
	{
		Object obj = e.getSource();
		if(obj instanceof Bin){
			Bin b = (Bin)obj;
			switch(e.type){
			case Bin.ANNOT_ADDED:
				// this is 
				bGraph.addBar(bGraph.numBars, b.getCurAnnot());
				break;
			case Bin.ANNOT_DELETED:				
				Annotation a = b.getCurAnnot();
				bgView.delBar(a);
				lgView.delAnnot(a);
				repaint();
				break;
			case Bin.ANNOTS_CLEARED:
				// This is a strange case that isn't handled right
				Vector binAnnots = b.annots;
				for(int i=0; i<binAnnots.getCount(); i++){
					bGraph.removeBar((Annotation)binAnnots.get(i));
				}
				break;
			}
		}
	}

	public void delAnnot(Annotation a)
	{
		if(a == null || a.bin == null) return;

		Bin b = (Bin)a.bin;
		b.delAnnot(a);

		// notice that when the bin is deleted
		// we will get an ANNOT_DELETED event
	}
	
	public void addAnnot()
	{
		Annotation a = null;

		// note this will cause an ANNOT_ADDED event which we will
		// recieve and add the bar
		a = lgView.addAnnot();

		if(a != null){
			repaint();
		}
	}

    public void reset()
    {
		bgView.reset();
		lgView.reset();

		curLineBin = null;
		for(int i=0; i<lineBins.getCount(); i++){
			((Bin)lineBins.get(i)).removeActionListener(this);
		}
		lineBins = new Vector();

		// repaint
		repaint();
    }

	public Annotation getSelectedAnnot()
	{
		return lgView.selAnnot;
	}

    boolean barDown = false;
    Timer timer = null;
    public void onEvent(Event e)
    {
		int i;

		if(e.type == 1000 && e.target == bgView) {
			// Selected bar change event

			if(bgView.selBar != null){
				timer = addTimer(750);
				barDown = true;
				// should update selected annotation on the graph
				if(bgView.selBar instanceof Annotation){
					lgView.setSelectedAnnot((Annotation)bgView.selBar);
					// this is a kludge
					postEvent(new ControlEvent(1008, this));
				}
			} else {
				lgView.setSelectedAnnot(null);
				if(timer != null){
					removeTimer(timer);
					timer = null;
				}
				postEvent(new ControlEvent(1008, this));
			}			
		} else if(e.type == 1001 && e.target == bgView) {
			// Bar up event
			barDown = false;
			if(timer != null){
				removeTimer(timer);
				timer = null;
			}
		} else if(e.type == 1006 && e.target == lgView) {
			// Annotation selection change event
			// need to update the selected bar
			bgView.setSelectedBar(lgView.selAnnot);
			postEvent(new ControlEvent(1008, this));
		} else if(e.type == ControlEvent.TIMER && e.target == this){
			if(timer != null){
				removeTimer(timer);
				timer = null;
			}
			if(barDown &&
			   (bgView.selBar instanceof Annotation)){
				postEvent(new ControlEvent(1007, this));
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
			curView.makeActive(false);

			curView = bgView;
			curView.makeActive(true);
			add(curView);			
		} else if(type == 1){
			if(curView instanceof GraphViewLine) return;
			
			remove(curView);
			curView.makeActive(false);
			curView = lgView;
			curView.makeActive(true);
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

    public void update()
    {
		curView.plot();
    }
    
    public void free()
    {
		if(lgView != null) lgView.free();
		if(bgView != null) bgView.free();

		curLineBin = null;
		for(int i=0; i<lineBins.getCount(); i++){
			((Bin)lineBins.get(i)).removeActionListener(this);
		}
		lineBins = new Vector();
    }

}
