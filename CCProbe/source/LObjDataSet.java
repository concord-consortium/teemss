package org.concord.CCProbe;

import extra.io.*;
import extra.util.*;
import waba.util.*;
import waba.ui.*;
import graph.*;
import org.concord.waba.extra.event.*;
import org.concord.LabBook.*;

class MyTimer extends Control
{
	LObjDataSet ds = null;
	Timer timer;
	
	MyTimer(LObjDataSet parent, int time)
	{
		ds = parent;
		timer = addTimer(time);
	}

	public void onEvent(Event e)
	{
		if(e.type == ControlEvent.TIMER){
			ds.continueCollecting();
			removeTimer(timer);
		}
	}
}

public class LObjDataSet extends LObjSubDict
	implements DataSource
{
    int chunkStartPos = 0;
	int numChunks = 0;
	int numSubObjs = 2;
    int numBins = 0;
	int numAnnots = 0;
    
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
		setName("DataSet");
	}
	
	public LObjDataSet makeSubChunk(Bin b, int xIndex, LabBookSession sess)
	{
		LObjDataSet sub = (LObjDataSet)factory.makeNewObj(DataObjFactory.DATA_SET, false);
		if(sub == null) return null;
		sub.myBin = b;
		sub.label = label;
		sub.chunkIndex = xIndex;
		sess.storeNew(sub);
		return sub;
	}

    public int addBin(Bin b, LabBookSession session)
    {
		int numBinChunks = b.numDataChunks();
		LObjDataSet first = makeSubChunk(b, 0, session);
		first.numBinChunks = numBinChunks;
		setObj(first, numSubObjs++);
		first.store();
		int ref = session.release(first);
		first.chunkIndex = -1;
		numChunks++;

		for(int i=1; i<numBinChunks; i++){
			LObjDataSet curChunk = makeSubChunk(b, i, session);
			setObj(curChunk, numSubObjs++);
			curChunk.store();
			ref = session.release(curChunk);
			curChunk.chunkIndex = -1;
			numChunks++;
		}

		return numBins++;
	}

	public void addBinAnnots(Bin b, int binIndex, LabBookSession session)
	{
		if(b.annots != null &&
		   b.annots.getCount() > 0){ 

			LObjDictionary aDict = getAnnotDict(session);
			if(aDict != null){
				for(int i=0; i<b.annots.getCount(); i++){
					Annotation a = (Annotation)b.annots.get(i);
					addAnnot(a, binIndex, session);
				}
			}
			aDict.store();
		}
	}

	public LObjDictionary getAnnotDict(LabBookSession session)
	{
		LabObject obj = getObj(1, session);
		LObjDictionary dict;
		if(obj == null){
			dict = DefaultFactory.createDictionary();
			session.storeNew(dict);
			setObj(dict,1);
			return dict;
		} else if(obj instanceof LObjDictionary){
			return(LObjDictionary)obj;
		} else {
			return null;
		}
	}

	public Vector getAnnots(LabBookSession session)
	{
		Vector annots = new Vector();
		LObjDictionary aDict = getAnnotDict(session);

		if(aDict == null) return null;
		
		for(int i = 0; i < numAnnots; i++){
			LabObject obj = session.getObj(aDict.getChildAt(i));
			if(!(obj instanceof LObjAnnotation)) return null;
			annots.add(obj);
		}
		return annots;
	}

	public void clearAnnots(LabBookSession session)
	{
		LObjDictionary aDict = getAnnotDict(session);
		if(aDict == null) return;
		aDict.removeAll();
		numAnnots = 0;
	}

	public void addAnnot(LObjAnnotation a, LabBookSession  session)
	{
		LObjDictionary aDict = getAnnotDict(session);
		if(aDict == null) return;

		aDict.add(a);
		a.store();
		numAnnots++;
	}

	public void addAnnots(Vector newAnnots, LabBookSession session)
	{
		for(int i=0 ;i < newAnnots.getCount(); i++){
			if(newAnnots.get(i) instanceof LObjAnnotation){
				addAnnot((LObjAnnotation)newAnnots.get(i), session);
			}
		}
	}

	public void addAnnot(Annotation a, int binIndex, LabBookSession session)
	{
		LObjAnnotation lObjA = DataObjFactory.createAnnotation();
		lObjA.setup(a, this, binIndex);

		LObjDictionary aDict = getAnnotDict(session);
		if(aDict == null) return;

		aDict.add(lObjA);
		lObjA.store();
		numAnnots++;
	}

    public void setDataViewer(LObjGraph graph)
    {
		setObj(graph,0);
		hasDataView = true;
    }

    public boolean needReadChunks = false;
	LObjGraphView gView;

    public LabObjectView getView(ViewContainer vc, boolean edit, 
								 LObjDictionary curDict, LabBookSession session)
    {
		if(hasDataView){
			LObjGraph dataView = (LObjGraph)getObj(0, session);
			if(dataView != null){
				gView = (LObjGraphView)dataView.getView(vc, edit, session);
				gView.showTitle(true);
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

	// Might need to reset this 
	boolean sentData = false;
	LabBookSession deliverySession = null;

	public void startDataDelivery(LabBookSession session)
	{
		if(sentData) return;		
		if(dataListener == null){
			return;
		}

		DataEvent startEvent = new DataEvent(DataEvent.DATA_READY_TO_START, 0);
		dataListener.dataStreamEvent(startEvent);
		new MyTimer(this, 50);
		sentData = true;
		deliverySession = session;
	}

	public void continueCollecting()
	{
		int chunkPos = 0;
		LObjDataSet curChunk = null;

		DataEvent stopEvent = new DataEvent(DataEvent.DATA_STOPPED, 0);
		DataEvent startEvent = new DataEvent(DataEvent.DATA_READY_TO_START, 0);

		// probably have to send start 
		// and idle events to make this work properly
		//		dataListener.dataRecieved(dEvent);
		
		int objNum=2;
		for(int i = 0; i < numChunks; i++){
			LabObject obj = getObj(objNum++, deliverySession);
			while(obj != null &&
				  !(obj instanceof LObjDataSet)){
				obj = getObj(objNum++, deliverySession);
			}
			if(obj == null) break;

			curChunk = (LObjDataSet)obj;

			if(curChunk.chunkIndex == 0 && i!= 0){
				dataListener.dataStreamEvent(stopEvent);

				dataListener.dataStreamEvent(startEvent);
				// probably need to set the labels of
				// the bin, and the units
			}

			dataListener.dataReceived(curChunk.dEvent);
			curChunk.dEvent = null;
		}

		dataListener.dataStreamEvent(stopEvent);
		sentData = true;
		if(gView != null){
			// add the annotations
			Vector annots = getAnnots(gView.getSession());
			GraphSettings curGS = gView.graph.getCurGraphSettings();
			for(int i=0; i<annots.getCount(); i++){
				LObjAnnotation a = (LObjAnnotation)annots.get(i);
				Bin b = (Bin)curGS.bins.get(a.binIndex);
				a.addToBin(b);
			}
			gView.repaint();
		}
	}

	public void stopDataDelivery(){}

	public CCUnit 	getUnit(LabBookSession session)
	{
		return unit;
	}
	public boolean 	setUnit(CCUnit u){
		unit = u;
		return true;
	}

	public String getQuantityMeasured(LabBookSession session)
	{return label;}

	public void setLabel(String l)
	{
		label = l;
	}

	public String getSummary(LabBookSession session)
	{
		return label;
	}

    int [] binInfo = null;

    // Notice for this to work correctly the dictionary
    // should not actually load its objects when it is loaded
    public void readExternal(DataStream ds)
    {
		byte type = ds.readByte() ;
		int majorType = type & 0x0f;

		if(majorType == 0){
			if((type & 0x10) == 0x10){
				hasDataView = true;
				chunkStartPos = 1;
			}

			numChunks = ds.readInt();
			numAnnots = ds.readInt();
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
		LObjDataSet child;
		int chunkPos = 0;

		if(myBin == null && dEvent == null){
			// header flag
			if(hasDataView){
				ds.writeByte(0x10);
			} else {
				ds.writeByte(0);
			}
			ds.writeInt(numChunks);
			ds.writeInt(numAnnots);
			if(unit == null) ds.writeInt(-1);
			else ds.writeInt(unit.code);
			ds.writeString(label);

			// probably want to write unit index here

		} else if(myBin != null || dEvent != null){
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
			
			if(myBin != null && dEvent == null){
				dEvent = myBin.getDataChunk(chunkIndex);
			}

			// Continuous data type
			ds.writeByte(0);
	
			// Write dt
			ds.writeFloat(dEvent.getDataDesc().getDt());
	    
			// Write start time
			ds.writeFloat(dEvent.getTime());//dima
	    
			// Write numb data
			ds.writeInt(dEvent.numbSamples);
	    
			int sampSize = dEvent.getDataDesc().getChPerSample();
			int endPos = dEvent.dataOffset + dEvent.numbSamples*sampSize;
			float [] data = dEvent.data;
	    
			byte [] buffer = new byte [dEvent.numbSamples*4];
			int bufPos = 3;
			float refVal = dEvent.refVal;
			for(int i=dEvent.dataOffset; i<endPos; i+=sampSize){
				int bits = waba.sys.Convert.toIntBitwise(data[i]+refVal);
				buffer[bufPos--] = (byte)(bits & 0xFF);
				bits>>=8;
				buffer[bufPos--] = (byte)(bits & 0xFF);
				bits>>=8;
				buffer[bufPos--] = (byte)(bits & 0xFF);
				bits>>=8;
				buffer[bufPos] = (byte)(bits & 0xFF);
				bufPos += 7;
			}
			dEvent.data = null;
			data = null;
			ds.writeBytes(buffer, 0, dEvent.numbSamples*4);
		   
			if(myBin != null){
				dEvent = null;
			}
		} else {
			ds.writeByte(3);
		}
    }

	public void getRootSources(Vector sources, LabBookSession session)
	{
		if(sources != null){
			sources.add(this);
		}
	}
}
