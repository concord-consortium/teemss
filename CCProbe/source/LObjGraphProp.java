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
	PropObject propDataSources;
	PropObject propVisibleSources;
    PropObject propXmin;
    PropObject propXmax;
	PropObject propXlabel;
    PropObject propYmin;
    PropObject propYmax;
	PropObject propYlabel;

	LObjGraph graph;

	PropertyView propView = null;
	int index=0;

	String [] testList = {"test1", "test2"};

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

	String [] dsStrings = null;

	public void setupProperties()
	{
		GraphSettings curGS = graph.getCurGraphSettings();
		if(curGS == null) return;

		props = new PropContainer();
		props.createSubContainer("Graph");
		props.createSubContainer("YAxis");	
		props.createSubContainer("XAxis");

		if(graph.autoTitle)  propTitle = new PropObject("Title", "*" + graph.title);
		else propTitle = new PropObject("Title", graph.title);
		propTitle.prefWidth = 120;
		dsStrings = new String [graph.numDataSources];
		for(int i=0; i<graph.numDataSources; i++){
			DataSource ds = graph.getDataSource(i);
			if(ds instanceof LabObject){
				dsStrings[i] = ((LabObject)ds).name;
			}
		}
		int defIndex = graph.getCurGraphSettings().dsIndex;
		propDataSources = new PropObject("Data", dsStrings, defIndex);
		propDataSources.prefWidth = 120;
		propDataSources.setType(PropObject.CHOICE_SETTINGS);
		propDataSources.setSettingsButtonName("Setup");

		propVisibleSources = new PropObject("Visible", dsStrings);
		propVisibleSources.prefWidth = 120;
		propVisibleSources.setType(PropObject.MULTIPLE_SEL_LIST);
		for(int i=0; i<dsStrings.length; i++){
			propVisibleSources.setCheckedValue(i, graph.getVisible(i));
		}

		props.addProperty(propTitle, "Graph");
		props.addProperty(propDataSources, "Graph");
		props.addProperty(propVisibleSources, "Graph");

		propXmin = new PropObject("Min", curGS.xmin + "");
		propXmax = new PropObject("Max", curGS.xmax + "");
		propXlabel = new PropObject("Label", curGS.xLabel);
		propXlabel.prefWidth = 100;
		props.addProperty(propXmax, "XAxis");
		props.addProperty(propXmin, "XAxis");
		props.addProperty(propXlabel, "XAxis");

		propYmin = new PropObject("Min", curGS.ymin + "");
		propYmax = new PropObject("Max", curGS.ymax + "");
		if(graph.autoTitle) propYlabel = new PropObject("Label", "*" + curGS.yLabel);
		else propYlabel = new PropObject("Label", curGS.yLabel);
		propYlabel.prefWidth = 100;
		props.addProperty(propYmax, "YAxis");
		props.addProperty(propYmin, "YAxis");
		props.addProperty(propYlabel, "YAxis");
    }

	public void actionPerformed(ActionEvent e)
	{
		GraphSettings curGS = graph.getCurGraphSettings();

		if(e.getActionCommand().equals("Apply")){
			int dsIndex = propDataSources.getIndex();
			graph.setCurGSIndex(dsIndex);

			GraphSettings newGS = graph.getCurGraphSettings();
			if(newGS != curGS) return;

			if(curGS == null) return;
			curGS.setXValues(propXmin.createFValue(), propXmax.createFValue());
			curGS.setYValues(propYmin.createFValue(), propYmax.createFValue());
			
			curGS.setXLabel(propXlabel.getValue());
		   
			String newTitle = propTitle.getValue();
			String newYLabel = propYlabel.getValue();
			String [] dsNames = propVisibleSources.getPossibleValues();
			for(int i=0; i<dsNames.length; i++){
				graph.setVisible(i, propVisibleSources.getCheckedValue(i));
			}

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
				curGS.setYLabel(newYLabel);
			}
			
			graph.notifyObjListeners(new LabObjEvent(graph, 0));
		} else if(e.getActionCommand().equals("Setup")){
			// This should be an index for safety
			String dataSourceName = propDataSources.getValue();
			if(dsStrings != null){
				for(int i=0; i<dsStrings.length; i++){
					if(dsStrings[i].equals(dataSourceName)){
						DataSource selDS = graph.getDataSource(i);
						if(selDS instanceof LObjProbeDataSource){
							LObjProbeDataSource pds = (LObjProbeDataSource)selDS;
							pds.showProp();
						}
						return;
					}
				}
			}
		} else {
			// this is a cancel or close
			if(container != null){
				container.done(this);
			}	    
		}
	}
	public int getPreferredWidth(waba.fx.FontMetrics fm){
		return -1;
	}

	public int getPreferredHeight(waba.fx.FontMetrics fm){
		return -1;
	}

	public extra.ui.Dimension getPreferredSize(){
		return null;
	}

}
