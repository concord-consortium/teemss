package org.concord.LabBook;

public abstract class LabObjectFactory
{
public LabObjDescriptor []labBookObjDesc;

	
	public LabObject makeNewObj(int objectType){
		return makeNewObj(objectType,true);
	}
	public LabObject makeNewObj(int objectType,boolean doInit){
		LabObject obj = constructObj(objectType,doInit);
		if(obj != null && doInit){
			obj.init();
		}
		return obj;
	}
	
	public abstract LabObjectFactory makeFactory();
	public abstract LabObject constructObj(int objectType,boolean doInit);
	public abstract void createLabBookObjDescriptors();
	public abstract int getFactoryType();
}
