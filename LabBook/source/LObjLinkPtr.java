package org.concord.LabBook;

import waba.util.*;
import org.concord.waba.extra.io.*;
import org.concord.waba.extra.ui.*;

public class LObjLinkPtr extends LabObject
{
	LabObjectPtr dbPtr;

    public LObjLinkPtr()
    {       
		super(DefaultFactory.LINK_PTR);
    }

	public LabObjectPtr getPointer()
	{
		if(dbPtr == null) return null;

		// Might want to add a special loading funct in
		// LabBook to handle loading database refs
		lBook.getObj(dbPtr, ptr.db, false);

		if(dbPtr.obj == null) return null;

		LabBookDB ptrDB = ((LObjDatabaseRef)dbPtr.obj).getDatabase();

		return ptrDB.getRootPtr();
	}

	public void readExternal(DataStream ds)
    {
		LabBookDB db = this.ptr.db;

		dbPtr = db.readPtr(ds);
    }

	public void writeExternal(DataStream ds)
	{
		LabBookDB db = this.ptr.db;

		db.writePtr(dbPtr, ds);
	}
}
