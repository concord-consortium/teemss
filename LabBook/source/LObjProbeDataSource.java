package org.concord.LabBook;

import waba.util.*;
import waba.ui.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;
import extra.ui.*;
import extra.util.CCUnit;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.probware.probs.*;

public class LObjProbeDataSource extends LObjDataSource
{
CCProb probe = null;
    public LObjProbeDataSource()
    {
		super();
    }
    public LObjProbeDataSource(CCProb probe)
    {
		super();
		setProbe(probe);
    }

    public LabObjectView getView(LObjViewContainer vc, boolean edit,LObjDictionary curDict){
    	return null;
    }
    
    public LabObjectView getPropertyView(LObjViewContainer vc, boolean edit,LObjDictionary curDict){
    	return null;
    }

	public CCProb 	getProbe(){return probe;}
	public void		setProbe(CCProb probe){
		this.probe = probe;
		setUnit();
		name = (probe == null)?null:probe.getName();
	}
	
	void setUnit(){
		if(probe == null){
			currentUnit = null;
			return;
		}
		currentUnit = CCUnit.getUnit(probe.getUnit());
	}

	public boolean dataArrived(org.concord.waba.extra.event.DataEvent e){
		if(probe == null) return false;
		return probe.dataArrived(e);
	}
	
	public boolean idle(org.concord.waba.extra.event.DataEvent e){
		if(probe == null) return false;
		return probe.idle(e);
	}
	
	public boolean startSampling(org.concord.waba.extra.event.DataEvent e){
		if(probe == null) return false;
		return probe.startSampling(e);
	}


    public void writeExternal(extra.io.DataStream out)
    {
    	ProbFactory.storeProbeToStream(probe,out);
		super.writeExternal(out);
    }

    public void readExternal(extra.io.DataStream in)
    {
    	probe = ProbFactory.createProbeFromStream(in);
		super.readExternal(in);
    }

	
	public static LObjProbeDataSource getProbeDataSource(int probeID,int interfacePort){
		CCProb p = ProbFactory.createProb(probeID,interfacePort);
		if(p == null) return null;
		return new LObjProbeDataSource(p);
	}

}

