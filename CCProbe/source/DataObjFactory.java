package org.concord.CCProbe;

import org.concord.LabBook.*;

public class DataObjFactory
	implements LabObjectFactory
{
	final public static int DATAOBJ_FACTORY 	= 0x00020000;

    final public static int DATA_SET 			= 3;
    final public static int GRAPH 				= 4;
    final public static int DATA_COLLECTOR 		= 5;
    final public static int UCONVERTOR 			= 10;
    final public static int PROBE_DATA_SOURCE 	= 12;

	public LabObjectFactory makeFactory(){
		DataObjFactory factory = new DataObjFactory();
		factory.createLabBookObjDescriptors();
		return factory;
	}
	
    public LabObject constructObj(int objectType)
    {
		LabObject obj = null;
		switch(objectType){
			case DATA_SET:
				obj = LObjDataSet();
			case GRAPH:
				obj = LObjGraph();
			case DATA_COLLECTOR:
				obj = LObjDataCollector();
			case UCONVERTOR:
				obj = LObjUConvertor();
			case PROBE_DATA_SOURCE:
				obj = LObjProbeDataSource();
		}

		return obj;
    }
	public int getFactoryType(){
		return DATAOBJ_FACTORY;
	}
	public void createLabBookObjDescriptors(){
		labBookObjDesc[0] = new LabObjDescriptor("DataSet",DATA_SET);
		labBookObjDesc[1] = new LabObjDescriptor("Graph",GRAPH);
		labBookObjDesc[2] = new LabObjDescriptor("Data Collector",DATA_COLLECTOR);
		labBookObjDesc[3] = new LabObjDescriptor("UnitConvertor",UCONVERTOR);
		labBookObjDesc[4] = new LabObjDescriptor("ProbeDataSource",PROBE_DATA_SOURCE);
	}

}
