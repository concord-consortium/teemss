package org.concord.CCProbe;

import waba.util.*;
import waba.ui.*;
import extra.io.*;
import extra.util.*;
import org.concord.waba.extra.probware.Transform;
import org.concord.waba.extra.probware.CCInterfaceManager;

import org.concord.waba.extra.ui.*;
import extra.ui.*;
import extra.util.CCUnit;
import org.concord.waba.extra.event.*;
//LabObject implements Storable
public interface DataSource
{

	public void addDataListener(DataListener l);
	public void removeDataListener(DataListener l);

	public void closeEverything();

	public void startDataDelivery();
	public void stopDataDelivery();

	public CCUnit 	getUnit();
	public boolean 	setUnit(CCUnit unit);

	public String getLabel();
}
