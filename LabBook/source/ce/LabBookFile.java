package org.concord.LabBook;

import waba.io.*;
import waba.util.*;
import waba.sys.*;
import extra.io.*;

class FileObject 
{
    int devId;
    int objId;
    byte [] buffer;
}

public class LabBookFile implements LabBookDB
{
	String fileName = null;

    int objIndexStart = 16;
    int [] objIndex;

    Vector objects = new Vector();

    int curDevId;
    int nextObjId;
    int rootDevId;
    int rootObjId;
	


    static public void writeString(DataStream ds, String s)
    {
		ds.writeFixedString(s, s.length());
    }

    public LabBookFile(String name)
    {
		boolean newDB = false;

		fileName = name;
		File file = new File(name, File.DONT_OPEN);
		if(file.exists()){
			file.close();
			file = new File(name, File.READ_WRITE);
		} else {
			file.close();
			file = new File(name, File.CREATE);
			file.close();
			file = new File(name, File.READ_WRITE);
			newDB = true;
		}

		DataStream ds = new DataStream(file);

		if(newDB){
			curDevId = 0;
			nextObjId = 0;
			rootDevId = 0;
			rootObjId = 0;
			return;
		} else {
			curDevId = ds.readInt();
			nextObjId = ds.readInt();
			rootDevId = ds.readInt();
			rootObjId = ds.readInt();
		}

		if(!readIndex(file, ds)){
			// Failed
			Debug.println("Error Reading index");
		}

		int curPos = 0;
		FileObject fObj = null;
		int filePos;
		int objSize = 0;

		while(curPos < objIndex.length){
			fObj = new FileObject();
			fObj.devId = objIndex[curPos++];
			fObj.objId = objIndex[curPos++];
			filePos = objIndex[curPos++];
			file.seek(filePos);
			objSize = ds.readInt();
			fObj.buffer = new byte [objSize];
			file.readBytes(fObj.buffer, 0, objSize);
			objects.add(fObj);
		}
		file.close();
    }

    public int getDevId()
    {
		return curDevId;
    }

    public int getNewObjId()
    {
		return nextObjId++;
    }

    public int getRootDevId(){return rootDevId;}
    public int getRootObjId(){return rootObjId;}

    public void setRootDevId(int id){ rootDevId = id;}
    public void setRootObjId(int id){ rootObjId = id;}

    public boolean save()
    {
		int curObjFilePos = 0;
		int curIndexPos = 0;
		int numObj = objects.getCount();
		FileObject fObj;
	
		Debug.println("About to write " + numObj);

		File file = new File(fileName, File.READ_WRITE);
		DataStream ds = new DataStream(file);

		file.seek(0);
		ds.writeInt(curDevId);
		ds.writeInt(nextObjId);
		ds.writeInt(rootDevId);
		ds.writeInt(rootObjId);
	
		file.seek(objIndexStart);
		ds.writeInt(numObj);
		ds.writeInt(numObj);
		curIndexPos = objIndexStart + 8;
		curObjFilePos = curIndexPos + numObj*3*4 + 4;
		for(int i=0; i<numObj; i++){
			fObj = (FileObject)objects.get(i);
			file.seek(curIndexPos);
			ds.writeInt(fObj.devId);
			ds.writeInt(fObj.objId);
			ds.writeInt(curObjFilePos);
			curIndexPos += 3*4;
	    
			file.seek(curObjFilePos);
			ds.writeInt(fObj.buffer.length);
			file.writeBytes(fObj.buffer, 0, fObj.buffer.length);
			curObjFilePos += 4 + fObj.buffer.length;
		}

		file.seek(curIndexPos);
		ds.writeInt(-1);

		file.close();

		return true;
    }
    
    public void close()
    {

    }

    /*
     * The ObjectIndex format is:
     * [ length ]
     * [ subLength ]
     * [ file pos ] [ device id ] [ object id ]
     * ...
     * [ next subIndex file pos ]
     * --------------------
     * [ subLength ]
     * [ file pos ] [ device id ] [ object id ]
     * ...
     * [ next subIndex file pos ]
     * --------------------
     * .............
     * --------------------
     * [ subLength ]
     * [ file pos ] [ device id ] [ object id ]
     * ...
     * [ <-1> ]
     */
    public boolean readIndex(File file, DataStream ds)
    {
		file.seek(objIndexStart);
		int length = ds.readInt();
		objIndex = new int [length*3];
		int subLen;
		int pos = 0;
		int i;
		int nextPos = -1;

		while(true){
			subLen = ds.readInt();
			for(i=0; i<subLen; i++){
				if((pos + 3) > objIndex.length){
					// file format error
					return false;
				}
				objIndex[pos++] = ds.readInt();
				objIndex[pos++] = ds.readInt();
				objIndex[pos++] = ds.readInt();
			}
			nextPos = ds.readInt();
			if(nextPos == -1 || pos >= length*3){
				break;
			} else {
				file.seek(nextPos);
			}
		}

		return true;
    }

    public boolean getError(){return false;};

    // search through object Index       
    // and find object bytes
    public byte [] readObjectBytes(int devId, int objId)
    {
		int numObj = objects.getCount();
		int i;
		FileObject fObj;

		for(i=0; i<numObj; i++){
			fObj = (FileObject)objects.get(i);
			if(fObj.devId == devId && fObj.objId == objId){
				return fObj.buffer;
			}
		}
	
		return null;

    }

    // check if this object already exists
    // if not add it to the file and add it
    // to the objIndex ??? (objIndex should be vector)
    // if it does exist, then replace it's entry in the the
    // objIndex with the new value after adding it to the file

    // In the longer term we will have to write the objects in 
    // peices and keep track of free space in the file.
    public boolean writeObjectBytes(int devId, int objId, byte [] buffer, int start,
									int count)
    {
		int numObj = objects.getCount();
		int i;
		FileObject fObj;

		Debug.println(" Saving " + count + " bytes to fObj");
	
		if(nextObjId <= objId) nextObjId = objId + 1;
		
		// Find the object
		for(i=0; i<numObj; i++){
			fObj = (FileObject)objects.get(i);
			if(fObj.devId == devId && fObj.objId == objId){
				fObj.buffer = new byte [count];
				Vm.copyArray(buffer, start, fObj.buffer, 0, count);
				return true;
			}
		}
	
		fObj = new FileObject();
		fObj.devId = devId;
		fObj.objId = objId;
		fObj.buffer = new byte [count];
		Vm.copyArray(buffer, start, fObj.buffer, 0, count);
		objects.add(fObj);
		if(devId == curDevId && nextObjId <= objId){
			nextObjId = objId;
		}
		return true;	
    }

    

}
