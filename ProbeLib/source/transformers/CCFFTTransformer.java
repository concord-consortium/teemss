package org.concord.waba.extra.probware.transformers;

import org.concord.waba.extra.probware.*;
import org.concord.waba.extra.event.*;

public class CCFFTTransformer extends CCTransformer{
	public CCFFTTransformer(String name){
		super(name);
	}
    	public boolean dataArrived(org.concord.waba.extra.event.DataEvent e){
    		return true;
    	}
    	public boolean idle(org.concord.waba.extra.event.DataEvent e){
    		return true;
    	}
    	public boolean startSampling(org.concord.waba.extra.event.DataEvent e){
    		return true;
    	}
}