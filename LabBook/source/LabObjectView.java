import waba.ui.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.ui.*;

public abstract class LabObjectView extends Container
{
    protected boolean showDone = false;
    protected boolean didLayout = false;

    protected LabObject lObj = null;

    LObjViewContainer container = null;

    public LabObjectView(LObjViewContainer vc)
    {
	container = vc;
    }

    public abstract void layout(boolean sDone);

    public abstract void close();

    public LabObject getLabObject()
    {
	return lObj;
    }

    public String getTitle()
    {
	if(lObj != null) return lObj.name;
	else return null;
    }
}