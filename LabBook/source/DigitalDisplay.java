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
package org.concord.LabBook;

import waba.ui.*;
import waba.fx.*;
import waba.io.*;
import waba.sys.*;
import waba.util.*;
import graph.*;
import extra.ui.*;
import org.concord.waba.extra.probware.*;
import org.concord.waba.extra.probware.probs.*;
import org.concord.waba.extra.event.*;

public class DigitalDisplay extends Container
{
    int RIGHT_PADDING = 2;
    int INNER_PADDING = 2;
    int OUTER_PADDING = 4;
    Vector bins = new Vector();
    Vector disps = new Vector();
    FontMetrics fm;
    Font _font;

    public DigitalDisplay(Font f)
    {
		_font = f;
		fm = getFontMetrics(f);
    }

    public void addBin(DecoratedValue bin)
    {
		bins.add(bin);
		LabelBuf curDisp = new LabelBuf("");
		add(curDisp);
		curDisp.setRect(0,0,fm.getTextWidth("-0.00"),fm.getHeight());
		disps.add(curDisp);
		repaint();
    }

    public void removeBin(DecoratedValue bin)
    {
		int index = bins.find(bin);
		bins.del(index);
		LabelBuf curDisp = (LabelBuf)disps.get(index);
		curDisp.free();
		disps.del(index);
		repaint();
    }

    public void onPaint(Graphics g)
    {
		DecoratedValue curBin;
		LabelBuf curDisp;
		String curLabel;
	
		int x=RIGHT_PADDING;

		g.setFont(_font);
		for(int i=0; i<bins.getCount(); i++){
			curBin = (DecoratedValue)bins.get(i);
			curLabel = curBin.getLabel() + ":";
			g.drawText(curLabel, x,0);
			x += fm.getTextWidth(curLabel) + INNER_PADDING;
			curDisp = (LabelBuf)disps.get(i);
			curDisp.setPos(x,0);	    
			x += curDisp.getWidth() + OUTER_PADDING;
		}
		g.setFont(MainWindow.defaultFont);
    }

    public void update()
    {
		for(int i=0; i<bins.getCount(); i++){
			((LabelBuf)disps.get(i)).setText(((DecoratedValue)bins.get(i)).getValue() + "");
		}
    }
}