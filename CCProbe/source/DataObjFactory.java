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
	
    public LabObject constructObj(int objectType,boolean doInit)
    {
		switch(objectType){
			case DATA_SET:
				return LObjDataSet.makeNewObj(!doInit);
			case GRAPH:
				return LObjGraph.makeNewObj(!doInit);
			case DATA_COLLECTOR:
				return LObjDataCollector.makeNewObj(!doInit);
			case UCONVERTOR:
				return LObjUConvertor.makeNewObj(!doInit);
			case PROBE_DATA_SOURCE:
				return LObjProbeDataSource.makeNewObj(!doInit);
		}

		return null;
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
