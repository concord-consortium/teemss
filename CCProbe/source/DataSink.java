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

public interface DataSink
{
	public void addDataSource(DataSource ds);
	public void removeDataSource(DataSource ds);
}
