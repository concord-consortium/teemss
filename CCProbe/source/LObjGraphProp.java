package org.concord.CCProbe;

import waba.util.*;
import waba.ui.*;
import extra.io.*;
import extra.util.*;
import org.concord.LabBook.*;
import graph.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.util.*;
import extra.util.*;

public class LObjGraphProp extends LabObjectView
	implements ActionListener
{
    PropertyDialog pDialog = null;
    PropContainer props = null;
	PropObject propTitle;
    PropObject propXmin;
    PropObject propXmax;
	PropObject propXlabel;
    PropObject propYmin;
    PropObject propYmax;
	PropObject propYlabel;

	LObjGraph graph;

	PropertyView propView = null;
	int index=0;

	public LObjGraphProp(ViewContainer vc, LObjGraph g, int index)
    {
		super(vc);
		graph = g;
		lObj = g;	
		this.index = index;

		setupProperties();
	}

	public void layout(boolean sDone)
	{
		if(didLayout) return;
		didLayout = true;

		propView = new PropertyView(props, index, this);
		add(propView);
	}

    public void setRect(int x, int y, int width, int height)
    {
		super.setRect(x,y,width,height);
		if(!didLayout) layout(false);

		propView.setRect(0,0,width,height);
	}

	public void setupProperties()
	{
		GraphSettings curGS = graph.curGS;
		if(curGS == null) return;

		props = new PropContainer();
		props.createSubContainer("Graph");
		props.createSubContainer("YAxis");	
		props.createSubContainer("XAxis");

		if(graph.autoTitle)  propTitle = new PropObject("Title", "*" + graph.title);
		else propTitle = new PropObject("Title", graph.title);
		propTitle.prefWidth = 100;

		propXmin = new PropObject("Min", curGS.xmin + "");
		propXmax = new PropObject("Max", curGS.xmax + "");
		propXlabel = new PropObject("Label", curGS.xLabel);
		propXlabel.prefWidth = 100;
		propYmin = new PropObject("Min", curGS.ymin + "");
		propYmax = new PropObject("Max", curGS.ymax + "");

		if(graph.autoTitle) propYlabel = new PropObject("Label", "*" + curGS.yLabel);
		else propYlabel = new PropObject("Label", curGS.yLabel);
		propYlabel.prefWidth = 100;

		props.addProperty(propTitle, "Graph");

		props.addProperty(propXmax, "XAxis");
		props.addProperty(propXmin, "XAxis");
		props.addProperty(propXlabel, "XAxis");

		props.addProperty(propYmax, "YAxis");
		props.addProperty(propYmin, "YAxis");
		props.addProperty(propYlabel, "YAxis");
    }

	public void actionPerformed(ActionEvent e)
	{
		GraphSettings curGS = graph.curGS;

		if(e.getActionCommand().equals("Apply")){
			if(curGS == null) return;
			curGS.setXValues(propXmin.createFValue(), propXmax.createFValue());
			curGS.setYValues(propYmin.createFValue(), propYmax.createFValue());
			
			curGS.setXLabel(propXlabel.getValue());

			String newTitle = propTitle.getValue();
			String newYLabel = propYlabel.getValue();

			if(!graph.autoTitle && 
			   ((newTitle.length() > 0 && 
				 newTitle.charAt(0) == '*') ||
				(newYLabel.length() > 0 &&
				 newYLabel.charAt(0) == '*'))){
				graph.autoTitle = true;
				graph.name = "..auto_title..";
			} else if(graph.autoTitle && 
			   ((newTitle.length() > 0 && 
				 newTitle.charAt(0) != '*') ||
				(newYLabel.length() > 0 &&
				 newYLabel.charAt(0) != '*'))){
				graph.autoTitle = false;
				graph.name = "Graph";
			}

			if(!graph.autoTitle){
				graph.title = newTitle;
				graph.curGS.setYLabel(newYLabel);
			}
			
			if(graph.gv != null) graph.gv.updateProp();
		} else {
			// this is a cancel or close
			if(container != null){
				container.done(this);
			}	    
		}
	}
}
