package org.concord.LabBook;

public class DefaultFactory 
	implements LabObjectFactory
{
    final public static int DICTIONARY =1;
    final public static int FORM = 2;
    final public static int DOCUMENT = 6;
    final public static int OUTPUT_SET = 7;
    final public static int QUESTION = 8;
    final public static int DRAWING = 9;
    final public static int IMAGE = 11;

    public static String [] typeNames = {
	"Zero", "Dict", "Form", "DataSet", "Graph", "DataControl", "Doc", "OutputSet", "Quest","Drawing",
	"UnitConvertor","Image","ProbeDataSource","Probes","Probe"};

	public LabObject makeNewObj(int objectType)
	{
		switch(objectType){
		case DICTIONARY:
			return new LObjDictionary();
		case FORM:
			return new LObjForm();
		case DOCUMENT:
			return new LObjDocument();
		case OUTPUT_SET:
			return new LObjOutputSet();
		case QUESTION:
			return new LObjQuestion();
		case DRAWING:
			return new LObjDrawing();
		case IMAGE:
			return new LObjImage();
		}

		return null;
    }


}
