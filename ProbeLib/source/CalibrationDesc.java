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
	public void writeExternal(extra.io.DataStream out){
		out.writeInt(countParams());
		for(int i = 0; i < countParams(); i++){
			CalibrationParam cp = (CalibrationParam)params.get(i);
			out.writeBoolean(cp != null);
			if(cp == null) continue;
			cp.writeExternal(out);
		}
	}
	public void readExternal(extra.io.DataStream in){
		int nParam = in.readInt();
		if(nParam < 1) return;
		params = new waba.util.Vector();
		for(int i = 0; i < nParam; i++){
			if(!in.readBoolean()) continue;
			addCalibrationParam(new CalibrationParam(in));
		}
	}
}
