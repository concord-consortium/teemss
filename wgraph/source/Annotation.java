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
import org.concord.waba.extra.util.*;
import extra.util.*;
import org.concord.ProbeLib.*;

public class Annotation
    implements DecoratedValue
{
    public static final int width = 10;
    public static final int height = 10;

    static final int xPts [] = {1, width - 2, (width - 2)/2 + 1,};
    static final int yPts [] = {1, 1, height - 2};
    int xPtsTrans [] = new int [3];
    int yPtsTrans [] = new int [3];

    public float time;
    public float value;
    public String label;
    public String text;
    public boolean selected = false;

    public Axis xaxis;
    public Object bin;

	CCUnit unit;

    public Annotation(String l, float t, float v, Axis xa)
    {
		time = t;
		label = l;
		value = v;
		xaxis = xa;
    }

    public String getLabel()
    {
		return label;
    }

    public float getValue()
    {
		return value;
    }

    public Color getColor()
    {
		return null;
    }

    public float getTime()
    {
		return time;
    }

	public CCUnit getUnit()
	{
		return unit;
	}

    /*
     * Give the top left corner of where to draw
     */
    public void draw(Graphics g, int x, int y)
    {
		if(selected){
			g.setColor(0,0,0);
		} else {
			g.setColor(255,255,255);
		}

		g.fillRect(x,y,width,height);

		if(selected){
			g.setColor(255,255,255);
		} else {
			g.setColor(0,0,0);
		}

		int i;
		for(i = 0; i < 3; i++){
			xPtsTrans[i] = xPts[i] + x;
			yPtsTrans[i] = yPts[i] + y;
		}

		g.fillPolygon(xPtsTrans, yPtsTrans, 3);	
    }

	public boolean checkPos(int x)
	{
		int pos;
		if(xaxis.drawnX != -1){
			pos = (int)((time - xaxis.dispMin) * xaxis.scale);
			if((pos*xaxis.axisDir >= 0) && 
			   (pos*xaxis.axisDir < xaxis.axisDir*xaxis.dispLen)){ 
				// Need to make this layout independent
		    
				if(x >= (pos + xaxis.drawnX + xaxis.axisDir - width/2) &&
				   x < (pos + xaxis.drawnX + xaxis.axisDir + width/2))
					return true;
			}
		}

		return false;
	}
}
