package org.concord.LabBook;

import extra.io.*;
import extra.util.*;
import waba.util.*;
import graph.*;
import org.concord.waba.extra.event.*;

public class LObjDataSet extends LObjSubDict
{
    Vector bins = null;
    int chunkStartPos = 0;
    
    boolean hasDataView = false;
    boolean writtenChunks = false;

    Bin myBin = null;
    int chunkIndex = -1;

    DataEvent dEvent = null;

    public LObjDataSet()
    {
	objectType = DATA_SET;
    }

    public LObjDataSet(Bin b, int xIndex)
    {
	this();
	myBin = b;
	chunkIndex = xIndex;

    }

    public static LObjDataSet makeNewDataSet()
    {
	LObjDataSet me = new LObjDataSet();
	me.dict = new LObjDictionary();
	me.dict.setMainObj(me);
	me.dict.name = "DataSet";
	me.name = "DataSetHeader";
	me.dict.hideChildren = true;
	return me;	
    }

    public void addBin(Bin b)
    {
	if(bins == null) bins = new Vector();
	bins.add(b);
    }

    public void setDataViewer(LObjGraph graph)
    {
	if(!hasDataView){
	    chunkStartPos = 1;
	    if(bins != null &&
	       writtenChunks){
		writeChunks();
	    }
	}
	setObj(graph,0);
	hasDataView = true;

    }

    public boolean needReadChunks = false;

    public LabObjectView getView(LObjViewContainer vc, boolean edit)
    {
	if(hasDataView){
	    LObjGraph dataView = (LObjGraph)getObj(0);
	    if(dataView != null){
		if(needReadChunks) readChunks();
		LObjGraphView gView = (LObjGraphView)dataView.getView(vc, edit);		
		gView.addBins(bins);
		return gView;
	    }
	}
	return null;
    }

    public DataEvent getDataEvent(){
	return dEvent;
    }

    int [] binInfo = null;
    int numBins = 0;

    // Notice for this to work correctly the dictionary
    // should not actually load its objects when it is loaded
    public void readExternal(DataStream ds)
    {
	int numChunks;
	Bin curBin;

	byte type = ds.readByte() ;

	if((type & 0x0F) == 0){
	    if((type & 0x10) == 0x10){
		hasDataView = true;
		chunkStartPos = 1;
	    }

	    super.readExternal(ds);

	    numBins = ds.readInt();
	    bins = new Vector();
	    binInfo = new int [numBins * 2];
	    needReadChunks = true;

	    for(int i=0; i < numBins; i++){
		// need to create a bin here????
		binInfo[i*2] = ds.readInt();
		binInfo[i*2 + 1] = ds.readInt();
		curBin = new Bin(binInfo[i*2]);
		bins.add(curBin);
		//dima
		curBin.time = new waba.sys.Time();
		
		curBin.label = ds.readString();
	    }
	} else if((type & 0x0f) == 1 && myBin != null){
	    DataDesc dataDesc;

	    // Continuous data type
	    ds.readByte();

	    // read dt
	    float dt = ds.readFloat();
	    
	    dataDesc = new DataDesc(dt, 1);
	    
	    float time = ds.readFloat();
	    int numData = ds.readInt();
	    
	    float [] data = new float [numData];

	    for(int i=0; i<numData; i++){
		data[i] = ds.readFloat();
	    }

	    dEvent = new DataEvent(DataEvent.DATA_RECEIVED, 
				   time, data, dataDesc);
	    dEvent.numbSamples = numData;
	    dEvent.dataOffset = 0;
	} else {
	    // Empty dataset

	}
    }

    public boolean readChunks()
    {
	int chunkPos = 0;
	Bin curBin = null;
	LObjDataSet curChunk = null;

	for(int i = 0; i < numBins; i++){
	    curBin = (Bin)bins.get(i);

	    for(int j = 0; j < binInfo[i*2 + 1]; j++){
		curChunk = loadChunk(chunkPos++);
		curChunk.myBin = curBin;
		lBook.reload((LabObject)curChunk);
		if(curChunk.getDataEvent() == null) // error
		    return false;
		curBin.dataReceived(curChunk.getDataEvent());
		
		// free chunk
	    }
	}

	writtenChunks = true;
	return true;
    }
    
    public void writeChunks()
    {
	Bin curBin = null;
	DataEvent curChunk = null;
	LObjDataSet child;
	int chunkPos = 0;
	int numChunks;

	if(bins == null) return;

	binInfo = new int [bins.getCount()*2];
	numBins = bins.getCount();

	// header flag
	for(int i = 0; i < bins.getCount(); i++){
	    curBin = (Bin)bins.get(i);
	    binInfo[i*2] = curBin.xaIndex;
	    binInfo[i*2 + 1] = curBin.numDataChunks();	    
	    for(int j =0; j < curBin.numDataChunks(); j++){		    
		child = new LObjDataSet(curBin, j);
		child.name = "chunk" + chunkPos;
		storeChunk(child, chunkPos++);
	    }

	}
	writtenChunks = true;

    }

    public void storeChunk(LabObject obj, int id)
    {
	setObj(obj, id + chunkStartPos);

	// is this correct?
	// we need something to blast this to the file
	//	LabBook.commit();

    }

    public LObjDataSet loadChunk(int id)
    {
	// need to set the object's bin and reload it.
	// if the object automatically loaded it's data 
	// then just viewing a dictionary full of these
	// objects would crash the palm
	return (LObjDataSet) getObj(id + chunkStartPos);
    }

    public void writeExternal(DataStream ds)
    {
	DataEvent dataEvent = null;
	LObjDataSet child;
	int chunkPos = 0;

	if(binInfo != null){
	    // header flag
	    if(hasDataView){
		ds.writeByte(0x10);
	    } else {
		ds.writeByte(0);
	    }

	    super.writeExternal(ds);

	    ds.writeInt(numBins);
	    for(int i = 0; i < numBins; i++){
		// If there is any bin info here I should store it here		
		// I'll at least need to write the index of the bin on the 
		// the graph
		ds.writeInt(binInfo[i*2]);
		ds.writeInt(binInfo[i*2 + 1]);
		ds.writeString(((Bin)bins.get(i)).getLabel());
	    }
	    // Write out the index for these bins ???
	} else if(myBin != null){
	    // no header flag
	    ds.writeByte(1);      
	 
	    dataEvent = myBin.getDataChunk(chunkIndex);
	    // Continuous data type
	    ds.writeByte(0);
	
	    // Write dt
	    ds.writeFloat(dataEvent.getDataDesc().getDt());
	    
	    // Write start time
	    ds.writeFloat(dataEvent.time);
	    
	    // Write numb data
	    ds.writeInt(dataEvent.numbSamples);
	    
	    int sampSize = dataEvent.getDataDesc().getChPerSample();
	    int endPos = dataEvent.dataOffset + dataEvent.numbSamples*sampSize;
	    float [] data = dataEvent.data;
	    
	    for(int i=dataEvent.dataOffset; i<endPos; i+=sampSize){
		ds.writeFloat(data[i]);
	    }	
	    dataEvent = null;
	} else {
	    ds.writeByte(2);
	}
    }
}
