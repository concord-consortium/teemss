package org.concord.waba.extra.probware.probs;
import org.concord.waba.extra.event.DataListener;
import org.concord.waba.extra.event.DataEvent;
import extra.util.DataDesc;
import org.concord.waba.extra.probware.*;
import extra.util.*;

public class CCLightIntens extends CCProb{
float  			[]lightData = new float[1];
float  			dtChannel = 0.0f;
	CCLightIntens(){
		this("unknown");
	}
	CCLightIntens(String name){
		setName(name);
		dDesc.setChPerSample(2);
		dDesc.setDt(0.0f);
		dEvent.setDataDesc(dDesc);
		dEvent.setDataOffset(0);
		dEvent.setNumbSamples(1);
		dEvent.setData(lightData);

		properties = new PropObject[1];
		properties[0] = new PropObject(samplingModeString,samplingModes); 
		setPropertyValue(0,samplingModes[CCProb.SAMPLING_10BIT_MODE]);

		
	}
	public void setDataDescParam(int chPerSample,float dt){
		dDesc.setDt(dt);
		dDesc.setChPerSample(chPerSample);
		dtChannel = dt / (float)chPerSample;
	}
	public boolean transform(DataEvent e){
		if(e.getType() != DataEvent.DATA_RECEIVED){
			notifyListeners(dEvent);
		}else{
			float t0 = e.getTime();
			float[] data = e.getData();
			int nOffset = e.getDataOffset();
			dDesc.setDt(e.getDataDesc().getDt());
			dDesc.setChPerSample(e.getDataDesc().getChPerSample());
			int ndata = e.getNumbSamples()*dDesc.getChPerSample();
			dtChannel = dDesc.getDt() / (float)dDesc.getChPerSample();
			int  	chPerSample = dDesc.getChPerSample();
			if(ndata < chPerSample) return false;
					
			for(int i = 0; i < ndata; i+=chPerSample){
				dEvent.setTime(t0 + dtChannel*(float)i);
				float ch1 = data[nOffset+i];
				float ch2 = data[nOffset+i+1];
				lightData[0] = ch1;
	//			lightData[0] = ch2;
				notifyListeners(dEvent);
			}
		}
		return true;
	}
}
