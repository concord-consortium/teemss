/*****************************************************************************

 *                                Waba Extras

 *

 * Version History

 * Date                Version  Programmer

 * ----------  -------  -------  ------------------------------------------

 * 08/05/1999  New      0.9.0    Stefan Kellner

 * Class created

 *

 * 27/03/2000  New      1.0.0    Rob Nielsen

 * Fairly massive reorganization.  Removed PulldownMenu class.

 ****************************************************************************/



package org.concord.waba.extra.ui;



import waba.fx.*;

import waba.ui.*;

import waba.util.Vector;

import extra.ui.*;



/**

 * This is a standard palm menu bar.

 * @author     <A HREF="mailto:kellner@no-information.de">Stefan Kellner</A>,

 * @version    1.0.0 08 May 1999

 */

public class MenuBar extends Control

{

  String name;

  boolean dropped=false;

  Vector menus;

  int xpositions[] = new int[10];

  int mwidths[] = new int[10];

  int selected=0;

  int oldselected=-1;

  // have to change that ...

  FontMetrics fm=null;
    Font menuFont = new Font("Helvetica", Font.BOLD, 12);



  public static int getMenuBarWidth(){return 240;}

  public static int getMenuBarHeight(){return 26;}



  /**

   * Construct a new empty menu bar of default size

   */

  public MenuBar()

  {

    this(new Vector());

  }



  /**

   * Construct a new menu bar with the given menus

   * @param menus a vector of Menu

   */

  public MenuBar(Vector menus)

  {

    this.menus=menus;

  }





  /**

   * Add a new Menu to the menu bar

   * @param name the Menu to add

   */

    public void add(Menu menu)
    {
	hide();
	menus.add(menu);
	
	menu.setMenuBar(this);
	show();
    }

    public void remove(Menu menu)
    {
	int mIndex = menus.find(menu);
	if(mIndex < 0 || mIndex >= menus.getCount()) return;

	hide();
	menus.del(mIndex);	    
	show();
    }

  /**

   * Gets the currently selected Menu

   * @returns the selected Menu

   */

  public Menu getSelected()

  {

    return (Menu)menus.get(selected);

  }



  /**

   * Get the index of the currently selected Menu

   * @returns the index

   */

  public int getSelectedIndex()

  {

    return selected;

  }



  int getPreferredWidth(FontMetrics fm)

  {

    return 0;

  }



  int getPreferredHeight(FontMetrics fm)

  {

    return 0;

  }



  public void onPaint(Graphics g)

  {

  //dima for permanent Menubar

      g.setColor(0,0,0);

      g.drawLine(0,0,width,0);

      drawListCE(g);

  }

  public boolean drawListCE(Graphics g)//dima

  {

    int xpos=0;

    if (fm==null)
        fm=getFontMetrics(menuFont);

    if (fm==null)
      return false;

    g.setColor(0,0,0);

    g.setFont(menuFont);

    for(int i=0;i<menus.getCount();i++){
      mwidths[i]=fm.getTextWidth(((Menu)menus.get(i)).name)+6;
      if(i==selected){
	  g.drawRect(xpos, 0, mwidths[i], 24);
	  g.setColor(255,255,255);
	  g.fillRect(xpos+1, 0, mwidths[i]-2, 23);
	  g.setColor(0,0,0);
      }
      g.drawText(((Menu)menus.get(i)).name,xpos+3,7);
      xpositions[i] = xpos;
      xpos += mwidths[i];
    }

    return true;

  }


  /**

   * Show this menu bar up

   */

  public void show()

  {

      oldselected=selected=-1;

      repaint();

  }

  public void show(int s)

  {

      oldselected=selected=s;
      repaint();

      ((Menu)menus.get(selected)).show(xpositions[selected]);

  }



    /**

   * Hide the bar

   */

  public void hide()
  {

    for(int i=0;i<menus.getCount();i++)
        ((Menu)menus.get(i)).hide();

    oldselected = selected =  -1;
    repaint();
  }





  /**

   * Process pen and key events to this component

   * @param event the event to process

   */

  public void onEvent(Event event)

  {



    int position=0;

    if (event.type==ControlEvent.FOCUS_OUT)
    {
	if (MainWindow.getMainWindow() instanceof ExtraMainWindow &&	    
	    !(((ExtraMainWindow)MainWindow.getMainWindow()).newFocus instanceof Menu))
			  hide();
    } else if (event instanceof PenEvent) {

      int px=((PenEvent)event).x;

      int py=((PenEvent)event).y;

      switch (event.type){
      case PenEvent.PEN_DOWN:
      case PenEvent.PEN_DRAG:
	  for(int i=0;i<menus.getCount();i++){
	      if (px<(xpositions[i]+mwidths[i])){
		      position = i;		      
		      break;		      
	      }	      
	  }

	  if (py>height || 
	      position < 0 ||
	      position >= menus.getCount()){
	      hide();//dima CE
	      return;
	  }

	  selected=position;
	  if (selected!=oldselected){//dima ce
	      if(oldselected != -1){
		  hide();
		  selected=position;
	      }
	      oldselected=selected;
	      ((Menu)menus.get(selected)).show(xpositions[selected]);
	      repaint();

	  }          
          break;

      case PenEvent.PEN_UP:
      }

    }
    
  }
}
