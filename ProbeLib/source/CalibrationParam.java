package org.concord.waba.extra.probware;

public class CalibrationParam{
boolean   valid = false;
float      value = 0.0f;
String   unit = "";
float     defaultValue = 1.0f;
int		index = 0;
	public CalibrationParam(){
		this(0,"",1.0f);
		valid = false;
	}
	public CalibrationParam(int index,String unit){
		this(index,unit,1.0f);
	}
	public CalibrationParam(int index,float defaultValue){
		this(index,"",defaultValue);
	}
	public CalibrationParam(int index,String unit,float defaultValue){
		this.unit = unit;
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
	
	public String getUnit(){return unit;}
	public void setUnit(String unit){this.unit = unit;}
	
}
