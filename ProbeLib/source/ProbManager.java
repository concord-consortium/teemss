package org.concord.waba.extra.probware;
import org.concord.waba.extra.event.DataEvent;
import org.concord.waba.extra.event.DataListener;
import org.concord.waba.extra.probware.probs.CCProb;
import extra.util.*;

public class ProbManager implements Transform{
public static ProbManager pb = null;
CCInterfaceManager im;
protected 	waba.util.Vector 	probs = null;
	protected ProbManager(){
		im = CCInterfaceManager.getInterfaceManager();
		im.setProbManager(this);
	}
	public static ProbManager getProbManager(){
		if(pb == null){
			pb = new ProbManager();
		}
		return pb;
	}
	
	public void registerProb(CCProb prob){
		if(probs == null) probs = new waba.util.Vector();
		if(probs.find(prob) < 0){
			probs.add(prob);
		}
		syncModeWithProb();
	}
	public void unRegisterProb(CCProb prob){
		int index = probs.find(prob);
		if(index >= 0){
			probs.del(index);
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
		}
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
		im.start();
	}
	public void stop(){
		if(im == null) return;
		im.stop();
	}
}