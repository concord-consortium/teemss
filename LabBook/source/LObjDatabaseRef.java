package org.concord.LabBook;

import waba.util.*;
import org.concord.waba.extra.io.*;
import org.concord.waba.extra.ui.*;

public class LObjDatabaseRef extends LabObject
{
	String dbRef;

    public LObjDatabaseRef()
    {       
		super(DefaultFactory.DATABASE_REF);
    }

	public void setDbRef(String ref)
	{
		dbRef = ref;
	}
	
	public String getDbRef(){ return dbRef; }

	public LabBookDB getDatabase()
	{
		return getDatabase(dbRef);
	}

	public static LabBookDB getDatabase(String url)
	{
		String filePath = url;

		if(filePath == null) return null;

		LabBookDB imDB = null;
		char [] filePathChars = filePath.toCharArray();
		int len = filePathChars.length;
		if(len > 4 &&
		   filePathChars[len - 4] == '.' &&
		   (filePathChars[len - 3] == 'p' ||
			filePathChars[len - 3] == 'P' ) &&
		   (filePathChars[len - 2] == 'd' ||
			filePathChars[len - 2] == 'D' ) &&
		   (filePathChars[len - 1] == 'b' ||
			filePathChars[len - 1] == 'B' )){
			imDB = (LabBookDB) new LabBookCatalog(new String(filePathChars, 0, len - 4));
		} else {
			imDB = (LabBookDB) new LabBookFile(filePath);
		}

		return imDB;
	}

	public void readExternal(DataStream ds)
    {
		dbRef = ds.readString();
    }

	public void writeExternal(DataStream ds)
	{
		ds.writeString(dbRef);
	}
}
