/*
Copyright (c) 1998, 1999 Wabasoft  All rights reserved.

This software is furnished under a license and may be used only in accordance
with the terms of that license. This software and documentation, and its
copyrights are owned by Wabasoft and are protected by copyright law.

THIS SOFTWARE AND REFERENCE MATERIALS ARE PROVIDED "AS IS" WITHOUT WARRANTY
AS TO THEIR PERFORMANCE, MERCHANTABILITY, FITNESS FOR ANY PARTICULAR PURPOSE,
OR AGAINST INFRINGEMENT. WABASOFT ASSUMES NO RESPONSIBILITY FOR THE USE OR
INABILITY TO USE THIS SOFTWARE. WABASOFT SHALL NOT BE LIABLE FOR INDIRECT,
SPECIAL OR CONSEQUENTIAL DAMAGES RESULTING FROM THE USE OF THIS PRODUCT.

WABASOFT SHALL HAVE NO LIABILITY OR RESPONSIBILITY FOR SOFTWARE ALTERED,
MODIFIED, OR CONVERTED BY YOU OR A THIRD PARTY, DAMAGES RESULTING FROM
ACCIDENT, ABUSE OR MISAPPLICATION, OR FOR PROBLEMS DUE TO THE MALFUNCTION OF
YOUR EQUIPMENT OR SOFTWARE NOT SUPPLIED BY WABASOFT.
*/

package waba.fx;

import palmos.*;

/**
 * FontMetrics computes font metrics including string width and height.
 * <p>
 * FontMetrics are usually used to obtain information about the widths and
 * heights of characters and strings when drawing text on a surface.
 * A FontMetrics object references a font and surface since fonts may have
 * different metrics on different surfaces.
 * <p>
 * Here is an example that uses FontMetrics to get the width of a string:
 *
 * <pre>
 * ...
 * Font font = new Font("Helvetica", Font.BOLD, 10);
 * FontMetrics fm = getFontMetrics();
 * String s = "This is a line of text.";
 * int stringWidth = fm.getTextWidth(s);
 * ...
 * </pre>
 */

public class FontMetrics
{
Font font;
ISurface surface;
int ascent;
int descent;
int leading;

/**
 * Constructs a font metrics object referencing the given font and surface.
 * <p>
 * If you are trying to create a font metrics object in a Control subclass,
 * use the getFontMetrics() method in the Control class.
 * @see waba.ui.Control#getFontMetrics(waba.fx.Font font)
 */
  public FontMetrics(Font font, ISurface surface)
   {
    try{
      this.font = font;
      this.surface = surface;
      int lastFont=Palm.FntSetFont(font.getStyle());
      ascent = Palm.FntBaseLine();
      descent = Palm.FntDescenderHeight();
      leading = Palm.FntLineHeight() - ascent - descent;
      Palm.FntSetFont(lastFont);
    }catch(Exception e){
      //temp
      Palm.WinDrawChars(e.toString(),e.toString().length(),0,100);
	  ascent = descent = leading = 0;
    }
	}

/**
 * Returns the ascent of the font. This is the distance from the baseline
 * of a character to its top.
 */
public int getAscent()
	{
	return ascent;
	}

/**
 * Returns the width of the given character in pixels.
 */
public int getCharWidth(char c)
{
  int lastFont=Palm.FntSetFont(font.getStyle());
  int width=Palm.FntLineWidth(c + "", 1);
  Palm.FntSetFont(lastFont);
  return width;
}

/**
 * Returns the descent of a font. This is the distance from the baseline
 * of a character to the bottom of the character.
 */
public int getDescent()
	{
	return descent;
	}

/**
 * Returns the height of the referenced font. This is equal to the font's
 * ascent plus its descent. This does not include leading (the space between lines).
 */
public int getHeight()
	{
	return ascent + descent;
	}

/**
 * Returns the external leading which is the space between lines.
 */
public int getLeading()
	{
	return leading;
	}

/**
 * Returns the width of the given text string in pixels.
 */
public int getTextWidth(String s)
{
	if(s.length() == 0) return 0;

  int lastFont=Palm.FntSetFont(font.getStyle());
  int width=Palm.FntLineWidth(s,s.length());
  Palm.FntSetFont(lastFont);
  return width;
}

/**
 * Returns the width of the given text in pixels.
 * @param chars the text character array
 * @param start the start position in array
 * @param count the number of characters
 */
public int getTextWidth(char chars[], int start, int count)
{
  String s=new String(chars,start,count);
  return getTextWidth(s);
}
}
