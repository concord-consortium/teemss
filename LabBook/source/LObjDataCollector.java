package org.concord.LabBook;

import waba.util.*;
import extra.io.*;
import extra.util.*;
import org.concord.waba.extra.util.*;
import org.concord.waba.extra.probware.probs.*;
import org.concord.waba.extra.probware.*;

public class LObjDataCollector extends LObjSubDict
{
	Vector dataSources;
	int numDataSources = 0;
	int [][] dsArray;

    int interfaceId = CCInterfaceManager.INTERFACE_2;
    int portId = CCProb.INTERFACE_PORT_A;
    //    LObjGraph graph;

	
	public static LObjSubDict makeNew()
    {
		LObjDataCollector me = new LObjDataCollector();
		me.initSubDict();

		LObjGraph graph = new LObjGraph();
		graph.name = "Graph";
		me.setGraph(graph);
		return me;
    }

	public String getSummaryTitle()
	{
		String title;

		// What's up with this.
		if(dataSources == null || dataSources.getCount() < 1 ||
		   !(dataSources.get(0) instanceof LObjProbeDataSource)){
			return "DS not a valid";
		}

		LObjProbeDataSource pds = (LObjProbeDataSource)dataSources.get(0);
		CCProb p = pds.getProbe();

		title = p.getName() + "(";
		PropObject [] props = p.getProperties();
		int i;
		for(i=0; i < props.length-1; i++){
			title += props[i].getName() + "- " + props[i].getValue() + "; ";
		}
		title += props[i].getName() + "- " + props[i].getValue() + ")";

		return title;
	}

    public void setGraph(LObjGraph g)
    {
		//	graph = g;
		setObj(g, 0);
    }
	public LObjGraph getGraph()
	{
		return (LObjGraph)getObj(0);
	}

	public void setDataSources(Vector sources)
	{
		numDataSources = sources.getCount();
		for(int i=0; i<numDataSources; i++){
			setObj((LabObject)sources.get(i), i+1);
			
		}
		dataSources = sources;
	}

	public Vector getDataSources()
	{
		dataSources = new Vector(numDataSources);
		for(int i=0; i<numDataSources; i++){
			dataSources.add(getObj(i+1));
		}

		return dataSources;
	}

	public void start()
	{ 
		for(int i=0; i<dataSources.getCount(); i++){
			((DataSource) dataSources.get(i)).startDataDelivery();
		}

	}

	public void stop()
	{
		for(int i=0; i<dataSources.getCount(); i++){
			((DataSource) dataSources.get(i)).stopDataDelivery();
		}
	}		

	public void closeSources()
	{
		for(int i=0; i<dataSources.getCount(); i++){
			((DataSource) dataSources.get(i)).closeEverything();
		}
	}

    public LObjDataCollector()
    {
		objectType = DATA_COLLECTOR;
    }

    public LabObjectView getView(LObjViewContainer vc, boolean edit, LObjDictionary curDict)
    {
		return new LObjDataCollectorView(vc, this, curDict);
    }
    public LabObjectView getPropertyView(LObjViewContainer vc, LObjDictionary curDict){
		return new LObjDataCollectorProp(vc, this, curDict);
	}
    public void readExternal(DataStream ds)
    {
		super.readExternal(ds);
		numDataSources = ds.readInt();
    }

    public void writeExternal(DataStream ds)
    {
		super.writeExternal(ds);
		if(dataSources == null){
			ds.writeInt(0);
		} else {
			numDataSources = dataSources.getCount();
			ds.writeInt(numDataSources);
		}
    }
}
