public class ProbeInfo
{
    public final static int SIZE = 0;
    public final static int NUM = 1;
    public final static int UNITS = 2;
    public final static int NAME = 3;
    public final static int MANAGER = 4;
    public final static int MSG = 5;

    public final static int INTEGER = 0;
    public final static int STRING = 1;

    public final static String [] names = {
	"Size", 
	"Num", 
	"Units",
	"Name",
        "Manager",
        "Message",};

    public String strVal = null;
    public int intVal = 0;
    public Object value = null;
    public int type = -1;
    public int id;
    public int format = -1;
    
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
	int i;
	String s;

	type = t;
	switch(t){
	case SIZE :
	case NUM :
	    i = TCPDataChannel.readInt(buf, 0);
	    value = (Object)new Integer(i);
	    break;
	case UNITS:
	case NAME:
	case MANAGER:
	case MSG:
	    value = (Object)new String(buf, 0);	    
	    break;
	default:
	}
    }

    public String toString()
    {
	if(type >= 0){
	    return names[type] + " " + value.toString();
	}

	return "Uninitialized";
    }

    public byte [] toBytes()
    {
	byte buf [];
	int len;
	int i;

	switch(type){
	case SIZE :
	case NUM :
	    buf = new byte[12];
	    TCPDataChannel.writeInt(type, buf, 0);
	    TCPDataChannel.writeInt(4, buf, 4);
	    TCPDataChannel.writeInt(((Integer)value).intValue(), buf, 8);
	    break;
	case UNITS:
	case NAME:
	case MANAGER:
	case MSG:
	    len = ((String)value).length();
	    buf = new byte[8 + len];
	    TCPDataChannel.writeInt(type, buf, 0);
	    TCPDataChannel.writeInt(len, buf, 4);
	    for(i=0; i < len; i++){
		buf[8+i] = (byte)((String)value).charAt(i);
	    }
	    break;
	default:
	    buf = null;
	}

	return buf;
    }

}















