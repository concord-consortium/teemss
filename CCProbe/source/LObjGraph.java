package org.concord.CCProbe;

import extra.io.*;
import extra.util.*;
import org.concord.LabBook.*;

public class LObjGraph extends LabObject
{
    float xmin = 0f, xmax = 100f;
    float ymin = -20f, ymax = 50f;

    String title = null;
    String xLabel = null;
    String yLabel = null;
	CCUnit xUnit = null;
	CCUnit yUnit = null;

    LObjDataSet dataSet;

    public LObjGraph()
    {
		super(DataObjFactory.GRAPH);
    }
    
    public LabObjectView getView(ViewContainer vc, boolean edit, LObjDictionary curDict)
    {
		return new LObjGraphView(vc, this, curDict);
    }

    public void readExternal(DataStream ds)
    {
		super.readExternal(ds);
		xmin = ds.readFloat();
		xmax = ds.readFloat();
		ymin = ds.readFloat();
		ymax = ds.readFloat();
		title = ds.readString();
		xLabel = ds.readString();
		yLabel = ds.readString();		
		int code;
		code = ds.readInt();
		if(code == -1) xUnit = null;
		else xUnit = CCUnit.getUnit(code);
		code = ds.readInt();
		if(code == -1) yUnit = null;
		else yUnit = CCUnit.getUnit(code);
    }
    
    public void writeExternal(DataStream ds)
    {
		super.writeExternal(ds);
		ds.writeFloat(xmin);
		ds.writeFloat(xmax);
		ds.writeFloat(ymin);
		ds.writeFloat(ymax);
		ds.writeString(title);
		ds.writeString(xLabel);
		ds.writeString(yLabel);
		if(xUnit == null) ds.writeInt(-1);
		else ds.writeInt(xUnit.code);
		if(yUnit == null) ds.writeInt(-1);
		else ds.writeInt(yUnit.code);
    }
	
	public LabObject copy()
	{
		LObjGraph g = new LObjGraph();

		g.xmin = xmin;
		g.ymin = ymin;
		g.xmax = xmax;
		g.ymax = ymax;
		g.title = title;
		g.xLabel = xLabel;
		g.yLabel = yLabel;
		g.xUnit = xUnit;
		g.yUnit = yUnit;		

		return g;
	}


}
