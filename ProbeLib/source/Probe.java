package org.concord.ProbeLib.probes;

import org.concord.ProbeLib.*;

import org.concord.waba.extra.event.*;
import org.concord.waba.extra.ui.ExtraMainWindow;
import extra.util.*;

public abstract class Probe extends PropContainer
	implements Transform
{
	public 		waba.util.Vector 	dataListeners = null;
	public 		waba.util.Vector 	probListeners = null;
	String		name = null;
	CalibrationDesc	calibrationDesc;
	public static final String defaultModeName = "Default";

	public final static int		INTERFACE_PORT_A	= 0;
	public final static int		INTERFACE_PORT_B	= 1;

	public int unit = CCUnit.UNIT_CODE_UNKNOWN;

	public final static int		CALIBRATION_PROB_START 	= 10000;
	public final static int		CALIBRATION_PROB_END 		= 10001;
	public final static int		PROPERTIES_PROB_START 	= 10002;
	public final static int		PROPERTIES_PROB_END 		= 10003;

	public DataDesc		dDesc = new DataDesc();
	public DataEvent	dEvent = new DataEvent();
	public ProbEvent	pEvent = new ProbEvent();

	public	int interfaceType = -1; 
	protected int interfaceMode = -1;

	protected int 	activeChannels = 1;

	protected	int	probeType = ProbFactory.Prob_Undefine;

	/*
	  interface modes
	  public final static int A2D_24_MODE = 1;
	  public final static int A2D_10_MODE = 2;
	  public final static int DIG_COUNT_MODE = 3;
	*/

	public final static int PROP_PORT = 0;
	public final static int PROP_MODE = 1;
	public final static int PROP_RANGE = 2;
	public final static int PROP_SPEED = 3;
	public final static int PROP_SAMPLING = 4;
	public final static int PROP_CHAN_NUM = 5;
	public final static int PROP_VERSION = 6;

	DataListener calibrationListener = null;

	String [] portNames = {"A", "B"};
	PropObject port = null;
	static String speedUnit = " per second";

	protected CCProb(boolean init, String name, int interfaceT){
		super("Properties");
		setName(name);
		calibrationDesc = null;
		pEvent.setProb(this);
		interfaceType = interfaceT;
		interfaceMode = CCInterfaceManager.A2D_24_MODE;
		if(interfaceType == CCInterfaceManager.INTERFACE_2){
			port = new PropObject("Port", "Port", PROP_PORT, portNames);
			addProperty(port);
		}
	}
   	
	public int getInterfaceMode(){return interfaceMode;}

	public int 	getInterfaceType(){return interfaceType;}
	public void setInterfaceType(int interfaceType){this.interfaceType =  interfaceType;}
	
	public int 	getInterfacePort()
	{
		if(port != null){
			return port.getIndex();
		} else {
			return -1;
		}
	}
	public void setInterfacePort(int interfacePort)
	{
		if(port != null){
			port.setIndex(interfacePort);
		}
	}

	public boolean needCalibration(){return ((calibrationDesc != null) && (calibrationDesc.countAvailableParams() > 0));}
	public CalibrationDesc getCalibrationDesc(){return calibrationDesc;}
	public void setCalibrationDesc(CalibrationDesc calibrationDesc){this.calibrationDesc = calibrationDesc;}

	public int	getActiveChannels(){return activeChannels;}
	public int	getActiveCalibrationChannels(){return getActiveChannels();}
	public void	setActiveChannels(int activeChannels){this.activeChannels = activeChannels;}

	public void setCalibrationListener(DataListener calibrationListener){
		this.calibrationListener = calibrationListener;
	}

	public int getProbeType(){return probeType;}

	public void clearCalibrationListener(){
		setCalibrationListener(null);
	}

	public void addProbListener(ProbListener l){
		if(probListeners == null) probListeners = new waba.util.Vector();
		if(probListeners.find(l) < 0) probListeners.add(l);
	}
	public void removeProbListener(ProbListener l){
		int index = probListeners.find(l);
		if(index >= 0) probListeners.del(index);
	}
	public void notifyProbListeners(ProbEvent e){
		if(probListeners == null) return;
		for(int i = 0; i < probListeners.getCount(); i++){
			ProbListener l = (ProbListener)probListeners.get(i);
			l.probChanged(e);
		}
	}
	
	public void addDataListener(DataListener l){
		if(dataListeners == null){ dataListeners = new waba.util.Vector();	   }
		if(dataListeners.find(l) < 0){
			dataListeners.add(l);
		}
	}
	public void removeDataListener(DataListener l){
		if(dataListeners == null) return;
		int index = dataListeners.find(l);
		if(index >= 0) dataListeners.del(index);
		if(dataListeners.getCount() == 0) dataListeners = null;
	}

	public DataListener setModeDataListener(DataListener l, int mode){return null;}

	public void notifyDataListenersEvent(DataEvent e){
		if(calibrationListener != null){
			calibrationListener.dataStreamEvent(e);
		}else{
			if(dataListeners == null) return;
			for(int i = 0; i < dataListeners.getCount(); i++){
				DataListener l = (DataListener)dataListeners.get(i);
				l.dataStreamEvent(e);
			}
		}
	}

	public void notifyDataListenersReceived(DataEvent e){
		if(calibrationListener != null){
			calibrationListener.dataReceived(e);
		}else{
			if(dataListeners == null) return;
			for(int i = 0; i < dataListeners.getCount(); i++){
				DataListener l = (DataListener)dataListeners.get(i);
				l.dataReceived(e);
			}
		}
	}

	public boolean startSampling(DataEvent e){
		if(calibrationListener == null){
			notifyDataListenersEvent(e);
		}
		return true;
	}

	public boolean stopSampling(DataEvent e){
		if(calibrationListener == null){
			notifyDataListenersEvent(e);
		}
		return true;
	}

	public boolean dataArrived(DataEvent e){
		notifyDataListenersReceived(e);
		return true;
	}

	public boolean idle(DataEvent e){
		notifyDataListenersEvent(e);
		return true;
	}
   	

	public abstract void setDataDescParam(int chPerSample,float dt);
    
    public DataDesc getDataDesc()
    {
		return dDesc;
    }

	public void setName(String name){this.name = name;}
	public String getName(){return name;}
	
	public void apply()
	{
		super.apply();

		pEvent.setInfo(this);
		notifyProbListeners(pEvent);
	}

	public void  calibrationDone(float []row1,float []row2,float []calibrated){}
	
	public void calibrateMe(ExtraMainWindow owner,DialogListener l,int interfaceType){
		CalibrationDialog cDialog = new CalibrationDialog(owner,l,"Calibration: "+getName(),this);
		cDialog.setRect(5,5,160,160);
		cDialog.show();		
	}
	public int getUnit(){return unit;}
	public boolean setUnit(int unit){this.unit = unit;return true;}

	public void writeExternal(extra.io.DataStream out){
		out.writeInt(interfaceType);
		out.writeInt(CALIBRATION_PROB_START);
		out.writeBoolean(calibrationDesc != null);
		if(calibrationDesc != null){
			calibrationDesc.writeExternal(out);
		}
		out.writeInt(CALIBRATION_PROB_END);
		out.writeInt(PROPERTIES_PROB_START);
		super.writeExternal(out);
		out.writeInt(PROPERTIES_PROB_END);
		writeInternal(out);
	}

	protected void writeInternal(extra.io.DataStream out){
	}
	protected void readInternal(extra.io.DataStream in){
	}
	
	public void readExternal(extra.io.DataStream in){
		interfaceType = in.readInt();
		int temp = in.readInt();
		if(temp != CALIBRATION_PROB_START) return;
		if(in.readBoolean()){
			if(calibrationDesc == null) calibrationDesc = new CalibrationDesc();
			calibrationDesc.readExternal(in);
			calibrationDescReady();
		}
		in.readInt();//CALIBRATION_PROB_END
		temp = in.readInt();
		if(temp != PROPERTIES_PROB_START) return;	
		super.readExternal(in);
		temp = in.readInt();//PROPERTIES_PROB_END
		readInternal(in);
	}
	public void calibrationDescReady(){}

	String [] quantityNames = null;
	String defQuantityName;
	public String [] getQuantityNames()
	{
		String [] retNames = quantityNames;
		if(retNames == null){
			retNames = new String [1];
			retNames[0] = defQuantityName;
		}
		return retNames;
	}	

	public String getDefQuantityName()
	{
		return defQuantityName;
	}

	public int getQuantityId(String quantityName)
	{
		if(quantityNames == null){
			return 0;
		} else {
			for(int i=0; i<quantityNames.length; i++){
				if(quantityNames[i].equals(quantityName)){
					return i;
				}
			}
			return -1;
		}
	}	

	public int getQuantityUnit(int id){return -1;}

	public String getQuantityName(int id)
	{
		if(id == 0 && quantityNames == null){
			return defQuantityName;
		}
		if(quantityNames == null){
			return null;
		}
		if(id >= 0 && id < quantityNames.length){
			return quantityNames[id];
		}
		return null;
	}
}
