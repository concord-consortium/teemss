package org.concord.LabBook;

import waba.ui.*;

public class LabObjEvent extends Event
{
	LabObject obj = null;
	
	public LabObjEvent(LabObject source, int type)
	{
		obj = source;
		this.type = type;
	}

	public LabObject getObject()
	{
		return obj;
	}

	public int getType()
	{
		return type;
	}
}
