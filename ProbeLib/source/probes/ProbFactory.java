package org.concord.waba.extra.probware.probs;

public class ProbFactory{

public final static int Prob_ThermalCouple 	= 1;
public final static int Prob_Light 			= 2;
public final static int Prob_SmartWheel		= 3;
public final static int Prob_RawData        = 4;

    public static String [] probeNames = {"Temperature", "Light", "SmartWheel", "RawData"};

	public static CCProb createProb(int probIndex){
		switch(probIndex){
			case Prob_ThermalCouple:
				return new CCThermalCouple("thermocouple");
			case Prob_Light:
				return new CCLightIntens("light");
			case Prob_SmartWheel:
				return new CCSmartWheel("smartwheel");
		case Prob_RawData:
		    return new CCRawData("raw data");
		}
		return null;
	}

    public static CCProb createProb(String name)
    {
	return createProb(getIndex(name));
    }

    public static String getName(int probIndex)
    {
	probIndex--;
	if(probIndex < 0 || probIndex >= probeNames.length) return null;

	return probeNames[probIndex];	
    }

    public static int getIndex(String name)
    {
	for(int i=0; i<probeNames.length; i++){
	    if(probeNames[i].equals(name)){
		return i+1;
	    }
	}
	return -1;


    }
    public static String [] getProbNames()
    {
	return probeNames;
    }

}
