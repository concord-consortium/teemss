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
import graph.*;

public class GraphUpdater extends Transform
{
    int bin = 0;
    boolean update = false;
    AnnotView lg;
    GraphTool gt;

    public GraphUpdater(AnnotView av, GraphTool gt)
    {
	lg = av;
	this.gt = gt;
    }
    
    public boolean transform(int num, int size, float data []){
	if(lg.active){

	    if(!lg.addPoints(0, num, data)){
		gt.stop();
		lg.curView.draw();
		return false;		
	    }
	}
	return true;
    }

}
