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



package extra.ui;



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

  Popup popup=null;

  Vector menus;

  int xpositions[] = new int[10];

  int mwidths[] = new int[10];

  int selected=0;

  int oldselected=0;

  int numDisplayed=0;

  // have to change that ...

  int height=16;

  int width=160;

  FontMetrics fm=null;



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

    menus.add(menu);

	menu.setMenuBar(this);

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

    if (popup != null)

    {

      g.setColor(255,255,255);

      g.fillRect(1,1,width,height);

      drawList(g);

      g.setColor(0,0,0);

      g.drawLine(1,0,width-3,0);

      g.drawLine(0,1,0,height-3);

      g.drawLine(width-2,1,width-2,height-2);

      g.drawLine(1,height-2,width-3,height-2);

      g.drawLine(2,height-1,width-3,height-1);

      g.drawLine(width-1,2,width-1,height-3);

    }

  }



  public boolean drawList(Graphics g)

  {

    // initial distance to the left

    int xpos=5;

    if (fm==null)

        fm=getFontMetrics(MainWindow.defaultFont);

    if (fm==null)

      return false;

    g.setColor(0,0,0);

    for(int i=0;i<numDisplayed;i++)

    {

      mwidths[i]=fm.getTextWidth(((Menu)menus.get(i)).name)+6;

      if (i==selected)

      {

        g.fillRect(xpos,1,mwidths[i],height-3);

        g.setColor(255,255,255);

      }

      g.drawText(((Menu)menus.get(i)).name,xpos+3,2);

      xpositions[i] = xpos;

      xpos += mwidths[i];

      if (i==selected)

        g.setColor(0,0,0);

    }

    return true;

  }



  /**

   * Show this menu bar up

   */

  public void show()

  {

    if (popup==null)

    {

        numDisplayed=menus.getCount();

        popup=new Popup(this);

        popup.popup(0,0,width,height);

        oldselected=selected=0;

        if (menus.getCount() > 0)

            ((Menu)menus.get(0)).show(5);

    }

  }



    /**

   * Hide the bar

   */

  public void hide()

  {

    for(int i=0;i<numDisplayed;i++)

        ((Menu)menus.get(i)).hide();

    if (popup!=null)

    {

        popup.unpop();

        popup=null;

    }

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

		  if (MainWindow.getMainWindow() instanceof ExtraMainWindow&&

			  !(((ExtraMainWindow)MainWindow.getMainWindow()).newFocus instanceof Menu))

			  hide();

    }

		else

    if (event instanceof PenEvent)

    {

      int px=((PenEvent)event).x;

      int py=((PenEvent)event).y;

      switch (event.type)

      {

        case PenEvent.PEN_DOWN:

        case PenEvent.PEN_DRAG:

          if (popup!=null)

          {

            for(int i=0;i<numDisplayed;i++)

            {

                if (px<(xpositions[i]+mwidths[i]))

								{

									position = i;

									break;

								}

            }

            if (py>height)

						{

						  MainWindow.getMainWindow().setFocus((Control)menus.get(selected));

              return;

						}

            selected=position;

            if (selected!=oldselected)

            {

              Graphics g=createGraphics();

              g.setColor(255,255,255);

              g.fillRect(xpositions[oldselected],1,mwidths[oldselected],height-3);

              g.setColor(0,0,0);

              g.drawText(((Menu)menus.get(oldselected)).name,xpositions[oldselected]+3,2);

              g.fillRect(xpositions[selected],1,mwidths[selected],height-3);

              g.setColor(255,255,255);

              g.drawText(((Menu)menus.get(selected)).name,xpositions[selected]+3,2);

              ((Menu)menus.get(oldselected)).hide();

							((Menu)menus.get(selected)).show(xpositions[selected]);

              oldselected=selected;

            }

          }

          break;

        case PenEvent.PEN_UP:

      }

    }

  }

}