package org.concord.waba.extra.probware.probs;
import org.concord.waba.extra.event.DataListener;
import org.concord.waba.extra.event.DataEvent;
import extra.util.DataDesc;
import org.concord.waba.extra.probware.*;
import extra.util.*;

public class CCRawData extends CCProb{
float  			[]rawData = new float[1];
float  			dtChannel = 0.0f;
	CCRawData(){
		this("unknown");
	}
	CCRawData(String name){
		setName(name);
		dDesc.setChPerSample(2);
		dDesc.setDt(0.0f);
		dEvent.setDataDesc(dDesc);
		dEvent.setDataOffset(0);
		dEvent.setNumbSamples(1);
		dEvent.setData(rawData);

		properties = new PropObject[1];
		properties[0] = new PropObject(samplingModeString,samplingModes); 
		setPropertyValue(0,samplingModes[CCProb.SAMPLING_10BIT_MODE]);

		
	}
	public void setDataDescParam(int chPerSample,float dt){
		dDesc.setDt(dt);
		dDesc.setChPerSample(chPerSample);
		dtChannel = dt / (float)chPerSample;
	}

    public boolean transform(DataEvent e)
    {
	//	System.out.println("Time: " + e.time + ", Val: " + e.data[e.dataOffset] + ", Ct: " + e.numbSamples);
	    notifyListeners(e);
	    return true;
    }
}
