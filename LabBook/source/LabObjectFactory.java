package org.concord.LabBook;

public abstract class LabObjectFactory
{
public LabObjDescriptor []labBookObjDesc;

	
	public LabObject makeNewObj(int objectType){
		return makeNewObj(objectType,true);
	}
	public LabObject makeNewObj(int objectType,boolean doInit){
		LabObject obj = constructObj(objectType);
		if(obj != null && doInit){
			obj.init();
		}
		return obj;
	}
	
	public abstract LabObjectFactory makeFactory();
	public abstract LabObject constructObj(int objectType);
	public abstract void createLabBookObjDescriptors();
	public abstract int getFactoryType();
}
