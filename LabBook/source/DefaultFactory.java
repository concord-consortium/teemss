package org.concord.LabBook;

public class DefaultFactory 
	extends LabObjectFactory
{
	private static DefaultFactory me = null;
	final public static int DEFAULT_FACTORY 	= 0x0001;

	public DefaultFactory()
	{
		super(DEFAULT_FACTORY);
		me = this;
	}

    final public static int UNDEFINED 			=0;
    final public static int DICTIONARY 			=1;
    final public static int FORM 				= 2;
    final public static int DOCUMENT 			= 6;
    final public static int OUTPUT_SET 			= 7;
    final public static int QUESTION 			= 8;
    final public static int DRAWING 			= 9;
    final public static int IMAGE 				= 11;
    final public static int CCTEXTAREA 			= 0x100;

	public LabObject constructObj(int objectType)
    {
		LabObject obj = null;
		switch(objectType){
			case DICTIONARY:
				obj= new LObjDictionary();
				break;
			case FORM:
				obj= new LObjForm();
				break;
			case DOCUMENT:
				obj= new LObjDocument();
				break;
			case OUTPUT_SET:
				obj = null;
				break;
			case QUESTION:
				obj = null;
				break;
			case DRAWING:
				obj= new LObjDrawing();
				break;
			case IMAGE:
				obj= new LObjImage();
				break;
			case CCTEXTAREA:
				obj = new LObjCCTextArea();
				break;
		}
		return obj;
	}

	public void createLabBookObjDescriptors(){
		labBookObjDesc = new LabObjDescriptor[6];
		labBookObjDesc[0] = new LabObjDescriptor("Folder",DICTIONARY);
		labBookObjDesc[1] = new LabObjDescriptor("Form",FORM);
		labBookObjDesc[2] = new LabObjDescriptor("Notes",DOCUMENT);
		labBookObjDesc[3] = new LabObjDescriptor("Drawing",DRAWING);
		labBookObjDesc[4] = new LabObjDescriptor("Image",IMAGE);
		labBookObjDesc[5] = new LabObjDescriptor("SuperNotes",CCTEXTAREA);
	}

	public static LabObject create(int objectType)
	{
		if(me == null) return null;
		return me.makeNewObj(objectType);
	}

	public static LObjDictionary createDictionary()
	{
		return (LObjDictionary)create(DICTIONARY);
	}

	public static LObjDocument createDocument()
	{
		return (LObjDocument) create(DOCUMENT);
	}
	public static LObjCCTextArea createCCTextArea()
	{
		return (LObjCCTextArea) create(CCTEXTAREA);
	}

}
