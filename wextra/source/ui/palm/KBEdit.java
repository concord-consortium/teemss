package org.concord.waba.extra.ui;

import waba.ui.*;
import waba.fx.*;
import waba.sys.*;

public class KBEdit extends Edit
{
	/** Called by the system to pass events to the edit control.
	 * ignore focus out and in events.  The Edit should always have
	 * focus.  
	 */
	public void onEvent(Event event)
	{
		switch (event.type){
		case ControlEvent.FOCUS_OUT:
		case ControlEvent.FOCUS_IN:
			break;
		default:
			super.onEvent(event);			
		}
	}

	public void setFocus(boolean focus)
	{
		if(focus){
			// some versions of the edit control can't receive focus until they have
			// been painted you'll need to fix this in the vm to use this class
			super.onEvent(new ControlEvent(ControlEvent.FOCUS_IN, this));
		} else {
			super.onEvent(new ControlEvent(ControlEvent.FOCUS_OUT, this));
		}
	}
	
}
