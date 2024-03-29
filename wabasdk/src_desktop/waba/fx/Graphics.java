/*

Copyright (c) 1998, 1999, 2000 Wabasoft  All rights reserved.



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



import waba.ui.Window;

import waba.applet.Applet;



/**

 * Graphics draws on a surface.

 * <p>

 * Surfaces are objects that implement the ISurface interface.

 * MainWindow and Image are both examples of surfaces.

 * <p>

 * Here is an example that uses Graphics to draw a line:

 *

 * <pre>

 * public class MyProgram extends MainWindow

 * {

 * public void onPaint(Graphics g)

 *  {

 *  g.setColor(0, 0, 255);

 *  g.drawLine(0, 0, 10, 10);

 * ...

 * </pre>

 */



public class Graphics

{

private ISurface surface;



private java.awt.Graphics _g;

private int _fontAscent;

private boolean _xorDrawMode;

private int _transX = 0, _transY = 0;



/**

 * The constant for a draw operation that draws the source over

 * the destination.

 */

public static final int DRAW_OVER = 1;



/**

 * The constant for a draw operation that AND's the source with the

 * destination. Commonly used to create image masks.

 */

public static final int DRAW_AND = 2;



/**

 * The constant for a draw operation that OR's the source with the

 * destination. Commonly used with image masks.

 */

public static final int DRAW_OR = 3;



/**

 * The constant for a draw operation that XOR's the source with the

 * destination.

 */

public static final int DRAW_XOR = 4;



/**

 * Constructs a graphics object which can be used to draw on the given

 * surface. For the sake of the methods in this class, the given surface

 * is known as the "current surface".

 * <p>

 * If you are trying to create a graphics object for drawing in a subclass

 * of control, use the createGraphics() method in the Control class. It

 * creates a graphics object and translated the origin to the origin of

 * the control.

 */

public Graphics(ISurface surface)

	{

	this.surface = surface;

	_g = createAWTGraphics();

	_g.setColor(java.awt.Color.black);

	setFont(new Font("Helvetica", Font.PLAIN, 12));

	}



private java.awt.Graphics createAWTGraphics()

	{

	java.awt.Graphics g;



	if (surface instanceof Window)

		{

		Window win = (Window)surface;

		g = win.createAWTGraphics();

		}

	else // _surface instanceof Image

		{

		Image image = (Image)surface;

		g = image.getAWTImage().getGraphics();

		}

	return g;

	}





/**

 * Clears the current clipping rectangle. This allows drawing to occur

 * anywhere on the current surface.

 */



public void clearClip()

	{

// NOTE: we had the following code in for the JDK 1.1, however, the

// JDK 1.1 is buggy and clearing a clip rectangle in this way seems

// to leave the graphics context messed up.

//

//	try

//		{

//		_g.translate(- _transX, - _transY);

//		_g.setClip(-64000, -64000, 64000, 64000);

//		_g.translate(_transX, _transY);

//		}

//	catch (NoSuchMethodError e)

//		{

		// JDK 1.02 - need to create a copy since there isn't

		// a way to clear the clip rect

		java.awt.Graphics newG = createAWTGraphics();

		newG.setFont(_g.getFont());

		newG.setColor(_g.getColor());

		newG.translate(_transX, _transY);

		if (_xorDrawMode)

			newG.setXORMode(java.awt.Color.white);

		_g.dispose();

		_g = newG;

//		}

	}



/**

 * Copies a rectangular area from a surface to the given coordinates on

 * the current surface. The copy operation is performed by combining

 * pixels according to the setting of the current drawing operation.

 * The same surface can be used as a source and destination to

 * implement quick scrolling.

 * <p>

 * Not all combinations of surfaces are supported on all platforms.

 * PalmOS has problems copying from an Window surface to an Image and

 * between two Image surfaces. Java doesn't allow copying from an

 * Window surface to an Image.

 *

 * @param surface the surface to copy from

 * @param x the source x coordinate

 * @param y the source y coordinate

 * @param width the width of the area on the source surface

 * @param height the height of the area on the source surface

 * @param dstX the destination x location on the current surface

 * @param dstY the destination y location on the current surface

 * @see #setDrawOp

 */



static boolean copyAppErrDisplayed = false;

public void copyRect(ISurface surface, int x, int y,

	int width, int height, int dstX, int dstY)

	{

	if (surface instanceof Window)

		{

		if (this.surface != surface)

			{

			if (!copyAppErrDisplayed)

				{

				System.out.println("WARNING: Copying from apps isn't " +

					"supported under non-native WabaVMs");

				copyAppErrDisplayed = true;

				}

			}

		else

			_g.copyArea(x, y, width, height, dstX - x, dstY - y);

		}

	else if (surface instanceof Image)

		{

		Image srcImage = (Image)surface;

		try

			{

			// JDK 1.1

			_g.drawImage(srcImage.getAWTImage(),

				dstX, dstY, dstX + width, dstY + height,

				x, y, x + width, y + height,

				null);

			}

		catch (NoSuchMethodError e)

			{

			// JDK 1.02

			java.awt.Rectangle r = _g.getClipRect();

			setClip(dstX, dstY, dstX + width, dstY + height);

			_g.drawImage(srcImage.getAWTImage(), dstX - x, dstY - y, null);

			setClip(r.x, r.y, r.width, r.height);

			}

		}

	}



/**

 * Frees any system resources (native device contexts) associated with the

 * graphics object. After calling this function, the graphics object can no

 * longer be used to draw. Calling this method is not required since any

 * system resources allocated will be freed when the object is garbage

 * collected. However, if a program uses many graphics objects, free()

 * should be called whenever one is no longer needed to prevent allocating

 * too many system resources before garbage collection can occur.

 */



public void free()

	{

	_g.dispose();

	}



/**

 * Draws text at the given coordinates. The x and y coordinates specify

 * the upper left hand corner of the text's bounding box.

 * @param chars the character array to display

 * @param start the start position in array

 * @param count the number of characters to display

 * @param x the left coordinate of the text's bounding box

 * @param y the top coordinate of the text's bounding box

 */



public void drawText(char chars[], int start, int count, int x, int y)

	{

	_g.drawChars(chars, start, count, x, y + _fontAscent);

	}



/** Draws an image at the given x and y coordinates.*/

public void drawImage(Image image, int x, int y)

	{

	copyRect(image, 0, 0, image.getWidth(), image.getHeight(), x, y);

	}



/** Draws a cursor by XORing the given rectangular area on the surface.

  * Since it is XORed, calling the method a second time with the same

  * parameters will erase the cursor.

  */



public void drawCursor(int x, int y, int width, int height)

	{

	// save current color (xor mode already saved)

	java.awt.Color c = _g.getColor();



	// set current color, XOR drawOp and fill rect

	_g.setColor(java.awt.Color.black);

	_g.setXORMode(java.awt.Color.white);

	_g.fillRect(x, y, width, height);



	// restore XOR drawOp and color

	if (!_xorDrawMode)

		_g.setPaintMode();

	_g.setColor(c);

	}



/**

 * Draws a line at the given coordinates. The drawing includes both

 * endpoints of the line.

 */



public void drawLine(int x1, int y1, int x2, int y2)

	{

	_g.drawLine(x1, y1, x2, y2);

	}



/**

 * Draws a rectangle at the given coordinates.

 */

public void drawRect(int x, int y, int width, int height)

	{

	// NOTE: only valid for drawing rects with width >=1, height >= 1

	int x2 = x + width - 1;

	int y2 = y + height - 1;

	drawLine(x, y, x2 - 1, y);

	drawLine(x2, y, x2, y2 - 1);

	drawLine(x2, y2, x + 1, y2);

	drawLine(x, y2, x, y + 1);

	}



/**

 * Draws a dotted line at the given coordinates. Dotted lines must

 * be either horizontal or vertical, they can't be drawn at arbitrary angles.

 */



public void drawDots(int x1, int y1, int x2, int y2)

	{

	if (x1 == x2) // vertical

		{

		if (y1 > y2)

			{

			int y = y1;

			y1 = y2;

			y2 = y;

			}

		for (; y1 < y2; y1 += 2)

			_g.drawLine(x1, y1, x1, y1);

		}

	else if (y1 == y2) // horizontal

		{

		if (x1 > x2)

			{

			int x = x1;

			x1 = x2;

			x2 = x;

			}

		for (; x1 < x2; x1 += 2)

			_g.drawLine(x1, y1, x1, y1);

		}

	}



/**

 * Draws the outline of a polygon with the given coordinates.

 * The polygon is automatically closed, you should not duplicate

 * the start point to close the polygon.

 * @param x x vertex coordinates

 * @param y y vertex coordinates

 * @param count number of vertices

 */

public void drawPolygon(int x[], int y[], int count)

	{

	if (count < 3)

		return;

	int i = 0;

	for (; i < count - 1; i++)

		drawLine(x[i], y[i], x[i + 1], y[i + 1]);

	drawLine(x[i], y[i], x[0], y[0]);

	}



/**

 * Draws a filled polygon with the given coordinates.

 * The polygon is automatically closed, you should not duplicate

 * the start point to close the polygon. The polygon is filled

 * according to Jordan's rule - a point is inside if a horizontal

 * line to the point intersects the polygon an odd number of times.

 * This function is not implemented for the PalmOS VM. Under PalmOS only

 * the outline of the polygon is drawn.

 * @param x x vertex coordinates

 * @param y y vertex coordinates

 * @param count number of vertices

 */



public void fillPolygon(int x[], int y[], int count)

	{

	_g.fillPolygon(x, y, count);

	}



/**

 * Draws text at the given coordinates. The x and y coordinates specify

 * the upper left hand corner of the text.

 */



public void drawText(String s, int x, int y)

	{

	_g.drawString(s, x, y + _fontAscent);

	}



/**

 * Fills a rectangular area with the current color.

 */



public void fillRect(int x, int y, int width, int height)

	{

	_g.fillRect(x, y, width, height);

	}



/**

 * Sets a clipping rectangle. Anything drawn outside of the rectangular

 * area specified will be clipped. Setting a clip overrides any previous clip.

 */



public void setClip(int x, int y, int width, int height)

	{

	try

		{

		// JDK 1.1

		_g.setClip(x, y, width, height);

		}

	catch (NoSuchMethodError e)

		{

		// JDK 1.02

		clearClip();

		_g.clipRect(x, y, width, height);

		}

	}



/**

 * Sets the x, y, width and height coordinates in the rectangle passed

 * to the current clip coordinates. To reduce the use of temporary objects

 * during drawing, this method does not allocate its own rectangle

 * object. If there is no current clip, null will be returned and

 * the rectangle passed will remain unchanged. Upon success, the

 * rectangle passed to the method will be returned.

 */



public Rect getClip(Rect r)

	{

	if (r == null)

		return null;

	java.awt.Rectangle awtRect;

	try

		{

		// JDK 1.1

		awtRect = _g.getClipBounds();

		}

	catch (NoSuchMethodError e)

		{

		// JDK 1.02

		awtRect = _g.getClipRect();

		}

	if (awtRect == null)

		return null;

	r.x = awtRect.x;

	r.y = awtRect.y;

	r.width = awtRect.width;

	r.height = awtRect.height;

	return r;

	}



/**

 * Sets the current color for drawing operations.

 * @param r the red value (0..255)

 * @param g the green value (0..255)

 * @param b the blue value (0..255)

 */



public void setColor(int r, int g, int b)

	{

	_g.setColor(new java.awt.Color(r, g, b));

	}



/**

 * Sets the drawing operation. The setting determines the raster

 * operation to use when drawing lines, rectangles, text and

 * images on the current surface. It also determines how pixels are

 * combined when copying one surface to another. The setting of

 * DRAW_OVER, where any drawing simply draws over the pixels on

 * a surface, is the default.

 * <p>

 * Not all operations are supported on all platforms. When used with

 * Java, DRAW_OVER is supported for all types of drawing and DRAW_XOR

 * is supported for drawing lines, rectangles, text and images.

 * However, DRAW_XOR is not supported when copying surface areas and

 * the DRAW_AND and DRAW_OR operations aren't supported at all under

 * Java.

 * <p>

 * PalmOS platforms supports all the drawing operations when drawing

 * images and copying surface regions. However, only the DRAW_OVER

 * operation is supported when drawing lines, rectangles and text.

 * If you need to use the XOR drawing operation for drawing lines

 * under PalmOS, you can draw the line into an image and then draw

 * the image with an XOR drawing operation.

 * <p>

 * Win32 and Windows CE platforms support all the drawing operations

 * except when drawing text. Only DRAW_OVER is supported when drawing

 * text. If you need to draw XOR'ed text, you can draw the text into

 * an image and then draw the image with an XOR draw operation.

 * <p>

 * When calculating the result of XOR, AND and OR drawing, the value

 * of the color black is all 1's (fully set) in binary and white is

 * all 0's (fully unset).

 *

 * @param op drawing operation

 * @see #DRAW_OVER

 * @see #DRAW_AND

 * @see #DRAW_OR

 * @see #DRAW_XOR

 */



static boolean drawErrDisplayed = false;

public void setDrawOp(int drawOp)

	{

	if (drawOp != DRAW_XOR)

		{

		_g.setPaintMode();

		_xorDrawMode = false;

		}

	else

		{

		_g.setXORMode(java.awt.Color.white);

		_xorDrawMode = true;

		}

	if (drawOp != DRAW_XOR && drawOp != DRAW_OVER && !drawErrDisplayed)

		{

		System.out.println("NOTICE: DRAW_AND and DRAW_OR aren't supported under Java");

		drawErrDisplayed = true;

		}

	}



/** Sets the current font for operations that draw text. */



public void setFont(Font font)

	{

	java.awt.Font awtFont = font.getAWTFont();

	_g.setFont(awtFont);

	java.awt.FontMetrics fm;

	fm = java.awt.Toolkit.getDefaultToolkit().getFontMetrics(font.getAWTFont());

	_fontAscent = fm.getAscent();

 	}



/**

 * Translates the origin of the current coordinate system by the given

 * x and y values.

 */



public void translate(int x, int y)

	{

	_g.translate(x, y);

	_transX += x;

	_transY += y;

	}

}

