package org.concord.waba.extra.event;

public class TextAreaEvent extends waba.ui.Event{
public static final int TA_CHANGED  	= 1000;

	public TextAreaEvent(Object target,int type){
		this.target = target;
		this.type = type;
		timeStamp = waba.sys.Vm.getTimeStamp();
	}
	
}
