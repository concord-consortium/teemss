package org.concord.waba.extra.probware;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.probware.probs.CCProb;
import extra.util.*;

public class ProbManager implements ProbListener, Transform{
public static ProbManager pb = null;
CCInterfaceManager im;

protected CCProb[]				probsArray = null;

	protected ProbManager(int interfaceType){
		im = CCInterfaceManager.getInterfaceManager(interfaceType);
		im.setProbManager(this);
	}
	public static ProbManager getProbManager(int interfaceType){
		if(pb == null){
			pb = new ProbManager(interfaceType);
		}
		return pb;
	}
	
	public void probChanged(ProbEvent e){
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
		if(probsArray != null && probsArray.length > 0){
			CCProb []newArray = new CCProb[probsArray.length];
			waba.sys.Vm.copyArray(probsArray,0,newArray,0,probsArray.length);

			for(int i = 0; i < newArray.length; i++){
				CCProb prob = newArray[i];
				if(prob != null) unRegisterProb(prob);
			}
		}
		probsArray = null;
		im.dispose();
		im = null;
		pb = null;
	}
	
//	public static java.io.FileWriter pbstream = null;
	
	public void start(){
		if(im == null) return;
/*
		try{
			pbstream = new java.io.FileWriter("VOLTMETER.txt");
		}catch(Exception e){
			pbstream = null;
		}
*/
		syncModeWithProb();

		im.start();
	}
	public void stop(){
/*
		if(pbstream != null){
			try{
				pbstream.close();
			}catch(Exception e){}
			pbstream = null;
		}
*/
		if(im == null) return;
		im.stop();
	}
}

