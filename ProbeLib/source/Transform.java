package org.concord.waba.extra.probware;

public interface Transform{
    	boolean dataArrived(org.concord.waba.extra.event.DataEvent e);
    	boolean idle(org.concord.waba.extra.event.DataEvent e);
    	boolean startSampling(org.concord.waba.extra.event.DataEvent e);
}
