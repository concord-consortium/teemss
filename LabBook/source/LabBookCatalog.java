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

	int objIndexLen = 0;
	int objIndexChunkSize = 200;

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

	public void delete()
	{
		if(cat != null){
			cat.delete();
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
		int [] curChunkObjIds = null;
		short [] curChunkRecIds = null;
		byte [] bitBuffer = new byte[objIndexChunkSize*8];
		for(int j=0; j < (length - 1)/objIndexChunkSize; j++){
			curChunkObjIds = new int [objIndexChunkSize];
			curChunkRecIds = new short [objIndexChunkSize];
			objIndexVec.add(curChunkObjIds);
			objIndexVec.add(curChunkRecIds);
			readIndexChunk(curChunkObjIds, curChunkRecIds, bitBuffer, objIndexChunkSize);
		}
		curChunkObjIds = new int [objIndexChunkSize];
		curChunkRecIds = new short [objIndexChunkSize];
		objIndexVec.add(curChunkObjIds);
		objIndexVec.add(curChunkRecIds);
		readIndexChunk(curChunkObjIds, curChunkRecIds, bitBuffer, (length % objIndexChunkSize));

		// Just to be clear :)
		bitBuffer = null;

		objIndexLen = length;

		cat.setRecordPos(-1);
		return true;
    }

	/**
	 * This is a bit silly because it has to be backwards compatible.
	 * the index is stored as an array of ints, but we are going to 
	 * load it into two arrays.  One of shorts the other of ints.
	 * Note: this might cause a problem with large recordIds.  The 
	 * problem will happen because recordIds can be unsigned shorts
	 * but we are making them shorts.  Currently this isn't a problem
	 * because of the way we are storing our index of records we can't
	 * exceed 8,000 objects.  So our recordIds should exceed 8k.
	 */
	public void readIndexChunk(int [] objIds, short [] recIds, byte [] bitBuffer, int len)
	{
		int offset = 0;
		int endPos = len*8;

		if(bitBuffer.length < endPos){
			// throw intentional null exception
			String dummy = null;
			dummy.equals("d");
		}

		byte [] bits = bitBuffer;
		ds.readBytes(bits,0,endPos);
		int i=0;
		for(;i<endPos;){
			objIds[offset] = (((bits[i++]&0xFF) << 24) | ((bits[i++]&0xFF) << 16) |
							  ((bits[i++]&0xFF) << 8) | (bits[i++]&0xFF));
			recIds[offset++] = (short)((((bits[i++]&0xFF) << 24) | ((bits[i++]&0xFF) << 16) |
								((bits[i++]&0xFF) << 8) | (bits[i++]&0xFF)));
		}
	}

	public int findObject(LabObjectPtr ptr)
	{
		if(ptr.recId > 0) return (int) ptr.recId;

		int objIndexVecCount = objIndexVec.getCount();
		if(ptr.devId != curDevId) return -1;
		int objId = ptr.objId;

		for(int j=0; j < objIndexVecCount - 2; j+=2){
			int [] objIds = (int [])objIndexVec.get(j);
			short [] recIds = (short [])objIndexVec.get(j+1);
			for(int i=0; i < objIds.length; i++){
				if(objIds[i] == objId){
					ptr.recId = recIds[i];
					return (int)recIds[i];
				} 
			}
		}
		if(objIndexVecCount - 2 >= 0){
			int [] objIds = (int [])objIndexVec.get(objIndexVecCount - 2);
			short [] recIds = (short [])objIndexVec.get(objIndexVecCount - 1);
			int endPoint = objIndexLen % objIndexChunkSize;
			for(int i=0; i < endPoint; i++){
				if(objIds[i] == objId){
					ptr.recId = recIds[i];
					return recIds[i];
				} 
			}			
		}
		return -1;
	}

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
		// Remember the objIndexVec has two entries per chunk
		if(curChunkIndex >= objIndexVec.getCount()/2){
			// we need to add a new chunk to the objIndexVec
			objIndexVec.add(new int [objIndexChunkSize]);
			objIndexVec.add(new short [objIndexChunkSize]);
		}

		
		int [] curChunkObjIds = (int []) objIndexVec.get(curChunkIndex*2);
		short [] curChunkRecIds = (short []) objIndexVec.get(curChunkIndex*2 + 1);

		// find the position in the chunk
		// these means we have to subtract off all the chunks before this one
		int objPos = objIndexLen - curChunkIndex*objIndexChunkSize;

		// store the object info in the chunk
		curChunkObjIds[objPos] = objId;
		curChunkRecIds[objPos] = (short)newRecPos;

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
