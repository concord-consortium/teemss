package org.concord.CCProbe;

import waba.util.*;
import waba.ui.*;

import org.concord.waba.extra.io.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.util.*;

import org.concord.LabBook.*;
import org.concord.ProbeLib.*;

public class LObjCalculusTrans extends LObjSubDict
	implements DataSink, DataSource, DataListener
{
	float  			[]data = new float[CCInterfaceManager.BUF_SIZE];
	float sum = 0f;
	DataEvent dEvent = new DataEvent();
	DataDesc dDesc = new DataDesc();
	Vector dataListeners = new Vector();
	private DataSource dataSource = null;

	int chPerSample;

    public LObjCalculusTrans()
    {
		super(DataObjFactory.CALCULUS_TRANS);
	}

	public void dataStreamEvent(DataEvent dataEvent)
	{
		switch(dataEvent.type){
		case DataEvent.DATA_READY_TO_START:
			DataDesc dataDesc = dataEvent.getDataDesc();
			dDesc.setChPerSample(1);
			dDesc.setDt(dataDesc.getDt());
			dEvent.setDataDesc(dDesc);
			dEvent.setDataOffset(0);
			dEvent.setNumbSamples(1);
			dEvent.setData(data);			
			chPerSample = dataDesc.getChPerSample();
			sum = 0f;
			dEvent.type = dataEvent.type;
			notifyDataListenersEvent(dEvent);
			return;
		case DataEvent.DATA_COLLECTING:
		case DataEvent.DATA_STOPPED:
			notifyDataListenersEvent(dataEvent);
			break;
		}
	}

	public void dataReceived(DataEvent dataEvent)
	{
		DataDesc dataDesc = dataEvent.getDataDesc();
		dEvent.type = dataEvent.type;

		int nSamps = dataEvent.getNumbSamples();
		int startPos = dataEvent.getDataOffset();
		int endPoint = startPos + nSamps*chPerSample;

		float [] inData = dataEvent.getData();
		int j = 0;
		float dt = dataDesc.getDt();
		for(int i= dataEvent.getDataOffset(); i < endPoint;
			i+= chPerSample, j++){
			data[j] = sum = inData[i]*dt + sum;
		}
		dEvent.setNumbSamples(j);
		notifyDataListenersReceived(dEvent);
		// notify listeners
	}

	public void addDataListener(DataListener l){
		if(dataListeners == null) dataListeners = new waba.util.Vector();
		if(dataListeners.find(l) < 0) dataListeners.add(l);

		// if(dataSource == null) getDataSource();
	}
	public void removeDataListener(DataListener l){
		if(dataListeners == null) return;
		int index = dataListeners.find(l);
		if(index >= 0) dataListeners.del(index);
	}

	public void notifyDataListenersEvent(DataEvent e){
		if(dataListeners == null) return;
		for(int i = 0; i < dataListeners.getCount(); i++){
			DataListener l = (DataListener)dataListeners.get(i);
			l.dataStreamEvent(e);
		}
	}

	public void notifyDataListenersReceived(DataEvent e){
		if(dataListeners == null) return;
		for(int i = 0; i < dataListeners.getCount(); i++){
			DataListener l = (DataListener)dataListeners.get(i);
			l.dataReceived(e);
		}
	}

	public void setDataSource(DataSource ds)
	{
		if(dataSource != ds){
			if(dataSource != null){
				dataSource.removeDataListener(this);
			}
			dataSource = ds;
			if(dataSource != null){
				dataSource.addDataListener(this);
			}
			if(ds instanceof LabObject) setObj((LabObject)ds,0);
		}
	}

	DataSource getDataSource(LabBookSession session)
	{
		LabObject obj = getObj(0, session);
		if(obj != null && obj instanceof DataSource){
			if(dataSource != null && dataSource != obj){
				dataSource.removeDataListener(this);
			}
			if(dataSource != obj){
				dataSource = (DataSource)obj; 
				dataSource.addDataListener(this);
			}
			dataSource = (DataSource)obj; 
			return dataSource;
		}
		return null;
	}

	public void closeEverything()
	{
		if(dataSource != null){
			dataSource.removeDataListener(this);
			dataSource.closeEverything();
		}
		dataSource = null;
	}

	public void startDataDelivery(LabBookSession session)
	{
		getDataSource(session);
		if(dataSource != null){
			dataSource.startDataDelivery(session);
		}
	}

	public void stopDataDelivery()
	{
		if(dataSource != null){
			dataSource.stopDataDelivery();
		}
	}

	public CCUnit 	getUnit(LabBookSession session){return null;}
	public boolean 	setUnit(CCUnit unit){return false;}
	public int getPrecision(){ return -2; }

	public String getQuantityMeasured(LabBookSession session)
	{return "";}

	public String getSummary(LabBookSession session)
	{return "";}

	// add the root sources to the passed in vector
	public void getRootSources(Vector sources, LabBookSession session)
	{
		getDataSource(session);
		if(dataSource != null && sources != null){
			dataSource.getRootSources(sources, session);
		}
	}

	public void readExternal(DataStream in){}
	public void writeExternal(DataStream out){}
}
