import waba.util.*;
import waba.ui.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;
import extra.ui.*;

public class LObjDocument extends LabObject
{
    String text = null;

    public LObjDocument()
    {
	objectType = DOCUMENT;

    }

    public void setText(String t)
    {
	text = t;
    }

    public LabObjectView getView(boolean edit)
    {
	return new LObjDocumentView(this);
    }

    public void writeExternal(DataStream out)
    {
	if(name == null){
	    out.writeString("_null_name_");
	    System.out.println("Writing noname doc");
	} else {
	    System.out.println("Writing " + name + " doc");
	    out.writeString(name);
	}
	out.writeString(text);
    }

    public void readExternal(DataStream in)
    {
	name = in.readString();
	if(name.equals("_null_name_")) name = null;
	System.out.println("Reading " + name + " doc");
	
	text = in.readString();
    }
}
