package org.concord.ProbeLib;

import extra.util.*;

public class ProbManager 
	implements ProbListener, Transform
{
	public static ProbManager pb = null;
	CCInterfaceManager im;

	protected Probe[]				probsArray = null;

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
	
	public int findProb(Probe prob){
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
	
	public void registerProb(Probe prob){
		boolean probAdded = false;
		if(probsArray == null){
			probsArray = new Probe[1];
			probsArray[0] = prob;
			probAdded = true;
		}else{
			if(findProb(prob) < 0){
				Probe []newArray = new Probe[probsArray.length + 1];
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
	public void unRegisterProb(Probe prob){
		int index = findProb(prob);
		if(index >= 0){
			if(probsArray.length == 1){
				probsArray = null;
			}else{
				for(int i = index + 1; i < probsArray.length; i++){
					probsArray[i - 1] = probsArray[i];
				}
				Probe []newArray = new Probe[probsArray.length - 1];
				waba.sys.Vm.copyArray(probsArray,0,newArray,0,probsArray.length - 1);
				probsArray = newArray;
			}
			prob.removeProbListener(this);
		}
	}
	
	protected void syncModeWithProb(){
		if(probsArray == null || probsArray.length < 1) return;
		int firstMode = -1;
		boolean isTheSame = true;
		for(int i = 0; i < probsArray.length; i++){
			Probe p = probsArray[i];
			int curMode = p.getInterfaceMode();

			if(curMode < 0) continue;
			if(firstMode < 0){
				firstMode = curMode;

			}else{
				if(firstMode != curMode){
					isTheSame = false;
					break;
				}
			}
		}

		if(firstMode < 0) return;
		if(!isTheSame) return;

		setMode(firstMode);
	}
	
	public int getNumbProbs(){
		if(probsArray == null) return 0;
		return probsArray.length;
	}
	
	public Probe getProbByIndex(int i){
		if(i < 0 || i >= getNumbProbs()) return null;
		return probsArray[i];
	}
	
	protected Probe getProbByName(String name){
		if(getNumbProbs() < 1) return null;
		for(int i = 0; i < probsArray.length; i++){
			Probe p = probsArray[i];
			if(p == null) continue;
			if(name.equals(p.getName())){
				return p;
			}
		}
		return null;
	}
	
	public boolean stopSampling(DataEvent e){
	    for(int i = 0; i < getNumbProbs(); i++){
			probsArray[i].stopSampling(e);//need offset important, but not relevant right now
	    }
	    return true;
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
			Probe []newArray = new Probe[probsArray.length];
			waba.sys.Vm.copyArray(probsArray,0,newArray,0,probsArray.length);

			for(int i = 0; i < newArray.length; i++){
				Probe prob = newArray[i];
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

