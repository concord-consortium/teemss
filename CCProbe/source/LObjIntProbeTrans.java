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

public class LObjIntProbeTrans extends LObjSubDict
	implements DataSink, DataSource, DataListener
{
	Vector dataListeners = new Vector();
	LObjProbeDataSource dataSource = null;

	int chPerSample;

    public LObjIntProbeTrans()
    {
		super(DataObjFactory.INT_PROBE_TRANS);
	}

	int type = -1;
	public int getType(){return type;}
	public void setType(int t)
	{
		type = t;
	}

	public String getQuantityMeasured()
	{
		if(dataSource == null) getDataSource();

		if(dataSource != null) return dataSource.getQuantityMeasured(type);
		return null;
	}

	public void dataStreamEvent(DataEvent dataEvent)
	{
		notifyDataListenersEvent(dataEvent);
	}

	public void dataReceived(DataEvent dataEvent)
	{
		notifyDataListenersReceived(dataEvent);
	}

	public void addDataListener(DataListener l){
		if(dataListeners == null) dataListeners = new waba.util.Vector();
		if(dataListeners.find(l) < 0) dataListeners.add(l);
		if(dataSource == null) getDataSource();
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

	public void setDataSource(LObjProbeDataSource ds)
	{
		if(dataSource != ds){
			if(dataSource != null){
				dataSource.setModeDataListener(null, type);
			}
			dataSource = ds;
			if(dataSource != null){
				dataSource.setModeDataListener(this, type);
			}
			if(ds instanceof LabObject) setObj((LabObject)ds,0);
		}
	}

	DataSource getDataSource()
	{
		LabObject obj = getObj(0);
		if(obj != null && obj instanceof DataSource){
			if(dataSource != null && dataSource != obj){
				dataSource.setModeDataListener(null, type);
			}
			if(dataSource != obj){
				dataSource = (LObjProbeDataSource)obj; 
				dataSource.setModeDataListener(this, type);
			}
			dataSource = (LObjProbeDataSource)obj; 
			return dataSource;
		}
		return null;
	}

	public void closeEverything()
	{
		if(dataSource != null){
			dataSource.setModeDataListener(null, type);
			dataSource.closeEverything();
		}
		dataSource = null;
	}

	public void startDataDelivery()
	{
		getDataSource();
		if(dataSource != null){
			dataSource.startDataDelivery();
		}
	}

	public void stopDataDelivery()
	{
		if(dataSource != null){
			dataSource.stopDataDelivery();
		}
	}

	public CCUnit 	getUnit()
	{
		if(dataSource == null) getDataSource();
		if(dataSource == null) return null;
		return dataSource.getQuantityUnit(type);
	}

	public boolean 	setUnit(CCUnit unit){return false;}

	public String getLabel(){return "";}

	public String getSummary(){return "";}

	// add the root sources to the passed in vector
	public void getRootSources(Vector sources)
	{
		getDataSource();
		if(dataSource != null && sources != null){
			dataSource.getRootSources(sources);
		}
	}

	public void writeExternal(DataStream out)
	{
		super.writeExternal(out);
		out.writeInt(type);
	}

	public void readExternal(DataStream in)
	{
		super.readExternal(in);
		type = in.readInt();
	}
}