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

package org.concord.waba.extra.ui;

import waba.ui.*;
import waba.fx.*;
import waba.util.*;

/**
 * This is a standard palm pushbutton.
 * @author     <A HREF="mailto:kellner@i-clue.de">Stefan Kellner</A>,
 * @author     <A HREF="mailto:rnielsen@cygnus.uwa.edu.au">Rob Nielsen</A>,
 * @version    1.1.0 5 November 1999
 */

public class ToggleButton extends Control 
{
	/** the name to be displayed on this button */
	String [] nameLines = null;

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
		if(name != null){
			char [] nameChars = name.toCharArray();
			int lineStartPos = 0;
			Vector lines = new Vector();
			for(int i=0; i<nameChars.length; i++){
				if(nameChars[i] == '|'){
					lines.add(new String(nameChars, lineStartPos, i - lineStartPos));
					lineStartPos = i+1;
				}
			}
			if(lineStartPos < nameChars.length){
				lines.add(new String(nameChars, lineStartPos, nameChars.length - lineStartPos));
			}	
			nameLines = new String [lines.getCount()];
			for(int i=0; i<lines.getCount(); i++){
				nameLines[i] = (String)lines.get(i);
			}
		}
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
		return nameLines[0];
	}
  
	public int getPreferredWidth(FontMetrics fm)
	{
		int maxWidth = 0;
		for(int i=0; i<nameLines.length; i++){
			int curWidth = fm.getTextWidth(nameLines[i])+6;
			if(curWidth > maxWidth){
				maxWidth = curWidth;
			}
		}
				
		return maxWidth;
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

		int textHeight = fm.getHeight();
		int textY = (height - textHeight*nameLines.length) / 2;
		for(int i=0; i<nameLines.length; i++){
			int textX = (width - fm.getTextWidth(nameLines[i])) / 2;
			g.drawText(nameLines[i], textX, textY);
			textY += textHeight;
		}
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
