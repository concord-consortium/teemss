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

public class LObjGraph extends LObjSubDict
{
    String title = null;
	boolean autoTitle = false;

	Vector graphSettings = null;
	//	GraphSettings curGS = null;
	Vector xAxisVector = new Vector();
	Vector yAxisVector = new Vector();

	int numDataSources = 0;
	int curGSIndex = -1;

    public LObjGraph()
    {
		super(DataObjFactory.GRAPH);
	}

    public void showProp()
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
			vDialog.setRect(0,0,159,159);
			vDialog.show();
		}
    }

	public void clear()
	{
		// need to clear the object that are stored in this one
		// ick.
		numDataSources = 0;		
		graphSettings = null;
		xAxisVector = new Vector();
		yAxisVector = new Vector();
	}

	public boolean getVisible(int index)
	{
		if(index >= 0 && index < graphSettings.getCount()){
			GraphSettings gs = (GraphSettings)graphSettings.get(index);
			return gs.getVisible();
		}
		return false;
	}

	public void setVisible(int index, boolean val)
	{
		if(index >= 0 && index < graphSettings.getCount()){
			GraphSettings gs = (GraphSettings)graphSettings.get(index);
			gs.setVisible(val);
		} else {
			index = -1;
		}

		if(val == true && index >= 0 && maxLines == 1 && index != curGSIndex){
			GraphSettings oldGS = (GraphSettings)graphSettings.get(curGSIndex);
			// need to change the current graph settings
			oldGS.setVisible(false);
			setCurGSIndex(index);
		}
	}

	int maxLines = 1;
	public int getMaxLines(){ return maxLines;}
	public void setMaxLines(int maxLines)
	{
		this.maxLines = maxLines;			
	}

    static float XMIN = 0f, XMAX = 100f;
    static float YMIN = -20f, YMAX = 50f;

	public void createDefaultAxis()
	{
		SplitAxis xaxis = new SplitAxis(Axis.BOTTOM);
		ColorAxis yaxis = new ColorAxis(Axis.LEFT);
		yaxis.setMaxDigits(6);
		
		xAxisVector.add(xaxis);
		yAxisVector.add(yaxis);

		xaxis.setRange(XMIN, XMAX-XMIN);
		yaxis.setRange(YMIN, YMAX-YMIN);	
	}

	public void addYAxis()
	{
		ColorAxis yaxis = new ColorAxis(Axis.LEFT);
		yaxis.setMaxDigits(6);
		
		yAxisVector.add(yaxis);
	}

	public void addXAxis()
	{
		SplitAxis xaxis = new SplitAxis(Axis.BOTTOM);
		
		xAxisVector.add(xaxis);
	}

	public Axis getXAxis(int i)
	{
		if(i<0 || i>= xAxisVector.getCount()){
			return null;
		}
		else return (Axis)xAxisVector.get(i);
	}

	public Axis getYAxis(int i)
	{
		if(i<0 || i>= yAxisVector.getCount()){
			return null;
		}
		else return (Axis)yAxisVector.get(i);
	}

	public void addDataSource(DataSource ds)
	{
		addDataSource(ds, true, -1, -1);
	}

	public void addDataSource(DataSource ds, boolean newSettings, int linkX, int linkY)
	{
		if(graphSettings == null){
			graphSettings = new Vector();
			curGSIndex = 0;
			numDataSources = 0;
		}
		if(ds instanceof LabObject){
			setObj((LabObject)ds, numDataSources);
		} else {
			setObj(null, numDataSources);
		}

		GraphSettings gs = null;
		if(newSettings){
			gs = new GraphSettings(this, numDataSources, linkX, linkY);
			gs.setXAuto(true);
			gs.setXUnit(CCUnit.getUnit(CCUnit.UNIT_CODE_S));
			
			gs.setYAuto(true);
			gs.setYUnit(ds.getUnit());

			title = ds.getSummary();

			graphSettings.add(gs);
			gs.close();
		} else if(graphSettings.getCount() > numDataSources){
			gs = (GraphSettings)graphSettings.get(numDataSources);
			gs.dsIndex = numDataSources;
			gs.ds = null;
		}
		if(maxLines == 1 && numDataSources > 0 && gs != null){
			gs.setVisible(false);
		}

		numDataSources++;
	}

	public DataSource getDataSource(int index)
	{
		if(index >= 0 && index < numDataSources){
			LabObject obj = getObj(index);
			if(obj instanceof DataSource){
				return (DataSource)obj;
			}
		}
		return null;
	}

	public void setCurGSIndex(int index)
	{		
		if(index >= 0 && index < graphSettings.getCount() &&
		   index != curGSIndex){
			curGSIndex = index;
			notifyObjListeners(new LabObjEvent(this, 1));
			getCurGraphSettings().setVisible(true);
		}
	}

	public GraphSettings getCurGraphSettings()
	{
		if(graphSettings != null &&
		   curGSIndex >= 0 &&
		   curGSIndex < graphSettings.getCount()){
			return (GraphSettings)graphSettings.get(curGSIndex);
		}
		return null;
	}

	public GraphSettings getGraphSettings(int index)
	{
		if(index >= 0 && index < graphSettings.getCount() &&
		   index != curGSIndex){
			return (GraphSettings)graphSettings.get(index);
		}
		return null;
	}

    public LabObjectView getPropertyView(ViewContainer vc, LObjDictionary curDict){
		return new LObjGraphProp(vc, this, 0);
	}

    public LabObjectView getView(ViewContainer vc, boolean edit, LObjDictionary curDict)
    {
		return new LObjGraphView(vc, this, curDict);
    }


	public void store()
	{
		if(autoTitle) name = "..auto_title..";
		
		super.store();
	}

    public void readExternal(DataStream ds)
    {
		super.readExternal(ds);
		title = ds.readString();
		numDataSources = ds.readInt();
		if(numDataSources <= 0) return;
		curGSIndex = ds.readInt();
		int numXAxis = ds.readInt();
		int numYAxis = ds.readInt();

		xAxisVector = new Vector(numXAxis+1);
		for(int i=0; i<numXAxis; i++){
			SplitAxis ax = new SplitAxis(Axis.BOTTOM);
			ax.readExternal(ds);
			xAxisVector.add(ax);
		}

		yAxisVector = new Vector(numYAxis+1);
		for(int i=0; i<numYAxis; i++){
			ColorAxis ax = new ColorAxis(Axis.LEFT);
			ax.readExternal(ds);
			yAxisVector.add(ax);
		}

		graphSettings = new Vector(numDataSources);
		for(int i=0; i<numDataSources; i++){
			GraphSettings gs = new GraphSettings(this, i);
			gs.readExternal(ds);
			graphSettings.add(gs);
		}
		
		
		if(name != null && name.equals("..auto_title..")){
			autoTitle = true;
		} 			
		// little white hack
		// curGS = (GraphSettings)graphSettings.get(0);
    }

    public void writeExternal(DataStream ds)
    {
		super.writeExternal(ds);
		ds.writeString(title);

		if(numDataSources <= 0 ||
		   graphSettings == null ||
		   numDataSources != graphSettings.getCount()){
			ds.writeInt(0);		
			return;
		} else {
			ds.writeInt(numDataSources);
		}
		ds.writeInt(curGSIndex);
		ds.writeInt(xAxisVector.getCount());
		ds.writeInt(yAxisVector.getCount());

		for(int i=0; i<xAxisVector.getCount(); i++){
			SplitAxis ax = (SplitAxis)xAxisVector.get(i);
			ax.writeExternal(ds);
		}

		for(int i=0; i<yAxisVector.getCount(); i++){
			ColorAxis ax = (ColorAxis)yAxisVector.get(i);
			ax.writeExternal(ds);
		}

		for(int i=0; i<graphSettings.getCount(); i++){
			GraphSettings gs = (GraphSettings)graphSettings.get(i);
			gs.writeExternal(ds);
		}
    }

	public LabObject copy(int gsIndex)
	{
		if(gsIndex >= 0 &&
		   gsIndex < numDataSources){
			LObjGraph g = DataObjFactory.createGraph();
			
			g.title = title.toString();

			/*
			g.graphSettings = new Vector(graphSettings.getCount());
		
			GraphSettings gs = (GraphSettings)graphSettings.get(gsIndex);
			g.graphSettings.add(gs.copy(g));
			g.curGSIndex = 0;
			g.numDataSources = 0;
			*/

			return g;
		}

		return null;
	}

	
	public LabObject copy()
	{
		return copy(0);
	}

	public void startAll()
	{ 
		if(graphSettings == null) return;
		for(int i=0; i<graphSettings.getCount(); i++){
			GraphSettings gs = (GraphSettings)graphSettings.get(i);
			if(gs != null) gs.startDataDelivery();
		}
	}

	public void stopAll()
	{
		if(graphSettings == null) return;
		for(int i=0; i<graphSettings.getCount(); i++){
			GraphSettings gs = (GraphSettings)graphSettings.get(i);
			if(gs != null) gs.stopDataDelivery();
		}
	}		

	public void clearAll()
	{
		if(graphSettings == null) return;
		for(int i=0; i<graphSettings.getCount(); i++){
			GraphSettings gs = (GraphSettings)graphSettings.get(i);
			gs.clear();
		}
	}

	public void closeAll()
	{
		if(graphSettings == null) return;
		for(int i=0; i<graphSettings.getCount(); i++){
			GraphSettings gs = (GraphSettings)graphSettings.get(i);
			if(gs != null) gs.close();
		}

		for(int i=0; i<xAxisVector.getCount(); i++){
			Axis ax = (Axis)xAxisVector.get(i);
			//			if(ax.refCount != 0) ystem.out.println("LOG: xaxis: " + i + " not freed");
		}

		for(int i=0; i<yAxisVector.getCount(); i++){
			Axis ax = (Axis)yAxisVector.get(i);
			// if(ax.refCount != 0) ystem.out.println("LOG: yaxis: " + i + " not freed");
		}
	}

	public void saveAllAnnots()
	{
		if(graphSettings == null) return;
		for(int i=0; i<graphSettings.getCount(); i++){
			GraphSettings gs = (GraphSettings)graphSettings.get(i);
			if(gs != null) gs.saveAnnots();
		}
	}

	public void saveCurData(LObjDictionary dataDict)
	{
		LObjDataSet dSet = DataObjFactory.createDataSet();

		LObjGraph dsGraph = (LObjGraph)copy();
		dsGraph.name = "Graph";

		dSet.setDataViewer(dsGraph);
		dSet.clearAnnots();
		GraphSettings curGS = getCurGraphSettings();
		if(curGS != null){
			curGS.saveData(dSet);
			Vector annots = curGS.getAnnots();
			dSet.addAnnots(annots);
			
			if(dataDict != null){
				dataDict.add(dSet);				
				dSet.store();
				SplitAxis xaxis = new SplitAxis(Axis.BOTTOM);
				ColorAxis yaxis = new ColorAxis(Axis.LEFT);
				yaxis.setMaxDigits(6);
		
				dsGraph.xAxisVector.add(xaxis);
				dsGraph.yAxisVector.add(yaxis);

				xaxis.setRange(curGS.getXMin(), curGS.getXMax()-curGS.getXMin());
				yaxis.setRange(curGS.getYMin(), curGS.getYMax()-curGS.getYMin());

				dsGraph.addDataSource(dSet, true, 0, 0);
				dsGraph.store();
			} else {
				// for now it is an error
				// latter it should ask the user for the name
			}
		}

	}

	public void exportCurData()
	{
		GraphSettings curGS = getCurGraphSettings();
		if(curGS != null){
			Bin curBin = curGS.getBin();
			if(curBin != null){
				curBin.description = title;
				DataExport.export(curBin);
			}
		}
	}
}
