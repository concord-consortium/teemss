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
package org.concord.waba.graph;

import waba.fx.*;
import waba.ui.*;

public class FloatLabel extends Control
{
	FloatConvert fConvert = new FloatConvert();
	int curExponent = 0;

    Image buffer = null;
    Graphics bufG;

    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int CENTER = 2;

    Font font = MainWindow.defaultFont;
    FontMetrics fm = null;
    int fmHeight = -1;
    int align = LEFT;
    int textY;

    public FloatLabel(){
    }

	public void setAlignment(int align)
	{
		this.align = align;
	}

	public void setPrecision(int precision)
	{
		fConvert.setPrecision(precision);
	}

	public int getExponent()
	{
		return fConvert.getExponent();
	}

    public void free()
    {
		if(buffer != null) buffer.free();
		if(bufG != null)bufG.free();
    }
    
	public void draw(int exponent)
	{
		curExponent = exponent;
		Graphics g = createGraphics();
		onPaint(g);
		g.free();
	}

    public void draw()
    {
		draw(curExponent);
    }

	public void setVal(float val)
	{
		fConvert.setVal(val);
	}

    public void setRect(int x, int y, int w, int h){
		super.setRect(x,y,w,h);
		if(fmHeight != -1){
			textY = (h - fmHeight)/2;
		}
    }

    public int getWidth(){return width;}

    public void setPos(int x, int y)
    {
		setRect(x,y,width,height);
    }

    public void setFont(Font f)
    {
		font = f;
    }

    public void onPaint(Graphics g)
    {
		String text = fConvert.getString(curExponent);

		if(buffer == null){
			Rect r = getRect();
			buffer = new Image(r.width, r.height);
			bufG = new Graphics(buffer);
			fm = getFontMetrics(font);
			fmHeight = fm.getHeight();
			textY = (this.height - fmHeight) / 2;
		}

		bufG.setColor(255,255,255);
		bufG.fillRect(0,0,width,height);
		bufG.setColor(0,0,0);

		bufG.setFont(font);

		int x = 0;
		int y = (this.height - fmHeight) / 2;
		if (align == CENTER)
			x = (this.width - fm.getTextWidth(text)) / 2;
		else if (align == RIGHT)
			x = this.width - fm.getTextWidth(text);
		bufG.drawText(text, x, y);

		if(g != null){
			g.copyRect(buffer, 0, 0, width, height, 0, 0); 	    
		}
    }

}
