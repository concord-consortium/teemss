package org.concord.CCProbe;

import waba.util.*;
import waba.ui.*;
import extra.io.*;
import extra.util.*;
import org.concord.LabBook.*;
import graph.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.util.*;
import extra.util.*;

public class LObjAnnotation extends LObjSubDict
{
	String label;
	float time;
	float value;
	Annotation annot = null;

	int binIndex = -1;

    public LObjAnnotation()
    {
		super(DataObjFactory.ANNOTATION);
	}

	public void setup(Annotation a, LObjDataSet dSet, int bIndex)
	{
		annot = a;
		setObj(dSet, 0);
		binIndex = bIndex;
	}

    public LabObjectView getPropertyView(ViewContainer vc,LObjDictionary curDict)
	{
		return new AnnotationProp(vc, this);
    }

	public void addToBin(Bin b)
	{
		annot = b.addAnnot(label, time);
	}

	public Annotation getAnnot()
	{
		if(annot != null) return annot;
		return null;
	}
	
	public void setLabel(String label)
	{
		if(annot != null){
			annot.label = label;
			// need to notify bin/graph somehow
		} else {
			this.label = label;
		}
	}

	public String getLabel()
	{
		if(annot != null) return annot.label;
		else return label;
	}

	public float getTime()
	{
		if(annot != null) return annot.time;
		else return time;
	}

	public float getValue()
	{
		if(annot != null) return annot.value;
		else return value;
	}

    public void readExternal(DataStream ds)
    {
		super.readExternal(ds);
		label = ds.readString();
		time = ds.readFloat();
		value = ds.readFloat();
		binIndex = ds.readInt();
    }

    public void writeExternal(DataStream ds)
    {
		super.writeExternal(ds);
		ds.writeString(getLabel());
		ds.writeFloat(getTime());
		ds.writeFloat(getValue());
		ds.writeInt(binIndex);
    }

}
