package org.concord.CCProbe;

import org.concord.LabBook.*;

public class DataObjFactory
	extends LabObjectFactory
{
	public static DataObjFactory me = null;
	final public static int DATAOBJ_FACTORY 	= 0x0002;

    final public static int DATA_SET 			= 3;
    final public static int GRAPH 				= 4;
    final public static int DATA_COLLECTOR 		= 5;
    final public static int UCONVERTOR 			= 10;
    final public static int PROBE_DATA_SOURCE 	= 12;
	final public static int ANNOTATION          = 13;
	final public static int CALCULUS_TRANS      = 14;

	public DataObjFactory()
	{
		super(DATAOBJ_FACTORY);
		me = this;
	}

	public LabObject constructObj(int objectType)
    {
		LabObject obj = null;
		switch(objectType){
		case ANNOTATION:
			obj = new LObjAnnotation();
			break;
		case DATA_SET:
			obj = new LObjDataSet();
			break;
		case GRAPH:
			obj = new LObjGraph();
			break;
		case DATA_COLLECTOR:
			obj = new LObjDataCollector();
			break;
		case UCONVERTOR:
			obj = new LObjUConvertor();
			break;
		case PROBE_DATA_SOURCE:
			obj = new LObjProbeDataSource();
			break;
		case CALCULUS_TRANS:
			obj = new LObjCalculusTrans();
			break;
		}

		return obj;
    }

	public static LObjAnnotation createAnnotation()
	{
		return (LObjAnnotation) create(ANNOTATION);
	}

	public static LObjDataSet createDataSet()
	{
		return (LObjDataSet) create(DATA_SET);
	}

	public static LObjProbeDataSource createProbeDataSource()
	{
		return (LObjProbeDataSource) create(PROBE_DATA_SOURCE);
	}

	public static LObjGraph createGraph()
	{
		return (LObjGraph) create(GRAPH);		
	}
	public static LabObject create(int objectType)
	{
		if(me == null) return null;
		return me.makeNewObj(objectType);
	}

	public void createLabBookObjDescriptors(){
		labBookObjDesc = new LabObjDescriptor[6];
		labBookObjDesc[0] = new LabObjDescriptor("DataSet",DATA_SET);
		labBookObjDesc[1] = new LabObjDescriptor("Graph",GRAPH);
		labBookObjDesc[2] = new LabObjDescriptor("Data Collector",DATA_COLLECTOR);
		labBookObjDesc[3] = new LabObjDescriptor("UnitConvertor",UCONVERTOR);
		labBookObjDesc[4] = new LabObjDescriptor("ProbeDataSource",PROBE_DATA_SOURCE);
		labBookObjDesc[5] = new LabObjDescriptor("CalculusTrans", CALCULUS_TRANS);
	}
}
