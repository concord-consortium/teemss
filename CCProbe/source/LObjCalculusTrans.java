package org.concord.CCProbe;

import waba.util.*;
import waba.ui.*;
import extra.io.*;
import extra.util.*;
import org.concord.waba.extra.probware.Transform;
import org.concord.waba.extra.probware.CCInterfaceManager;

import org.concord.waba.extra.ui.*;
import extra.ui.*;
import extra.util.CCUnit;
import org.concord.waba.extra.event.*;
import org.concord.LabBook.*;

public class LObjCalculusTrans extends LObjSubDict
	implements DataSink, DataSource, DataListener
{
	float  			[]data = new float[CCInterfaceManager.BUF_SIZE];
	float sum = 0f;
	DataEvent dEvent = new DataEvent();
	DataDesc dDesc = new DataDesc();
	Vector dataListeners = new Vector();
	Vector dataSources = new Vector();

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
		for(int i= dataEvent.getDataOffset(); i < endPoint;
			i+= chPerSample, j++){
			data[j] = sum = inData[i] + sum;
		}
		notifyDataListenersReceived(dEvent);
		// notify listeners
	}

	public void addDataListener(DataListener l){
		if(dataListeners == null) dataListeners = new waba.util.Vector();
		if(dataListeners.find(l) < 0) dataListeners.add(l);
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

	public void addDataSource(DataSource ds)
	{
		if(dataSources == null) dataSources = new waba.util.Vector();
		if(dataSources.find(ds) < 0){
			dataSources.add(ds);
			ds.addDataListener(this);
		}
	}
	public void removeDataSource(DataSource ds)
	{
		if(dataSources == null) return;
		int index = dataSources.find(ds);
		if(index >= 0){
			dataSources.del(index);
			ds.removeDataListener(this);
		}
	}

	DataSource getDS(int i)
	{
		return (DataSource)dataSources.get(i);
	}

	public void closeEverything()
	{
		for(int i=0; i < dataSources.getCount(); i++){
			getDS(i).closeEverything();
		}
	}

	public void startDataDelivery()
	{
		for(int i=0; i < dataSources.getCount(); i++){
			getDS(i).startDataDelivery();
		}
	}

	public void stopDataDelivery()
	{
		for(int i=0; i < dataSources.getCount(); i++){
			getDS(i).stopDataDelivery();
		}
	}

	public CCUnit 	getUnit(){return null;}
	public boolean 	setUnit(CCUnit unit){return false;}

	public String getLabel(){return "";}

	public String getSummary(){return "";}


}