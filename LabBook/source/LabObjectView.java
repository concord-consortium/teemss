import waba.ui.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.ui.*;

public abstract class LabObjectView extends Container
{
    protected boolean showName = false;
    protected boolean showDone = false;
    protected boolean didLayout = false;

    protected LabObject lObj = null;

    LObjViewContainer container = null;

    public abstract void layout(boolean sDone, boolean sName);

    public abstract void close();

    public void addViewContainer(LObjViewContainer vc)
    {
	container = vc;
    }

    public LabObject getLabObject()
    {
	return lObj;
    }
}
