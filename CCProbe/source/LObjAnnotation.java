package org.concord.CCProbe;

import waba.util.*;
import waba.ui.*;
import org.concord.waba.graph.*;

import org.concord.waba.extra.io.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.util.*;

import org.concord.LabBook.*;

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
		if(a != null){
			setName(annot.getLabel());
		}
		setObj(dSet, 0);
		binIndex = bIndex;
	}

    public ViewDialog showPropDialog(DialogListener dl)
    {
		MainWindow mw = MainWindow.getMainWindow();
		if(mw instanceof ExtraMainWindow){
		    AnnotationProp aProp = 
				(AnnotationProp) getPropertyView(null, null, null);
			ViewDialog vDialog = new ViewDialog((ExtraMainWindow)mw, dl, "Properties", aProp);
			vDialog.setRect(0,0,159,159);
			vDialog.show();
			return vDialog;
		}
		return null;
    }

    public LabObjectView getPropertyView(ViewContainer vc,
										 LObjDictionary curDict,
										 LabBookSession session)
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
		label = ds.readString();
		time = ds.readFloat();
		value = ds.readFloat();
		binIndex = ds.readInt();
    }

    public void writeExternal(DataStream ds)
    {
		ds.writeString(getLabel());
		ds.writeFloat(getTime());
		ds.writeFloat(getValue());
		ds.writeInt(binIndex);
    }

}
