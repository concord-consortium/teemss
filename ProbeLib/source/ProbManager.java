package org.concord.waba.extra.probware;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.probware.probs.CCProb;
import extra.util.*;

public class ProbManager implements ProbListener, Transform{
public static ProbManager pb = null;
CCInterfaceManager im;

protected CCProb[]				probsArray;
protected ProbManagerListener[]	listenersArray;

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
		if(listenersArray == null || listenersArray.length < 1) return;
		pmEvent.setType(type);
		pmEvent.setInfo(info);
		boolean registration = ((type == ProbManagerEvent.PM_REGISTERED) || (type == ProbManagerEvent.PM_UNREGISTERED));
		for(int i = 0; i < listenersArray.length; i++){
			ProbManagerListener l = listenersArray[i];
			if(registration){
				l.pmRegistration(pmEvent);
			}else{
				l.pmActionPerformed(pmEvent);
			}
		}
	}
	
	public int findListener(ProbManagerListener l){
		int retValue = -1;
		if(listenersArray == null || l == null  || listenersArray.length < 1) return retValue;
		for(int i = 0; i < listenersArray.length; i++){
			if(listenersArray[i] == l){
				retValue = i;
				break;
			}
		}
		return retValue;
	}
	
	
	public void addProbManagerListener(ProbManagerListener l){
		if(listenersArray == null){
			listenersArray = new ProbManagerListener[1];
			listenersArray[0] = l;
		}else{
			if(findListener(l) < 0){
				ProbManagerListener []newArray = new ProbManagerListener[listenersArray.length + 1];
				waba.sys.Vm.copyArray(listenersArray,0,newArray,0,listenersArray.length);
				newArray[listenersArray.length] = l;
				listenersArray = newArray;
			}
		}
	}
	
	
	public void removeProbManagerListener(ProbManagerListener l){
		int index = findListener(l);
		if(index >= 0){
			if(listenersArray.length == 1){
				listenersArray = null;
			}else{
				for(int i = index + 1; i < listenersArray.length; i++){
					listenersArray[i - 1] = listenersArray[i];
				}
				ProbManagerListener []newArray = new ProbManagerListener[listenersArray.length - 1];
				waba.sys.Vm.copyArray(listenersArray,0,newArray,0,listenersArray.length);
				listenersArray = newArray;
			}
		}
	}
	
	public int findProb(CCProb prob){
		int retValue = -1;
		if(probsArray == null || prob == null  || probsArray.length < 1) return retValue;
		for(int i = 0; i < probsArray.length; i++){
			if(probsArray[i] == prob){
				retValue = i;
				break;
			}
		}
		return retValue;
	}
	
	public void registerProb(CCProb prob){
		boolean probAdded = false;
		if(probsArray == null){
			probsArray = new CCProb[1];
			probsArray[0] = prob;
			probAdded = true;
		}else{
			if(findProb(prob) < 0){
				CCProb []newArray = new CCProb[probsArray.length + 1];
				waba.sys.Vm.copyArray(probsArray,0,newArray,0,probsArray.length);
				newArray[probsArray.length] = prob;
				probsArray = newArray;
				probAdded = true;
			}
		}
		if(probAdded){
			prob.addProbListener(this);
			notifyListeners(ProbManagerEvent.PM_REGISTERED,prob);
		}
		syncModeWithProb();
	}
	public void unRegisterProb(CCProb prob){
		int index = findProb(prob);
		if(index >= 0){
			if(probsArray.length == 1){
				probsArray = null;
			}else{
				for(int i = index + 1; i < probsArray.length; i++){
					probsArray[i - 1] = probsArray[i];
				}
				CCProb []newArray = new CCProb[probsArray.length - 1];
				waba.sys.Vm.copyArray(probsArray,0,newArray,0,probsArray.length - 1);
				probsArray = newArray;
			}
			prob.removeProbListener(this);
			notifyListeners(ProbManagerEvent.PM_UNREGISTERED,prob);
		}
	}
	
	protected void syncModeWithProb(){
		if(probsArray == null || probsArray.length < 1) return;
		String modeValue = null;
		boolean isTheSame = true;
		for(int i = 0; i < probsArray.length; i++){
			CCProb p = probsArray[i];
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
		CCProb p = probsArray[0];
		if(p.samplingModes[CCProb.SAMPLING_24BIT_MODE] != null && modeValue.equals(p.samplingModes[CCProb.SAMPLING_24BIT_MODE])){
			setMode(CCInterfaceManager.A2D_24_MODE);
		}else if(p.samplingModes[CCProb.SAMPLING_10BIT_MODE] != null && modeValue.equals(p.samplingModes[CCProb.SAMPLING_10BIT_MODE])){
			setMode(CCInterfaceManager.A2D_10_MODE);
		}else if(p.samplingModes[CCProb.SAMPLING_DIG_MODE] != null && modeValue.equals(p.samplingModes[CCProb.SAMPLING_DIG_MODE])){
			setMode(CCInterfaceManager.DIG_COUNT_MODE);
		}
	}
	
	public int getNumbProbs(){
		if(probsArray == null) return 0;
		return probsArray.length;
	}
	
	public CCProb getProbByIndex(int i){
		if(i < 0 || i >= getNumbProbs()) return null;
		return probsArray[i];
	}
	
	protected CCProb getProbByName(String name){
		if(getNumbProbs() < 1) return null;
		for(int i = 0; i < probsArray.length; i++){
			CCProb p = probsArray[i];
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
	
    	public boolean idle(DataEvent e){
	    for(int i = 0; i < getNumbProbs(); i++){
		probsArray[i].idle(e);//need offset important, but not relevant right now
	    }
	    return true;
	}
    	public boolean startSampling(DataEvent e){
	    for(int i = 0; i < getNumbProbs(); i++){
		probsArray[i].startSampling(e);//need offset important, but not relevant right now
	    }
	    return true;
    	}

    	public boolean dataArrived(DataEvent e){
	    for(int i = 0; i < getNumbProbs(); i++){
		probsArray[i].dataArrived(e);//need offset important, but not relevant right now
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
