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
	}
	
	void setUnit(){
		if(probe == null){
			currentUnit = null;
			return;
		}
		currentUnit = CCUnit.getUnit(probe.getUnit());
	}
	
	public static LObjProbeDataSource getProbeDataSource(int probeID,int interfacePort){
		CCProb p = ProbFactory.createProb(probeID,interfacePort);
		if(p == null) return null;
		return new LObjProbeDataSource(p);
	}

}

