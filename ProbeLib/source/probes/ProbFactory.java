package org.concord.waba.extra.probware.probs;

public class ProbFactory{

public final static int Prob_ThermalCouple 	= 0;
public final static int Prob_Light 			= 1;
public final static int Prob_SmartWheel		= 2;
public final static int Prob_RawData        		= 3;
public final static int Prob_Force        		= 4;
public final static int Prob_VoltCurrent    		= 5;

    public static String [] probeNames = {"Temperature", "Light", "SmartWheel", "RawData","Force","Voltage/Current"};

	public static CCProb createProb(int probIndex,int interfacePort){
		CCProb newProb = null;
		switch(probIndex){
			case Prob_ThermalCouple:
				newProb = new CCThermalCouple("thermocouple");
				break;
			case Prob_Light:
				newProb = new CCLightIntens("light");
				break;
			case Prob_SmartWheel:
				newProb = new CCSmartWheel("smartwheel");
				break;
			case Prob_RawData:
			   newProb = new CCRawData("raw data");
				break;
			case Prob_Force:
			   	newProb = new CCForce(probeNames[Prob_Force]);
				break;
			case Prob_VoltCurrent:
			   	newProb = new CCVoltCurrent(probeNames[Prob_VoltCurrent]);
				break;
		}
		if(newProb != null){
			newProb.setInterfacePort(interfacePort);
		}
		return newProb;
	}

    public static CCProb createProb(String name,int interfacePort)
    {
		return createProb(getIndex(name),interfacePort);
    }

    public static String getName(int probIndex)
    {
	if(probIndex < 0 || probIndex >= probeNames.length) return null;

	return probeNames[probIndex];	
    }

    public static int getIndex(String name)
    {
	for(int i=0; i<probeNames.length; i++){
	    if(probeNames[i].equals(name)){
		return i;
	    }
	}
	return -1;


    }
    public static String [] getProbNames()
    {
	return probeNames;
    }

}
