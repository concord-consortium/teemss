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

public class PropWindow extends Container
{
    Button set;
    Button cancel;
    
    public static Container topContainer = null;
    public static int DEF_WIDTH = 150;
    public static int DEF_HEIGHT = 150;

    MainWindow mw; 

    public PropWindow()
    {
	setRect(0,0, DEF_WIDTH, DEF_HEIGHT);

	set=new Button("Set");
	set.setRect(5, DEF_HEIGHT-20, 30, 17);
	add(set);

	cancel=new Button("Cancel");
	cancel.setRect(DEF_WIDTH-45, DEF_HEIGHT-20, 40, 17);
	add(cancel);

	mw = MainWindow.getMainWindow();
    }

    PropObject po;
    PropPage pp;

    public void showProp(PropObject po, PropPage pp)
    {
	this.po = po;
	this.pp = pp;

	pp.setRect(3,15,width, height-30);
	po.updateProp(pp, PropPage.REFRESH);
	add(pp);
	
	if(topContainer != null){
	    mw.remove(topContainer);
	}
	mw.add(this);
	
    }

    public void onEvent(Event e)
    {
	if(e.type == ControlEvent.PRESSED){
	    if(e.target == cancel){
		po = null;
		mw.remove(this);
		if(topContainer != null) mw.add(topContainer);
	    } else {
		if(po != null){
		    po.updateProp(pp, pp.UPDATE);
		    mw.remove(this);
		    if(topContainer != null) mw.add(topContainer);
		}
	    }
	}
    }
}






