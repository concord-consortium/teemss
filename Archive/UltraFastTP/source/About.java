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
import waba.ui.*;
import waba.fx.*;

public class About extends Container
{
    Button done = new Button("Done");
    MainWindow mw;

    int xStart;
    int yStart;

    Font curFont;
    FontMetrics fm;

    public About(MainWindow mw, int w, int h)
    {
	this.mw = mw;
	setRect(0,0,w,h);
	done.setRect((w-40)/2,h-20, 40, 20);
	add(done);
	xStart = 0;
	curYpos = yStart = 20;
	curFont = mw.defaultFont;
	fm = getFontMetrics(curFont);
    }

    public void onEvent(Event e)
    {
	if(e.target == done && e.type == ControlEvent.PRESSED){
	    mw.remove(this);
	    mw.exit(0);
	}
    }

    int curYpos;

    public void printLine(String text, Graphics g)
    {
	int tWidth = fm.getTextWidth(text);
	g.drawText(text, (width - tWidth)/2, curYpos);
	curYpos += 11;
    }

    public void onPaint(Graphics g)
    {
	g.setColor(0,0,0);
	printLine("ProbeWare Tool", g);
	printLine("©2001 Concord Consortium", g);
	printLine("All Rights Reserved", g);
	printLine("", g);
	printLine("Development Team: ", g);
	printLine(" Stephen Bannasch", g);
	printLine(" Scott Cytacki", g);
	printLine(" Dmitry Markman", g);
    }
}
