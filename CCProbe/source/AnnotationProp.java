package org.concord.CCProbe;

import waba.util.*;
import waba.ui.*;
import org.concord.waba.graph.*;

import org.concord.waba.extra.io.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.util.*;

import org.concord.LabBook.*;

public class AnnotationProp extends LabObjectView
	implements ActionListener
{
    PropContainer props = null;
	PropObject propLabel;
	PropObject propTime;
	PropObject propValue;

	LObjAnnotation annot;
	PropertyView propView = null;

	public AnnotationProp(ViewContainer vc, LObjAnnotation a)
	{
		super(vc, (LabObject)a, null);
		annot = a;

		setupProperties();
	}

	public void layout(boolean sDone)
	{
		if(didLayout) return;
		didLayout = true;

		propView = new PropertyView(this);
		propView.addContainer(props);
		add(propView);
	}

    public void setRect(int x, int y, int width, int height)
    {
		super.setRect(x,y,width,height);
		if(!didLayout) layout(false);

		propView.setRect(0,0,width,height);
	}

	public void setupProperties()
	{
		int id= 0;
		if(props == null){
			props = new PropContainer("Annot");
			
			propLabel = new PropObject("Label", "Label", id++, annot.getLabel());
			propTime = new PropObject("Time", "Time", id++, "" + annot.getTime());
			propValue = new PropObject("Value", "Value", id++, "" + annot.getValue());

			props.addProperty(propLabel);
			props.addProperty(propTime);
			props.addProperty(propValue);

		}			
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("Apply")){
			annot.setLabel(propLabel.getValue());			
		} else if(e.getActionCommand().equals("Close")){
			// this is a cancel or close
			if(container != null){
				container.done(this);
			}	    
		}
	}

}
