package extra.util;

import waba.ui.Control;



public class PropObject

{

String 	name;

String 	[]possibleValues;

String 	value;

float		fval = 0.0f;

Control	valueKeeper = null;

	public 	PropObject(String name,String []possibleValues,int defaultIndex){

		this.name = name;

		this.possibleValues = possibleValues;

		if(possibleValues != null && possibleValues.length > 0){

			value = possibleValues[defaultIndex];

		}

	}

	public 	PropObject(String name,String []possibleValues){

		this(name,possibleValues,0);

	}

	public 	PropObject(String name,String value){

		this(name,null,0);

		setValue(value);

	}

	public 	PropObject(String name){

		this(name,null,0);

	}

	public void setValueKeeper(Control c){

		valueKeeper = c;

	}

	public Control getValueKeeper(){return valueKeeper;}

	

	public void setValue(String value){

		if(value == null) return;

		boolean inPossibleValue = false;

		if(possibleValues == null){

			inPossibleValue = true;

		}else{

			for(int i = 0;i < possibleValues.length; i++){

				if(value.equals(possibleValues[i])){

					inPossibleValue = true;

					break;

				}

			}

		}

		if(inPossibleValue) this.value = value;

	}

	

	public float createFValue(){

		if(value != null){

			fval = ConvertExtra.toFloat(value);

		}

		return fval;

	}

	public float getFValue(){ return fval;}

	

	public String getValue(){ return value;}

	public String []getPossibleValues(){ return possibleValues;}

	public String getName(){ return name;}

}