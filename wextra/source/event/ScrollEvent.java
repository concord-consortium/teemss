package org.concord.waba.extra.event;

public class ScrollEvent extends waba.ui.Event{
public static final int SCROLL_INCREMENT 		= 1000;
public static final int SCROLL_DECREMENT 		= 1001;
public static final int SCROLL_DRAG_FINISH  	= 1002;
public static final int SCROLL_DRAG_INPROCESS  	= 1003;
public static final int SCROLL_PAGE_INC  		= 1004;
public static final int SCROLL_PAGE_DEC  		= 1005;

public int	scrollValue;


	public ScrollEvent(Object target,int type, int scrollValue){
		this.target = target;
		this.type = type;
		this.scrollValue = scrollValue;
		timeStamp = waba.sys.Vm.getTimeStamp();
	}
		
	public int 	getScrollValue(){return scrollValue;}
	
}
