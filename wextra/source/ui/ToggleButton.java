/*****************************************************************************
 *                                Waba Extras
 *
 * Version History
 * Date                 Version  Programmer
 * ----------  -------  -------  ------------------------------------------
 * 25/03/1999  New      1.0.0    Stefan Kellner
 * Class created
 *
 * 19/10/1999  New      1.0.1    Rob Nielsen
 * Added checks in onEvent to see if pushgroup is null
 *
 * 05/11/1999  New      1.1.0    Rob Nielsen
 * Rewrote to remove dependence on PushbuttonGroup.
 *
 ****************************************************************************/

package extra.ui;

import waba.ui.*;
import waba.fx.*;

/**
 * This is a standard palm pushbutton.
 * @author     <A HREF="mailto:kellner@i-clue.de">Stefan Kellner</A>,
 * @author     <A HREF="mailto:rnielsen@cygnus.uwa.edu.au">Rob Nielsen</A>,
 * @version    1.1.0 5 November 1999
 */

public class ToggleButton extends Control 
{
  /** the name to be displayed on this button */
  String name;

  /** is this button selected? */
  private boolean selected=false;

  /**
   * Constructs a new unconnected, unselected push button.
   * @param name the text to go on the button
   */
  public ToggleButton(String name) 
  {
    this(name,false);
  }

  /**
   * Construct a new unconnected push button with the given
   * selection status.
   * @param name the text to go on the button
   * @param state the state of the button
   */
  public ToggleButton(String name, boolean state) 
  {
    this.name=name;
    setSelected(state);
  }

  /**
   * Is this pushbutton selected?
   * @return true if it is, false otherwise
   */
  public boolean isSelected()
  {
    return selected;
  }

  /**
   * Sets whether the push button is pressed down or not
   * @param b true if selected, false otherwise
   */
  public void setSelected(boolean b)
  {
	selected = b;
  }

  /**
   * Returns the text of this Button
   */
  public String getText()
  {
    return name;
  }
  
  public int getPreferredWidth(FontMetrics fm)
  {
    return fm.getTextWidth(name)+6;
  }
  
  public int getPreferredHeight(FontMetrics fm)
  {
    return fm.getHeight()+3;
  }
  
  /**
   * Paints the push button to the screen
   */
  public void onPaint(Graphics g)
  {
    FontMetrics fm=getFontMetrics(MainWindow.defaultFont);
    int textX = (width - fm.getTextWidth(name)) / 2;
    int textY = (height - fm.getHeight()) / 2;

    if (selected)
    {
      // make pressed or active button
      g.setColor(0,0,0);
      g.fillRect(0,0,width,height);
      g.setColor(255,255,255);
    }
    else
    {
      // make normal button
      g.setColor(0, 0, 0);
      g.drawRect(0,0,width,height);
    }
    g.drawText(name, textX, textY);
  }

  /**
   * Process pen and key events to this component
   * @param event the event to process
   */
  public void onEvent(Event event)
  {
    if (event instanceof PenEvent)
    {
      int px=((PenEvent)event).x;
      int py=((PenEvent)event).y;
      switch (event.type)
      {
        case PenEvent.PEN_DOWN:          
            selected=!selected;
            repaint();
	    break;
        case PenEvent.PEN_UP:
          if (px>=0&&px<width&&py>=0&&py<height)
          {
              postEvent(new ControlEvent(ControlEvent.PRESSED,this));
          }
          else
          {
	      selected=!selected;
	      repaint();
          }
          break;
      }
    }
  }
}
