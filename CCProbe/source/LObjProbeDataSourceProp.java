package org.concord.CCProbe;

import waba.ui.*;
import waba.util.*;
import extra.io.*;
import extra.ui.*;
import extra.util.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.probware.probs.*;
import org.concord.waba.extra.probware.ProbManager;
import org.concord.waba.extra.probware.CCInterfaceManager;
import org.concord.LabBook.*;

public class LObjProbeDataSourceProp extends LabObjectView
	implements ActionListener
{
	LObjProbeDataSource pds;
	CalibrationView cView = null;

	public LObjProbeDataSourceProp(ViewContainer vc, LObjProbeDataSource ds)
	{
		super(vc);
		pds = ds;
		lObj = ds;			
	}

	public void layout(boolean showDone)
	{
		if(didLayout) return;
		didLayout = true;

		cView = new CalibrationView(pds.getProbe(), pds.interfaceType, this);
		add(cView);
	}

    public void setRect(int x, int y, int width, int height)
    {
		super.setRect(x,y,width,height);
		if(!didLayout) layout(false);

		cView.setRect(0,0,width,height);
	}

	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getActionCommand().equals("Close")){
			// this is a cancel or close
			if(container != null){
				container.done(this);
			}	    
		}

		pds.notifyObjListeners(new LabObjEvent(pds, 0));
		return;
	}  	
}
