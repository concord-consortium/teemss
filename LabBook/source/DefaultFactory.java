package org.concord.LabBook;

public class DefaultFactory 
	extends LabObjectFactory
{
	final public static int DEFAULT_FACTORY 	= 0x00010000;

/*
*/

    final public static int UNDEFINED 			=0;
    final public static int DICTIONARY 			=1;
    final public static int FORM 				= 2;
    final public static int DOCUMENT 			= 6;
    final public static int OUTPUT_SET 			= 7;
    final public static int QUESTION 			= 8;
    final public static int DRAWING 			= 9;
    final public static int IMAGE 				= 11;

	public LabObjectFactory makeFactory(){
		DefaultFactory factory = new DefaultFactory();
		factory.createLabBookObjDescriptors();
		return factory;
	}
	public LabObject constructObj(int objectType,boolean doInit){
		LabObject obj = null;
		switch(objectType){
			case DICTIONARY:
				obj= new LObjDictionary();
			case FORM:
				obj= LObjForm.makeNewObj();
			case DOCUMENT:
				obj= LObjDocument.makeNewObj();
			case OUTPUT_SET:
				obj= LObjOutputSet.makeNewObj();
			case QUESTION:
				obj= LObjQuestion.makeNewObj();
			case DRAWING:
				obj= LObjDrawing.makeNewObj();
			case IMAGE:
				obj= LObjImage.makeNewObj();
		}
		return obj;
	}
	public int getFactoryType(){
		return DEFAULT_FACTORY;
	}
	public void createLabBookObjDescriptors(){
		labBookObjDesc = new LabObjDescriptor[8];
		labBookObjDesc[0] = new LabObjDescriptor("Folder",DICTIONARY);
		labBookObjDesc[1] = new LabObjDescriptor("Form",FORM);
		labBookObjDesc[2] = new LabObjDescriptor("Notes",DOCUMENT);
		labBookObjDesc[3] = new LabObjDescriptor("OutputSet",OUTPUT_SET);
		labBookObjDesc[4] = new LabObjDescriptor("Questions",QUESTION);
		labBookObjDesc[5] = new LabObjDescriptor("Drawing",DRAWING);
		labBookObjDesc[7] = new LabObjDescriptor("Image",IMAGE);
	}
}
