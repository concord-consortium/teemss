package org.concord.waba.extra.probware.probs;
import org.concord.waba.extra.event.DataListener;
import org.concord.waba.extra.event.DataEvent;
import extra.util.DataDesc;
import org.concord.waba.extra.probware.*;
import extra.util.*;

public class CCVoltCurrent extends CCProb{
	float  			[]data = new float[CCInterfaceManager.BUF_SIZE/2];
	int  			[]intData = new int[CCInterfaceManager.BUF_SIZE];
	float  			dtChannel = 0.0f;
	float				energy = 0.0f;
	public final static int		CURRENT_OUT 			= 0;
	public final static int		VOLTAGE_OUT 			= 1;
	public final static int		POWER_OUT 			= 2;
	public final static int		ENERGY_OUT 			= 3;
	public final static String [] propNames = {"Mode", "Range", "Speed", "Version"};

	float					zeroPointCurrent				= 1257f;//	
	float					zeroPointVoltage				= 1257f;//	
	float					currentResolution		= 700f; //       mV(reading)/A
	float					voltageResolution		= 650f/20f; //     mV(reading)/(true)V

	int					outputMode 			= VOLTAGE_OUT;
	public static String [] modeNames = {"Current", "Voltage","Power","Energy"};
	public static String [] rangeNames = {"unknown"};
	public static String [] speed1Names = {3 + speedUnit, 200 + speedUnit, 400 + speedUnit};
	public static String [] speed2Names = {3 + speedUnit, 200 + speedUnit};
	String [] versionNames = {"1.0", "2.0"};
   
	int 				curChannel = 0;

	int version = 1;
	int voltOff = 1;
	int currentOff = 0;

	CCVoltCurrent(boolean init, String name, int interfaceT){
		super(init, name, interfaceT);
		probeType = ProbFactory.Prob_VoltCurrent;
		activeChannels = 2;

		dDesc.setChPerSample(1);
		dDesc.setDt(0.0f);
		dEvent.setDataDesc(dDesc);
		dEvent.setDataOffset(0);
		dEvent.setNumbSamples(1);
		dEvent.setData(data);
		dEvent.setIntData(intData);

		if(init){
			addProperty(new PropObject(propNames[0], modeNames, 1));
			addProperty(new PropObject(propNames[1], rangeNames));
			addProperty(new PropObject(propNames[2], speed2Names));
			addProperty(new PropObject(propNames[3], versionNames));
		
			calibrationDesc = new CalibrationDesc();
			calibrationDesc.addCalibrationParam(new CalibrationParam(0,zeroPointCurrent));
			calibrationDesc.addCalibrationParam(new CalibrationParam(1,currentResolution));
			calibrationDesc.addCalibrationParam(new CalibrationParam(2,zeroPointVoltage));
			calibrationDesc.addCalibrationParam(new CalibrationParam(3,voltageResolution));
		}
	}

	public int getUnit()
	{
		int outputMode = getProperty(propNames[0]).getIndex();

		switch(outputMode){
		case CURRENT_OUT:
			unit = CCUnit.UNIT_CODE_AMPERE;
			break;
		case VOLTAGE_OUT:
			unit = CCUnit.UNIT_CODE_VOLT;
			break;
		case POWER_OUT:
			unit = CCUnit.UNIT_CODE_WATT;
			break;
		case ENERGY_OUT:
			unit = CCUnit.UNIT_CODE_JOULE;
			break;
		}

		return unit;
	}

	public CalibrationDesc getCalibrationDesc()
	{
		CalibrationParam cp = calibrationDesc.getCalibrationParam(0);
		if(cp != null) cp.setAvailable(outputMode == CURRENT_OUT);
		cp = calibrationDesc.getCalibrationParam(1);
		if(cp != null) cp.setAvailable(outputMode == CURRENT_OUT);
		cp = calibrationDesc.getCalibrationParam(2);
		if(cp != null) cp.setAvailable(outputMode == VOLTAGE_OUT);
		cp = calibrationDesc.getCalibrationParam(3);
		if(cp != null) cp.setAvailable(outputMode == VOLTAGE_OUT);

		return calibrationDesc;
	}

	public int  getActiveCalibrationChannels(){return 1;}
	public void setDataDescParam(int chPerSample,float dt){
		dDesc.setDt(dt);
		dDesc.setChPerSample(chPerSample);
		dtChannel = dt / (float)chPerSample;
	}

	public boolean visValueChanged(PropObject po)
	{
		PropObject mode = getProperty(propNames[0]);
		PropObject version = getProperty(propNames[3]);
		if(po == mode || po == version){
			int mIndex = mode.getVisIndex();
			int vIndex = version.getVisIndex();
			   
			PropObject speed = getProperty(propNames[2]);
			if((mIndex == 0 && vIndex == 0)  ||
			   (mIndex == 1 && vIndex == 1)){
				speed.setVisPossibleValues(speed1Names);
			} else {
				speed.setVisPossibleValues(speed2Names);
			}
		}

		return true;
	}

	public int getInterfaceMode()
	{
		int vIndex = getProperty(propNames[3]).getIndex();
		if(vIndex == 0){
			version = 1;
			voltOff = 1;
			currentOff = 0;
		}else {
			version = 2;
			voltOff = 0;
			currentOff = 1;
		}

		int speedIndex = getProperty(propNames[1]).getIndex();
		if(speedIndex == 0){
			interfaceMode = CCInterfaceManager.A2D_24_MODE;
			activeChannels = 2;
		} else if(speedIndex == 1){
			interfaceMode = CCInterfaceManager.A2D_10_MODE;
			activeChannels = 2;
		} else if(speedIndex == 2){
			interfaceMode = CCInterfaceManager.A2D_10_MODE;
			activeChannels = 1;
		}
		return interfaceMode;
	}
	
	public boolean startSampling(org.concord.waba.extra.event.DataEvent e){
		energy = 0.0f;
		dEvent.type = e.type;
		dDesc.setDt(e.getDataDesc().getDt());
		dEvent.setNumbSamples(1);
		dDesc.setTuneValue(e.getDataDesc().getTuneValue());
		if(calibrationListener != null){
			dDesc.setChPerSample(2);
		}else{
			dDesc.setChPerSample(1);
		}
		dDesc.setIntChPerSample(2);
		dtChannel = dDesc.getDt() / (float)dDesc.getChPerSample();
		return super.startSampling(dEvent);
	}
    public boolean dataArrived(DataEvent e)
    {
		dEvent.type 		= e.type;
		int nOffset 		= e.getDataOffset();
		int ndata 			= e.getNumbSamples()*e.dataDesc.getChPerSample();
		float t0 			= e.getTime();
		int[] dataEvent 	= e.getIntData();
		if(calibrationListener != null){
			float v = 0.0f;
			switch(outputMode){
			case CURRENT_OUT:
				v = (float)dataEvent[nOffset + currentOff]*dDesc.tuneValue;
				data[0] = (v - zeroPointCurrent)/currentResolution;
				data[1] = v;
				break;
			case VOLTAGE_OUT:
				v = (float)dataEvent[nOffset + voltOff]*dDesc.tuneValue;
				data[0] = (v - zeroPointVoltage)/voltageResolution;
				data[1] = v;
				break;
			}
			dEvent.setNumbSamples(1);
		}else{
			int  	chPerSample = e.dataDesc.chPerSample;
			int	dataIndex = 0;
			dEvent.intTime = e.intTime;
			for(int i = 0; i < ndata; i+=chPerSample){
				intData[i] = dataEvent[nOffset+i];
				intData[i+1] = dataEvent[nOffset+i+1];
				switch(outputMode){
				case CURRENT_OUT:
					data[dataIndex] = (intData[i+currentOff]*dDesc.tuneValue - zeroPointCurrent)/currentResolution;
					break;
				case VOLTAGE_OUT:
					data[dataIndex] = (intData[i+voltOff]*dDesc.tuneValue - zeroPointVoltage)/voltageResolution;
					break;
				case POWER_OUT:
				case ENERGY_OUT:
					float		amper = (intData[i+currentOff]*dDesc.tuneValue - zeroPointCurrent)/currentResolution;
					float		voltage = (intData[i+voltOff]*dDesc.tuneValue - zeroPointVoltage)/voltageResolution;
					data[dataIndex] = amper*voltage;
					if(data[dataIndex] < 0f){
					    data[dataIndex] = -data[dataIndex];
					}
					if(outputMode == ENERGY_OUT){
						energy 	+= data[dataIndex]*dDesc.dt; 
						data[dataIndex] 	= energy;
					}
					break;
				}
				dataIndex++;
			}
			dEvent.setNumbSamples(dataIndex);
		}
		return super.dataArrived(dEvent);
    }
    
	protected void writeInternal(extra.io.DataStream out){}
	
	protected void readInternal(extra.io.DataStream in){}
	
	public void  calibrationDone(float []row1,float []row2,float []calibrated){
		if(outputMode != CURRENT_OUT && outputMode != VOLTAGE_OUT) return;
		if(row1 == null  || calibrated == null) return;
		float zeroPoint = (calibrated[0]*row1[1] - calibrated[1]*row1[0])/(calibrated[0] - calibrated[1]);
		float resolution = (row1[0] - row1[1])/(calibrated[0] - calibrated[1]);
		
		if(outputMode == CURRENT_OUT){
			zeroPointCurrent 		= zeroPoint;
			currentResolution 		= resolution;
			if(calibrationDesc != null){
				CalibrationParam p = calibrationDesc.getCalibrationParam(0);
				if(p != null) p.setValue(zeroPointCurrent);
				p = calibrationDesc.getCalibrationParam(1);
				if(p != null) p.setValue(currentResolution);
			}
		}else if(outputMode == VOLTAGE_OUT){
			zeroPointVoltage 		= zeroPoint;
			voltageResolution 		= resolution;
			if(calibrationDesc != null){
				CalibrationParam p = calibrationDesc.getCalibrationParam(2);
				if(p != null) p.setValue(zeroPointVoltage);
				p = calibrationDesc.getCalibrationParam(3);
				if(p != null) p.setValue(voltageResolution);
			}
		}
	}
	public void calibrationDescReady(){
		if(calibrationDesc == null) return;
		CalibrationParam p = calibrationDesc.getCalibrationParam(0);
		if(p != null && p.isValid()){
			zeroPointCurrent = p.getValue();
		}
		p = calibrationDesc.getCalibrationParam(1);
		if(p != null && p.isValid()){
			currentResolution = p.getValue();
		}
		p = calibrationDesc.getCalibrationParam(2);
		if(p != null && p.isValid()){
			zeroPointVoltage = p.getValue();
		}
		p = calibrationDesc.getCalibrationParam(3);
		if(p != null && p.isValid()){
			voltageResolution = p.getValue();
		}
	}

}
