package org.concord.waba.extra.probware.probs;

import org.concord.waba.extra.event.DataListener;
import org.concord.waba.extra.event.DataEvent;
import org.concord.waba.extra.probware.*;
import extra.util.*;

public abstract class CCProb implements Transform{
public 		waba.util.Vector 	dataListeners = null;
String		name = null;
PropObject		[]properties = null;
CalibrationDesc	calibrationDesc;
public static final String defaultModeName = "Default";

public final static String samplingModeString = "SamplingMode";
public String	[]samplingModes =  {"Slow Sampling","Fast Sampling"};

public final static int		SAMPLING_24BIT_MODE = 0;
public final static int		SAMPLING_10BIT_MODE = 1;


DataListener calibrationListener = null;
	protected CCProb(){
		this("unknown");
	}
	
	protected CCProb(String name){
		setName(name);
		calibrationDesc = null;
	}

	public boolean needCalibration(){return (calibrationDesc != null);}
	public CalibrationDesc getCalibrationDesc(){return calibrationDesc;}
	public void setCalibrationDesc(CalibrationDesc calibrationDesc){this.calibrationDesc = calibrationDesc;}

	public int	getActiveChannels(){return 1;}

	public void setCalibrationListener(DataListener calibrationListener){
		this.calibrationListener = calibrationListener;
	}

	public void clearCalibrationListener(){
		setCalibrationListener(null);
	}

	public void addDataListener(DataListener l){
		if(dataListeners == null) dataListeners = new waba.util.Vector();
		if(dataListeners.find(l) < 0) dataListeners.add(l);
	}
	public void removeDataListener(DataListener l){
		int index = dataListeners.find(l);
		if(index >= 0) dataListeners.del(index);
	}
	public void notifyListeners(DataEvent e){
		if(calibrationListener != null){
			calibrationListener.dataReceived(e);
		}else{
			if(dataListeners == null) return;
			for(int i = 0; i < dataListeners.getCount(); i++){
				DataListener l = (DataListener)dataListeners.get(i);
				l.dataReceived(e);
			}
		}
	}
	public abstract void setDataDescParam(int chPerSample,float dt);
	
	public void setName(String name){this.name = name;}
	public String getName(){return name;}
	
	public static CCProb getCCThermalCoupleProb(String name){
		return new CCThermalCouple(name);
	}
	
	public static CCProb getCCLightIntensityProb(String name){
		return new CCLightIntens(name);
	}
	
	public PropObject getProperty(String nameProperty){
		if(nameProperty == null) return null;
		if(countProperties() < 1) return null;
		for(int i = 0; i < countProperties(); i++){
			PropObject p = properties[i];
			if(p == null) continue;
			if(nameProperty.equals(p.getName())){
				return p;
			}
		}
		return null;
	}
	public PropObject getProperty(int index){
		if(index < 0 || index >= countProperties()) return null;
		return properties[index];
	}
	public int countProperties(){
		if(properties == null) return 0;
		return properties.length;
	}
	
	public void setPropertyValue(String nameProperty,String value){
		PropObject p = getProperty(nameProperty);
		if(p == null) return;
		p.setValue(value);
	}
	public void setPropertyValue(int index,String value){
		PropObject p = getProperty(index);
		if(p == null) return;
		p.setValue(value);
	}
	
	public String getPropertyValue(String nameProperty){
		PropObject p = getProperty(nameProperty);
		if(p == null) return null;
		return p.getValue();
	}
	public float getPropertyValueAsFloat(String nameProperty){
		PropObject p = getProperty(nameProperty);
		if(p == null) return 0.0f;
		p.createFValue();
		return p.getFValue();
	}
	
	public PropObject[]	getProperties(){return properties;}
	
	
	public void  calibrationDone(float []row1,float []row2,float []calibrated){}
	
}
