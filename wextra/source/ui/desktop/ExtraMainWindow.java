/*****************************************************************************

 *                                Waba Extras

 *

 * Version History

 * Date                Version  Programmer

 * ----------  -------  -------  ------------------------------------------

 * 25/04/1999  New      1.0.0    Rob Nielsen

 * Class created

 *

 ****************************************************************************/



package org.concord.waba.extra.ui;



import waba.ui.*;

import waba.fx.*;

import waba.sys.Vm;

import extra.ui.*;



/**

 * A simple extension to MainWindow which adds a title if desired plus

 * the functionality of RelativeContainer.  At the moment you need to extend

 * this class for your main app to use the extra.ui.List control but hopefully that

 * won't always be the case.

 *

 * @author     <A HREF="mailto:rnielsen@cygnus.uwa.edu.au">Rob Nielsen</A>,

 * @version    1.0.0 25 April 1999

 */

public abstract class ExtraMainWindow extends MainWindow implements java.awt.event.ActionListener

{

  // platform constants





  /** Java based platform */

  public static final int JAVA=1;







  // positioning constants



  public static final int LEFT=RelativeContainer.LEFT;

  public static final int LEFTOF=RelativeContainer.LEFTOF;

  public static final int CENTER=RelativeContainer.CENTER;

  public static final int RIGHTOF=RelativeContainer.RIGHTOF;

  public static final int RIGHT=RelativeContainer.RIGHT;

  public static final int SAME=RelativeContainer.SAME;

  public static final int SAME_LEFT=RelativeContainer.SAME_LEFT;

  public static final int SAME_RIGHT=RelativeContainer.SAME_RIGHT;

  public static final int TOP=RelativeContainer.TOP;

  public static final int ABOVE=RelativeContainer.ABOVE;

  public static final int BELOW=RelativeContainer.BELOW;

  public static final int BOTTOM=RelativeContainer.BOTTOM;

  public static final int SAME_TOP=RelativeContainer.SAME_TOP;

  public static final int SAME_BOTTOM=RelativeContainer.SAME_BOTTOM;



  public static final int AUTO=RelativeContainer.AUTO;

  public static final int FILL=RelativeContainer.FILL;

  public static final int REST=RelativeContainer.REST;



  /** the platform we are running on (from Vm.getPlatform()) */

  public static int platform = JAVA;



  /** the container to pass all add and remove calls on to */

  protected RelativeContainer content;





	/** the menubar, if any */

	private boolean awtMenubarWasInserted = false;



  /** should this application be double buffered? */

  protected boolean doubleBuffered;



  // double buffering fields



  /** the buffered image */

  protected Image bufIm;



  /** a graphics context for the buffer image */

  protected Graphics ig;



  /** a graphics context for the screen */

  protected Graphics sg;



	protected Control newFocus;



  protected boolean needsPaint=false;

  protected int paintX, paintY, paintWidth, paintHeight;





  /**

   * Constructs a new application without a title and no double buffering

   */

  public ExtraMainWindow()

  {

    this(null,false);

  }



  /**

   * Constructs a new application without a title and the given double buffering

   * @param doubleBuffered true if an image buffer should be used, false otherwise.

   */

  public ExtraMainWindow(boolean doubleBuffered)

  {

    this(null,doubleBuffered);

  }



  /**

   * Constructs a new application with the given title and no double buffering.

   */

  public ExtraMainWindow(String name)

  {

    this(name,false);

  }



  /**

   * Constructs a new application with a standard pilot title bar and the given

   * double buffering options.

   * @param title the name of the app

   * @param doubleBuffered true if an image buffer should be used, false otherwise.

   */

  public ExtraMainWindow(String name,boolean doubleBuffered)

  {

    this.doubleBuffered=doubleBuffered;

    if (platform==JAVA) setRect(0,0,260,260);

    super.add(content=new RelativeContainer());

    content.setRect(x,y,width,height);

  }





	/**

	 * Sets focus to the given control. When a user types a key, the control with

	 * focus get the key event. At any given time, only one control in a window

	 * can have focus. Calling this method will cause a FOCUS_OUT control event

	 * to be posted to the window's current focus control (if one exists)

	 * and will cause a FOCUS_IN control event to be posted to the new focus

	 * control.

	 */

	public void setFocus(Control c)

	{

	  newFocus=c;

		super.setFocus(c);

	}



  /**

   * Adds a damage rectangle to the current list of areas that need

   * repainting.

   */

  protected void damageRect(int x, int y, int width, int height)

  {

    super.damageRect(x,y,width,height);



    if (!doubleBuffered)

      return;

    if (needsPaint)

      {

      int ax = x + width;

      int ay = y + height;

      int bx = paintX + paintWidth;

      int by = paintY + paintHeight;

      if (paintX < x)

        x = paintX;

      if (paintY < y)

        y = paintY;

      if (ax > bx)

        width = ax - x;

      else

        width = bx - x;

      if (ay > by)

        height = ay - y;

      else

        height = by - y;

      }

    paintX = x;

    paintY = y;

    paintWidth = width;

    paintHeight = height;

    needsPaint = true;

  }



  /**

   * Called by the VM to post key and pen events. This method is not private

   * to prevent the compiler from removing it during optimization.

   */

  public void _postEvent(int type, int key, int x, int y, int modifiers, int timeStamp)

  {

    super._postEvent(type,key,x,y,modifiers,timeStamp);

    if (doubleBuffered&&needsPaint)

      _doPaint(paintX, paintY, paintWidth, paintHeight);

  }



  /**

   * Called by the VM to repaint an area. This method is not private

   * to prevent the compiler from removing it during optimization.

   */

  public void _doPaint(int x, int y, int width, int height)

  {

    if (doubleBuffered&&!needsPaint)

      return;

    if (!doubleBuffered)

    {

      super._doPaint(x,y,width,height);

      return;

    }

    if (ig == null||width>bufIm.getWidth()||height>bufIm.getHeight())

    {

      bufIm=new Image(width,height);

      ig = new Graphics(bufIm);

      sg = new Graphics(this);

    }

    // clear background

    ig.setClip(x, y, width, height);

    if (Vm.isColor())

      ig.setColor(200, 200, 200);

    else

      ig.setColor(255, 255, 255);

    ig.fillRect(x, y, width, height);

    onPaint(ig);

    ig.clearClip();

    paintChildren(ig, x, y, width, height);



    if (needsPaint)

    {

      int ax = x + width;

      int ay = y + height;

      int bx = paintX + paintWidth;

      int by = paintY + paintHeight;

      if (x <= paintX && y <= paintY && ax >= bx && ay >= by)

        needsPaint = false;

    }

    if (doubleBuffered)

      sg.copyRect(bufIm,x,y,width,height,x,y);

  }



  /** Returns the child located at the given x and y coordinates. */

  public Control findChild(int x, int y)

  {

    return content.findChild(x, y);

  }





  /**

   * Sets the standard gap between components when added with the LEFTOF, RIGHTOF, ABOVE

   * and BELOW settings.

   * @param x the x gap in pixels

   * @param y the y gap in pixels

   */

  public void setGaps(int x,int y)

  {

    content.setGaps(x,y);

  }

	public void setMenuBar(MenuBar menubar)

	{

		if(!awtMenubarWasInserted){

			java.awt.Frame awtFrame = (java.awt.Frame)waba.applet.Applet.currentApplet.getParent();

			if(awtFrame != null){

				awtFrame.setMenuBar(menubar.getAWTMenuBar());

				awtMenubarWasInserted = true;

			}

		}

	}



	public void add(PopupMenu pMenu){

		waba.applet.Applet.currentApplet.add(pMenu.getAWTPopupMenu());

	}





  /**

   * Adds a component to this application.  See RelativeContainer for details

   * @see extra.ui.RelativeContainer#add(Control,int,int,int,int,Control)

   */

  public void add(Control control,int x,int y)

  {

    content.add(control,x,y);

  }



  /**

   * Adds a component to this application.  See RelativeContainer for details

   * @see extra.ui.RelativeContainer#add(Control,int,int,int,int,Control)

   */

  public void add(Control control,int x,int y,Control relative)

  {

    content.add(control,x,y,relative);

  }



  /**

   * Adds a component to this application.  See RelativeContainer for details

   * @see extra.ui.RelativeContainer#add(Control,int,int,int,int,Control)

   */

  public void add(Control control,int x,int y,int width,int height)

  {

    content.add(control,x,y,width,height);

  }



  /**

   * Adds a component to this application.  See RelativeContainer for details

   * @see extra.ui.RelativeContainer#add(Control,int,int,int,int,Control)

   */

  public void add(Control control,int x,int y,int width,int height,Control relative)

  {

    content.add(control,x,y,width,height,relative);

  }



  /**

   * Adds a component to this application.  See RelativeContainer for details

   * @see extra.ui.RelativeContainer#add(Control,int,int,int,int,Control)

   */

  public void add(Control control)

  {

    content.add(control);

  }



  /**

   * Temporarily removes a component to this application.  See RelativeContainer for details

   * @see extra.ui.RelativeContainer#add(Control,int,int,int,int,Control)

   */

  public void remove(Control control)

  {

    content.remove(control);

  }



  /**

   * Permanently removes a component to this application.  See RelativeContainer for details

   * @see extra.ui.RelativeContainer#add(Control,int,int,int,int,Control)

   */

  public void removePermanently(Control control)

  {

    content.removePermanently(control);

  }



  /**

   * Sets the rectangle of this application.

   */

  public void setRect(int x,int y,int width,int height)

  {

    super.setRect(x,y,width,height);

    if (content!=null)

      content.setRect(x,y,width,height);

  }

}



