package org.concord.waba.extra.probware;
import org.concord.waba.extra.event.DataEvent;
import org.concord.waba.extra.event.DataListener;
import org.concord.waba.extra.probware.probs.CCProb;

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
	}
	public void unRegisterProb(CCProb prob){
		int index = probs.find(prob);
		if(index >= 0){
			probs.del(index);
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
	public void setMode(int mode){
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