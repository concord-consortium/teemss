package org.concord.ProbeLib.probes;

import org.concord.ProbeLib.*;
import org.concord.waba.extra.io.*;

public class ProbFactory
{
	public final static int Prob_Undefine 		= -1;
	public final static int Prob_ThermalCouple 	= 0;
	public final static int Prob_Light 			= 1;
	public final static int Prob_SmartWheel		= 2;
	public final static int Prob_RawData        = 3;
	public final static int Prob_Force        	= 4;
	public final static int Prob_VoltCurrent    = 5;

	public static int DefaultInterfaceType = CCInterfaceManager.INTERFACE_2;

    public static String [] probeNames = {"Temperature", "Light", "SmartWheel", "RawData","Force","VoltageCurrent"};
	public static Probe createProbeFromStream(DataStream in){
		boolean validProbe = in.readBoolean();
		if(!validProbe) return null;

		int probeType 		= in.readInt();
		int interfacePort 	= in.readInt();
		Probe probe = createProb(false, probeType,interfacePort, DefaultInterfaceType);
		if(probe != null) probe.readExternal(in);
		return  probe;
	}
	public static void storeProbeToStream(Probe probe, DataStream out){
    	out.writeBoolean(probe != null);
    	if(probe != null){
    		out.writeInt(probe.getProbeType());
    		out.writeInt(probe.getInterfacePort());
    		probe.writeExternal(out);
    	}
	}

	public static Probe createProb(boolean init, int probIndex,int interfacePort, int interfaceType){
		Probe newProb = null;
		switch(probIndex){
		case Prob_ThermalCouple:
			newProb = new CCThermalCouple(init, probeNames[Prob_ThermalCouple], interfaceType);
			break;
		case Prob_Light:
			newProb = new CCLightIntens(init, probeNames[Prob_Light], interfaceType);
			break;
		case Prob_SmartWheel:
			newProb = new CCSmartWheel(init, probeNames[Prob_SmartWheel], interfaceType);
			break;
		case Prob_RawData:
			newProb = new CCRawData(init, probeNames[Prob_RawData], interfaceType);
			break;
		case Prob_Force:
			newProb = new CCForce(init, probeNames[Prob_Force], interfaceType);
			break;
		case Prob_VoltCurrent:
			newProb = new CCVoltCurrent(init, probeNames[Prob_VoltCurrent], interfaceType);
			break;
		}
		if(newProb != null){
			newProb.setInterfacePort(interfacePort);
		}
		return newProb;
	}

	public static Probe createProb(int index, int interfacePort)
	{
		return createProb(true, index, interfacePort, DefaultInterfaceType);
	}

    public static Probe createProb(String name,int interfacePort)
    {
		return createProb(true, getIndex(name),interfacePort, DefaultInterfaceType);
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
