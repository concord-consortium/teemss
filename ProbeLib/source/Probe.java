package org.concord.waba.extra.probware.probs;

import org.concord.waba.extra.event.DataListener;
import org.concord.waba.extra.event.DataEvent;
import org.concord.waba.extra.probware.Transform;

public abstract class CCProb implements Transform{
public 	waba.util.Vector 	dataListeners = null;
String	name = null;

	protected CCProb(){
		this("unknown");
	}
	
	protected CCProb(String name){
		setName(name);
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
		if(dataListeners == null) return;
		for(int i = 0; i < dataListeners.getCount(); i++){
			DataListener l = (DataListener)dataListeners.get(i);
			l.dataReceived(e);
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
}
