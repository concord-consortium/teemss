package org.concord.LabBook;

import waba.ui.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.ui.*;

public abstract class LabObjectView extends Container
{
    protected boolean showDone = false;
    protected boolean didLayout = false;

    protected LabObject lObj = null;

    ViewContainer container = null;

    public LabObjectView(ViewContainer vc)
    {
		container = vc;
    }

    public abstract void layout(boolean sDone);

    public void close()
    {
		lObj.store();
    }

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
