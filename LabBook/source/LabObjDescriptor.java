package org.concord.LabBook;public class LabObjDescriptor{public String 	name;public int		objType;	private LabObjDescriptor(String name,int factoryType, int objType){		this.name 			= name;		this.factoryType 	= factoryType;		this.objType 		= objType;	}		public static LabObjDescriptor getLabObjDescriptorByName(String name){		if(name == null) return null;		for(int i = 0; i < labBookObjDesc.length; i++){			if(name.equals(labBookObjDesc[i].name)) return labBookObjDesc[i];		}		return  null;	}		public static LabObjDescriptor getLabObjDescriptorByObjType(int objType){		for(int i = 0; i < labBookObjDesc.length; i++){			if(objType == labBookObjDesc[i].objType) return labBookObjDesc[i];		}		return  null;	}	}