package org.concord.waba.extra.probware.probs;

public class ProbFactory{

public final static int Prob_ThermalCouple 	= 1;
public final static int Prob_Light 			= 2;
public final static int Prob_SmartWheel		= 3;


	public static CCProb createProb(int probIndex){
		switch(probIndex){
			case Prob_ThermalCouple:
				return new CCThermalCouple("thermocouple");
			case Prob_Light:
				return new CCLightIntens("light");
			case Prob_SmartWheel:
				return new CCSmartWheel("smartwheel");
		}
		return null;
	}

}
