package org.concord.waba.extra.probware.probs;
import org.concord.waba.extra.event.DataListener;
import org.concord.waba.extra.event.DataEvent;
import extra.util.DataDesc;
import org.concord.waba.extra.probware.*;
import extra.util.*;

public class CCForce extends CCProb{
float  			[]forceData = new float[CCInterfaceManager.BUF_SIZE/2];
int  			[]forceIntData = new int[CCInterfaceManager.BUF_SIZE];

	public final static String [] modeNames = {"End of Arm", "Middle of Arm"};
	public final static String [] range1Names = {"+/- 2N", "+/- 20N"};
	public final static String [] range2Names = {"+/- 20N", "+/- 200N"};
	public final static String [] speed1Names = {3 + speedUnit, 200 + speedUnit, 400 + speedUnit};
	public final static String [] speed2Names = {3 + speedUnit, 200 + speedUnit};

	public final static String [] propNames = {"Mode", "Range", "Speed"};
	public int curChannel = 1;
float	A = 0.01734f;
float B = -25.31f;

	CCForce(boolean init, String name, int interfaceT){
		super(init, name, interfaceT);
		probeType = ProbFactory.Prob_Force;
	    activeChannels = 1;

		dDesc.setChPerSample(1);
		dDesc.setDt(0.0f);
		dEvent.setDataDesc(dDesc);
		dEvent.setDataOffset(0);
		dEvent.setData(forceData);
		dEvent.setIntData(forceIntData);

		if(init){
			addProperty(new PropObject(propNames[0], modeNames));
			addProperty(new PropObject(propNames[1], range1Names));
			addProperty(new PropObject(propNames[2], speed2Names));
		
			calibrationDesc = new CalibrationDesc();
			calibrationDesc.addCalibrationParam(new CalibrationParam(0,A));
			calibrationDesc.addCalibrationParam(new CalibrationParam(1,B));
		}
		unit = CCUnit.UNIT_CODE_NEWTON;		
	}
	public void setDataDescParam(int chPerSample,float dt){
		dDesc.setDt(dt);
		dDesc.setChPerSample(chPerSample);
	}

	public boolean visValueChanged(PropObject po)
	{
		int index = po.getVisIndex();
		if(po.getName().equals(propNames[1])){
			PropObject speed = getProperty(propNames[2]);
			if(index == 0){
				speed.setVisPossibleValues(speed2Names);
			} else if(index == 1){
				speed.setVisPossibleValues(speed1Names);
			}
		} else if(po.getName().equals(propNames[0])){
			PropObject range = getProperty(propNames[1]);
			if(index == 0){
				range.setVisPossibleValues(range1Names);
			} else if(index == 1){
				range.setVisPossibleValues(range2Names);
			}
		} 
		return true;
	}

	public int getInterfaceMode()
	{
		int rangeIndex = getProperty(propNames[1]).getIndex();
		int speedIndex = getProperty(propNames[2]).getIndex();

		curChannel = 1-rangeIndex;

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
		return interfaceMode;
	}

	int chPerSample = 2;
	int channelOffset = 0;

	public boolean startSampling(DataEvent e){
		dEvent.type = e.type;
		dDesc.setDt(e.getDataDesc().getDt());
		chPerSample = e.getDataDesc().getChPerSample();
		dDesc.setTuneValue(e.getDataDesc().getTuneValue());
		if(calibrationListener != null){
			if(activeChannels == 2)
				dDesc.setChPerSample(3);
			else
				dDesc.setChPerSample(2);
		}else{
			dDesc.setChPerSample(1);
		}
		dDesc.setIntChPerSample(1);
		channelOffset = curChannel;
		if(curChannel > activeChannels - 1) channelOffset = activeChannels - 1;

		return super.startSampling(dEvent);
    }

	public boolean dataArrived(DataEvent e){
		dEvent.type = e.type;
		dEvent.intTime = e.intTime;
		float v = dDesc.tuneValue;
		if(calibrationListener != null){
			dEvent.numbSamples = 1;
			forceData[0] = A*e.intData[e.dataOffset+channelOffset]*v+B;
			if(activeChannels == 2){
				forceData[1] = e.intData[e.dataOffset]*v;
				forceData[2] = e.intData[e.dataOffset+1]*v;
			}else{
				forceData[1] = e.intData[e.dataOffset+channelOffset]*v;
				forceData[2]  = 0f;
			}
		}else{
			dEvent.intTime = e.intTime;
			dEvent.numbSamples = e.numbSamples;
			dEvent.pTimes = e.pTimes;
			dEvent.numPTimes = e.numPTimes;
			int ndata = dEvent.numbSamples*e.dataDesc.chPerSample;
			int dOff = e.dataOffset;
			int data [] = e.intData;
			int currPos = 0;
			for(int i = 0; i < ndata; i+= chPerSample){
				forceIntData[currPos] = data[dOff + i+channelOffset];
				forceData[currPos] = A*forceIntData[currPos]*v+B;
				currPos++;
			}
		}
		return super.dataArrived(dEvent);
	}

	public void  calibrationDone(float []row1,float []row2,float []calibrated){
		if(row1 == null || calibrated == null) return;
		float x1, x2;
		if(curChannel ==0){
		    x1 = row1[0];
		    x2 = row1[1];
		} else {
		    x1 = row2[0];
		    x2 = row2[1];
		}
		float y1 = calibrated[0];
		float y2 = calibrated[1];
		A = (y2 - y1)/(x2 - x1);
		B = y2 - A*x2;
		if(calibrationDesc != null){
			CalibrationParam p = calibrationDesc.getCalibrationParam(0);
			if(p != null) p.setValue(A);
			p = calibrationDesc.getCalibrationParam(1);
			if(p != null) p.setValue(B);
		}
		//		System.out.println("A "+A);
		// System.out.println("B "+B);
	}
	public void calibrationDescReady(){
		if(calibrationDesc == null) return;
		CalibrationParam p = calibrationDesc.getCalibrationParam(0);
		if(p == null || !p.isValid()) return;
		A = p.getValue();
		p = calibrationDesc.getCalibrationParam(1);
		if(p == null || !p.isValid()) return;
		B = p.getValue();
	}
}
