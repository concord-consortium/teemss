import waba.util.*;
import waba.ui.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;
import extra.ui.*;

public class LObjDrawing extends LabObject
{
    public LObjDrawing()
    {
	objectType = DRAWING;

    }

    public LabObjectView getView(LObjViewContainer vc, boolean edit)
    {
	return new LObjDrawingView(vc, this);
    }

    public void writeExternal(DataStream out)
    {
	super.writeExternal(out);
    }

    public void readExternal(DataStream in)
    {
	super.readExternal(in);
	
    }
}
