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
package org.concord.waba.extra.util;

import waba.util.*;
import waba.fx.*;
import waba.ui.*;

public abstract class DecoratedValue 
{
	public final static int UNKNOWN_PRECISION = 0x7FFFFFFF;

	protected CCColor color;
	protected int precision = UNKNOWN_PRECISION;
	protected CCUnit unit;
	protected float time;

    public CCColor getColor()
	{
		return color;
	}

    public float getTime()
	{
		return time;
	}

	public void setUnit(CCUnit u)
	{
		unit = u;
	}

	public CCUnit getUnit()
	{
		return unit;
	}

	public int getPrecision()
	{
		return precision;
	}

	public void setPrecision(int precision)
	{
		this.precision = precision;
	}

    public abstract String getLabel();
    public abstract float getValue();
}
