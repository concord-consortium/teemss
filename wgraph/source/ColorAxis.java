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

public class ColorAxis extends Axis
{
	
    public ColorAxis(int type)
    {
		super(type);
    }

	public ColorAxis(float min, float max, int len, int type)
	{
		this(type);
		init();
		setLength(len);
		setDispMin(min);
		setScale((float)len/ (max - min));
	}

    public void setTempColor(Graphics graphics, float temp)
    {
        int percent;
		int r, g, b;
		r = 0;
		b = 0;
		g = 0;


		percent = (int)((temp - dispMin)*1000/ (length/scale ));
		setBarColor(graphics, percent);
    
    }

    // Notice this used 10*percent
    void setBarColor(Graphics graphics, int percent)
    {
		int r, g, b;
		r = 0;
		b = 0;
		g = 0;

		if(percent < 250){
			b = 255;
			g = (255 * percent) / 250;
		} else if(percent < 500){
			g = 255;
			b = (255 * (500 - percent)) / 250;
		} else if(percent < 750){
			g = 255;
			r = (255 * (percent - 500)) / 250;
		} else {
			r = 255;
			g = (255 * (1000 - percent)) / 250;
		} 
	
		graphics.setColor(r, g, b);

		return;
    }

}
