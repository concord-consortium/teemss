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
public class ThermalCouple extends Transform
{
    float curTemp;
    float [] output = new float [2];

    public final static float temperature (float mV)
    {

	float mV2 = mV * mV;
	float mV3 = mV2 * mV;
	return mV * (float)17.084 + mV2 * (float)-0.25863 + mV3 * (float)0.011012;
    }

    public boolean transform(int num, int size, float data[])
    {
	float lastColdJunct;
	int endPos = num*size;

	for(int i=0; i<endPos; i+= size){
	    lastColdJunct = (data[i+2] / 10) - (float)50;
	    output[1] = curTemp = temperature(data[i+1]) + lastColdJunct;
	    output[0] = data[i];
	    next.transform(1, 2, output);
	}

	return true;
    }
}
