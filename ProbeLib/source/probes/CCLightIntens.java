package org.concord.waba.extra.probware.probs;
import org.concord.waba.extra.event.DataListener;
import org.concord.waba.extra.event.DataEvent;
import extra.util.DataDesc;
import org.concord.waba.extra.probware.*;


public class CCLightIntens extends CCProb{
public DataDesc	dDesc = new DataDesc();
public DataEvent	dEvent = new DataEvent();
float  			[]lightData = new float[1];
float  			dtChannel = 0.0f;
	protected CCLightIntens(){
		this("unknown");
	}
	protected CCLightIntens(String name){
		setName(name);
		dDesc.setChPerSample(2);
		dDesc.setDt(0.0f);
		dEvent.setDataDesc(dDesc);
		dEvent.setDataOffset(0);
		dEvent.setNumbData(1);
		dEvent.setData(lightData);
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
			lightData[0] = ch1;
//			lightData[0] = ch2;
			notifyListeners(dEvent);
		}
		return true;
	}
}
