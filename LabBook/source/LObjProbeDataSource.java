package org.concord.LabBook;

import waba.util.*;
import waba.ui.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;
import extra.ui.*;
import extra.util.CCUnit;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.probware.probs.*;
import org.concord.waba.extra.probware.ProbManager;
import org.concord.waba.extra.probware.CCInterfaceManager;

public class LObjProbeDataSource extends LObjDataSource implements DataListener, ProbListener
{
CCProb 			probe = null;
DataListener 	calibrationListener = null;
public 			waba.util.Vector 	probListeners = null;
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


	public void startDataDelivery(){
		if(probe == null) return;
		ProbManager pb = ProbManager.getProbManager(probe.getInterfaceType());
		if(pb != null) pb.start();
	}
	
	public void stopDataDelivery(){
		if(probe == null) return;
		ProbManager pb = ProbManager.getProbManager(probe.getInterfaceType());
		if(pb != null) pb.stop();
	}

	
	public void setCalibrationListener(DataListener calibrationListener){
		if(probe != null){
			this.calibrationListener = calibrationListener;
			if(calibrationListener != null)
				probe.setCalibrationListener(this);
			else
				probe.setCalibrationListener(null);
		}else{
			calibrationListener = null;
		}
	}
	
	public void clearCalibrationListener(){
		setCalibrationListener(null);
	}

	public CCProb 	getProbe(){return probe;}
	public void		setProbe(CCProb probe){
		unRegisterProbeWithPM();
		if(this.probe != null){
			this.probe.removeDataListener(this);
			this.probe.removeProbListener(this);
		}
		this.probe = probe;
		setUnit();
		name = (probe == null)?null:probe.getName();
		if(this.probe != null){
			this.probe.addProbListener(this);
		}
		registerProbeWithPM();
	}
	
	public void unRegisterProbeWithPM(){
		if(probe == null) return;
		ProbManager pb = ProbManager.getProbManager(probe.getInterfaceType());
		pb.unRegisterProb(probe);
	}
	public void registerProbeWithPM(){
		if(probe == null) return;
		ProbManager pb = ProbManager.getProbManager(probe.getInterfaceType());
		if(pb == null) return;
		pb.registerProb(probe);
		pb.addDataListenerToProb(probe.getName(),this);
	}
	
	public void addProbManagerListener(ProbManagerListener l){
		ProbManager pb = ProbManager.getProbManager(probe.getInterfaceType());
		if(pb != null) pb.addProbManagerListener(l);
	}
	
	public void closeEverything(){
		ProbManager pb = ProbManager.getProbManager(probe.getInterfaceType());
		if(pb != null){
			pb.dispose();
			pb = null;
		}
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

	public void notifyDataListeners(DataEvent e){
		if(calibrationListener != null){
			calibrationListener.dataReceived(e);
		}else{
			super.notifyDataListeners(e);
		}
	}
	public void addProbListener(ProbListener l){
		if(probListeners == null) probListeners = new waba.util.Vector();
		if(probListeners.find(l) < 0) probListeners.add(l);
	}
	public void removeProbListener(ProbListener l){
		int index = probListeners.find(l);
		if(index >= 0) probListeners.del(index);
	}
	public void notifyProbListeners(ProbEvent e){
		if(probListeners == null) return;
		for(int i = 0; i < probListeners.getCount(); i++){
			ProbListener l = (ProbListener)probListeners.get(i);
			l.probChanged(e);
		}
	}
	
	public void dataReceived(DataEvent dataEvent){
		notifyDataListeners(dataEvent);
	}
    public void probChanged(ProbEvent e){
    	notifyProbListeners(e);
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
		setProbe(probe);
    }

	public void calibrateMe(ExtraMainWindow owner,DialogListener l){
		if(probe == null) return;
		probe.calibrateMe(owner,l,probe.getInterfaceType());
	}
	
	
	public static LObjProbeDataSource getProbeDataSource(int probeID,int interfaceType, int interfacePort){
		CCProb p = ProbFactory.createProb(probeID,interfacePort);
		if(p == null) return null;
		p.setInterfaceType(interfaceType);
		return new LObjProbeDataSource(p);
	}

}

