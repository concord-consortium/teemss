package org.concord.LabBook;

import extra.io.*;
import extra.util.*;
import org.concord.waba.extra.util.*;
import org.concord.waba.extra.probware.probs.*;
import org.concord.waba.extra.probware.*;

public class LObjDataControl extends LObjSubDict
{
    int probeId = ProbFactory.Prob_ThermalCouple;
    CCProb curProbe = null;
    int interfaceId = CCInterfaceManager.INTERFACE_2;
    int portId = CCProb.INTERFACE_PORT_A;
    //    LObjGraph graph;

	
	public static LObjSubDict makeNew()
    {
		LObjDataControl me = new LObjDataControl();
		me.initSubDict();

		LObjGraph graph = new LObjGraph();
		graph.name = "Graph";
		me.setGraph(graph);
		return me;
    }

	public void updateGraphProp()
	{
		CCProb p = getProbe();
		LObjGraph graph = getGraph();
		graph.yUnit = CCUnit.getUnit(p.unit);
	}

	public String getSummaryTitle()
	{
		String title;

		// What's up with this.
		CCProb p = getProbe();
		title = p.getName() + "(";
		PropObject [] props = p.getProperties();
		int i;
		for(i=0; i < props.length-1; i++){
			title += props[i].getName() + "- " + props[i].getValue() + "; ";
		}
		title += props[i].getName() + "- " + props[i].getValue() + ")";

		return title;
	}

    public CCProb getProbe()
    {
		if(curProbe == null){
			curProbe = ProbFactory.createProb(probeId, portId);
		}
		return curProbe;
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

    public LObjDataControl()
    {
		objectType = DATA_CONTROL;
    }

    public LabObjectView getView(LObjViewContainer vc, boolean edit, LObjDictionary curDict)
    {
		return new LObjDataControlView(vc, this, curDict);
    }
    public LabObjectView getPropertyView(LObjViewContainer vc, LObjDictionary curDict){
		return new LObjDataControlEditView(vc, this, curDict);
	}
    public void readExternal(DataStream ds)
    {
		super.readExternal(ds);
		probeId = ds.readInt();
		portId = ds.readInt();
		curProbe = ProbFactory.createProb(probeId, portId);
		curProbe.readExternal(ds);
		//	graph = (LObjGraph)getObj(0);
    }

    public void writeExternal(DataStream ds)
    {
		super.writeExternal(ds);
		ds.writeInt(probeId);
		ds.writeInt(portId);
		getProbe().writeExternal(ds);
    }

    /*
	  public void setDict(LObjDictionary d)
	  {
	  super.setDict(d);
	  if(graph != null) setObj(graph, 0);
	  }
    */
}
