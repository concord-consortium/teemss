import waba.io.*;
import waba.util.*;
import waba.sys.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;

class FileObject 
{
    int devId;
    int objId;
    byte [] buffer;
}

public class LabBookFile 
    implements LabBookDB, DialogListener
{
    DataStream ds;

    int [] objIndex;

    Vector objects = new Vector();

    int curDevId;
    int nextObjId;

    Catalog cat;

    boolean error = false;

    public LabBookFile(String name)
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

	

	int curPos = 0;
	FileObject fObj = null;
	int filePos;
	int objSize = 0;

	while(curPos < objIndex.length){
	    fObj = new FileObject();
	    fObj.devId = objIndex[curPos++];
	    fObj.objId = objIndex[curPos++];
	    filePos = objIndex[curPos++];

	    if(!cat.setRecordPos(filePos)){
		error = true; 
		openErr("load:" + curPos + ":" + filePos + ":" + fObj.devId + ":" + fObj.objId);
		return;
	    }
	    objSize = ds.readInt();
	    fObj.buffer = new byte [objSize];
	    cat.readBytes(fObj.buffer, 0, objSize);
	    cat.setRecordPos(-1);

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
	int curObjFilePos = 0;
	int curIndexPos = 0;
	int numObj = objects.getCount();
	FileObject fObj;
	int i;

	if(cat == null) return true;

	int indexSize = 12 + 12*numObj;
	if(cat.getRecordCount() < 1){
	    cat.addRecord(indexSize);
	} else {	
	    if(!cat.setRecordPos(0)) return closeErr("1" + ":" + indexSize);
	    if(!cat.resizeRecord(indexSize)) return  closeErr("2" + ":" + indexSize + ":" + cat.getError());
	    if(!cat.setRecordPos(0)) return  closeErr("3" + ":" + indexSize);
	}


	ds.writeInt(curDevId);
	ds.writeInt(nextObjId);
	ds.writeInt(numObj);

	for(i=0; i<numObj; i++){
	    fObj = (FileObject)objects.get(i);
	    ds.writeInt(fObj.devId);
	    ds.writeInt(fObj.objId);
	    ds.writeInt(i+1);
	}

	cat.setRecordPos(-1);

	int objSize = 0;
	for(i=0; i<numObj; i++){	    
	    fObj = (FileObject)objects.get(i);
	    objSize = fObj.buffer.length + 4;

	    if(cat.getRecordCount() <= i+1){
		cat.addRecord(objSize);
	    } else {		
		if(!cat.setRecordPos(i+1)) return closeErr("4:" + i + ":" + numObj + ":" + objSize);
		if(!cat.resizeRecord(objSize)) return closeErr("5:" + i + ":" + numObj + ":" + objSize + ":" + cat.getError());
		if(!cat.setRecordPos(i+1)) return closeErr("6:" + i + ":" + numObj + ":" + objSize);
	    }
	    
	    ds.writeInt(fObj.buffer.length);
	    cat.writeBytes(fObj.buffer, 0, fObj.buffer.length);

	}

	cat.setRecordPos(-1);

	return true;

    }
    
    public void close()
    {
	if(cat != null){	    
	    cat.close();
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
	
	if(!cat.setRecordPos(0)) return false;

	curDevId = ds.readInt();
	nextObjId = ds.readInt();

	int length = ds.readInt();
	objIndex = new int [length*3];
	int pos = 0;
	int i;

	for(i=0; i<length; i++){
	    objIndex[pos++] = ds.readInt();
	    objIndex[pos++] = ds.readInt();
	    objIndex[pos++] = ds.readInt();
	}

	cat.setRecordPos(-1);
	return true;
    }

    public boolean getError(){return error;};

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
