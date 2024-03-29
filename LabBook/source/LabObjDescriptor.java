package org.concord.LabBook;

public class LabObjDescriptor{
public String 	name;
public int		objType;

	public LabObjDescriptor(String name,int objType){
		this.name 			= name;
		this.objType 		= objType;
	}
	
	public static LabObjDescriptor getLabObjDescriptorByName(LabObjDescriptor []desc,String name){
		if(name == null || desc == null) return null;
		for(int i = 0; i < desc.length; i++){
			if(name.equals(desc[i].name)) return desc[i];
		}
		return  null;
	}
	
	public static LabObjDescriptor getLabObjDescriptorByObjType(LabObjDescriptor []desc,int objType){
		if(desc == null) return null;
		for(int i = 0; i < desc.length; i++){
			if(objType == desc[i].objType) return desc[i];
		}
		return  null;
	}
	
}







