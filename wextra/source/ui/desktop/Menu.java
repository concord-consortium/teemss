/*****************************************************************************
 *                                Waba Extras
 *
 * Version History
 * Date                Version  Programmer
 * ----------  -------  -------  ------------------------------------------
 * 08/05/1999  New      0.9.0    Stefan Kellner
 * Class created
 *
 ****************************************************************************/

package org.concord.waba.extra.ui;
import org.concord.waba.extra.event.*;
import waba.ui.*;
public class Menu implements java.awt.event.ActionListener
{
	java.awt.Menu menu;
    ActionListener actionListener = null;
	public Menu()
	{
		this("");
	}
	public Menu(String label)
	{
		menu = new java.awt.Menu(label);
		menu.addActionListener(this);
	}


    public void setName(String name) {
	    menu.setLabel(name);
    }
    public String getName() {
        return menu.getLabel();
    }

	/**
	 * Add a new item to the end of the menu. The first item defines
	 * the name of the menu. To insert a separator simply add an option 
	 * "-" to the menu.
	 * @param name the item to add
	 */
	public void add(String nameItem)
	{
		menu.add(nameItem);
	}

	public void removeAll()
	{
		menu.removeAll();
	}
  
	public java.awt.Menu getAWTMenu() {return menu;}
	public void 	addActionListener(ActionListener l){
		if(actionListener == null){
			actionListener = l;
		}
	}
	public void 	removeActionListener(ActionListener l){
		if(actionListener == l){
			actionListener = null;
		}
	}
	public void actionPerformed(java.awt.event.ActionEvent e){
		if(actionListener != null){
			synchronized(waba.applet.Applet.uiLock){
				actionListener.actionPerformed(new ActionEvent(this,null,e.getActionCommand()));
				MainWindow mw = MainWindow.getMainWindow();
				if(mw instanceof ExtraMainWindow){
					((ExtraMainWindow) mw)._finishAWTEvent();
				}
			}
		}
	}
}
