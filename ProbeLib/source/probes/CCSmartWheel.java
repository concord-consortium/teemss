package org.concord.waba.extra.probware.probs;
import org.concord.waba.extra.event.DataListener;
import org.concord.waba.extra.event.DataEvent;
import extra.util.*;
import org.concord.waba.extra.probware.*;


public class CCSmartWheel extends CCProb{
float  			[]wheelData = new float[CCInterfaceManager.BUF_SIZE*2];
float  			dtChannel = 0.0f;
int				nTicks = 400;
float				radius = 0.0599f;
float				koeff = 2f*Maths.PI;
public final static String	wheelModeString = "Output Mode";
public final static String	[]wheelModes =  {defaultModeName,"Ang. Vel.","Lin. Vel.", "Lin. Pos."};
public final static int		ANG_MODE_OUT 		= 1;
public final static int		LINEAR_MODE_OUT 	= 2;
    public final static int     LIN_POS_MODE_OUT        = 3;
public final static int		DEFAULT_MODE_OUT   = ANG_MODE_OUT;
int					outputMode = DEFAULT_MODE_OUT;
	private boolean fromConstructor = true;
	CCSmartWheel(){
		this("unknown");
	}
	CCSmartWheel(String name){	   
		setName(name);
		dDesc.setChPerSample(1);
		dDesc.setDt(0.01f);
		dEvent.setDataDesc(dDesc);
		dEvent.setDataOffset(0);
		dEvent.setNumbSamples(1);
		dEvent.setData(wheelData);
		
		properties = new PropObject[2];
		properties[0] = new PropObject(samplingModeString,samplingModes); 
		properties[1] = new PropObject(wheelModeString,wheelModes); 
		setPropertyValue(0,samplingModes[CCProb.SAMPLING_DIG_MODE]);
		setPropertyValue(1,wheelModes[ANG_MODE_OUT]);
		
		calibrationDesc = new CalibrationDesc();
		calibrationDesc.addCalibrationParam(new CalibrationParam(0,radius));
		unit = CCUnit.UNIT_CODE_ANG_VEL;
		outputMode = DEFAULT_MODE_OUT;
		fromConstructor = false;
	}
	
	public int	getActiveChannels(){return 1;}

	protected boolean setPValue(PropObject p,String value){
		if(p == null || value == null) return false;
		String nameProperty = p.getName();
		if(nameProperty == null) return false;
		if(!fromConstructor && (nameProperty.equals(samplingModeString))){
			return true;
		}
		if(nameProperty.equals(wheelModeString)){
			outputMode = DEFAULT_MODE_OUT;
			for(int i = 1; i < wheelModes.length;i++){
				if(wheelModes[i].equals(value)){
					outputMode = i;
					break;
				}
			}
			switch(outputMode){
				case LINEAR_MODE_OUT:
					unit = CCUnit.UNIT_CODE_LINEAR_VEL;
					break;
				default:
				case ANG_MODE_OUT:
					unit = CCUnit.UNIT_CODE_ANG_VEL;
					break;
			case LIN_POS_MODE_OUT:
			    unit = CCUnit.UNIT_CODE_METER;
			    break;
			}
		}
		return super.setPValue(p,value);
	}
	
	public void setDataDescParam(int chPerSample,float dt){
		dDesc.setDt(dt);
		dDesc.setChPerSample(chPerSample);
		dtChannel = dt / (float)chPerSample;
	}
    float posOffset = 0f;
	public boolean transform(DataEvent e){
		dEvent.type = e.type;
		if(e.getType() == DataEvent.DATA_READY_TO_START){
			dDesc.setDt(e.getDataDesc().getDt());
			dDesc.setChPerSample(e.getDataDesc().getChPerSample());
			if(calibrationListener != null){
				dDesc.setChPerSample(2);
			}else{
				dDesc.setChPerSample(1);
			}
			dEvent.setNumbSamples(1);
			dtChannel = dDesc.getDt() / (float)dDesc.getChPerSample();
			posOffset = 0f;
		}
		if(e.getType() != DataEvent.DATA_RECEIVED){
			notifyDataListeners(dEvent);
			return true;
		}
	    //System.out.println("wheel transform "+e);
		float t0 = e.getTime();
		float[] data = e.getData();
		int nOffset = e.getDataOffset();

		float dt = dDesc.getDt();

		int ndata = e.getNumbSamples()*dDesc.getChPerSample();
		int  	chPerSample = dDesc.getChPerSample();
		if(ndata < chPerSample) return false;
				
		for(int i = 0; i < ndata; i+=chPerSample){
			dEvent.setTime(t0 + dtChannel*(float)i);
			wheelData[i] = data[nOffset+i];//row
			float calibrated = wheelData[0]*koeff/(float)nTicks/dt;
			if(calibrationListener != null){
				wheelData[1] = wheelData[0] ;
				wheelData[0] = calibrated * radius*koeff;
			}else{
				switch(outputMode){
					case LINEAR_MODE_OUT:
						wheelData[i+1] = calibrated * radius*koeff;
						break;
				case LIN_POS_MODE_OUT:
				    wheelData[i+1] = posOffset = posOffset + calibrated * radius*koeff;
				    break;
				default:
						wheelData[i+1] = calibrated;
						break;
				}
			}
		}
		notifyDataListeners(dEvent);
		return true;
	}
	public void  calibrationDone(float []row1,float []row2,float []calibrated){
		if(row1 == null || calibrated == null) return;
		if(Maths.abs(row1[0]) < 1e-5) return;//zero
		radius = calibrated[0] / koeff / koeff / nTicks/ row1[0] / dDesc.getDt();
		if(calibrationDesc != null){
			CalibrationParam p = calibrationDesc.getCalibrationParam(0);
			if(p != null) p.setValue(radius);
		}
	}
	public void calibrationDescReady(){
		if(calibrationDesc == null) return;
		CalibrationParam p = calibrationDesc.getCalibrationParam(0);
		if(p == null || !p.isValid()) return;
		radius = p.getValue();
	}
}
