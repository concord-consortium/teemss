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





public class PopupMenu implements java.awt.event.ActionListener

{

java.awt.PopupMenu menu;

org.concord.waba.extra.event.ActionListener actionListener = null;

  public PopupMenu()

  {

	this("");

  }

  public PopupMenu(String name)

  {

	menu = new java.awt.PopupMenu(name);

	menu.addActionListener(this);

  }





    public void setName(String name) {

	    menu.setName(name);

    }

    public String getName() {

        return menu.getName();

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

  

  public void show(int x,int y){

  	menu.show(waba.applet.Applet.currentApplet,x,y);

  }



  

  public java.awt.PopupMenu getAWTPopupMenu() {return menu;}

  public void 	addActionListener(org.concord.waba.extra.event.ActionListener l){

  	if(actionListener == null){

  		actionListener = l;

  	}

  }

  public void 	removeActionListener(org.concord.waba.extra.event.ActionListener l){

  	if(actionListener == l){

  		actionListener = null;

  	}

  }

    public void actionPerformed(java.awt.event.ActionEvent e){

		if(actionListener != null){

			actionListener.actionPerformed(new org.concord.waba.extra.event.ActionEvent(this,null,e.getActionCommand()));

		}

    }



}