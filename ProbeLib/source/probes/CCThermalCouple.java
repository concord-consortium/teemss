package org.concord.waba.extra.probware.probs;
import org.concord.waba.extra.event.DataListener;
import org.concord.waba.extra.event.DataEvent;
import extra.util.*;
import org.concord.waba.extra.probware.*;


public class CCThermalCouple extends CCProb{
float  			[]tempData 		= new float[3];
int  			[]tempIntData 	= new int[3];
float  			dtChannel = 0.0f;
public final static int		CELSIUS_TEMP_OUT = 0;
public final static int		FAHRENHEIT_TEMP_OUT = 1;
public final static int		KELVIN_TEMP_OUT = 2;
int				outputMode = CELSIUS_TEMP_OUT;
public final static String	tempModeString = "Output Mode";
public final static String	[]tempModes =  {"C","F","K"};
float AC = 17.084f;
float BC = -0.25863f;
float CC = 0.011012f;
float DC = 10f;
float EC = -50f;
float FC = 0.0f;

	CCThermalCouple(boolean init, String name, int interfaceT){
		super(init, name, interfaceT);
		probeType = ProbFactory.Prob_ThermalCouple;
		activeChannels = 2;

		dDesc.setChPerSample(2);
		dDesc.setDt(0.0f);
		dEvent.setDataDesc(dDesc);
		dEvent.setDataOffset(0);
		dEvent.setNumbSamples(1);
		dEvent.setData(tempData);
		dEvent.setIntData(tempIntData);

		if(init){
			addProperty(new PropObject(tempModeString,tempModes,CELSIUS_TEMP_OUT)); 

			calibrationDesc = new CalibrationDesc();
			calibrationDesc.addCalibrationParam(new CalibrationParam(0,FC));			
		}
	}

	public int getUnit()
	{
		PropObject tempMode = getProperty(tempModeString);
		int oMode = tempMode.getIndex();
		
		switch(oMode){
		case FAHRENHEIT_TEMP_OUT:
			unit = CCUnit.UNIT_CODE_FAHRENHEIT;
			break;
		case KELVIN_TEMP_OUT:
			unit = CCUnit.UNIT_CODE_KELVIN;
			break;
		default:
		case CELSIUS_TEMP_OUT:
			unit = CCUnit.UNIT_CODE_CELSIUS;
			break;
		}	   

		return unit;
	}

	public int getInterfaceMode()
	{
		PropObject tempMode = getProperty(tempModeString);
		int outputMode = tempMode.getIndex();
		return interfaceMode;
	}

	public void setDataDescParam(int chPerSample,float dt){
		dDesc.setDt(dt);
		dDesc.setChPerSample(chPerSample);
		dtChannel = dt / (float)chPerSample;
	}
    public boolean startSampling(DataEvent e){
		dEvent.type = e.type;
		dDesc.setDt(e.getDataDesc().getDt());
		dDesc.setChPerSample(e.getDataDesc().getChPerSample());
		dEvent.setNumbSamples(1);
		dDesc.setTuneValue(e.getDataDesc().getTuneValue());
				
		if(calibrationListener != null){
			dDesc.setChPerSample(3);
		}else{
			dDesc.setChPerSample(1);
		}
		dDesc.setIntChPerSample(2);
		dtChannel = dDesc.getDt() / (float)dDesc.getChPerSample();
		return super.startSampling(dEvent);
    }

	public boolean dataArrived(DataEvent e){
		dEvent.type = e.type;
		int nOffset = e.getDataOffset();
		int ndata = e.getNumbSamples()*e.dataDesc.getChPerSample();
		int  	chPerSample = e.dataDesc.getChPerSample();
		if(ndata == 0) return false;
		
		float t0 = e.getTime();
		int[] data = e.getIntData();
		for(int i = 0; i < ndata; i+=chPerSample){
			dEvent.setTime(t0 + dtChannel*(float)i);
			float mV = (float)data[nOffset+i]*dDesc.tuneValue;
			float ch2 = (float)data[nOffset+i+1]*dDesc.tuneValue;
			float lastColdJunct = (ch2 / DC) + EC;
			tempData[0] = mV * (AC + mV * (BC + mV * CC)) + lastColdJunct;
			tempData[0] += FC;
			tempIntData[0] = data[nOffset+i];
			tempIntData[1] = data[nOffset+i+1];
			switch(outputMode){
				case FAHRENHEIT_TEMP_OUT:
					tempData[0] = tempData[0]*1.8f + 32f;
					break;
				case KELVIN_TEMP_OUT:
					tempData[0] += 273.15f;
					break;
				default:
					break;
			}
			if(calibrationListener != null){
				tempData[1]  = mV;
				tempData[2]  = ch2;
			}
			super.dataArrived(dEvent);
		}
		return true;
	}
	public void  calibrationDone(float []row1,float []row2,float []calibrated){
		if(row1 == null || calibrated == null) return;
		float ch1 = row1[0];
		float ch2 = row2[0];
		float lastColdJunct = (ch2 / DC) + EC;
		float mV = ch1;
		float mV2 = mV * mV;
		float mV3 = mV2 * mV;
		float trueValue = mV * AC + mV2 * BC + mV3 * CC + lastColdJunct;
		float userValue = calibrated[0];
		switch(outputMode){
			case FAHRENHEIT_TEMP_OUT:
				userValue = (userValue - 32f)/1.8f;
				break;
			case KELVIN_TEMP_OUT:
				userValue -= 273.15f;
				break;
			default:
				break;
		}
		FC = userValue - trueValue;
		if(calibrationDesc != null){
			CalibrationParam p = calibrationDesc.getCalibrationParam(0);
			if(p != null) p.setValue(FC);
		}
	}
	public void calibrationDescReady(){
		if(calibrationDesc == null) return;
		CalibrationParam p = calibrationDesc.getCalibrationParam(0);
		if(p == null || !p.isValid()) return;
		FC = p.getValue();
	}
}
