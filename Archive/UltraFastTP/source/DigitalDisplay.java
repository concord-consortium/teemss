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
import graph.*;

public class DigitalDisplay extends Transform
{
    LabelBuf tLabel, vLabel;
    TextLine convertor = new TextLine("0");
    String units = "";
    float time, value;

    public DigitalDisplay(LabelBuf time, LabelBuf value)
    {
	tLabel = time;
	vLabel = value;
	convertor.maxDigits = 2;
    }

    public boolean transform(int num, int size, float data []){
	int offset = (num-1)*size;
	value = data[offset + 1];
	time = data[offset];

	//	System.out.println("v:" + data[1] + ", t:" + data[0]);

	return next.transform(num, size, data);
    }

    public void update()
    {
	vLabel.setText(convertor.fToString(value) + units);
	tLabel.setText(convertor.fToString(time) + "s");
	if(next != null)
	    next.update();
    }
}
