package org.concord.CCProbe;

import waba.util.*;
import waba.ui.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;
import extra.ui.*;
import extra.util.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.probware.probs.*;
import org.concord.waba.extra.probware.ProbManager;
import org.concord.waba.extra.probware.CCInterfaceManager;
import org.concord.LabBook.*;


public class LObjProbeDataSource extends LObjSubDict
	implements DataSource, ProbListener
{
CCProb 			probe = null;
CCUnit		currentUnit = null;
	public waba.util.Vector probListeners = null;
	public static int interfaceType = CCInterfaceManager.INTERFACE_0;

    public LObjProbeDataSource()
    {
		super(DataObjFactory.PROBE_DATA_SOURCE);
    }

    public LObjProbeDataSource(CCProb probe)
    {
		this();
		setProbe(probe);
    }

    public LabObjectView getView(ViewContainer vc, boolean edit,LObjDictionary curDict){
    	return null;
    }
    
    public LabObjectView getPropertyView(ViewContainer vc, LObjDictionary curDict){
		return new LObjProbeDataSourceProp(vc, this);
    }

    public void showProp()
    {
		MainWindow mw = MainWindow.getMainWindow();
		if(mw instanceof ExtraMainWindow){
			LObjProbeDataSourceProp pdsProp = (LObjProbeDataSourceProp) getPropertyView(null, null);
			ViewDialog vDialog = new ViewDialog((ExtraMainWindow)mw, null, "Properties", pdsProp);
			vDialog.setRect(0,0,150,150);
			vDialog.show();
		}
    }

	public void addDataListener(DataListener l){
		if(probe != null){
			probe.addDataListener(l);
		}
	}

	public void removeDataListener(DataListener l){
		if(probe != null){
			probe.removeDataListener(l);
		}
	}

	public String getLabel()
	{
		if(probe != null) return probe.getLabel();
		else return null;		
	}

	public String getSummary()
	{
		String summary;

		CCProb p = getProbe();

		summary = p.getName() + "(";
		PropObject [] props = p.getProperties();
		int i;
		for(i=0; i < props.length-1; i++){
			summary += props[i].getName() + "- " + props[i].getValue() + "; ";
		}
		summary += props[i].getName() + "- " + props[i].getValue() + ")";

		return summary;
	}


	public CCUnit 	getUnit()
	{
		if(probe != null) return CCUnit.getUnit(probe.getUnit());
		else return null;
	}
	public boolean 	setUnit(CCUnit unit){
		boolean retValue = false;
		if(probe == null || unit == null) return retValue;
		retValue = probe.setUnit(unit.code);
		if(retValue){
			currentUnit = CCUnit.getUnit(probe.getUnit());
		}
		return retValue;
	}

	public void startDataDelivery(){
		if(probe == null) return;
		ProbManager pb = ProbManager.getProbManager(probe.getInterfaceType());
		
		if(pb != null){
			pb.start();
		}
	}
	
	public void stopDataDelivery(){
		if(probe == null) return;
		ProbManager pb = ProbManager.getProbManager(probe.getInterfaceType());
		if(pb != null){
			pb.stop();
		}
	}

	
	public void setCalibrationListener(DataListener calibrationListener){
		if(probe != null && calibrationListener != null){
			probe.setCalibrationListener(calibrationListener);
		}
	}
	
	public void clearCalibrationListener(){
		if(probe != null) probe.setCalibrationListener(null);
	}

	public CCProb 	getProbe(){return probe;}
	public void		setProbe(CCProb probe){
		unRegisterProbeWithPM();
		if(this.probe != null){
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
	}
	
	public void closeEverything(){
		if(probe != null){
			probe.removeProbListener(this);
		}

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
	
    public void probChanged(ProbEvent e){
    	notifyProbListeners(e);
    }

    public void writeExternal(extra.io.DataStream out)
    {
		super.writeExternal(out);
    	ProbFactory.storeProbeToStream(probe,out);
    }

    public void readExternal(extra.io.DataStream in)
    {
		super.readExternal(in);
    	CCProb probe = ProbFactory.createProbeFromStream(in);
		probe.setInterfaceType(interfaceType);
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
		LObjProbeDataSource me = DataObjFactory.createProbeDataSource();
		me.setProbe(p);
		return me;
	}

	public void getRootSources(Vector sources)
	{
		if(sources != null){
			sources.add(this);
		}
	}

}

