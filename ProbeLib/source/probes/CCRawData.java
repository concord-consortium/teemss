package org.concord.waba.extra.probware.probs;
import org.concord.waba.extra.event.DataListener;
import org.concord.waba.extra.event.DataEvent;
import extra.util.DataDesc;
import org.concord.waba.extra.probware.*;
import extra.util.*;

public class CCRawData extends CCProb{
float  			[]rawData = new float[1];
float  			dtChannel = 0.0f;
    String [] portNames = {"A", "B"};
    String [] channelNames = {"0", "1"};
    String [] numbChannels = {"1", "2"};

    int curChannel = 0;

	CCRawData(){
		this("unknown");
	}
	CCRawData(String name){
		activeChannels = 2;
		setName(name);
		dDesc.setChPerSample(2);
		dDesc.setDt(0.0f);
		dEvent.setDataDesc(dDesc);
		dEvent.setDataOffset(0);
		dEvent.setNumbSamples(1);
		dEvent.setData(rawData);

		properties = new PropObject[4];
		properties[0] = new PropObject("Port", portNames);
		properties[1] = new PropObject("Num Channels", numbChannels);
		properties[2] = new PropObject("Channel #", channelNames);
		properties[3] = new PropObject(samplingModeString,samplingModes); 
		
		setPropertyValue(0,samplingModes[CCProb.SAMPLING_10BIT_MODE]);

		
	}
	public void setDataDescParam(int chPerSample,float dt){
		dDesc.setDt(dt);
		dDesc.setChPerSample(chPerSample);
		dtChannel = dt / (float)chPerSample;
	}

    public boolean transform(DataEvent e)
    {
	if(activeChannels == 2 && curChannel == 1){
	    e.dataOffset = e.dataOffset + 1;
	    notifyDataListeners(e);
	    e.dataOffset = e.dataOffset - 1;
	} else {
	    notifyDataListeners(e);
	}
	return true;
    }

	protected boolean setPValue(PropObject p,String value){
		if(p == null || value == null) return false;
		String nameProperty = p.getName();
		if(nameProperty == null) return false;
		if(nameProperty.equals("Port")){
			if(value.equals("A")){
				interfacePort = INTERFACE_PORT_A;
			} else if(value.equals("B")){
				interfacePort = INTERFACE_PORT_B;
			}
		} else if(nameProperty.equals("Num Channels")){
			if(value.equals("1")){
				activeChannels = 1;
			} else if(value.equals("2")){
				activeChannels = 2;
			}		
		} else if(nameProperty.equals("Channel #")){
			if(value.equals("0")){
				curChannel = 0;
			} else if(value.equals("1")){
				curChannel = 1;
			}
		}
		return  super.setPValue(p,value);
	}

}
