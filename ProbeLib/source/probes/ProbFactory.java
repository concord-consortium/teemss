package org.concord.waba.extra.probware.probs;

public class ProbFactory{

public final static int Prob_ThermalCouple 	= 1;
public final static int Prob_Light 			= 2;

	public static CCProb createProb(int probIndex){
		switch(probIndex){
			case Prob_ThermalCouple:
				return new CCThermalCouple("thermocouple");
			case Prob_Light:
				return new CCLightIntens("light");
		}
		return null;
	}

}
