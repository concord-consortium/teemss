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

    public LabObjectView getView(LObjViewContainer vc, boolean edit)
    {
	return new LObjDocumentView(vc, this);
    }

    public void writeExternal(DataStream out)
    {
	super.writeExternal(out);
	out.writeString(text);
    }

    public void readExternal(DataStream in)
    {
	super.readExternal(in);
	
	text = in.readString();
    }
}
