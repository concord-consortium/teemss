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

	public void addToBin(Bin b)
	{
		annot = b.addAnnot(label, time);
	}

	public Annotation getAnnot()
	{
		if(annot != null) return annot;
		return null;
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
		if(annot != null){
			ds.writeString(annot.label);
			ds.writeFloat(annot.time);
			ds.writeFloat(annot.value);
		} else {
			ds.writeString(label);
			ds.writeFloat(time);
			ds.writeFloat(value);
		}
		ds.writeInt(binIndex);
    }

}
