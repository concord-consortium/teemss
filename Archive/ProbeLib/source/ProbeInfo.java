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
public class ProbeInfo
{
    public final static int SIZE = 0;
    public final static int NUM = 1;
    public final static int UNITS = 2;
    public final static int NAME = 3;
    public final static int MANAGER = 4;
    public final static int MSG = 5;
    public final static int STOPPED = 6;
    public final static int DELETED = 7;
    public final static int NEW_PROBE = 8;

    public final static int INTEGER = 0;
    public final static int STRING = 1;

    public final static String [] names = {
	"Size", 
	"Num", 
	"Units",
	"Name",
        "Manager",
        "Message",
	"Stopped",
	"Deleted",
	"NewProbe"};

    public String strVal = null;
    public int intVal = 0;
    public int type = -1;
    public int format = -1;
    public int id;
    
    public ProbeInfo(int t, String val)
    {
	type = t;
	format = STRING;
	strVal = val;
    }

    public ProbeInfo(int t, int val)
    {
	type = t;
	format = INTEGER;
	intVal = val;
    }

    public ProbeInfo(int id, int t, String val)
    {
	this(t, val);
	this.id = id;
    }

    public ProbeInfo(int id, int t, int val)
    {
	this(t, val);
	this.id = id;
    }

    public ProbeInfo(int t, byte [] buf)
    {
	int len;
	int i;
	String s;

	type = t;
	switch(t){
	case SIZE :
	case NUM :
	    format = INTEGER;
	    intVal = TCPDataChannel.readInt(buf, 0);
	    break;
	case UNITS:
	case NAME:
	case MANAGER:
	case MSG:
	case STOPPED:
	case DELETED:
	case NEW_PROBE:
	    format = STRING;
	    s = "";
	    for(i=0; i < buf.length; i++){
		s = s + ((char)buf[i]);
	    } 
	    strVal = s;
	    break;
	default:
	}
    }

    public String toString()
    {
	switch(format){
	case INTEGER :
	    return names[type]  + " " + intVal;
	case STRING :
	    if(strVal != null){
		return names[type] + " " + strVal;
	    }
	    break;
	}

	return "Uninitialized";
    }

    public byte [] toBytes()
    {
	byte buf [];
	int len;
	int i;

	switch(format){
	case INTEGER :
	    buf = new byte[12];
	    TCPDataChannel.writeInt(type, buf, 0);
	    TCPDataChannel.writeInt(4, buf, 4);
	    TCPDataChannel.writeInt(intVal, buf, 8);
	    break;
	case STRING :
	    len = strVal.length();
	    buf = new byte[8 + len];
	    TCPDataChannel.writeInt(type, buf, 0);
	    TCPDataChannel.writeInt(len, buf, 4);
	    for(i=0; i < len; i++){
		buf[8+i] = (byte)(strVal.charAt(i));
	    }
	    break;
	default:
	    buf = null;
	}

	return buf;
    }

}















