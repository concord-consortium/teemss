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

    // old CCA2D2 interface
    // 	static int defaultInterfaceType = CCInterfaceManager.INTERFACE_0;

    // new CCA2D2v2 interface
	static int defaultInterfaceType = CCInterfaceManager.INTERFACE_2;

	ProbManager pb = null;

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

	public DataListener setModeDataListener(DataListener l, int type)
	{
		if(probe != null){
			return probe.setModeDataListener(l, type);
		}
		return null;
	}

	public static String [] getProbeNames()
	{
		return ProbFactory.getProbNames();	
	}

	public String getProbeName()
	{
		int probeId = getProbe().getProbeType();
		return ProbFactory.getName(probeId);
	}

	public String getSummary()
	{
		String summary;

		CCProb p = getProbe();

		summary = p.getName() + "(";
		PropObject [] props = p.getPropArray();
		int i;
		for(i=0; i < props.length-1; i++){
			summary += props[i].getLabel() + "- " + props[i].getValue() + "; ";
		}
		summary += props[i].getLabel() + "- " + props[i].getValue() + ")";

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

	boolean started = false;
	public void startDataDelivery(){
		if(probe == null || started) return;

		if(pb == null){
			// This ds was disposed so we should reset the probe
			setProbe(probe);
		}
		
		if(pb != null){
			pb.start();
			started = true;
		}
	}
	
	public void stopDataDelivery(){
		if(probe == null || !started) return;
		if(pb != null){
			pb.stop();
			started = false;
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

	public void zeroForce()
	{
		if(probe instanceof CCForce){
			CCForce fProbe = (CCForce)probe;
			
			fProbe.startZero();
			startDataDelivery();
		}
	}

	public CCProb 	getProbe(){return probe;}
	public void		setProbe(CCProb probe){
		unRegisterProbeWithPM();
		if(this.probe != null){
			this.probe.removeProbListener(this);
		}
		this.probe = probe;
		setUnit();
		setName((probe == null)?null:probe.getName());
		if(this.probe != null){
			this.probe.addProbListener(this);
		}
		registerProbeWithPM();
	}
	
	public void unRegisterProbeWithPM(){
		if(probe == null || pb == null) return;
		pb.unRegisterProb(probe);
	}
	public void registerProbeWithPM(){
		if(probe == null) return;

		// This could caused problems in a mixed environment if the 
		// user is using probes with different interface managers at the
		// same time
		if(pb == null) pb = ProbManager.getProbManager(probe.getInterfaceType());

		if(pb == null) return;
		pb.registerProb(probe);
	}
	
	public void closeEverything(){
		if(probe != null){
			probe.removeProbListener(this);
		}

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
	
    public void probChanged(ProbEvent e)
	{
		if(e.getProb() instanceof CCForce &&
		   e.getType() == CCForce.ZEROING_DONE){
			stopDataDelivery();
			store();
		}

    	notifyProbListeners(e);
    }

    public void writeExternal(extra.io.DataStream out)
    {
    	ProbFactory.storeProbeToStream(probe,out);
    }

    public void readExternal(extra.io.DataStream in)
    {
    	CCProb probe = ProbFactory.createProbeFromStream(in);
		setProbe(probe);
    }

	public void calibrateMe(ExtraMainWindow owner,DialogListener l){
		if(probe == null) return;
		probe.calibrateMe(owner,l,probe.getInterfaceType());
	}
	
	public static LObjProbeDataSource getProbeDataSource(String probeName)
	{
		return getProbeDataSource(ProbFactory.getIndex(probeName), defaultInterfaceType);
	}

	public static LObjProbeDataSource getProbeDataSource(String probeName, int interfaceType)
	{
		return getProbeDataSource(ProbFactory.getIndex(probeName), interfaceType);
	}

	public static LObjProbeDataSource getProbeDataSource(int probeID,int interfaceType){
		CCProb p = ProbFactory.createProb(probeID,CCProb.INTERFACE_PORT_A);
		if(p == null) return null;
		p.setInterfaceType(interfaceType);
		LObjProbeDataSource me = DataObjFactory.createProbeDataSource();
		me.setProbe(p);
		return me;
	}

	public DataSource getQuantityDataSource(String qName)
	{
		if(qName.equals(getQuantityMeasured())){
			return this;
		} else {
			int quantId = getQuantityId(qName);
			if(quantId < 0) return null;

			LObjIntProbeTrans trans = 
				(LObjIntProbeTrans)DataObjFactory.create(DataObjFactory.INT_PROBE_TRANS);
			trans.setDataSource(this);
			trans.setType(quantId);
			return trans;
		}
	}

	public String getQuantityMeasured(int id)
	{
		if(probe != null) return probe.getQuantityName(id);
		return null;
	}

	public int getQuantityId(String quantityName)
	{
		if(probe != null) return probe.getQuantityId(quantityName);
		return -1;
	}

	public String getQuantityMeasured()
	{
		if(probe != null) return probe.getDefQuantityName();
		return null;
	}
 
	public String [] getQuantityNames()
	{
		if(probe != null){
		    return probe.getQuantityNames();
		} 
		return null;
	}
	
	public CCUnit getQuantityUnit(int id)
	{
		if(probe != null){
			int unitId =  probe.getQuantityUnit(id);
			if(unitId >= 0){
				return CCUnit.getUnit(unitId);
			}
		}
		return null;
	}

	public void getRootSources(Vector sources)
	{
		if(sources != null){
			sources.add(this);
		}
	}

}

