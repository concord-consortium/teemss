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

import waba.ui.*;
import waba.util.*;

public class PropPage extends Container
{
    public Vector props = new Vector();
    int curYpos = 2;
    PropObject po;

    public final static int UPDATE = 1;
    public final static int REFRESH = 2;

    public PropPage(PropObject o)
    {
	po = o;
    }

    public void addEdit(String label, int fieldLength)
    {
	Label tmpLabel;
	Edit tmpEdit;

	tmpLabel = new Label(label);
	tmpLabel.setRect(2,curYpos,65,17);
	add(tmpLabel);
	
	tmpEdit = new Edit();
	tmpEdit.setRect(70,curYpos, fieldLength, 17);
	add(tmpEdit);
	props.add(tmpEdit);
	
	curYpos += 19;
    }

    public void showProp()
    {
	PropWindow pwin = new PropWindow();
	pwin.showProp(po, this);
    }
}
