package org.concord.ProbeLib;

public interface Transform{
	boolean dataArrived(DataEvent e);
	boolean idle(DataEvent e);
	boolean startSampling(DataEvent e);
}
