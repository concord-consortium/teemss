package org.concord.LabBook;

import waba.ui.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.ui.*;

public abstract class LabObjectView extends Container
{
    protected boolean showDone = false;
    protected boolean didLayout = false;

    protected LabObject lObj = null;

    protected ViewContainer container = null;

	protected boolean showMenus = false;
	protected boolean embeddedState = false;

    public LabObjectView(ViewContainer vc)
    {
		container = vc;
    }

    public abstract void layout(boolean sDone);

    public void close()
    {
		lObj.store();
		setShowMenus(false);
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
    
	public void setShowMenus(boolean state)
	{
		if(!showMenus && state){
			showMenus = true;
			addMenus();
		} else if(showMenus && !state){
			showMenus = false;
			delMenus();
		}
	}

    protected void addMenus(){}
	protected void delMenus(){}
	
	public void setEmbeddedState(boolean embeddedState){
		this.embeddedState = embeddedState;
	}
	public boolean getEmbeddedState(){return embeddedState;}
}
