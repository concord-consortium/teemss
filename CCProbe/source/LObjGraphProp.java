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
    PropContainer propsGraph = null;
	PropContainer propsXAxis = null;
	PropContainer propsYAxis = null;
	PropObject propTitle;
	PropObject propDataSources;
	PropObject propVisibleSources = null;
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

		propView = new PropertyView(this);
		propView.addContainer(propsGraph);
		propView.addContainer(propsYAxis);
		propView.addContainer(propsXAxis);
		propView.setCurTab(index);
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

		if(propsGraph == null){
			propsGraph = new PropContainer("Graph");
			propsYAxis = new PropContainer("YAxis");
			propsXAxis = new PropContainer("XAxis");

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
			
			if(dsStrings.length > 1){
				propVisibleSources = new PropObject("Visible", dsStrings);
				propVisibleSources.prefWidth = 120;
				propVisibleSources.setType(PropObject.MULTIPLE_SEL_LIST);
				for(int i=0; i<dsStrings.length; i++){
					propVisibleSources.setCheckedValue(i, graph.getVisible(i));
				}
			}
			
			propsGraph.addProperty(propTitle);
			propsGraph.addProperty(propDataSources);
			if(propVisibleSources != null) propsGraph.addProperty(propVisibleSources);
			
			propXmin = new PropObject("Min", curGS.getXMin() + "");
			propXmax = new PropObject("Max", curGS.getXMax() + "");
			propXlabel = new PropObject("Label", curGS.getXLabel());
			propXlabel.prefWidth = 100;
			propsXAxis.addProperty(propXmax);
			propsXAxis.addProperty(propXmin);
			propsXAxis.addProperty(propXlabel);

			propYmin = new PropObject("Min", curGS.getYMin() + "");
			propYmax = new PropObject("Max", curGS.getYMax() + "");

			propYlabel = new PropObject("Label", curGS.getYLabel());

			propYlabel.prefWidth = 100;
			propsYAxis.addProperty(propYmax);
			propsYAxis.addProperty(propYmin);
			propsYAxis.addProperty(propYlabel);			
		} else {
			if(graph.autoTitle)  propTitle.setValue("*" + graph.title);
			else propTitle.setValue(graph.title);

			propDataSources.setValue(dsStrings[graph.getCurGraphSettings().dsIndex]);
			
			if(propVisibleSources != null){
				for(int i=0; i<dsStrings.length; i++){
					propVisibleSources.setCheckedValue(i, graph.getVisible(i));
				}
			}

			propXmin.setValue(curGS.getXMin() + "");
			propXmax.setValue(curGS.getXMax() + "");
			propXlabel.setValue(curGS.getXLabel());

			propYmin.setValue(curGS.getYMin() + "");
			propYmax.setValue(curGS.getYMax() + "");
			propYlabel.setValue("*" + curGS.getYLabel());
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		GraphSettings curGS = graph.getCurGraphSettings();

		if(e.getActionCommand().equals("Apply")){
			int dsIndex = propDataSources.getIndex();
			graph.setCurGSIndex(dsIndex);

			GraphSettings newGS = graph.getCurGraphSettings();
			if(newGS != curGS){
				return;
			}

			if(curGS == null) return;
			curGS.setXValues(propXmin.getFValue(), propXmax.getFValue());
			curGS.setYValues(propYmin.getFValue(), propYmax.getFValue());
			
			curGS.setXLabel(propXlabel.getValue());
		   
			String newTitle = propTitle.getValue();
			String newYLabel = propYlabel.getValue();

			if(propVisibleSources != null){
				String [] dsNames = propVisibleSources.getPossibleValues();
				for(int i=0; i<dsNames.length; i++){
					graph.setVisible(i, propVisibleSources.getCheckedValue(i));
				}
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
		} else if(e.getActionCommand().equals("Close")){
			// this is a cancel or close
			if(container != null){
				container.done(this);
			}	    
		}
	}
}
