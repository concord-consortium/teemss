import extra.io.*;
import org.concord.waba.extra.probware.probs.*;


public class LObjDataControl extends LObjSubDict
{
    int probeId = ProbFactory.Prob_ThermalCouple;
    LObjGraph graph;
    
    public static LObjDataControl makeNew()
    {
	LObjDataControl me = new LObjDataControl();
	me.dict = new LObjDictionary();
	me.dict.mainObject = me;
	me.graph = new LObjGraph();
	me.graph.name = "Graph";
	me.dict.add(me.graph);
	return me;
    }

    public void setGraph(LObjGraph g)
    {
	graph = g;
	setObj(g, 0);
    }

    public LObjDataControl()
    {
	objectType = DATA_CONTROL;
    }

    public LabObjectView getView(boolean edit)
    {
	return new LObjDataControlView(this);
    }

    public void readExternal(DataStream ds)
    {
	super.readExternal(ds);
	graph = (LObjGraph)getObj(0);
    }

    public void writeExternal(DataStream ds)
    {
	super.writeExternal(ds);
	ds.writeInt(probeId);
    }

    public void setDict(LObjDictionary d)
    {
	super.setDict(d);
	if(graph != null) setObj(graph, 0);
    }

}
