package org.concord.ProbeLib;

public class ProbEvent extends waba.ui.Event
{
	public static final int PROBE_PROPERTY_CHANGED 		= 3000;
	Object info = null;

	public ProbEvent(){
		this(null,PROBE_PROPERTY_CHANGED,null);
	}
	public ProbEvent(Probe  target, int type, Object info){
		this.type = type;
		this.target = target;
		this.info = info;
	}
	
	public void setType(int type){this.type = type;}
	public void setInfo(Object info){this.info = info;}
	public void setProb(Probe target){this.target = target;}
	
	public int getType(){return type;}
	public Object getInfo(){return info;}
	public Probe getProb(){return (Probe)target;}
	
	
	public String toString(){
		return ("Type: "+type+"; Probe: "+target+"; Info: "+info);
	}
}
