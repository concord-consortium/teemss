package org.concord.CCProbe;

import waba.util.*;
import extra.io.*;
import extra.util.*;
import org.concord.waba.extra.util.*;
import org.concord.waba.extra.probware.probs.*;
import org.concord.waba.extra.probware.*;
import org.concord.LabBook.*;

public class LObjDataCollector extends LObjSubDict
{
	private Vector dataSources;
	int numDataSources = 0;
	int [][] dsArray;

    // old CCA2D2 interface
    // int interfaceId = CCInterfaceManager.INTERFACE_0;

    // new CCA2D2v2 interface
    int interfaceId = CCInterfaceManager.INTERFACE_2;

    int portId = CCProb.INTERFACE_PORT_A;
    //    LObjGraph graph;

	
    public LObjDataCollector()
    {	
		super(DataObjFactory.DATA_COLLECTOR);
		LObjProbeDataSource.interfaceType = interfaceId;
    }

    public void init(){
    	super.init();
		LObjGraph graph = DataObjFactory.createGraph();
		graph.setName("..auto_title..");
		graph.store();
		setGraph(graph);
    }
    
	public void store()
	{
		if(dataSources != null){
			for(int i=0; i<dataSources.getCount(); i++){
				LabObject obj = (LabObject)dataSources.get(i);
				if(obj != null) obj.store();
			}
		}
		super.store();		
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

    public LabObjectView getView(ViewContainer vc, boolean edit, LObjDictionary curDict)
    {
	    return new LObjDataCollectorView(vc, this, curDict);
    }
    public LabObjectView getPropertyView(ViewContainer vc, LObjDictionary curDict){
		return new LObjDataCollectorProp(vc, this, curDict);
	}
    public void readExternal(DataStream ds)
    {
		numDataSources = ds.readInt();
    }

    public void writeExternal(DataStream ds)
    {
		ds.writeInt(numDataSources);
    }
    
    public int getInterfaceID(){return interfaceId;}
}
