package org.concord.LabBook;

import extra.io.*;

public class LObjGraph extends LabObject
{
    float xmin = 0f, xmax = 100f;
    float ymin = -20f, ymax = 50f;

    String title = null;
    String xLabel = null;
    String yLabel = null;

    LObjDataSet dataSet;

    public LObjGraph()
    {
	objectType = GRAPH;
    }
    
    public LabObjectView getView(LObjViewContainer vc, boolean edit)
    {
	return new LObjGraphView(vc, this);
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
    }
}
