package org.concord.LabBook;

import extra.io.*;

public class LabObjectPtr 
{
    int devId;
    int objId;

    LabObject obj = null;

    public LabObjectPtr(int dId, int oId, LabObject o)
    {
	devId = dId;
	objId = oId;
	obj = o;

    }

    public LabObjectPtr()
    {
    }

    public static LabObjectPtr readExternal(DataStream in)
    {
	LabObjectPtr me = new LabObjectPtr();
	me.devId = in.readInt();
	me.objId = in.readInt();
	return me;
    }
    
    public void writeExternal(DataStream out)
    {
	out.writeInt(devId);
	out.writeInt(objId);
    }
}
