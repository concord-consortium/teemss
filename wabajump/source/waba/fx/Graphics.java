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
    private static int iMWinHandle;
	private static Graphics mainGraphics = null;
    static Graphics drawWinGraphics = null;
    private int iWinHandle = 0;
	private ISurface surface;

    private static Rectangle mwOldClip = new Rectangle();
    private Rectangle oOldClip = new Rectangle();
	private Rectangle clip=null;
	private int tx=0;
	private int ty=0;

	private int drawOp;
	private Font font=waba.ui.MainWindow.defaultFont;

	// color management
	private static final int BLACK=0;
	private static final int WHITE=1;
	private int col=BLACK;
	private static boolean isColor = true;  // set if the device is color
	/* Variable to deal with color management */
    private static RGBColor curColor = new RGBColor(0,0,0,0);
    private byte blackIndex;
    private byte curColorIndex = 0;

	/**
	 * Utility method for filling the parameters of a rectangle.
	 */
	private static Rectangle getRectangle(int x,int y,int width,int height)
	{
		Rectangle oRectangle=new Rectangle();
		oRectangle.topLeft_x=(short)x;
		oRectangle.topLeft_y=(short)y;
		oRectangle.extent_x=(short)width;
		oRectangle.extent_y=(short)height;

		return oRectangle;
	}

	Rectangle copyRectangle(Rectangle orig)
	{
		Rectangle copyRect =new Rectangle();
		copyRect.topLeft_x=orig.topLeft_x;
		copyRect.topLeft_y=orig.topLeft_y;
		copyRect.extent_x=orig.extent_x;
		copyRect.extent_y=orig.extent_y;

		return copyRect;
	}

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
		iWinHandle=surface.getWinHandle();

		setColor(0,0,0);

		/*
		  curColor.r = (byte)0;
		  curColor.g = (byte)0;
		  curColor.b = (byte)0;
		  blackIndex = Palm.WinRGBToIndex(curColor);
		*/
	}

	private static int [] paletteInts = new int [217];
	static int [] getPalette()
	{
		return paletteInts;

	}

    public Graphics(ISurface surface, int cacheSize)
    {
		this(surface);

		CACHE_SIZE = cacheSize;

		colCache = new int [CACHE_SIZE];
		colCacheI = new byte [CACHE_SIZE];
    }


	/**
	 *  This function attempts to reuse freed Graphics objects.  This
	 *  can really speed up the code.  This function is meant to only
	 *  be called by createGraphics.  
	 */
	private static waba.util.Vector savedGraphics = new waba.util.Vector(10);
	public static Graphics getGraphics(ISurface surface)
	{
		Graphics g = null;
		int numSavedGraphics = savedGraphics.getCount();
		if(numSavedGraphics > 0){
			g = (Graphics)savedGraphics.get(numSavedGraphics - 1);
			savedGraphics.del(numSavedGraphics - 1);
			g.translateZero();
			g.surface = surface;
			g.iWinHandle = surface.getWinHandle();
			// These were added at 1.8
			g.numColsCached = 0;
			g.curCol = 0;
			g.col = BLACK;
			g.clip = null;
			g.setColor(0,0,0);
		} else {
			g = new Graphics(surface);
		}
		return g;
	}

	/**
	 * This returns the MainWindow Graphics.  This function should be
	 * used to create the mainWindow Graphics.  This adds protection
	 * from freeing the MainWindow Graphics.  (if your MainWindow
	 * caches the graphics, freeing is bad)
	 */
	public static Graphics getMainGraphics(waba.ui.Window win)
	{
		mainGraphics = new Graphics(win);
		return mainGraphics;
	}


	/**
	 * Clears the current clipping rectangle. This allows drawing to occur
	 * anywhere on the current surface.  However it tries to preserve the
	 * clip that was on the draw window before we changed this clip.
	 */
	public void clearClip()
	{
		//debug("clearClip");
		clip=null;
		Palm.WinSetClip(oOldClip);
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
	public void copyRect(ISurface src, int x, int y,
						 int width, int height, int dstX, int dstY)
	{
		if(this != drawWinGraphics) setDrawWindow();
     
		byte oldColIndex = curColorIndex;
		setColor(0,0,0);

		Rectangle rect=getRectangle(x,y,width,height);
		//here implement drawOp
		//here test to see if dstX and dstY need tx and ty added
		Palm.WinCopyRectangle(src.getWinHandle(), iWinHandle, rect, dstX+tx, dstY+ty, 0);

		curColorIndex = oldColIndex;
		Palm.WinSetForeColor(curColorIndex);

	}


	/**
	 * Frees any system resources (native device contexts) associated with the
	 * graphics object. After calling this function, the graphics object can no
	 * longer be used to draw. Calling this method is not required since any
	 * system resources allocated will be freed when the object is garbage
	 * collected. However, if a program uses many graphics objects, free()
	 * should be called whenever one is no longer needed to prevent allocating
	 * too many system resources before garbage collection can occur.<br>
	 * Also this can speed up the function Control.createGraphics().  
	 * createGraphics reuses these freed Graphics objects.
	 */
    public void free() {//should this free an associated image?
		// Don't free the mainWindow graphics
		if(this == mainGraphics) return;

		if(drawWinGraphics == this){
			clearDrawWindow();
		}

		if(savedGraphics.getCount() < 9){
			int index = savedGraphics.find(this);
			if(index < 0){
				surface = null;
				clip = null;
				savedGraphics.add(this);
			}
		}
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
		drawText(new String(chars,start,count),x,y);
	}

	/** Draws an image at the given x and y coordinates.*/
	public void drawImage(Image image, int x, int y)
	{
		copyRect(image, 0, 0, image.getWidth(), image.getHeight(), x, y);
	}

	static int cursorImage=0;
	static int cursorImageWidth,cursorImageHeight;

	/** Draws a cursor by XORing the given rectangular area on the surface.
     * Since it is XORed, calling the method a second time with the same
     * parameters will erase the cursor.
     */
	public void drawCursor(int x, int y, int width, int height)
	{
		if(this != drawWinGraphics) setDrawWindow();

		int dstX = x+tx;
		int dstY = y+ty;
		// I don't set the color here because what if the user wants the 
		// cursor to be red, blue, green...

		Rectangle rect=getRectangle(0,0,width,height);
		ShortHolder err=new ShortHolder((short)0);
		if (cursorImage==0||cursorImageWidth!=width||cursorImageHeight!=height){
			if (cursorImage!=0)
				Palm.WinDeleteWindow(cursorImage,false);
			cursorImage = Palm.WinCreateOffscreenWindow(width,height,0,err);
			cursorImageWidth=width;
			cursorImageHeight=height;
		}
		if (err.value!=0)
			return;
		int oldDraw=Palm.WinSetDrawWindow(cursorImage);
		Palm.WinDrawRectangle(rect, 0);
		Palm.WinSetDrawWindow(oldDraw);
		Palm.WinCopyRectangle(cursorImage, iWinHandle, rect, dstX, dstY, 3 );
	}

    public static void saveState()
    {
		iMWinHandle =  Palm.WinGetDisplayWindow(); //assuming this is the right window.. could be bad
		Palm.WinGetClip(mwOldClip);

		int r,g,b,i;
		paletteInts[0] = 0xFFFFFF;
		int count = 1;
		for(r = 0; r <=255; r += 51){
			for(g = 0; g <=255; g += 51){
				for(b = 0; b <=255; b += 51){
					paletteInts[count] = count << 24 | r << 16 | g << 8 | b;
					count++;
				}
			}
		}		

		Palm.WinSetDrawWindow(iMWinHandle);
		Palm.WinPalette(RGBColor.winPaletteSet, 0, paletteInts.length, 
						(Object)paletteInts);
    }


    public static void restoreState()
    {
		Palm.WinSetDrawWindow(iMWinHandle);
		Palm.WinSetClip(mwOldClip);
    }

	public static void clearDrawWindow()
	{
		if(drawWinGraphics != null){
			Palm.WinSetClip(drawWinGraphics.oOldClip);
		}
		drawWinGraphics = null;		
	}

	static ISurface getCurSurface()
	{
		if(drawWinGraphics != null){
			return drawWinGraphics.surface;
		} else {
			return null;
		}
	}

    public void setDrawWindow()
    {
		if(drawWinGraphics != null){
			Palm.WinSetClip(drawWinGraphics.oOldClip);
		}
		drawWinGraphics = this;
		Palm.WinSetDrawWindow(iWinHandle);
		Palm.WinGetClip(oOldClip);
		setClip();
		Palm.WinSetForeColor(curColorIndex);
    }

	/**
	 * Draws a line at the given coordinates. The drawing includes both
	 * endpoints of the line.
	 */
	public void drawLine(int x1, int y1, int x2, int y2)
	{
		if(this != drawWinGraphics) setDrawWindow();
		if (col==BLACK)
			Palm.WinDrawLine(x1+tx,y1+ty,x2+tx,y2+ty);
		else
			Palm.WinEraseLine(x1+tx,y1+ty,x2+tx,y2+ty);       
	}
    /*
	  public void drawPath(int points[], int start, int count)
	  {
	  int iOldWinHandle=0;
	  int iWinHandle=0;
	    
	  if(this.surface instanceof waba.fx.Image){
	  iOldWinHandle=iMWinHandle;
	  iWinHandle=this.surface.getWinHandle();
	  Palm.WinSetDrawWindow(iWinHandle);
	  }
	  else{
	  Palm.WinSetDrawWindow(iMWinHandle);
	  iOldWinHandle=0;
	  }
	    
	  if(flgHasClip){
	  Palm.WinGetClip(oOldClip);
	  Rectangle oRectangle=getRectangle(clip.x, clip.y, clip.width, clip.height);
	  Palm.WinClipRectangle(oRectangle);
	  Palm.WinSetClip(oRectangle);
	  }

	  if (count < 2)
	  return;
	  int i = start;
	  for (; i < count - 2; i+=2)
	  Palm.WinDrawLine(points[i]+tx,points[i+1]+ty,points[i+2]+tx,points[i+3]+ty);

	  if(flgHasClip){
	  Palm.WinSetClip(oOldClip);
	  }
	  if(iOldWinHandle!=0){
	  Palm.WinSetDrawWindow(iOldWinHandle);
	  }

	  }
    */
	/**
	 * Draws a rectangle at the given coordinates.
	 */
	public void drawRect(int x, int y, int width, int height)
	{
		// NOTE: only valid for drawing rects with width >=1, height >= 1
		x += tx;
		y += ty;

		int x2 = x + width - 1;
		int y2 = y + height - 1;
		if(this != drawWinGraphics) setDrawWindow();
		if (col==BLACK){
			Palm.WinDrawLine(x, y, x2 - 1, y);
			Palm.WinDrawLine(x2, y, x2, y2 - 1);
			Palm.WinDrawLine(x2, y2, x + 1, y2);
			Palm.WinDrawLine(x, y2, x, y + 1);
		} else {
			Palm.WinEraseLine(x, y, x2 - 1, y);
			Palm.WinEraseLine(x2, y, x2, y2 - 1);
			Palm.WinEraseLine(x2, y2, x + 1, y2);
			Palm.WinEraseLine(x, y2, x, y + 1);
		}
  	}

	/**
	 * Draws a dotted line at the given coordinates. Dotted lines must
	 * be either horizontal or vertical, they can't be drawn at arbitrary angles.
	 */
	public void drawDots(int x1, int y1, int x2, int y2)
	{
		if(this != drawWinGraphics) setDrawWindow();
		x1 += tx;
		y1 += ty;
		x2 += tx;
		y2 += ty;
		if (x1 == x2){
			// vertical
			if (y1 > y2){
				int y = y1;
				y1 = y2;
				y2 = y;
			}

			if (col==BLACK){
				for (; y1 <= y2; y1 += 2){
					Palm.WinDrawLine(x1, y1, x1, y1);
				}
				
			} else {
				for (; y1 <= y2; y1 += 2){
					Palm.WinEraseLine(x1, y1, x1, y1);
				}
			}
		} else if (y1 == y2) {
				// horitzontal
				if (x1 > x2){
					int x = x1;
					x1 = x2;
					x2 = x;
				}
				
			if (col==BLACK){
				for (; x1 <= x2; x1 += 2){
					Palm.WinDrawLine(x1, y1, x1, y1);
				}
			} else {
				for (; x1 <= x2; x1 += 2){
					Palm.WinEraseLine(x1, y1, x1, y1);
				}
			}
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
		if(this != drawWinGraphics) setDrawWindow();
		if (col==BLACK){
			for (; i < count - 1; i++)
				Palm.WinDrawLine(x[i]+tx, y[i]+ty, x[i + 1]+tx, y[i + 1]+ty);
			Palm.WinDrawLine(x[i]+tx, y[i]+ty, x[0]+tx, y[0]+ty);
		} else {
			for (; i < count - 1; i++)
				Palm.WinEraseLine(x[i]+tx, y[i]+ty, x[i + 1]+tx, y[i + 1]+ty);
			Palm.WinEraseLine(x[i]+tx, y[i]+ty, x[0]+tx, y[0]+ty);
		}
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
		drawPolygon(x,y,count);
	}


	/**
	 * Draws text at the given coordinates. The x and y coordinates specify
	 * the upper left hand corner of the text.
	 */
	public void drawText(String s, int x, int y)
	{
		if(this != drawWinGraphics) setDrawWindow();

		// WinDrawChars doesn't seem to like null strings.  I think it can
		// handle 0 length strings, but why push it.
		if(s == null || s.length() == 0) return; 
		//font.select();
		Palm.FntSetFont(this.font.getStyle());

		if (col==BLACK)
			Palm.WinDrawChars(s,s.length(),tx+x,ty+y); //here
		else
			Palm.WinEraseChars(s,s.length(),tx+x,ty+y); //here
	}

	/**
	 * Fills a rectangular area with the current color.
	 */
	public void fillRect(int x, int y, int width, int height)
	{
		if(this != drawWinGraphics) setDrawWindow();
		Rectangle oRect=getRectangle(x+tx,y+ty,width,height);
		if (col==BLACK)
			Palm.WinDrawRectangle(oRect,0);
		else
			Palm.WinEraseRectangle(oRect,0);
	}

	/**
	 * Sets a clipping rectangle. Anything drawn outside of the rectangular
	 * area specified will be clipped. Setting a clip overrides any previous clip.
	 * However setting the clip does not override the OS clip.  (or at least its
	 * not supposed to)
	 */
	public void setClip(int x, int y, int width, int height)
	{
		//      String sDebug="setClip " + x + "," + y + "," + width + "," + height;
		//      debug(sDebug);

		clip= getRectangle(x+tx,y+ty,width,height);
		if(this != drawWinGraphics){
			// This will set the clip and the current draw window
			setDrawWindow();
		} else {
			// The draw window is already set so we only need to reset the clip
			setClip();
		}
	}

	/**
	 * This sets the palm draw window clip to be the intersection of the graphics 
	 * clip and the clip of the drawWindow.  (usually set by PalmOS)
	 *
	 * 1) it sets the clip of the draw window to be what it was the last time
	 *     this graphics was made the draw window
	 * 2) it copies the existing clip of the Graphics 
	 * 3) it uses ClipRectangle to get the instersection of the Graphics clip 
	 *      and the clip of the draw window
	 * 4) it sets the clip of the draw window to this intersection
	 *
	 * The first step probably isn't necessary because before the draw window
	 * changed to another draw window the original clip is installed. (see 
	 * setDrawWindow())
	 * Step two is necessary, incase the clip of the drawWindow is temporarilly
	 * smaller than the clip of the Graphics.  If the drawWindow clip increases
	 * at some point in the future, the "real" Graphics clip needs to be preserved.
	 * I believe step three takes care of cases where the drawWindow is obscured by 
	 * popup windows.  (Beam receive message, appointment alert...)
	 *
	 * I'm not totally sure about any of this, (because I can't look at the source
	 * code for PalmOS), I can only make informed guesses based on the documentation.
	 * If you know one of my guesses is wrong, or you have different guesses 
	 * please let me(scott@concord.org) know.
	 */
    private void setClip()
    {
		if(clip != null){
			Palm.WinSetClip(oOldClip);
			Rectangle clipCopy=copyRectangle(clip);
			Palm.WinClipRectangle(clipCopy);
			Palm.WinSetClip(clipCopy);
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
		//      debug("getClip");
		if(clip==null || r==null){
			return null;
		}
		else{
			r.x = clip.topLeft_x - tx;
			r.y = clip.topLeft_y - ty;
			r.width=clip.extent_x;
			r.height=clip.extent_y;
			return r;
		}
	}

	// variables for color cache management
	// The cache size isn't static because it can be different in different
	// instances of Graphics (see Graphics(surface, int))
    private int CACHE_SIZE = 16; // 16 colors
    private int [] colCache = new int [CACHE_SIZE];
    private byte [] colCacheI = new byte [CACHE_SIZE];
    private int numColsCached = 0;
    private int curCol = 0;

	static byte getColorIndex(int r, int g, int b)
	{
		return (byte)(r/51 * 36 + g/51 * 6 + b/51 + 1);
	}

	/**
	 * Sets the current color for drawing operations.
	 * @param r the red value (0..255)
	 * @param g the green value (0..255)
	 * @param b the blue value (0..255)
	 */

	public void setColor(int r, int g, int b)
	{
		if(this != drawWinGraphics) setDrawWindow();

		if (r==255 && g==255 && b==255){
			col=WHITE;
			return;
		}
		else
			col=BLACK;
       

		if (isColor) { // handles color methods for all other colors
			int i;
       
			/*
			// first get the color and see if it is cached, and use the cached color
			int reqColor = (r << 16) | (g << 8) | b;
			for(i=0; i < numColsCached; i++){
				if(colCache[i] == reqColor){
					Palm.WinSetForeColor(colCacheI[i]);
					curColorIndex = colCacheI[i];
					return;
				}
			}
       

			// new color, set it and cache it
			curColor.r = (byte)r;
			curColor.g = (byte)g;
			curColor.b = (byte)b;
			curColorIndex = Palm.WinRGBToIndex(curColor);
			*/

			curColorIndex = (byte)(r/51 * 36 + g/51 * 6 + b/51 + 1);
			Palm.WinSetForeColor(curColorIndex);
			/*
			// now cache the color
			colCache[curCol] = reqColor;
			colCacheI[curCol] = curColorIndex;
			curCol = (curCol + 1) % CACHE_SIZE;
			if(numColsCached < CACHE_SIZE) numColsCached++;
			*/
		} // handling setting the colors and managing our color cache
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
	public void setDrawOp(int drawOp)
	{
		this.drawOp=drawOp;
	}

	/** Sets the current font for operations that draw text. */
	public void setFont(Font font)
	{
		this.font=font;
	}

	/**
	 * Translates the origin of the current coordinate system by the given
	 * x and y values.
	 */
	public void translate(int x, int y)
	{
		tx+=x;
		ty+=y;
	}

    public void translateZero()
    {
		tx = 0; ty = 0;
    }

	private void debug(String sText)
	{
		String sDisplay=sText;
		Palm.WinDrawChars(sDisplay,sDisplay.length(),80,0);
		waba.sys.Vm.sleep(500);
	}

	/**
	* Return a String with the current translation coordiantes
	*<br>
	* Useful for debugging.
	*
	*/
	public String toString()
	{
		return "Graphics("+tx+","+ty+")"; //here
	}

}
