package org.concord.LabBook;

import org.concord.waba.extra.io.*;

public abstract class LabBookDB 
{
	protected abstract int getDevId();
	protected abstract int getNewObjId();
	public LabObjectPtr getNewLocalObjPtr(LabObject lObj)
	{
		if(lObj != null){
			return new LabObjectPtr(getDevId(), getNewObjId(), lObj, this);
		} else {
			return new LabObjectPtr(-1, -1, null, this);
		}
	}

	public LabObjectPtr getNewLocalObjPtr()
	{
		return new LabObjectPtr(getDevId(), getNewObjId(), null, this);
	}

	public void writePtr(LabObjectPtr ptr, DataStream ds)
	{
		ptr.writeExternalP(ds);
	}

	public LabObjectPtr readPtr(DataStream ds)
	{
		return new LabObjectPtr(ds, this);
	}

    public abstract boolean save();
    
    public abstract void close();

    public abstract boolean getError();
 
	// if numBytes is -1 it reads all bytes
	public abstract byte [] readObjectBytes(LabObjectPtr lObjPtr, int numBytes);

	public abstract boolean writeObjectBytes(LabObjectPtr lObjPtr, byte [] buffer, 
											 int start, int count);

	public abstract LabObjectPtr getRootPtr();

	public abstract void setRootPtr(LabObjectPtr ptr);
}
