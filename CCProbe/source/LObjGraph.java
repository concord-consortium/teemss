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
{
    String title = null;
	boolean autoTitle = false;

	Vector graphSettings = null;
	GraphSettings curGS = null;

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

    public void showAxisProp()
	{
		showAxisProp(0);
	}

    public void showAxisProp(int index)
    {
		MainWindow mw = MainWindow.getMainWindow();
		if(mw instanceof ExtraMainWindow){
			LObjGraphProp gProp = (LObjGraphProp) getPropertyView(null, null);
			gProp.index = index;
			ViewDialog vDialog = new ViewDialog((ExtraMainWindow)mw, null, "Properties", gProp);
			vDialog.setRect(0,0,150,150);
			vDialog.show();
		}
    }

    public LabObjectView getPropertyView(ViewContainer vc, LObjDictionary curDict){
		return new LObjGraphProp(vc, this, 0);
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
	int numDataSources = 0;
	public void addDataSource(DataSource ds)
	{
		if(ds == null) return;

		// need to pass in object at this point to identify which 
		// data source is which
		if(graphSettings == null) graphSettings = new Vector();

		if(numDataSources < graphSettings.getCount()){
			curGS = (GraphSettings)graphSettings.get(numDataSources);
		} else {
			curGS = new GraphSettings();
			graphSettings.add(curGS);
		}

		numDataSources++;
		
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
