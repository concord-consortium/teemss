package org.concord.CCProbe;

import extra.io.*;
import extra.util.*;
import waba.util.*;
import graph.*;
import org.concord.waba.extra.event.*;
import org.concord.LabBook.*;

public class LObjDataSet extends LObjSubDict
	implements DataSource
{
    Vector bins = null;
    int chunkStartPos = 0;
	int numChunks = 0;
    
    boolean hasDataView = false;
    boolean writtenChunks = false;

    Bin myBin;
    int chunkIndex = -1;
	int numBinChunks = -1;
    DataEvent dEvent;
	waba.sys.Time time;
	String label;
	CCUnit unit;

    public LObjDataSet()
    {
		super(DataObjFactory.DATA_SET);
    }

	public void init()
	{
		super.init();
		name = "DataSet";
	}
	
	public LObjDataSet makeSubChunk(Bin b, int xIndex)
	{
		LObjDataSet sub = (LObjDataSet)factory.makeNewObj(DataObjFactory.DATA_SET, false);
		if(sub == null) return null;
		sub.myBin = b;
		sub.label = label;
		sub.chunkIndex = xIndex;
		return sub;
	}

    public void addBin(Bin b)
    {
		int numBinChunks = b.numDataChunks();
		LObjDataSet first = makeSubChunk(b, 0);
		first.numBinChunks = numBinChunks;
		setObj(first, numChunks + 1);
		first.storeNow();
		first.chunkIndex = -1;
		numChunks++;

		for(int i=1; i<numBinChunks; i++){
			LObjDataSet curChunk = makeSubChunk(b, i);
			setObj(curChunk, numChunks + 1);				
			curChunk.storeNow();
			curChunk.chunkIndex = -1;
			numChunks++;
		}
    }

    public void setDataViewer(LObjGraph graph)
    {
		setObj(graph,0);
		hasDataView = true;
    }

    public boolean needReadChunks = false;
	LObjGraphView gView;

    public LabObjectView getView(ViewContainer vc, boolean edit, LObjDictionary curDict)
    {
		if(hasDataView){
			LObjGraph dataView = (LObjGraph)getObj(0);
			if(dataView != null){
				gView = (LObjGraphView)dataView.getView(vc, edit);
				gView.addDataSource(this);
				gView.doInstantCollection();
				return gView;
			}
		}
		return null;
    }

	DataListener dataListener;

	public void addDataListener(DataListener l)
	{
		dataListener = l;
	}

	public void removeDataListener(DataListener l)
	{
		dataListener = null;
	}

	public void closeEverything(){}

	public void startDataDelivery()
	{
		DataEvent startEvent = new DataEvent(DataEvent.DATA_READY_TO_START, 0);
		DataEvent stopEvent = new DataEvent(DataEvent.DATA_STOPPED, 0);
		
		if(dataListener == null){
			System.out.println("DS: un-initialized start");
			return;
		}

		int chunkPos = 0;
		LObjDataSet curChunk = null;

		// probably have to send start 
		// and idle events to make this work properly
		//		dataListener.dataRecieved(dEvent);
		
		boolean firstTime = true;
		for(int i = 0; i < numChunks; i++){
			curChunk = (LObjDataSet)getObj(i+1);

			if(curChunk.chunkIndex == 0){
				if(!firstTime) dataListener.dataStreamEvent(stopEvent);

				firstTime = false;
				dataListener.dataStreamEvent(startEvent);
				// probably need to set the labels of
				// the bin, and the units
			}

			dataListener.dataReceived(curChunk.dEvent);
			curChunk.dEvent = null;
		}

		dataListener.dataStreamEvent(stopEvent);
	}

	public void stopDataDelivery(){}

	public CCUnit 	getUnit()
	{
		return unit;
	}
	public boolean 	setUnit(CCUnit u){
		unit = u;
		return true;
	}

	public String getLabel(){return label;}

	public void setLabel(String l)
	{
		label = l;
	}

    int [] binInfo = null;
    int numBins = 0;

    // Notice for this to work correctly the dictionary
    // should not actually load its objects when it is loaded
    public void readExternal(DataStream ds)
    {
		super.readExternal(ds);

		byte type = ds.readByte() ;
		int majorType = type & 0x0f;

		if(majorType == 0){
			if((type & 0x10) == 0x10){
				hasDataView = true;
				chunkStartPos = 1;
			}

			numChunks = ds.readInt();
			needReadChunks = true;
			int uCode = ds.readInt();
			if(uCode == -1) unit = null;
			else unit = CCUnit.getUnit(uCode);
			label = ds.readString();

		} else if(majorType == 1 || majorType == 2){
			DataDesc dataDesc;

			if(majorType == 1){
				chunkIndex = 0;
				numBinChunks = ds.readInt();
				label = ds.readString();				
			}  else {				
				chunkIndex = ds.readInt();
			}

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
			int intTime = (int)(time/dt + 0.5f);//dima
			dEvent = new DataEvent(DataEvent.DATA_RECEIVED, 
								   intTime, data, dataDesc);
			dEvent.numbSamples = numData;
			dEvent.dataOffset = 0;
		} else {
			// Empty dataset

		}
    }
    
    public void writeExternal(DataStream ds)
    {
		DataEvent dataEvent = null;
		LObjDataSet child;
		int chunkPos = 0;

		super.writeExternal(ds);

		if(myBin == null){
			// header flag
			if(hasDataView){
				ds.writeByte(0x10);
			} else {
				ds.writeByte(0);
			}
			ds.writeInt(numChunks);
			if(unit == null) ds.writeInt(-1);
			else ds.writeInt(unit.code);
			ds.writeString(label);

			// probably want to write unit index here

		} else if(myBin != null){
			// not header flag
			if(chunkIndex == 0){
				ds.writeByte(1);      
	 
				ds.writeInt(numBinChunks);
				ds.writeString(label);
			} else if(chunkIndex > 0){
				ds.writeByte(2);
				ds.writeInt(chunkIndex);
			} else {
				return;
			}

			dataEvent = myBin.getDataChunk(chunkIndex);
			// Continuous data type
			ds.writeByte(0);
	
			// Write dt
			ds.writeFloat(dataEvent.getDataDesc().getDt());
	    
			// Write start time
			ds.writeFloat(dataEvent.getTime());//dima
	    
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
			ds.writeByte(3);
		}
    }
}
