/*
  Copyright (C) 2001 Concord Consortium

  This program is free software; you can redistribute it and/or
  modify it under the terms of the GNU General Public License
  as published by the Free Software Foundation; either version 2
  of the License, or (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package graph;

import waba.fx.*;
import waba.ui.*;
import extra.util.*;

public class TextLine extends Object {
    // Constant
    public final static int RIGHT = 0;
    public final static int LEFT = 1;
    public final static int UP = 2;
    public final static int DOWN = 3;


    public final static int RIGHT_EDGE = 0;
    public final static int LEFT_EDGE = 1;
    public final static int TOP_EDGE = 2;
    public final static int BOTTOM_EDGE = 3;

    public int minDigits = 0;
    public int maxDigits = 2;

	/**
	 * Font to use for text
	 */
    public Font font     = null;
    public FontMetrics fontMet = null;
	/**
	 * Text color
	 */
    public int [] col = {0,0,0,};

	/**
	 * Background Color
	 */
    public int [] bgCol = {255,255,255,};

    
	/**
	 * The text to display
	 */
    public  String text   = null;

    /**
     * the text direction
     */
    public int direction = 0;

    protected int width = 0;
    protected int height = 0;
    protected int textWidth = 0;
    protected int textHeight = 0;

	boolean palm = false;

	public TextLine(float v, Font f){
		if(waba.sys.Vm.getPlatform().equals("PalmOS")) palm = true;

		font = f;
		fontMet = MainWindow.getMainWindow().getFontMetrics(font);

		setText(v);
	}

	public TextLine(float v){
		this(v, MainWindow.defaultFont);
		font = null;
    }

    public TextLine(String s, Font f) {
		if(waba.sys.Vm.getPlatform().equals("PalmOS")) palm = true;

		font = f;
		fontMet = MainWindow.getMainWindow().getFontMetrics(font);

		setText(s);
    }

    public TextLine(String s){
		this(s, MainWindow.defaultFont);
		font = null;
    }

    public TextLine(String s, Font f, Color c) {
		this(s, f);
		col[0] = c.getRed();
		col[1] = c.getGreen();
		col[2] = c.getBlue();
	
    }

    public TextLine(String s, int d){
		this(s);
		font = null;
		direction = d;
    }

	public TextLine(){
		fontMet = MainWindow.getMainWindow().getFontMetrics(MainWindow.defaultFont);
	}		

    public void free()
    {
		if(buffer != null) buffer.free();
		if(bufG != null)bufG.free();
    }
    

	/**
	 * Convert float with correct digits
	 */
	public String fToString(float val)
	{
		// We don't want any exponents printed so we need to do this ourselves.
        int j;
		char intChars[];
		char fltChars[];
		int len, nLen;
		int start=0, end;
		int exp=0;
		int multExp;
		int count;
		String absLabel;

		for(j=0; j<maxDigits; j++) val *= (float)10;
		if(val < 0) val -= (float)0.5;
		else val += (float)0.5;

		if(((int)val) == 0){
			if(minDigits != 0)
				return new String("0.0");
			else
				return new String("0");
		}

		float absVal = val;
		if(val < 0f){ 
			absVal = -val;
		}
		intChars = String.valueOf((int)absVal).toCharArray();
		len = intChars.length;

		if(len <= maxDigits){
			fltChars = new char[maxDigits + 2];
			fltChars[0] = '0';
			fltChars[1] = '.';
			for(j=0; j < maxDigits - len; j++){
				fltChars[2+j] = '0';
			}
			start = 2+j;
			for(j=0; j < len; j++)
				fltChars[start + j] = intChars[j];
		} else {
			if(minDigits == 0 && len >= maxDigits*2 + 1){
				fltChars = new char[len - maxDigits];
				for(j=0; j < len - maxDigits; j++){
					fltChars[j] = intChars[j];
				}
			} else {
				int zeros = 0;
				for(int i=len-1; i >= len - maxDigits + minDigits; i--){
					if(intChars[i] == '0') zeros++;
					else break;											  
				}
				fltChars = new char [len + 1 - zeros];
				for(j=0; j < len - maxDigits; j++){
					fltChars[j] = intChars[j];
				}
				fltChars[j] = '.';
				for(; j < len - zeros; j++){
					fltChars[j + 1] = intChars[j];
				}
			}
		}

		absLabel = new String(fltChars, 0, fltChars.length);

		if(val < 0)
			return new String("-" + absLabel);
		else 
			return absLabel;

	}    

    public boolean  parseText()
    {
		textWidth = fontMet.getTextWidth(text);
		textHeight = fontMet.getHeight();
		textHeight -= 1;
		if(palm){
			textWidth--;
			textHeight--;
		}

		if(direction < 2){
			width = textWidth;
			height = textHeight;
		} else {
			height = textWidth;
			width = textHeight;
		}

		return true;
    }

	float fVal = Maths.NaN;
	int fMinDigits = -1;
	int fMaxDigits = -1;
	public void setText(float v)
	{
		if(v == fVal && fMinDigits == minDigits && 
		   fMaxDigits == maxDigits){
			return;
		} else {
			fVal = v;
			fMaxDigits = maxDigits;
			fMinDigits = minDigits;
			setText(fToString(v));
		}
	}

    public void setText(String s)
    {
		if(text != null && text.equals(s)) return;
		text = s;
		parseText();
		if(buffer != null) buffer.free();
		if(bufG != null)bufG.free();
		buffer = null;
		updateBuffer();
    }

    /* Draw starting at the upper left hand corner
     */

    Image buffer = null;
    Graphics bufG;

    public void drawRight(Graphics g, int x, int y)
    {
		g.setColor(bgCol[0],bgCol[1],bgCol[2]);
		g.fillRect(x, y, textWidth, textHeight);

		if(font != null) g.setFont(font);
		g.setColor(col[0],col[1],col[2]);

		if(palm) g.drawText(text, x, y-2);
		else g.drawText(text, x, y-1);

		return;
    }

	public void updateBuffer()
	{
		Image offsI = null;
		Image rotImage = null;
		Graphics offsG = null;
		Graphics rotG = null;
		Graphics g = null;

		if(width <= 0 || height <= 0){
			buffer = null;
			return;
		}
		buffer = new Image(width, height);
		g = bufG = new Graphics(buffer);

		if(direction == RIGHT){
			drawRight(g, 0, 0);
			return;
		}

		offsI = new Image(textWidth, textHeight);
		offsG = new Graphics((ISurface)offsI);

		if(font != null) offsG.setFont(font);
		offsG.setColor(col[0],col[1],col[2]);

		drawRight(offsG, 0, 0);

		rotImage = new Image(width, height);
		rotG =  new Graphics(rotImage);
		rotateImage(offsI, rotG);
		rotG.free();
		offsG.free();
		offsI.free();

		g.drawImage(rotImage, 0, 0);
		rotImage.free();

	}

    public void draw(Graphics _g, int x, int y)
    {
		Image offsI = null;
		Image rotImage = null;
		Graphics offsG = null;
		Graphics rotG = null;
		Graphics g = null;

		if(text == null || text.equals("")) return;

		if(buffer != null){
			_g.copyRect(buffer, 0, 0, width, height, x, y); 	    
			return;
		}

		updateBuffer();

		_g.copyRect(buffer, 0, 0, width, height, x, y);


    }

    public void drawCenter(Graphics g, int x, int y, int edge)
    {
		int x0, y0;

		switch(edge){
		case RIGHT_EDGE:
			x0 = x - width - 1;
			y0 = y - height/2;
			break;
		case LEFT_EDGE:
			x0 = x;
			y0 = y - height/2;
			break;
		case TOP_EDGE:
			x0 = x - width/2;
			y0 = y;
			break;
		case BOTTOM_EDGE:
		default :
			x0 = x - width/2;
			y0 = y - height - 1;
			break;
		}

		draw(g, x0, y0);
    }

    public int getXOffset(int edge)
    {

		switch(edge){
		case RIGHT_EDGE:
			return -width - 1;
		case LEFT_EDGE:
			return 0;
		case TOP_EDGE:
		case BOTTOM_EDGE:
		default :
			return  -(width/2);
		}
    }

    public int getYOffset(int edge)
    {
		switch(edge){
		case RIGHT_EDGE:
		case LEFT_EDGE:
			return  -(height/2);
		case TOP_EDGE:
			return 0;
		case BOTTOM_EDGE:
		default :
			return -height - 1;
		}
    }

    public void rotateImage(Image srcImg, Graphics destG) 
    {
		int x, y;
		int tmpOffset;

		switch(direction){
		case UP:
			tmpOffset = height - 1;
			for(y = 0 ; y < textHeight; y++) {
				for(x = 0; x < textWidth; x++) {
					destG.copyRect(srcImg, x, y, 1, 1, y, tmpOffset - x);
				}
			}
			break;
		case DOWN:
			tmpOffset = width - 1;
			for(y =0; y < textHeight; y++) {
				for(x = 0; x < textWidth; x++){
					destG.copyRect(srcImg, x, y, 1, 1, tmpOffset - y, x);
				}
			}
			break;
		case LEFT:
			for(y = 0; y < textHeight; y++) {
				for( x=0; x< textWidth; x++){
					destG.copyRect(srcImg, x, y, 1, 1, width - x - 1, height - y - 1); 
				}
			}
			break;
		default:
		}
	
		return;

    }

}













