package org.concord.waba.extra.probware.probs;
import org.concord.waba.extra.event.DataListener;
import org.concord.waba.extra.event.DataEvent;
import extra.util.DataDesc;
import org.concord.waba.extra.probware.*;
import extra.util.*;

public class CCVoltCurrent extends CCProb{
float  			[]data = new float[3];
float  			dtChannel = 0.0f;
float				energy = 0.0f;
public final static int		CURRENT_OUT 			= 0;
public final static int		VOLTAGE_OUT 			= 1;
public final static int		WATT_OUT 			= 2;
public final static int		ENERGY_OUT 			= 3;
public final static String [] propNames = {"Port", "Output mode"};

float					zeroPoint						= 1250f;//	mV
float					zeroPointCurrent				= zeroPoint;//	
float					zeroPointVoltage				= zeroPoint;//	
float					currentResolution		= 700f; //       mV(reading)/A
float					voltageResolution		= 0.650f/20; //     mV(reading)/(true)V

int					outputMode 			= VOLTAGE_OUT;
    String [] portNames = {"A", "B"};
public static String [] modelNames = {"Current", "Voltage","Watt","Joul"};
   
   
   
	int 				curChannel = 0;
	private boolean 	fromConstructor = true;

	CCVoltCurrent(){
		this("unknown");
	}
	CCVoltCurrent(String name){
		activeChannels = 2;
		setName(name);
		dDesc.setChPerSample(1);
		dDesc.setDt(0.0f);
		dEvent.setDataDesc(dDesc);
		dEvent.setDataOffset(0);
		dEvent.setNumbSamples(1);
		dEvent.setData(data);

		properties = new PropObject[3];
		properties[0] = new PropObject(samplingModeString,samplingModes); 
		properties[1] = new PropObject(propNames[0], portNames);
		properties[2] = new PropObject(propNames[1], modelNames);
		
		setPropertyValue(0,samplingModes[CCProb.SAMPLING_24BIT_MODE]);
		setPropertyValue(1,portNames[0]);
		setPropertyValue(2,modelNames[1]);
		calibrationDesc = new CalibrationDesc();
		calibrationDesc.addCalibrationParam(new CalibrationParam(0,zeroPoint));
		unit = CCUnit.UNIT_CODE_VOLT;
		fromConstructor = false;

		
	}
	public void setDataDescParam(int chPerSample,float dt){
		dDesc.setDt(dt);
		dDesc.setChPerSample(chPerSample);
		dtChannel = dt / (float)chPerSample;
	}
	protected boolean setPValue(PropObject p,String value){
		if(p == null || value == null) return false;
		String nameProperty = p.getName();
		if(nameProperty == null) return false;
		if(!fromConstructor && (nameProperty.equals(samplingModeString))){
			return true;
		}
		if(nameProperty.equals(propNames[0])){
			if(value.equals("A")){
				interfacePort = INTERFACE_PORT_A;
			} else if(value.equals("B")){
				interfacePort = INTERFACE_PORT_B;
			}
		} else if(nameProperty.equals(propNames[1])){
			for(int i = 0; i < modelNames.length;i++){
				if(modelNames[i ].equals(value)){
					outputMode = i ;
					break;
				}
			}
			switch(outputMode){
				case CURRENT_OUT:
					unit = CCUnit.UNIT_CODE_AMPER;
					break;
				case VOLTAGE_OUT:
					unit = CCUnit.UNIT_CODE_MILLIVOLT;
					break;
				case WATT_OUT:
					unit = CCUnit.UNIT_CODE_WATT;
					break;
				case ENERGY_OUT:
					unit = CCUnit.UNIT_CODE_JOULE;
					break;
			}
		}
		return  super.setPValue(p,value);
	}

	public boolean idle(org.concord.waba.extra.event.DataEvent e){
		return true;
	}
	public boolean startSampling(org.concord.waba.extra.event.DataEvent e){
		energy = 0.0f;
		dEvent.type = e.type;
		dDesc.setDt(e.getDataDesc().getDt());
		dEvent.setNumbSamples(1);
		if(calibrationListener != null){
			dDesc.setChPerSample(3);
		}else{
			dDesc.setChPerSample(1);
		}
		dtChannel = dDesc.getDt() / (float)dDesc.getChPerSample();
		return true;
	}
    public boolean dataArrived(DataEvent e)
    {
	dEvent.type 		= e.type;
	int nOffset 		= e.getDataOffset();
	int ndata 			= e.getNumbSamples()*e.dataDesc.getChPerSample();
	float t0 			= e.getTime();
	float[] dataEvent 	= e.getData();
	if(calibrationListener != null){
		switch(outputMode){
			case CURRENT_OUT:
				data[0] = (dataEvent[nOffset] - zeroPointCurrent)/currentResolution;
				break;
			case VOLTAGE_OUT:
				data[0] = (dataEvent[nOffset+1] - zeroPointVoltage)/voltageResolution;
				break;
		}
		data[1] = data[nOffset];
		data[2] = data[nOffset+1];
	}else{
		int  	chPerSample = e.dataDesc.getChPerSample();
		for(int i = 0; i < ndata; i+=chPerSample){
			dEvent.setTime(t0 + dtChannel*(float)i);
			System.out.println("amper "+dataEvent[nOffset+i]+" voltage "+dataEvent[nOffset+i+1]);
			switch(outputMode){
				case CURRENT_OUT:
					data[0] = (dataEvent[nOffset+i] - zeroPointCurrent)/currentResolution;
//					System.out.println("amper "+data[0]);
					break;
				case VOLTAGE_OUT:
					data[0] = (dataEvent[nOffset+i +1] - zeroPointVoltage)/voltageResolution;
					System.out.println("voltage "+data[0]);
					break;
				case WATT_OUT:
				case ENERGY_OUT:
					float		amper = (data[nOffset+i] - zeroPointCurrent)/currentResolution;
					float		voltage = (data[nOffset+i +1] - zeroPointVoltage)/voltageResolution;
					data[0] = amper*voltage;
					if(outputMode == ENERGY_OUT){
						energy 	+= data[0]*dDesc.dt; 
						data[0] 	= energy;
					}
					break;
			}
		}
	}
	notifyDataListeners(dEvent);
	return true;
    }
	public void  calibrationDone(float []row1,float []row2,float []calibrated){
		if(outputMode != CURRENT_OUT && outputMode != VOLTAGE_OUT) return;
		if((row1 == null && (outputMode == CURRENT_OUT)) || calibrated == null) return;
		if((row2 == null && (outputMode == VOLTAGE_OUT)) || calibrated == null) return;
		if(outputMode == CURRENT_OUT){
			zeroPointCurrent = row1[0]  - calibrated[0]*currentResolution;
			zeroPoint = zeroPointCurrent;
			if(calibrationDesc != null){
				CalibrationParam p = calibrationDesc.getCalibrationParam(0);
				if(p != null) p.setValue(zeroPoint);
			}
		}else if(outputMode == VOLTAGE_OUT){
			zeroPointVoltage = row2[0]  - calibrated[0]*voltageResolution;
			zeroPoint = zeroPointVoltage;
			if(calibrationDesc != null){
				CalibrationParam p = calibrationDesc.getCalibrationParam(0);
				if(p != null) p.setValue(zeroPoint);
			}
		}
	}
	public void calibrationDescReady(){
		if(calibrationDesc == null) return;
		CalibrationParam p = calibrationDesc.getCalibrationParam(0);
		if(p != null && p.isValid()){
			zeroPoint = p.getValue();
			zeroPointCurrent = zeroPoint;
			zeroPointVoltage = zeroPoint;
		}
	}

}
