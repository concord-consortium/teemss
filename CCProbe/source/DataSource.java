package org.concord.CCProbe;

import waba.util.*;
import waba.ui.*;
import org.concord.waba.extra.io.*;
import org.concord.waba.extra.util.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;

import org.concord.ProbeLib.*;
import org.concord.LabBook.*;

//LabObject implements Storable
public interface DataSource
{

	public void getRootSources(Vector sources,
							   LabBookSession session);

	public void addDataListener(DataListener l);
	public void removeDataListener(DataListener l);

	public void closeEverything();

	public void startDataDelivery(LabBookSession session);
	public void stopDataDelivery();

	public CCUnit 	getUnit(LabBookSession session);
	public boolean 	setUnit(CCUnit unit);

	public int getPrecision();

	public String getQuantityMeasured(LabBookSession session);

	public String getSummary(LabBookSession session);
}
