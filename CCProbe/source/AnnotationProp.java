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

public class AnnotationProp extends LabObjectView
	implements ActionListener
{
    PropContainer props = null;
	PropObject propName;
	PropObject propLabel;
	PropObject propTime;
	PropObject propValue;

	LObjAnnotation annot;
	PropertyView propView = null;

	public AnnotationProp(ViewContainer vc, LObjAnnotation a)
	{
		super(vc);
		annot = a;
		lObj = a;	

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
		if(props == null){
			props = new PropContainer("Annot");
			
			propName = new PropObject("Name", annot.name);
			propLabel = new PropObject("Label", annot.getLabel());
			propTime = new PropObject("Time", "" + annot.getTime());
			propValue = new PropObject("Value", "" + annot.getValue());

			props.addProperty(propName);
			props.addProperty(propLabel);
			props.addProperty(propTime);
			props.addProperty(propValue);

		}			
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("Apply")){
			annot.name = propName.getValue();
			annot.setLabel(propLabel.getValue());			
		} else if(e.getActionCommand().equals("Close")){
			// this is a cancel or close
			if(container != null){
				System.out.println("AnnoProp: calling cont.done");
				container.done(this);
			}	    
		}
	}

}
