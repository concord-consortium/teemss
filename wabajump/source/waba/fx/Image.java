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
import palmos.Palm;
import palmos.ShortHolder;
import palmos.RGBColor;

/**
 * Image is a rectangular image.
 * <p>
 * You can draw into an image and copy an image to a surface using a Graphics
 * object.
 * @see Graphics
 */
public class Image implements ISurface
{
	int width=0;
	int height=0;
	int iResID=0;
	int winHandle=-1;

	/**
	 * Creates an image of the specified width and height. The image has
	 * a color depth (number of bitplanes) and color map that matches the
	 * default drawing surface.
	 */
	public Image(int width, int height)
	{
		this.width = width;
		this.height = height;
		winHandle=Palm.WinCreateOffscreenWindow(width,height,0,new ShortHolder((short)0));
		if(winHandle == 0) { 
			// There is no more image memory
			width = -1;
			height = -1;
			winHandle = -1;
		}
		int iOldWinHandle=Palm.WinSetDrawWindow(winHandle);
		Palm.WinPalette(RGBColor.winPaletteSet, 0, Graphics.getPalette().length, 
						(Object)Graphics.getPalette());
		Palm.WinSetDrawWindow(iOldWinHandle);
	}

    public Image(String dummy)
    {
		this(10,10);	
    }

	public Image(int iNewResID, int width, int height)
	{
		iResID=iNewResID;

		//for the moment, I don't know how to get the height and width of the image
		this.width=width;
		this.height=height;
		winHandle=Palm.WinCreateOffscreenWindow(width,height,0,new ShortHolder((short)0));

		int iOldWinHandle=Palm.WinSetDrawWindow(winHandle);

		int Tbmp=0x54626D70;
		int iHandle=Palm.DmGet1Resource(Tbmp,iResID);
		if(iHandle!=0){
			int iPtr=Palm.MemHandleLock(iHandle);
			Palm.WinDrawBitmap(iPtr, 0, 0);
			Palm.MemHandleUnlock(iHandle);
		}
		Palm.DmReleaseResource(iHandle);

		Palm.WinPalette(RGBColor.winPaletteSet, 0, Graphics.getPalette().length, 
						(Object)Graphics.getPalette());

		Palm.WinSetDrawWindow(iOldWinHandle);
	}

	/*public int getResourceID()
	  {
	  return iResID;
	  } */

	public int getWinHandle()
	{
		//At some point, I am going to make this use an offscreen window
		return winHandle;
	}


	//private native void _nativeCreate();

	/**
	 * Loads and constructs an image from a file. The path given is the path to the
	 * image file. The file must be in 2, 16 or 256 color uncompressed BMP bitmap
	 * format. If the image cannot be loaded, the image constructed will have
	 * a width and height of 0.
	 */
	/*public Image(String path)
	  {
	  _nativeLoad(path);
	  }

	  private native void _nativeLoad(String path);*/


	/**
	 * Sets one or more row(s) of pixels in an image. This method sets the values of
	 * a number of pixel rows in an image and is commonly used when writing code
	 * to load an image from a stream such as a file. The source pixels byte array
	 * must be in 1, 4 or 8 bit per pixel format with a matching color map size
	 * of 2, 16 or 256 colors.
	 * <p>
	 * Each color in the color map of the source pixels is identified by a single
	 * integer value. The integer is composed of 8 bits (value [0..255]) of red,
	 * green and blue using the following calculation:
	 * <pre>
	 * int color = (red << 16) | (green << 8) | blue;
	 * </pre>
	 * As an example, to load a 16 color image, we would pass bitsPerPixel
	 * as 4 and would create a int array of 16 values for the color map.
	 * Then we would set each of the values in the color map to the colors
	 * used using the equation above. We could then either read data line
	 * by line from the source stream, calling this method for each row of
	 * pixels or could read a number of rows at once and then call this
	 * method to set the pixels.
	 * <p>
	 * The former approach uses less memory, the latter approach is faster.
	 *
	 * @param bitsPerPixel bits per pixel of the source pixels (1, 4 or 8)
	 * @param colorMap the color map of the source pixels (must be 2, 16 or 256 in length)
	 * @param bytesPerRow number of bytes per row of pixels in the source pixels array
	 * @param numRows the number of rows of pixels in the source pixels array
	 * @param y y coordinate in the image to start setting pixels
	 * @param pixels array containing the source pixels
	 */
	public void setPixels(int bitsPerPixel, int colorMap[], int bytesPerRow,
						  int numRows, int y, byte pixels[]) 
	{
		// Efficient way(maybe):
		// CreateBmp
		// BmpBitsSize (returns number of bytes in data)
		// BmpGetBits (returns data)
		// Set bmp palette
		// CreateBmpWin
		// WinDrawLine ...
		// WinCopyRect
		// DestroyWin
		// DestroyBmp

		int j;
		int curCol =0;
		Graphics g;
		int lineOffset = 0;
		int colorIndex;
		int pixPtr;
		int filledBytes = (width*bitsPerPixel)/8;
		if(filledBytes > bytesPerRow){
			filledBytes = bytesPerRow;
		}
		int lastByte = (width * bitsPerPixel - 1)/8 + 1;
		if(lastByte > bytesPerRow){
			lastByte = bytesPerRow;
		}

		int endRow = y + numRows;
		if(endRow > height){
			endRow = height;
		}

		switch(bitsPerPixel){
		case 1:
			g = new Graphics(this);

			for(int i = y; i < endRow; i++){
				pixPtr = 0;
				while(pixPtr/8 < filledBytes){
					colorIndex = (pixels[lineOffset + pixPtr/8] >>> 
								  (7 - pixPtr%8)) & 0x01;
					curCol = colorMap[colorIndex];
					g.setColor((curCol >> 16) & 0xFF, (curCol >> 8) & 0xFF, curCol & 0xFF);
					g.drawLine(pixPtr, i, pixPtr, i);
					pixPtr++;
				}
				if(pixPtr/8 < lastByte){
					while(pixPtr < width){
						colorIndex = (pixels[lineOffset + pixPtr/8] >>> 
									  (7 - pixPtr%8)) & 0x01;
						curCol = colorMap[colorIndex];
						g.setColor((curCol >> 16) & 0xFF, (curCol >> 8) & 0xFF, curCol & 0xFF);
						g.drawLine(pixPtr, i, pixPtr, i);
						pixPtr++;
					}		
				}
				lineOffset += bytesPerRow;
			}
			break;
		case 2:
			g = new Graphics(this);

			for(int i=y; i < endRow; i++){
				pixPtr = 0;
				while(pixPtr/4 < filledBytes){
					colorIndex = (pixels[lineOffset + pixPtr/4] >>> 
								  (6 - (pixPtr*2)%8)) & 0x03;
					curCol = colorMap[colorIndex];
					g.setColor((curCol >> 16) & 0xFF, (curCol >> 8) & 0xFF, curCol & 0xFF);
					g.drawLine(pixPtr, i, pixPtr, i);
					pixPtr++;
				}
				if(pixPtr/4 < lastByte){
					while(pixPtr < width){
						colorIndex = (pixels[lineOffset + pixPtr/4] >>> 
									  (6 - (pixPtr*2)%8)) & 0x03;
						curCol = colorMap[colorIndex];
						g.setColor((curCol >> 16) & 0xFF, (curCol >> 8) & 0xFF, curCol & 0xFF);
						g.drawLine(pixPtr, i, pixPtr, i);
						pixPtr++;
					}		
				}
				lineOffset += bytesPerRow;		
			}
			break;
		case 4:
			g = new Graphics(this);

			for(int i=y; i < endRow; i++){
				pixPtr = 0;
				while(pixPtr/2 < filledBytes){
					colorIndex = (pixels[lineOffset + pixPtr/2] >>> 
								  (4 - (pixPtr*4)%8)) & 0x0F;
					curCol = colorMap[colorIndex];
					g.setColor((curCol >> 16) & 0xFF, (curCol >> 8) & 0xFF, curCol & 0xFF);
					g.drawLine(pixPtr, i, pixPtr, i);
					pixPtr++;
				}
				if(pixPtr/2 < lastByte){
					while(pixPtr < width){
						colorIndex = (pixels[lineOffset + pixPtr/2] >>> 
									  (4 - (pixPtr*4)%8)) & 0x0F;
						curCol = colorMap[colorIndex];
						g.setColor((curCol >> 16) & 0xFF, (curCol >> 8) & 0xFF, curCol & 0xFF);
						g.drawLine(pixPtr, i, pixPtr, i);
						pixPtr++;
					}		
				}
				lineOffset += bytesPerRow;		
			}
			break;
		case 8:
			Graphics.clearDrawWindow();
			Palm.WinSetDrawWindow(winHandle);

			boolean colorTableMatches = false;
			int [] curColorTable = Graphics.getPalette();
			if(colorMap.length == curColorTable.length){
				colorTableMatches = true;
				for(int i=0; i < colorMap.length; i++){
					if(colorMap[i] != (curColorTable[i] & 0xFFFFFF)){
						colorTableMatches = false;
						break;
					}
				}
			}
			if(!colorTableMatches){
				byte [] transColMap = new byte [colorMap.length];

				for(int i=0; i < colorMap.length; i++){
					int col = colorMap[i];
					transColMap[i] = Graphics.getColorIndex(col >> 16 & 0xFF,
															col >> 8 & 0xFF,
															col & 0xFF);
				}

				byte [] oldPixels = pixels;
				byte [] newPixels = new byte [pixels.length];
				for(int i=0; i<pixels.length; i++){
					if((int)(pixels[i] & 0xFF) < transColMap.length){  
						newPixels[i] = transColMap[oldPixels[i] & 0x0FF];
					} else {
						newPixels[i] = 0;
					}
				}
				pixels = newPixels;
				colorTableMatches = true;
			}
			

			int memHandle;
			if(colorTableMatches){
				memHandle = Palm.MemHandleNew(20);			
			} else {
				memHandle = Palm.MemHandleNew(20 + 2+ colorMap.length*4);
			}

			int memPtr = Palm.MemHandleLock(memHandle);

			// Int16    width
			Palm.nativeSetShort(memPtr, (short)bytesPerRow);
			// Int16      height;
			Palm.nativeSetShort(memPtr+2, (short)numRows);
			// UInt16      rowBytes
			Palm.nativeSetShort(memPtr+4, (short)bytesPerRow);
			// BitmapFlagsType flags
			if(colorTableMatches){
				Palm.nativeSetShort(memPtr+6, (short)0x1000);
			} else {
				Palm.nativeSetShort(memPtr+6, (short)0x5000);
			}				
			// UInt8    pixelSize
			// 8 bits per pixel
			Palm.nativeSetByte(memPtr+8, (byte)8);
			// UInt8    version
			Palm.nativeSetByte(memPtr+9, (byte)2);
			// UInt16 nextDepthOffset;
			Palm.nativeSetShort(memPtr+10, (short)0);
			// UInt8    transparentIndex
			Palm.nativeSetByte(memPtr+12, (byte)0);
			// UInt8    compressionType
			Palm.nativeSetByte(memPtr+13, (byte)0);
			// UInt16 reserved; 
			Palm.nativeSetShort(memPtr+14, (short)0);

			int pixelOffset = 16;
			if(!colorTableMatches){
				// UInt 16 color table length
				Palm.nativeSetShort(memPtr+16, (short)colorMap.length);
				for(int i=0; i< colorMap.length; i++){
					Palm.nativeSetInt(memPtr+18+i*4, (i << 24 | (colorMap[i] & 0xFFFFFF)));
				}
				pixelOffset += 2+colorMap.length*4;
			}
			

			int pixelPtr = Palm.nativeObjectLock((Object)pixels);
			Palm.nativeSetInt(memPtr+pixelOffset, pixelPtr);

			Palm.WinDrawBitmap(memPtr, 0, y);
			Palm.nativeObjectUnlock((Object)pixels);
			
			Palm.MemHandleUnlock(memHandle);
			Palm.MemHandleFree(memHandle);

			break;
		}
	}

	/**
	 * Sets the image width and height to 0 and frees any systems resources
	 * associated with the image.
	 */
	public void free()
	{
		if (winHandle == -1)
			return;

		if(this == Graphics.getCurSurface()){
			// This is the current surface to be drawn on
			// this is bad.  When a new surface is switched
			// it will try to save the state of the old now invalid
			// surface
			Graphics.clearDrawWindow();
		}

		Palm.WinDeleteWindow(winHandle, false);

		winHandle = -1;
	}


	/** Returns the height of the image. */
	public int getHeight()
	{
		return height;
	}

	/** Returns the width of the image. */
	public int getWidth()
	{
		return width;
	}
}
