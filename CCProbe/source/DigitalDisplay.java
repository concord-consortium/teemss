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
package org.concord.CCProbe;

import waba.ui.*;
import waba.fx.*;
import waba.io.*;
import waba.sys.*;
import waba.util.*;
import org.concord.waba.graph.*;

import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.util.*;
import org.concord.ProbeLib.*;

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
		FloatConvert.makeLookup();
		_font = f;
		fm = getFontMetrics(f);
    }

    public void addBin(DecoratedValue bin)
    {
		bins.add(bin);
		
		FloatLabel curDisp = new FloatLabel();
		curDisp.setAlignment(FloatLabel.RIGHT);
		add(curDisp);
		
		int prec = bin.getPrecision();
		if(prec != bin.UNKNOWN_PRECISION){
			curDisp.setPrecision(prec);
		} else {
			curDisp.setPrecision(-2);
		}

		curDisp.setRect(0,0,fm.getTextWidth("-0.00E0"),fm.getHeight());
		disps.add(curDisp);
		repaint();
    }

    public void removeBin(DecoratedValue bin)
    {
		int index = bins.find(bin);
		if(index < 0) return;
		bins.del(index);

		FloatLabel curDisp = (FloatLabel)disps.get(index);
		remove(curDisp);
		curDisp.free();
		disps.del(index);
		repaint();
    }

	public void free()
	{
		for(int i=0; i<disps.getCount(); i++){
			((FloatLabel)disps.get(i)).free();
		}
		
		bins = null;
	}

    public void onPaint(Graphics g)
    {
		DecoratedValue curBin;
		FloatLabel curDisp;
		String curLabel;
		String curUnitStr;
		CCUnit curUnit;

		if(bins == null) return;

		int x=RIGHT_PADDING;

		g.setFont(_font);
		g.setColor(255,255,255);
		g.fillRect(0,0,width,height);

		g.setColor(0,0,0);
		for(int i=0; i<bins.getCount(); i++){
			curBin = (DecoratedValue)bins.get(i);
			if(curBin == null) continue;
			curLabel = curBin.getLabel();
			if(curLabel == null) curLabel = ":";
			else curLabel = curLabel + ":";
			g.drawText(curLabel, x,0);
			x += fm.getTextWidth(curLabel) + INNER_PADDING;
			curDisp = (FloatLabel)disps.get(i);
			if(curDisp != null){
				curDisp.setPos(x,0);	    
				x += curDisp.getWidth();
			}
			curUnit = curBin.getUnit();
			if(curUnit != null){
				curUnitStr = curUnit.abbreviation;
				g.setColor(0,0,0);
				g.drawText(curUnitStr, x, 0);
				x += fm.getTextWidth(curUnitStr);
			}
			x += OUTER_PADDING;
		}
		g.setFont(MainWindow.defaultFont);
    }

    public void update()
    {
		for(int i=0; i<bins.getCount(); i++){
			FloatLabel fLabel = (FloatLabel)disps.get(i);

			DecoratedValue bin = (DecoratedValue)bins.get(i);

			fLabel.setVal(bin.getValue());

			int prec = bin.getPrecision();
			if(prec != bin.UNKNOWN_PRECISION){
				fLabel.setPrecision(prec);
			} else {
				fLabel.setPrecision(fLabel.getExponent() - 3);
			}

			// Should change this to add some historisis.  
			// Hmm..
			if(fLabel.getExponent() >= 0){
				// Temporary hack to remove exponent from display
				fLabel.draw(0);
				// fc.getExponent()/3*3));
			} else {
				fLabel.draw((fLabel.getExponent()-2)/3*3);
			}
		}
    }
}
