package org.concord.CCProbe;

import org.concord.LabBook.*;

public class DataObjFactory
	implements LabObjectFactory
{
    final public static int DATA_SET = 3;
    final public static int GRAPH = 4;
    final public static int DATA_COLLECTOR = 5;
    final public static int UCONVERTOR = 10;
    final public static int PROBE_DATA_SOURCE = 12;

    public LabObject makeNewObj(int objectType)
    {
		switch(objectType){
		case DATA_SET:
			return new LObjDataSet();
		case GRAPH:
			return new LObjGraph();
		case DATA_COLLECTOR:
			return new LObjDataCollector();
		case UCONVERTOR:
			return new LObjUConvertor();
		case PROBE_DATA_SOURCE:
			return new LObjProbeDataSource();
		}

		return null;
    }
}
