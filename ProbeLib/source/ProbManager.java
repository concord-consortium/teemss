package org.concord.waba.extra.probware;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.probware.probs.CCProb;
import extra.util.*;

public class ProbManager implements ProbListener{
public static ProbManager pb = null;
CCInterfaceManager im;
protected 	waba.util.Vector 	probs 	= null;
protected 	waba.util.Vector 	listeners 	= null;
protected 	static ProbManagerEvent   pmEvent = new ProbManagerEvent();
	protected ProbManager(int interfaceType){
		im = CCInterfaceManager.getInterfaceManager(interfaceType);
		im.setProbManager(this);
		pmEvent.setProbManager(this);
	}
	public static ProbManager getProbManager(int interfaceType){
		if(pb == null){
			pb = new ProbManager(interfaceType);
		}
		return pb;
	}
	
	public void probChanged(ProbEvent e){
	}
	
	protected void notifyListeners(int type,Object info){
		if(listeners == null) return;
		pmEvent.setType(type);
		pmEvent.setInfo(info);
		boolean registration = ((type == ProbManagerEvent.PM_REGISTERED) || (type == ProbManagerEvent.PM_UNREGISTERED));
		for(int i = 0; i < listeners.getCount(); i++){
			ProbManagerListener l = (ProbManagerListener)listeners.get(i);
			if(l == null) continue;
			if(registration){
				l.pmRegistration(pmEvent);
			}else{
				l.pmActionPerformed(pmEvent);
			}
		}
	}
	
	public void addProbManagerListener(ProbManagerListener l){
		if(listeners == null) listeners = new waba.util.Vector();
		if(listeners.find(l) < 0) listeners.add(l);
	}
	public void removeProbManagerListener(ProbManagerListener l){
		int index = listeners.find(l);
		if(index >= 0) listeners.del(index);
	}
	
	public void registerProb(CCProb prob){
		if(probs == null) probs = new waba.util.Vector();
		if(probs.find(prob) < 0){
			probs.add(prob);
			prob.addProbListener(this);
			notifyListeners(ProbManagerEvent.PM_REGISTERED,prob);
		}
		syncModeWithProb();
	}
	public void unRegisterProb(CCProb prob){
		int index = probs.find(prob);
		if(index >= 0){
			prob.removeProbListener(this);
			probs.del(index);
			notifyListeners(ProbManagerEvent.PM_UNREGISTERED,prob);
		}
	}
	
	protected void syncModeWithProb(){
		if(probs == null) return;
		String modeValue = null;
		boolean isTheSame = true;
		for(int i = 0; i < probs.getCount(); i++){
			CCProb p = (CCProb)probs.get(i);
			PropObject po = p.getProperty(CCProb.samplingModeString);
			String value = po.getValue();
			if(value == null) continue;
			if(modeValue == null){
				modeValue = value;
			}else{
				if(!modeValue.equals(value)){
					isTheSame = false;
					break;
				}
			}
		}
		if(modeValue == null) return;
		if(!isTheSame) return;
		CCProb p = (CCProb)probs.get(0);
		if(p.samplingModes[CCProb.SAMPLING_24BIT_MODE] != null && modeValue.equals(p.samplingModes[CCProb.SAMPLING_24BIT_MODE])){
			setMode(CCInterfaceManager.A2D_24_MODE);
		}else if(p.samplingModes[CCProb.SAMPLING_10BIT_MODE] != null && modeValue.equals(p.samplingModes[CCProb.SAMPLING_10BIT_MODE])){
			setMode(CCInterfaceManager.A2D_10_MODE);
		}else if(p.samplingModes[CCProb.SAMPLING_DIG_MODE] != null && modeValue.equals(p.samplingModes[CCProb.SAMPLING_DIG_MODE])){
			setMode(CCInterfaceManager.DIG_COUNT_MODE);
		}
	}
	
	public int getNumbProbs(){
		if(probs == null) return 0;
		return probs.getCount();
	}
	
	public CCProb getProbByIndex(int i){
		if(i < 0 || i >= getNumbProbs()) return null;
		return (CCProb)probs.get(i);
	}
	
	protected CCProb getProbByName(String name){
		if(probs == null) return null;
		for(int i = 0; i < probs.getCount(); i++){
			CCProb p = (CCProb)probs.get(i);
			if(p == null) continue;
			if(name.equals(p.getName())){
				return p;
			}
		}
		return null;
	}
	
	public void addDataListenerToProb(String name,DataListener l){
		CCProb p = getProbByName(name);
		if(p!=null) p.addDataListener(l);
	}
	public void removeDataListenerFromProb(String name,DataListener l){
		CCProb p = getProbByName(name);
		if(p!=null) p.removeDataListener(l);
	}
	
    	public boolean transform(DataEvent e){
    		if(probs == null) return false;
    		for(int i = 0; i < probs.getCount(); i++){
    			CCProb p = (CCProb)probs.get(i);
			p.transform(e); //need offset important, but not relevant right now
    		}
    		return true;
    	}
	public int getMode(){return im.getMode();}
	protected void setMode(int mode){
		im.setMode(mode);
	}
	public void dispose(){
		im.dispose();
		im = null;
	}
	public void start(){
		if(im == null) return;
		syncModeWithProb();
		notifyListeners(ProbManagerEvent.PM_START,null);
		im.start();
	}
	public void stop(){
		if(im == null) return;
		im.stop();
		notifyListeners(ProbManagerEvent.PM_STOP,null);
	}
}
