package org.concord.waba.extra.probware;

public class CalibrationParam{
boolean   valid = false;
float      value = 0.0f;
float     defaultValue = 1.0f;
int		index = 0;
	public CalibrationParam(){
		this(0,1.0f);
		valid = false;
	}
	public CalibrationParam(int index){
		this(index,1.0f);
	}
	public CalibrationParam(extra.io.DataStream in){
		valid = true;
		readExternal(in);
	}
	public CalibrationParam(int index,float defaultValue){
		this.index = index;
		this.defaultValue = defaultValue;
		valid = false;
	}
	public int getIndex(){return index;}
	
	public void  clear(){
		valid = false;
		value = 0.0f;
	}
	
	public boolean isValid(){return valid;}
	
	public float getValue(){return value;}
	public void setValue(float value){
		this.value = value;
		valid = true;
	}
	public void setValueToDefault(){
		setValue(defaultValue);
	}
	public void writeExternal(extra.io.DataStream out){
		out.writeInt(index);
		out.writeFloat(value);
		out.writeFloat(defaultValue);
	}
	public void readExternal(extra.io.DataStream in){
		this.index 		= in.readInt();
		this.value 		= in.readFloat();
		this.defaultValue 	= in.readFloat();
	}
	
	
}
