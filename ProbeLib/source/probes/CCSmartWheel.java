package org.concord.ProbeLib.probes;

import org.concord.ProbeLib.*;

import org.concord.waba.extra.util.*;


public class CCSmartWheel extends Probe
{
float  			[]wheelData 	= new float[CCInterfaceManager.BUF_SIZE*2];
int  			[]wheelIntData 	= new int[CCInterfaceManager.BUF_SIZE*2];
float  			dtChannel = 0.0f;
int				nTicks = 660;
float				radius = 0.06f;
float				koeff = 2f*Maths.PI;

	/*
	PropObject modeProp = new PropObject("Output Mode", "Mode", PROP_MODE, wheelModes,
										 LIN_POS_MODE_OUT);
	*/

	public final static String	[]wheelModes =  {"Position", "Velocity", "Ang. Velocity"};
	public final static int		ANG_MODE_OUT 		= 0;
	public final static int		LINEAR_MODE_OUT 	= 1;
    public final static int     LIN_POS_MODE_OUT        = 2;
	//	int	 outputMode = LIN_POS_MODE_OUT;

	CCSmartWheel(boolean init, String name, int interfaceT){
		super(init, name, interfaceT);
		probeType = ProbFactory.Prob_SmartWheel;
		quantityNames = wheelModes;
		defQuantityName = wheelModes[2];

		activeChannels = 1;
		interfaceMode = CCInterfaceManager.DIG_COUNT_MODE;

		dDesc.setChPerSample(1);
		dDesc.setDt(0.01f);
		dEvent.setDataDesc(dDesc);
		dEvent.setDataOffset(0);
		dEvent.setNumbSamples(1);
		dEvent.setData(wheelData);
		dEvent.setIntData(wheelIntData);

		//		addProperty(modeProp);

		if(init){
			calibrationDesc = new CalibrationDesc();
			calibrationDesc.addCalibrationParam(new CalibrationParam(0,radius));
		}		
	}
	
	DataListener veloListener = null;
	DataListener posListener = null;
	public DataListener setModeDataListener(DataListener l, int mode)
	{
		DataListener old = null;

		switch(mode){
		case 0:
			old = posListener;
			posListener = l;
			break;
		case 1:
			old = veloListener;
			veloListener = l;
			break;
		}
		return old;
	}

	public int getQuantityUnit(int mode)
	{
		switch(mode){
		case 0:
			return CCUnit.UNIT_CODE_METER;
		case 1:
			return CCUnit.UNIT_CODE_LINEAR_VEL;
		}
		return -1;
	}

	// Get the default unit
	public int getUnit()
	{
		return CCUnit.UNIT_CODE_ANG_VEL;
	}

	public int getInterfaceMode()
	{
		//		outputMode = modeProp.getIndex();
		return interfaceMode;
	}

	public void setDataDescParam(int chPerSample,float dt){
		dDesc.setDt(dt);
		dDesc.setChPerSample(chPerSample);
		dtChannel = dt / (float)chPerSample;
	}


    float posOffset = 0f;
    float dt;

	float calFactor = 1f;
	float posCalFactor = 1f;
	float velCalFactor = 1f;

    public boolean startSampling(DataEvent e){
		dEvent.type = e.type;
		dDesc.setDt(e.getDataDesc().getDt());

		dDesc.setTuneValue(e.getDataDesc().getTuneValue());
		if(calibrationListener != null){
			dDesc.setChPerSample(2);
		}else{
			dDesc.setChPerSample(1);
		}
		dEvent.setNumbSamples(1);
		dtChannel = dDesc.getDt() / (float)dDesc.getChPerSample();
		posOffset = 0f;
		dt = dDesc.getDt();
		dEvent.setData(wheelData);
		dEvent.setIntData(wheelIntData);
		dEvent.setIntTime(0);

		calFactor = -(koeff/(float)nTicks/dt);
		posCalFactor = -(koeff/(float)nTicks) * dDesc.tuneValue  * radius;
		velCalFactor = -(koeff/(float)nTicks/dt) * dDesc.tuneValue * radius;

		// This will call notifyDataListenersEvent
		return super.startSampling(dEvent);
	}

	public void notifyDataListenersEvent(DataEvent e)
	{
		if(veloListener != null){
			veloListener.dataStreamEvent(e);
		}
		if(posListener != null){
			posListener.dataStreamEvent(e);
		}

		super.notifyDataListenersEvent(e);
	}
    	
	public boolean dataArrived(DataEvent e){
		dEvent.type = e.type;
		int[] data = e.getIntData();
		int nOffset = e.dataOffset;
		
		int ndata = e.getNumbSamples()*dDesc.getChPerSample();
		int  	chPerSample = dDesc.getChPerSample();
		if(ndata < chPerSample) return false;

		if(calibrationListener != null){
		    wheelData[0] = (float)data[nOffset]*dDesc.tuneValue;//row
			float calibrated;
			float calFactor = koeff/(float)nTicks/dt;
		    calibrated = wheelData[0]*calFactor;
		    wheelData[1] = wheelData[0] ;
		    wheelData[0] = calibrated * radius*koeff;
		    dEvent.setTime(e.getTime());
		    return super.dataArrived(dEvent);
		}

		// note this is the time at the begining of the event
		dEvent.intTime = e.getIntTime();
		dEvent.numbSamples = e.getNumbSamples();
		dEvent.setData(wheelData);

		boolean ret = true;
		if(dataListeners != null){
			for(int i = 0; i < ndata; i+=chPerSample){
				wheelIntData[i] = data[nOffset+i];
				wheelData[i] = (float)wheelIntData[i]*calFactor;			    
			}
			ret = super.dataArrived(dEvent);
		}

		if(veloListener != null){
			for(int i = 0; i < ndata; i+=chPerSample){
				wheelIntData[i] = data[nOffset+i];
				wheelData[i] = (float)wheelIntData[i]*velCalFactor;
			}
			veloListener.dataReceived(dEvent);
		}

		if(posListener != null){
			for(int i = 0; i < ndata; i+=chPerSample){
				wheelIntData[i] = data[nOffset+i];
				wheelData[i] = posOffset = posOffset + (float)wheelIntData[i]*posCalFactor;				
			}
			posListener.dataReceived(dEvent);
		}			

		return ret;
	}

	public void  calibrationDone(float []row1,float []row2,float []calibrated){
		if(row1 == null || calibrated == null) return;
		
		if(Maths.abs(row1[0]) < 1e-5) return;//zero
		radius = calibrated[0] / koeff / koeff / nTicks/ row1[0] / dDesc.getDt();
		if(calibrationDesc != null){
			CalibrationParam p = calibrationDesc.getCalibrationParam(0);
			if(p != null) p.setValue(radius);
		}
		
	}
	public void calibrationDescReady(){
		if(calibrationDesc == null) return;
		CalibrationParam p = calibrationDesc.getCalibrationParam(0);
		if(p == null || !p.isValid()) return;
		radius = p.getValue();
	}
}
