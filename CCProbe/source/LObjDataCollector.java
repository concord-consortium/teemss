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
    public LObjDataCollector()
    {	
		super(DataObjFactory.DATA_COLLECTOR);
    }

    public void init(){
    	super.init();
		LObjGraph graph = DataObjFactory.createGraph();
		graph.setName("..auto_title..");
		graph.store();
		setGraph(graph);
    }
    
    public void setGraph(LObjGraph g)
    {
		setObj(g, 0);
    }
	public LObjGraph getGraph()
	{
		return (LObjGraph)getObj(0);
	}

    public LabObjectView getView(ViewContainer vc, boolean edit, LObjDictionary curDict)
    {
	    return new LObjDataCollectorView(vc, this, curDict);
    }
    public LabObjectView getPropertyView(ViewContainer vc, LObjDictionary curDict){
		return new LObjDataCollectorProp(vc, this, curDict);
	}
    public void readExternal(DataStream ds){}

    public void writeExternal(DataStream ds){}
}
