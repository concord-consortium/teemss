package org.concord.waba.extra.probware.transformers;

import org.concord.waba.extra.probware.*;
import org.concord.waba.extra.event.*;
import extra.util.PropObject;

public abstract class CCTransformer implements Transform{
String		name = null;
PropObject		[]properties = null;
public 		waba.util.Vector 	dataListeners = null;

	public CCTransformer(){
		this("unknown");
	}
	public CCTransformer(String name){
		this.name = name;
	}
	public void setName(String name){this.name = name;}
	public String getName(){return name;}
	
	public void addDataListener(DataListener l){
		if(dataListeners == null) dataListeners = new waba.util.Vector();
		if(dataListeners.find(l) < 0) dataListeners.add(l);
	}
	public void removeDataListener(DataListener l){
		int index = dataListeners.find(l);
		if(index >= 0) dataListeners.del(index);
	}
	public void notifyDataListeners(DataEvent e){
		if(dataListeners == null) return;
		for(int i = 0; i < dataListeners.getCount(); i++){
			DataListener l = (DataListener)dataListeners.get(i);
			l.dataReceived(e);
		}
	}

}