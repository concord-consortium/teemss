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
	PropObject propDataSources;
	PropObject propVisibleSources = null;
	PropObject propTitle;
	PropObject propAutoTitle = null;
    PropObject propXmin;
    PropObject propXmax;
	PropObject propXlabel;
	PropObject propAutoXlabel;
    PropObject propYmin;
    PropObject propYmax;
	PropObject propYlabel;
	PropObject propAutoYlabel;

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
		int id = 0;

		GraphSettings curGS = graph.getCurGraphSettings();
		if(curGS == null) return;

		if(propsGraph == null){
			propsGraph = new PropContainer("Graph");
			propsYAxis = new PropContainer("YAxis");
			propsXAxis = new PropContainer("XAxis");

			dsStrings = new String [graph.numDataSources];
			for(int i=0; i<graph.numDataSources; i++){
				DataSource ds = graph.getDataSource(i);
				dsStrings[i] = ds.getQuantityMeasured();
			}
			if(graph.getMaxLines() != 1){
				int defIndex = graph.getCurGraphSettings().dsIndex;
				propDataSources = new PropObject("Data", "Data", id++, dsStrings, defIndex);
				propDataSources.prefWidth = 120;
				propDataSources.setType(PropObject.CHOICE_SETTINGS);
				propDataSources.setSettingsButtonName("Setup");
			}

			if(dsStrings.length > 1){
				propVisibleSources = new PropObject("Visible", "Visible", id++, dsStrings);
				propVisibleSources.prefWidth = 120;
				propVisibleSources.setType(PropObject.MULTIPLE_SEL_LIST);
				if(graph.getMaxLines() == 1) propVisibleSources.setRadio(true);
				for(int i=0; i<dsStrings.length; i++){
					propVisibleSources.setCheckedValue(i, graph.getVisible(i));
				}
			}
			
			propTitle = new PropObject("Title", "Title", id++, graph.title);
			propTitle.prefWidth = 120;

			propAutoTitle = new PropObject("Auto", "Auto", id++, graph.autoTitle);
			
			if(propDataSources != null) propsGraph.addProperty(propDataSources);
			if(propVisibleSources != null) propsGraph.addProperty(propVisibleSources);
			propsGraph.addProperty(propTitle);
			propsGraph.addProperty(propAutoTitle);

			propXmin = new PropObject("Min", "Min", id++, curGS.getXMin() + "");
			propXmax = new PropObject("Max", "Max", id++, curGS.getXMax() + "");
			propXlabel = new PropObject("Label", "Label", id++, curGS.getXLabel());
			propXlabel.prefWidth = 100;
			propAutoXlabel = new PropObject("Auto", "Auto", id++, curGS.getXAuto());

			propsXAxis.addProperty(propXmax);
			propsXAxis.addProperty(propXmin);
			propsXAxis.addProperty(propXlabel);
			propsXAxis.addProperty(propAutoXlabel);

			propYmin = new PropObject("Min", "Min", id++, curGS.getYMin() + "");
			propYmax = new PropObject("Max", "Max", id++, curGS.getYMax() + "");

			propYlabel = new PropObject("Label", "Label", id++, curGS.getYLabel());
			propAutoYlabel = new PropObject("Auto", "Auto", id++, curGS.getYAuto());

			propYlabel.prefWidth = 100;
			propsYAxis.addProperty(propYmax);
			propsYAxis.addProperty(propYmin);
			propsYAxis.addProperty(propYlabel);
			propsYAxis.addProperty(propAutoYlabel);
		} else {
			propAutoTitle.setChecked(graph.autoTitle);
			propTitle.setValue(graph.title);
			if(propDataSources != null){
				propDataSources.setValue(dsStrings[graph.getCurGraphSettings().dsIndex]);
			}

			if(propVisibleSources != null){
				if(graph.getMaxLines() == 1) propVisibleSources.setRadio(true);
				for(int i=0; i<dsStrings.length; i++){
					propVisibleSources.setCheckedValue(i, graph.getVisible(i));
				}
			}

			propXmin.setValue(curGS.getXMin() + "");
			propXmax.setValue(curGS.getXMax() + "");
			propXlabel.setValue(curGS.getXLabel());
			propAutoXlabel.setChecked(curGS.getXAuto());

			propYmin.setValue(curGS.getYMin() + "");
			propYmax.setValue(curGS.getYMax() + "");
			propYlabel.setValue("*" + curGS.getYLabel());
			propAutoYlabel.setChecked(curGS.getYAuto());
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		GraphSettings curGS = graph.getCurGraphSettings();

		if(e.getActionCommand().equals("Apply")){
			int firstDS = -1;
			if(propVisibleSources != null){
				String [] dsNames = propVisibleSources.getPossibleValues();
				for(int i=0; i<dsNames.length; i++){
					graph.setVisible(i, propVisibleSources.getCheckedValue(i));
					if(propVisibleSources.getCheckedValue(i)){
						firstDS = i;
					}
				}
			}

			GraphSettings newGS;
			if(propDataSources == null){
				newGS = graph.getCurGraphSettings();
			} else {
				int dsIndex = propDataSources.getIndex();
				graph.setCurGSIndex(dsIndex);
				newGS = graph.getCurGraphSettings();
			}

			if(newGS != curGS){
				return;
			}

			if(curGS == null) return;
			curGS.setXValues(propXmin.getFValue(), propXmax.getFValue());
			curGS.setYValues(propYmin.getFValue(), propYmax.getFValue());
			
			curGS.setXAuto(propAutoXlabel.getChecked());
			curGS.setXLabel(propXlabel.getValue());

			curGS.setYAuto(propAutoYlabel.getChecked());
			curGS.setYLabel(propYlabel.getValue());
		   
			// This should be cleaned up
			boolean autoTitle = propAutoTitle.getChecked();
			if(autoTitle){
				graph.name = "..auto_title..";
				graph.autoTitle = true;
			} else {
				graph.name = "Graph";
				graph.autoTitle = false;
				graph.title = propTitle.getValue();
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
