package org.concord.waba.extra.probware;

public class CalibrationDesc{
waba.util.Vector params = null;
	public CalibrationDesc(){
		params = new waba.util.Vector();
	}
	
	public int countParams(){
		return params.getCount();
	}
	
	public CalibrationParam getCalibrationParam(int index){
		if(countParams() < 1) return null;
		for(int i = 0; i < countParams(); i++){
			CalibrationParam cp = (CalibrationParam)params.get(i);
			if(cp == null) continue;
			if(cp.getIndex() == index){
				return cp;
			}
		}
		return null;
	}
	
	public void addCalibrationParam(CalibrationParam cp){
		params.add(cp);
	}
}
