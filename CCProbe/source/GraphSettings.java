package org.concord.CCProbe;

import extra.util.*;
import extra.io.*;
import graph.*;

public class GraphSettings
{
    float xmin = 0f, xmax = 100f;
    float ymin = -20f, ymax = 50f;

    String description = null;
    String xLabel = null;
    String yLabel = null;
	CCUnit xUnit = null;
	CCUnit yUnit = null;

	public void setXValues(float min, float max)
	{
		xmin = min;
		xmax = max;
	}

	public void setYValues(float min, float max)
	{
		ymin = min;
		ymax = max;
	}

	public void setXLabel(String label)
	{
		xLabel = label;
	}

	public void setYLabel(String label)
	{
		yLabel = label;
	}

	public void setYUnit(CCUnit unit)
	{
		yUnit = unit;
	}

	public void setXUnit(CCUnit unit)
	{
		xUnit = unit;
	}


	public void updateAv(AnnotView av, Bin curBin)
	{
		av.setYLabel(yLabel, yUnit);
		av.setXLabel(xLabel, xUnit);		
		av.setRange(xmin, xmax, ymin, ymax);
		curBin.setUnit(yUnit);
	}

	public void updateGS(AnnotView av)
	{
		ymin = av.getYmin();
		ymax = av.getYmax();
		xmin = av.getXmin();
		xmax = av.getXmax();		
	}

	public void readExternal(DataStream ds)
	{
		xmin = ds.readFloat();
		xmax = ds.readFloat();
		ymin = ds.readFloat();
		ymax = ds.readFloat();
		description = ds.readString();
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
		ds.writeFloat(xmin);
		ds.writeFloat(xmax);
		ds.writeFloat(ymin);
		ds.writeFloat(ymax);
		ds.writeString(description);
		ds.writeString(xLabel);
		ds.writeString(yLabel);
		if(xUnit == null) ds.writeInt(-1);
		else ds.writeInt(xUnit.code);
		if(yUnit == null) ds.writeInt(-1);
		else ds.writeInt(yUnit.code);
    }

	public GraphSettings copy()
	{
		GraphSettings g = new GraphSettings();

		g.xmin = xmin;
		g.ymin = ymin;
		g.xmax = xmax;
		g.ymax = ymax;
		g.description = description.toString();
		g.xLabel = xLabel;
		g.yLabel = yLabel;
		g.xUnit = xUnit;
		g.yUnit = yUnit;		

		return g;
	}
}
