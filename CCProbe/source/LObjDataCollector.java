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

	public void firstStore(LabBookSession session)
	{
		super.firstStore(session);
		LObjGraph graph = DataObjFactory.createGraph();
		graph.setName("..auto_title..");
		session.storeNew(graph);
		setGraph(graph);
	}

    public void setGraph(LObjGraph g)
    {
		setObj(g, 0);
    }

	public LObjGraph getGraph(LabBookSession session)
	{
		return (LObjGraph)getObj(0, session);
	}

    public LabObjectView getView(ViewContainer vc, 
								 boolean edit, LObjDictionary curDict,
								 LabBookSession session)
    {
	    return new LObjDataCollectorView(vc, this, curDict, session);
    }
    public LabObjectView getPropertyView(ViewContainer vc, 
										 LObjDictionary curDict,
										 LabBookSession session){
		return new LObjDataCollectorProp(vc, this, curDict, session);
	}
    public void readExternal(DataStream ds){}

    public void writeExternal(DataStream ds){}
}
