package org.concord.waba.extra.event;

public class TreeControlEvent extends waba.ui.Event{
public static final int TC_EXPAND 		= 1000;
public static final int TC_COLLAPSE 	= 1001;
public static final int TC_CHANGED  	= 1002;

	public TreeControlEvent(Object target,int type){
		this.target = target;
		this.type = type;
		timeStamp = waba.sys.Vm.getTimeStamp();
	}
	
}
