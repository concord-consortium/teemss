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
public class Accumulator extends Transform
{
    float [] output = new float [2];
    float total;

    public void start()
    {
	total = 0f;
	super.start();
    }

    public boolean transform(int num, int size, float data[])
    {

	for(int i=0; i< num; i++){
	    total += data[i*size + 1];
	
	    output[0] = data[i*size];
	    output[1] = total;

	    next.transform(1,2, output);
	}

	return true;
    }
}
