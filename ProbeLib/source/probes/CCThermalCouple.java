package org.concord.waba.extra.probware.probs;
import org.concord.waba.extra.event.DataListener;
import org.concord.waba.extra.event.DataEvent;
import extra.util.*;
import org.concord.waba.extra.probware.*;


public class CCThermalCouple extends CCProb{
public DataDesc	dDesc = new DataDesc();
public DataEvent	dEvent = new DataEvent();
float  			[]tempData = new float[1];
float  			dtChannel = 0.0f;
public final static int		CELSIUS_TEMP_OUT = 1;
public final static int		FAHRENHEIT_TEMP_OUT = 2;
public final static int		KELVIN_TEMP_OUT = 3;
public final static int		DEFAULT_TEMP_OUT = CELSIUS_TEMP_OUT;
int				outputMode = DEFAULT_TEMP_OUT;
public final static String	tempModeString = "Temperature Mode";
public final static String	[]tempModes =  {defaultModeName,"Celsius","Fahrenheit","Kelvin"};
	protected CCThermalCouple(){
		this("unknown");
	}
	protected CCThermalCouple(String name){
		setName(name);
		dDesc.setChPerSample(2);
		dDesc.setDt(0.0f);
		dEvent.setDataDesc(dDesc);
		dEvent.setDataOffset(0);
		dEvent.setNumbData(1);
		dEvent.setData(tempData);
		properties = new PropObject[1];
		properties[0] = new PropObject(tempModeString,tempModes); 
		outputMode = DEFAULT_TEMP_OUT;
	}
	public void setPropertyValue(String nameProperty,String value){
		super.setPropertyValue(nameProperty,value);
		if(nameProperty == null || value == null) return;
		if(!nameProperty.equals(tempModeString)) return;
		outputMode = DEFAULT_TEMP_OUT;
		for(int i = 1; i < tempModes.length;i++){
			if(tempModes[i].equals(value)){
				outputMode = i;
				break;
			}
		}
	}
	public void setPropertyValue(int index,String value){
		super.setPropertyValue(index,value);
		if(index != 0 || value == null) return;
		outputMode = DEFAULT_TEMP_OUT;
		for(int i = 1; i < tempModes.length;i++){
			if(tempModes[i].equals(value)){
				outputMode = i;
				break;
			}
		}
	}
	
	public void setDataDescParam(int chPerSample,float dt){
		dDesc.setDt(dt);
		dDesc.setChPerSample(chPerSample);
		dtChannel = dt / (float)chPerSample;
	}
	public boolean transform(DataEvent e){
		float t0 = e.getTime();
		float[] data = e.getData();
		int ndata = e.getNumbData();
		int nOffset = e.getDataOffset();
		dDesc.setDt(e.getDataDesc().getDt());
		dDesc.setChPerSample(e.getDataDesc().getChPerSample());
		dtChannel = dDesc.getDt() / (float)dDesc.getChPerSample();
		int  	chPerSample = dDesc.getChPerSample();
		if(ndata < chPerSample) return false;
				
		for(int i = 0; i < ndata; i+=chPerSample){
			dEvent.setTime(t0 + dtChannel*(float)i);
			float ch1 = data[nOffset+i];
			float ch2 = data[nOffset+i+1];
			float lastColdJunct = (ch2 / 10f) - 50f;
			float mV = ch1;
			float mV2 = mV * mV;
			float mV3 = mV2 * mV;
			tempData[0] = mV * 17.084f - mV2 * 0.25863f + mV3 * 0.011012f + lastColdJunct;
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
			notifyListeners(dEvent);
		}
		return true;
	}
}
