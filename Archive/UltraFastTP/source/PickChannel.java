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
public class PickChannel extends Transform
{
    final static int TRANS_SIZE = 420;
    float [] output = new float [TRANS_SIZE];
    int channel = 1;

    public PickChannel(int chan)
    {
	channel = 1+chan;
    }

    public boolean transform(int num, int size, float data[])
    {
	int endPos = num*size;
	int outPos = 0;

	for(int i=0; i<endPos; i+= size){
	    output[outPos++] = data[i];
	    output[outPos++] = data[i+channel];
	    if(outPos >= TRANS_SIZE){
		next.transform(TRANS_SIZE/2, 2, output);
		outPos = 0;
	    }
	}

	if(outPos > 0)
	    return next.transform(outPos/2, 2, output);
	
	return true;
    }
}
