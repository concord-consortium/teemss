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

    public void setPropertyValue(String nameProperty,String value)
    {
	
	if(nameProperty == null || value == null) return;
	if(nameProperty.equals("Port")){
	    setPropertyValue(0, value);
	    return;
	} else if(nameProperty.equals("Num Channels")){
	    setPropertyValue(1, value);
	    return;
	} else if(nameProperty.equals("Channel #")){
	    setPropertyValue(2, value);
	    return;
	}

	super.setPropertyValue(nameProperty,value);	       
	return;
    }

    public void setPropertyValue(int index,String value)
    {

	switch(index){
	case 0:
	    if(value.equals("A")){
		interfacePort = INTERFACE_PORT_A;
	    } else if(value.equals("B")){
		interfacePort = INTERFACE_PORT_B;
	    }
	    break;
	case 1:
	    if(value.equals("1")){
		activeChannels = 1;
	    } else if(value.equals("2")){
		activeChannels = 2;
	    }		
	    break;
	case 2:
	    if(value.equals("0")){
		curChannel = 0;
	    } else if(value.equals("1")){
		curChannel = 1;
	    }
	}

	super.setPropertyValue(index, value);
    }
}
