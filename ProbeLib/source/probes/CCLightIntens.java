package org.concord.ProbeLib.probes;

import org.concord.ProbeLib.*;
import org.concord.waba.extra.util.*;

public class CCLightIntens extends Probe
{
	float  			[]lightData = new float[CCInterfaceManager.BUF_SIZE/2];
	int  			[]lightIntData = new int[CCInterfaceManager.BUF_SIZE/2];
	float  			dtChannel = 0.0f;

	/*
	  Lux=(input(mV)-offset(mV))/sensitivity(mV/Lux)

		                    calculated	standard	maximum	
		offset	sensitivity	range	    deviation	deviation	
		5.11	0.0206	    121209	    1.2%	     1.5%	   125k Lux Range
		6.42	0.5655	      4409	    0.8%	     1.3%      4k Lux range
	*/

	// A = 1/sensitivity
	// B = -offset/sensitivity
	float AHigh  = 1f/0.0206f;
	float BHigh  = -5.11f/0.0206f;
	float ALow   = 1f/0.5655f;
	float BLow   = -6.42f/0.5655f;

	public final static int		HIGH_LIGHT_MODE 			= 0;
	public final static int		LOW_LIGHT_MODE 			= 1;
	int				lightMode = HIGH_LIGHT_MODE;

	PropObject rangeProp = new PropObject("Range", "Range", PROP_RANGE, rangeNames);
	PropObject speedProp = new PropObject("Speed", "Speed", PROP_SPEED, speed1Names);

	public static String [] rangeNames = {"Bright Light", "Dim Light"};
	public static String [] speed1Names = {3 + speedUnit, 200 + speedUnit, 400 + speedUnit};
	public static String [] speed2Names = {3 + speedUnit, 200 + speedUnit};

	CCLightIntens(boolean init, String name, int interfaceT){
		super(init, name, interfaceT);
		probeType = ProbFactory.Prob_Light;
	    activeChannels = 2;
		defQuantityName = "Intensity";

		dDesc.setChPerSample(2);
		dDesc.setIntChPerSample(2);
		dDesc.setDt(0.0f);
		dEvent.setDataDesc(dDesc);
		dEvent.setDataOffset(0);
		dEvent.setNumbSamples(1);
		dEvent.setData(lightData);
		dEvent.setIntData(lightIntData);

		addProperty(rangeProp);
		addProperty(speedProp);

		if(init){
			lightMode = 0;

			calibrationDesc = new CalibrationDesc();
			calibrationDesc.addCalibrationParam(new CalibrationParam(0,AHigh));
			calibrationDesc.addCalibrationParam(new CalibrationParam(1,BHigh));
			calibrationDesc.addCalibrationParam(new CalibrationParam(2,ALow));
			calibrationDesc.addCalibrationParam(new CalibrationParam(3,BLow));
		}

		unit = CCUnit.UNIT_CODE_LUX;		
	}

	public void setDataDescParam(int chPerSample,float dt){
		dDesc.setDt(dt);
		dDesc.setChPerSample(chPerSample);
		dtChannel = dt / (float)chPerSample;
	}
	
	public boolean visValueChanged(PropObject po)
	{
		int index = po.getVisIndex();
		if(po == rangeProp){
			if(index == 0){
				speedProp.setVisPossibleValues(speed1Names);
			} else {
				speedProp.setVisPossibleValues(speed2Names);
			}
		}

		return true;
	}

	public CalibrationDesc getCalibrationDesc()
	{
		int lightMode = rangeProp.getIndex();

		CalibrationParam cp = calibrationDesc.getCalibrationParam(0);
		if(cp != null) cp.setAvailable(lightMode == HIGH_LIGHT_MODE);
		cp = calibrationDesc.getCalibrationParam(1);
		if(cp != null) cp.setAvailable(lightMode == HIGH_LIGHT_MODE);
		cp = calibrationDesc.getCalibrationParam(2);
		if(cp != null) cp.setAvailable(lightMode == LOW_LIGHT_MODE);
		cp = calibrationDesc.getCalibrationParam(3);
		if(cp != null) cp.setAvailable(lightMode == LOW_LIGHT_MODE);

		return calibrationDesc;
	}

	public int getInterfaceMode()
	{
		int speedIndex = speedProp.getIndex();

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

		lightMode = rangeProp.getIndex();

		return interfaceMode;
	}
	
	public int  getActiveCalibrationChannels(){return 1;}

	public boolean startSampling(DataEvent e){
		dEvent.type = e.type;
		dDesc.setDt(e.getDataDesc().getDt());
		dDesc.setChPerSample(e.getDataDesc().getChPerSample());
		if(calibrationListener != null){
			dDesc.setChPerSample(2);
		}else{
			dDesc.setChPerSample(1);
		}
		dDesc.setTuneValue(e.getDataDesc().getTuneValue());
	    return super.startSampling(dEvent);
    }

	public boolean dataArrived(DataEvent e){
		dEvent.type = e.type;
		int[] data = e.getIntData();
		int nOffset = e.getDataOffset();
		if(calibrationListener != null){
			if(lightMode == HIGH_LIGHT_MODE){
				lightData[0] = AHigh*dDesc.tuneValue*(float)data[nOffset]+BHigh;
				lightData[1] = dDesc.tuneValue*(float)data[nOffset];
			}else{
				lightData[0] = ALow*dDesc.tuneValue*(float)data[nOffset+1]+BLow;
				lightData[1] = dDesc.tuneValue*(float)data[nOffset+1];
			}
			dEvent.setNumbSamples(1);
		}else{
			int  	chPerSample = e.dataDesc.getChPerSample();
			int ndata = e.getNumbSamples()*chPerSample;
			dtChannel = e.dataDesc.getDt() / (float)chPerSample;
			if(ndata < chPerSample) return false;
			int dataIndex = 0;	
			dEvent.intTime = e.intTime;
			for(int i = 0; i < ndata; i+=chPerSample){
				if(lightMode == HIGH_LIGHT_MODE){
					int v = data[nOffset+i];
					lightIntData[dataIndex] = v;
					lightData[dataIndex] = AHigh*dDesc.tuneValue*(float)v+BHigh;
				}else{
					int v = data[nOffset+i+1];
					lightIntData[dataIndex] = v;
					lightData[dataIndex] = ALow*dDesc.tuneValue*(float)v+BLow;
				}
				dataIndex++;
			}
			dEvent.setNumbSamples(dataIndex);
		}
		return super.dataArrived(dEvent);
	}
	public void  calibrationDone(float []row1,float []row2,float []calibrated){
		if(row1 == null || calibrated == null) return;
		float x1 = row1[0];
		float x2 = row1[1];
		float y1 = calibrated[0];
		float y2 = calibrated[1];
		float A = (y2 - y1)/(x2 - x1);
		float B = y2 - A*x2;
		if(lightMode == HIGH_LIGHT_MODE){
			AHigh = A;
			BHigh = B;
			if(calibrationDesc != null){
				CalibrationParam p = calibrationDesc.getCalibrationParam(0);
				if(p != null) p.setValue(AHigh);
				p = calibrationDesc.getCalibrationParam(1);
				if(p != null) p.setValue(BHigh);
			}
		}else if(lightMode == LOW_LIGHT_MODE){
			ALow = A;
			BLow = B;
			if(calibrationDesc != null){
				CalibrationParam p = calibrationDesc.getCalibrationParam(2);
				if(p != null) p.setValue(ALow);
				p = calibrationDesc.getCalibrationParam(3);
				if(p != null) p.setValue(BLow);
			}
		}
	}
	public void calibrationDescReady(){
		if(calibrationDesc == null) return;
		CalibrationParam p = calibrationDesc.getCalibrationParam(0);
		if(p != null && p.isValid()){
			AHigh = p.getValue();
		}
		p = calibrationDesc.getCalibrationParam(1);
		if(p != null && p.isValid()){
			BHigh = p.getValue();
		}
		p = calibrationDesc.getCalibrationParam(2);
		if(p != null && p.isValid()){
			ALow = p.getValue();
		}
		p = calibrationDesc.getCalibrationParam(3);
		if(p != null && p.isValid()){
			BLow = p.getValue();
		}
	}
}
