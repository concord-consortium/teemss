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

	public void setContainer(ViewContainer vc)
	{ 
		container = vc;
	}

    public abstract void layout(boolean sDone);

    public void close()
    {
		if(lObj != null) lObj.store();
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

	public int getPreferredWidth(){return -1;}

	public int getPreferredHeight(){return -1;}

	public extra.ui.Dimension getPreferredSize()
	{
		int pWidth = getPreferredWidth();
		int pHeight = getPreferredHeight();

		if(pWidth < 0 || pHeight < 0){
			return null;
		} else {
			return new extra.ui.Dimension(getPreferredWidth(),getPreferredHeight());
		}
	}

    
/***************************************************************************/
/*	The idea is that a container calls setShowMenus not addMenus.          */
/*	I also changed the close method, so that it automatically removes the  */
/*	menus if they haven't already been removed.                            */
/***************************************************************************/
/*	If a View is not a container then it probaly just needs to overide     */
/*	addMenus and delMenus.                                                 */
/*	If a View _is_ a container then it might need to override setShowMenus.*/

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
