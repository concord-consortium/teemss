package org.concord.waba.extra.probware.probs;
import org.concord.waba.extra.event.DataListener;
import org.concord.waba.extra.event.DataEvent;
import extra.util.DataDesc;
import org.concord.waba.extra.probware.*;


public class CCThermalCouple extends CCProb{
public DataDesc	dDesc = new DataDesc();
public DataEvent	dEvent = new DataEvent();
float  			[]tempData = new float[1];
float  			dtChannel = 0.0f;
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
			notifyListeners(dEvent);
		}
		return true;
	}
}
