package org.concord.LabBook;

import waba.io.*;
import waba.util.*;
import waba.sys.*;
import extra.io.*;
import graph.*;

class FileObject 
{
    int devId;
    int objId;
    byte [] buffer;
}

public class LabBookFile implements LabBookDB
{
    File file;
    DataStream ds;

    int objIndexStart = 8;
    int [] objIndex;

    Vector objects = new Vector();

    int curDevId;
    int nextObjId;

    static public void writeString(DataStream ds, String s)
    {
	ds.writeFixedString(s, s.length());
    }

    static public void export(Bin b, Vector points)
    {
	int i;

	if(b == null ||
	   b.time == null) return;
	String name = "Data-" + b.time.year + "_" + b.time.month + "_" + b.time.day + "-" +
	    b.time.hour + "_" + b.time.minute + ".txt";
	File file = new File(name, File.DONT_OPEN);
	if(file.exists()){
	    file.close();
	    file = new File(name, File.READ_WRITE);
	} else {
	    file.close();
	    file = new File(name, File.CREATE);
	    file.close();
	    file = new File(name, File.READ_WRITE);
	}

	DataStream ds = new DataStream(file);
	
	writeString(ds, b.time.month + "/" + b.time.day + "/" + b.time.year + " " +
		    b.time.hour + ":" + b.time.minute + "\r\n");
	writeString(ds, b.description + "\r\n");
	if(points == null){
	    writeString(ds, b.label + "\r\n");
	    writeString(ds, "time\tvalue\r\n");
	    float curTime = 0f;
	    for(i=0; i < b.lfArray.getCount(); i++){
		writeString(ds, curTime + "\t" + b.lfArray.getFloat(i) + "\r\n");
		curTime += b.dT;
	    }

	} else {
	    writeString(ds, "label\ttime\tvalue\r\n");
	    for(i=0; i < points.getCount(); i++){
		DecoratedValue pt = (DecoratedValue)points.get(i);
		writeString(ds, pt.getLabel() + "\t" + 
			    pt.getTime() + "\t" + 
			    pt.getValue() + "\r\n");
	    }
	    
	}

	file.close();
	
		
    }

    public LabBookFile(String name)
    {
	boolean newDB = false;

	file = new File(name, File.DONT_OPEN);
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

	ds = new DataStream(file);

	if(newDB){
	    curDevId = 0;
	    nextObjId = 0;
	    return;
	} else {
	    curDevId = ds.readInt();
	    nextObjId = ds.readInt();
	}

	if(!readIndex()){
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
    }

    public int getDevId()
    {
	return curDevId;
    }

    public int getNewObjId()
    {
	return nextObjId++;
    }

    public boolean save()
    {
	int curObjFilePos = 0;
	int curIndexPos = 0;
	int numObj = objects.getCount();
	FileObject fObj;
	
	Debug.println("About to write " + numObj);

	file.seek(0);
	ds.writeInt(curDevId);
	ds.writeInt(nextObjId);

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

	return true;
    }
    
    public void close()
    {
	if(file != null){
	    file.close();
	}
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
    public boolean readIndex()
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
	return true;	
    }

    

}
