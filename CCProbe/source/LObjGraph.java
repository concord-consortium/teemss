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

public class LObjGraph extends LabObject
	implements DialogListener
{
	Vector graphSettings = null;

    String title = null;

    PropertyDialog pDialog = null;
    PropContainer props = null;
	PropObject propTitle;
    PropObject propXmin;
    PropObject propXmax;
	PropObject propXlabel;
    PropObject propYmin;
    PropObject propYmax;
	PropObject propYlabel;

	GraphSettings curGS = null;

	boolean autoTitle = false;

	LObjGraphView gv = null;

    public LObjGraph()
    {
		super(DataObjFactory.GRAPH);
	}

	public GraphSettings getNewGS()
	{
		if(graphSettings == null){
			graphSettings = new Vector();
			curGS = new GraphSettings();
			graphSettings.add(curGS);
		}

		return curGS;
	}

	public void setupProperties()
	{
		if(curGS == null) return;

		props = new PropContainer();
		props.createSubContainer("Graph");
		props.createSubContainer("YAxis");	
		props.createSubContainer("XAxis");

		if(autoTitle)  propTitle = new PropObject("Title", "*" + title);
		else propTitle = new PropObject("Title", title);
		propTitle.prefWidth = 100;

		propXmin = new PropObject("Min", curGS.xmin + "");
		propXmax = new PropObject("Max", curGS.xmax + "");
		propXlabel = new PropObject("Label", curGS.xLabel);
		propXlabel.prefWidth = 100;
		propYmin = new PropObject("Min", curGS.ymin + "");
		propYmax = new PropObject("Max", curGS.ymax + "");

		if(autoTitle) propYlabel = new PropObject("Label", "*" + curGS.yLabel);
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

    public void showAxisProp()
	{
		showAxisProp(0);
	}

    public void showAxisProp(int index)
    {
		MainWindow mw = MainWindow.getMainWindow();
		if(mw instanceof ExtraMainWindow){
			setupProperties();

			pDialog = new PropertyDialog((ExtraMainWindow)mw, this, "Properties", props, index);
			pDialog.setRect(0,0, 140,140);
			pDialog.show();
		}
    }
    
    public void dialogClosed(DialogEvent e)
    {
		if(!e.getActionCommand().equals("Cancel")){
			if(curGS == null) return;
			curGS.setXValues(propXmin.createFValue(), propXmax.createFValue());
			curGS.setYValues(propYmin.createFValue(), propYmax.createFValue());
			
			curGS.setXLabel(propXlabel.getValue());

			String newTitle = propTitle.getValue();
			String newYLabel = propYlabel.getValue();

			if(!autoTitle && 
			   ((newTitle.length() > 0 && 
				 newTitle.charAt(0) == '*') ||
				(newYLabel.length() > 0 &&
				 newYLabel.charAt(0) == '*'))){
				autoTitle = true;
				name = "..auto_title..";
			} else if(autoTitle && 
			   ((newTitle.length() > 0 && 
				 newTitle.charAt(0) != '*') ||
				(newYLabel.length() > 0 &&
				 newYLabel.charAt(0) != '*'))){
				autoTitle = false;
				name = "Graph";
			}

			if(!autoTitle){
				title = newTitle;
				curGS.setYLabel(newYLabel);
			}

			if(gv != null) gv.updateProp();
		}
    }

    public LabObjectView getView(ViewContainer vc, boolean edit, LObjDictionary curDict)
    {
		if(graphSettings != null){
			curGS = (GraphSettings)graphSettings.get(0);
		}

		gv = new LObjGraphView(vc, this, curDict);
		return gv;
    }

	public void removeGV()
	{
		if(autoTitle) name = "..auto_title..";

		if(curDS != null){
			curDS.removeDataListener(curGS);
		}

		gv = null;
	}

    public void readExternal(DataStream ds)
    {
		super.readExternal(ds);
		title = ds.readString();
		int numSettings = ds.readInt();
		if(numSettings <= 0) return;

		graphSettings = new Vector(numSettings);
		for(int i=0; i<numSettings; i++){
			GraphSettings gs = new GraphSettings();
			gs.readExternal(ds);
			graphSettings.add(gs);
		}
		
		// little white hack
		curGS = (GraphSettings)graphSettings.get(0);
    }

    public void writeExternal(DataStream ds)
    {
		super.writeExternal(ds);
		ds.writeString(title);
		if(graphSettings == null ||
		   graphSettings.getCount() <= 0){
			ds.writeInt(0);		
			return;
		} else {
			ds.writeInt(graphSettings.getCount());
		}

		for(int i=0; i<graphSettings.getCount(); i++){
			GraphSettings gs = (GraphSettings)graphSettings.get(i);
			gs.writeExternal(ds);
		}
    }
	
	public LabObject copy()
	{
		LObjGraph g = DataObjFactory.createGraph();

		g.title = title.toString();
		g.graphSettings = new Vector(graphSettings.getCount());
		
		for(int i=0; i<graphSettings.getCount(); i++){
			GraphSettings gs = (GraphSettings)graphSettings.get(i);
			g.graphSettings.add(gs.copy());
		}

		return g;
	}

	public void startDataDelivery()
	{
		curDS.startDataDelivery();
	}

	DataSource curDS;
	public void addDataSource(DataSource ds)
	{
		if(ds == null) return;

		// need to pass in object at this point to identify which 
		// data source is which
		if(curGS != null) ds.addDataListener(curGS);

		curDS = ds;
		if(curDS != null && curGS != null){			
			curGS.setYUnit(curDS.getUnit());
			if(name.equals("..auto_title..")){
				curGS.setYLabel(curDS.getLabel());
				title = curDS.getSummary();
				autoTitle = true;
			} 			
		}
	}

}
