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

public class LabelBuf extends Control
{
    Image buffer = null;
    Graphics bufG;

    static final int LEFT = 0;
    static final int RIGHT = 1;
    static final int CENTER = 2;

    String text;
    Font font = MainWindow.defaultFont;
    int align = LEFT;

    public LabelBuf(String t){
	text = t;
    }

    public void free()
    {
	if(buffer != null) buffer.free();
	if(bufG != null)bufG.free();
    }
    
    public void draw()
    {
	onPaint(createGraphics());
    }

    public void setText(String s)
    {
	text = s;
	draw();
    }
	
    public void setFont(Font f)
    {
	font = f;
    }

    public void onPaint(Graphics g)
    {
	if(buffer == null){
	    Rect r = getRect();
	    buffer = new Image(r.width, r.height);
	    bufG = new Graphics(buffer);
	}

	bufG.setColor(255,255,255);
	bufG.fillRect(0,0,width,height);
	bufG.setColor(0,0,0);

	bufG.setFont(font);
	FontMetrics fm = getFontMetrics(font);
	int x = 0;
	int y = (this.height - fm.getHeight()) / 2;
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
