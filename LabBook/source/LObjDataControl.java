package org.concord.LabBook;

import extra.io.*;
import org.concord.waba.extra.probware.probs.*;
import org.concord.waba.extra.probware.*;

public class LObjDataControl extends LObjSubDict
{
    int probeId = ProbFactory.Prob_ThermalCouple;
    int interfaceId = CCInterfaceManager.INTERFACE_2;
    int portId = CCProb.INTERFACE_PORT_A;
    //    LObjGraph graph;
    
    public static LObjDataControl makeNew()
    {
	LObjDataControl me = new LObjDataControl();
	me.dict = new LObjDictionary();
	me.dict.setMainObj(me);
	LObjGraph graph = new LObjGraph();
	graph.name = "Graph";
	me.setGraph(graph);
	return me;
    }

    public void setGraph(LObjGraph g)
    {
	//	graph = g;
	setObj(g, 0);
    }

    public void setDataDict(LObjDictionary d)
    {
	setObj(d, 1);
    }

    public LObjDataControl()
    {
	objectType = DATA_CONTROL;
    }

    public LabObjectView getView(LObjViewContainer vc, boolean edit)
    {
	if(edit){
	    return new LObjDataControlEditView(vc, this);
	} else {
	    return new LObjDataControlView(vc, this);
	}
    }

    public void readExternal(DataStream ds)
    {
	super.readExternal(ds);
	probeId = ds.readInt();
	//	graph = (LObjGraph)getObj(0);
    }

    public void writeExternal(DataStream ds)
    {
	super.writeExternal(ds);
	ds.writeInt(probeId);
    }

    /*
    public void setDict(LObjDictionary d)
    {
	super.setDict(d);
	if(graph != null) setObj(graph, 0);
    }
    */
}
