package org.concord.LabBook;

import waba.io.*;
import waba.util.*;
import waba.sys.*;
import org.concord.waba.extra.io.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;

public class LabBookCatalog extends LabBookDB
    implements DialogListener
{
    DataStream ds;

	Vector objIndexVec = new Vector();

	int version = 0;
    int curDevId;
    int nextObjId;
    int rootDevId;
    int rootObjId;

    Catalog cat;

    boolean error = false;

    public LabBookCatalog(String name)
    {
		cat = new Catalog(name + ".LaBk.DATA", Catalog.CREATE);
		if(cat.isOpen()){
			cat.close();
			cat = new Catalog(name  + ".LaBk.DATA", Catalog.READ_WRITE);
			if(!cat.isOpen()){
				//Errorr
				cat = null;
				error = true;
				return;
			}		
		} else {
			cat = null;
			error = true;
			return;
			// Error hmmm..  I don't know what to do here
		}

		int numRecs;
		if((numRecs = cat.getRecordCount()) == 0){
			// New database
			curDevId = 0;
			nextObjId = 0;
			rootDevId = 0;
			rootObjId = 0;
			ds = new DataStream(cat);
			return;
		}

		ds = new DataStream(cat);
		if(!readIndex()){
			// Failed
			openErr("1");
			error = true;
			return;
		}
	
    }

	public LabObjectPtr getRootPtr()
	{
		return new LabObjectPtr(rootDevId, rootObjId, null, this);
	}

	public void setRootPtr(LabObjectPtr ptr)
	{
		rootDevId = ptr.devId;
		rootObjId = ptr.objId;
	}

    public int getDevId()
    {
		return curDevId;
    }

    public int getNewObjId()
    {
		return nextObjId++;
    }

    String [] errButtons = {"Bummer"};
    Dialog errDialog = null;
    public void showError(String msg)
    {
		errDialog = Dialog.showInputDialog(this, "Error saving LbBk", msg, errButtons, Dialog.EDIT_INP_DIALOG);
    }

    public void dialogClosed(DialogEvent e)
    {
		String command = e.getActionCommand();
		if(e.getSource() == errDialog){
			if(command.equals(errButtons[0])){
			}
		}
    }

    public void openErr(String msg)
    {
		showError("LbDB open: " + msg);
		if(cat != null){
			cat.close();
			cat = null;
		}
    }

    public boolean closeErr(String msg)
    {
		showError("LbDB close: " + msg);
		if(cat != null){
			cat.close();
			cat = null;
		}
		return false;
    }

    public boolean save()
    {
		if(cat.getRecordCount() == 0 ||
		   !cat.setRecordPos(0)) {
			return false;
		}
    
		cat.skipBytes(8);
		ds.writeInt(nextObjId);
		ds.writeInt(rootDevId);
		ds.writeInt(rootObjId);

		cat.setRecordPos(-1);

		return true;

    }
    
    public void close()
    {
		if(cat != null){	    
			cat.close();
			cat = null;
		}
    }

    /*
     * The ObjectIndex format is:
     * [ length ]
     * [ subLength ]
     * [ file pos ] [ object id ]
     * ...
     * [ next subIndex file pos ]
     * --------------------
     * [ subLength ]
     * [ file pos ] [ object id ]
     * ...
     * [ next subIndex file pos ]
     * --------------------
     * .............
     * --------------------
     * [ subLength ]
     * [ file pos ] [ object id ]
     * ...
     * [ <-1> ]
     */
    public boolean readIndex()
    {
	
		if(!cat.setRecordPos(0)) return false;

		version = ds.readInt();
		curDevId = ds.readInt();
		nextObjId = ds.readInt();
		rootDevId = ds.readInt();
		rootObjId = ds.readInt();

		int length = ds.readInt();
		objIndexVec = new Vector();
		int [] curChunk = null;
		for(int j=0; j < (length - 1)/objIndexChunkSize; j++){
			curChunk = new int [objIndexChunkSize * 2];
			objIndexVec.add(curChunk);
			ds.readInts(curChunk, 0, curChunk.length);
		}
		curChunk = new int [objIndexChunkSize * 2];
		objIndexVec.add(curChunk);
		ds.readInts(curChunk, 0, (length % objIndexChunkSize) * 2);

		objIndexLen = length;

		cat.setRecordPos(-1);
		return true;
    }

	public int findObject(LabObjectPtr ptr)
	{
		int objIndexVecCount = objIndexVec.getCount();
		if(ptr.devId != curDevId) return -1;
		int objId = ptr.objId;

		for(int j=0; j < objIndexVecCount - 1; j++){
			int [] objIndex = (int [])objIndexVec.get(j);
			for(int i=0; i < objIndex.length; i+=2){
				if(objIndex[i] == objId){
					return objIndex[i+1];
				} 
			}
		}
		if(objIndexVecCount - 1 >= 0){
			int [] objIndex = (int [])objIndexVec.get(objIndexVecCount - 1);
			int endPoint = (objIndexLen % objIndexChunkSize) * 2;
			for(int i=0; i < endPoint; i+=2){
				if(objIndex[i] == objId){
					return objIndex[i+1];
				} 
			}			
		}
		return -1;
	}

	int objIndexLen = 0;
	int objIndexChunkSize = 200;

	int addObject(LabObjectPtr ptr, int newRecCount)
	{
		int devId = ptr.devId;
		int objId = ptr.objId;

		if(objIndexVec.getCount() == 0){
			//			System.out.println("LBC: adding first object");
			// this is the first object added
			// initialize the index record
			cat.addRecord(24);
			ds.writeInt(version);
			ds.writeInt(curDevId);
			ds.writeInt(nextObjId);
			ds.writeInt(rootDevId);
			ds.writeInt(rootObjId);
			ds.writeInt(0);
			cat.setRecordPos(-1);
		}

		// add the record to the catalog
		int newRecPos = cat.addRecord(newRecCount);
		if(newRecPos < 0) return newRecPos;
		cat.setRecordPos(-1);

		// find the chunk where we will store this new record index
		int curChunkIndex = objIndexLen / objIndexChunkSize;

		// See if we need to make a new chunk
		if(curChunkIndex >= objIndexVec.getCount()){
			// we need to add a new chunk to the objIndexVec
			objIndexVec.add(new int [objIndexChunkSize * 2]);
		}

		
		int [] curChunk = (int []) objIndexVec.get(curChunkIndex);
		
		// find the position in the chunk
		int objPos = (objIndexLen - curChunkIndex*objIndexChunkSize) * 2;

		// store the object info in the chunk
		curChunk[objPos] = objId;
		curChunk[objPos+1] = newRecPos;

		// Prepare to store the object info in the catalog
		if(!cat.setRecordPos(0)) return -1;
		cat.resizeRecord(cat.getRecordSize() + 8);

		cat.skipBytes(8);
		ds.writeInt(nextObjId);

		cat.skipBytes(8);
		ds.writeInt(objIndexLen + 1);
		cat.skipBytes(objIndexLen*8);

		ds.writeInt(objId);
		ds.writeInt(newRecPos);

		cat.setRecordPos(-1);

		objIndexLen++;	
		
		return newRecPos;
	}

    public boolean getError(){return error;};

    // search through object Index       
    // and find object bytes
    public byte [] readObjectBytes(LabObjectPtr ptr, int numBytes)
    {
		int i;
		int objSize = 0;
		byte [] buffer = null;

		int index = findObject(ptr);
		if(index < 0) return null;

		if(!cat.setRecordPos(index)){
			error = true; 
			openErr("read:" + index);
			return null;
		}
				
		objSize = cat.getRecordSize();
		if(numBytes > 0 && numBytes < objSize){
			objSize = numBytes;
		}
		buffer = new byte [objSize];
		cat.readBytes(buffer, 0, objSize);
		cat.setRecordPos(-1);
		return buffer;
    }

    // check if this object already exists
    // if not add it to the file and add it
    // to the objIndex ??? (objIndex should be vector)
    // if it does exist, then replace it's entry in the the
    // objIndex with the new value after adding it to the file

    // In the longer term we will have to write the objects in 
    // peices and keep track of free space in the file.
    public boolean writeObjectBytes(LabObjectPtr ptr, byte [] buffer, int start,
									int count)
    {
		int i;

		if(nextObjId <= ptr.objId) nextObjId = ptr.objId + 1;

		int index = findObject(ptr);
		if(index >= 0){
			if(!cat.setRecordPos(index)){
				error = true; 
				openErr("write:" + index);
				return false;
			}

			if(cat.getRecordSize() != count){
				if(!cat.resizeRecord(count)) return closeErr("5:" + cat.getError());
				if(!cat.setRecordPos(index)) return closeErr("6:" + cat.getError());
			}
			
			cat.writeBytes(buffer, start, count);	
			cat.setRecordPos(-1);
			return true;
		}

		// We didn't find the object so add it
		int newRecPos = addObject(ptr, count);

		if(newRecPos < 0){
			return false;
		}
	    
		cat.setRecordPos(newRecPos);
		cat.writeBytes(buffer, start, count);
		cat.setRecordPos(-1);


		return true;       
    }
  
}
