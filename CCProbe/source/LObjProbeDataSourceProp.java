package org.concord.CCProbe;

import waba.ui.*;
import waba.util.*;

import org.concord.waba.extra.io.*;
import org.concord.waba.extra.util.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;

import org.concord.ProbeLib.*;
import org.concord.ProbeLib.probes.*;
import org.concord.LabBook.*;

public class LObjProbeDataSourceProp extends LabObjectView
	implements ActionListener
{
	LObjProbeDataSource pds;
	PropertyView pView = null;
	CalibrationPane cPane = null;

	public LObjProbeDataSourceProp(ViewContainer vc, LObjProbeDataSource ds)
	{
		super(vc, (LabObject)ds, null);
		pds = ds;
	}

	public void layout(boolean showDone)
	{
		if(didLayout) return;
		didLayout = true;

		pView = new PropertyView(this);
		pView.addContainer(pds.getProbe());
		pView.addPane(new CalibrationPane(pds.getProbe(), null, pView));
		add(pView);
	}

    public void setRect(int x, int y, int width, int height)
    {
		super.setRect(x,y,width,height);
		if(!didLayout) layout(false);

		pView.setRect(0,0,width,height);
	}

	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getActionCommand().equals("Close")){
			if(container != null){
				container.done(this);
			}	    
			return;
		}

		pds.notifyObjListeners(new LabObjEvent(pds, 0));
		return;
	}  	
}
