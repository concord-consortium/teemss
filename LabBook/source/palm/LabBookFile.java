package org.concord.LabBook;

import waba.io.*;
import waba.util.*;
import waba.sys.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import graph.*;

public class LabBookFile 
    implements LabBookDB
{

    public LabBookFile(String dud){}

    public boolean save(){return false;}
    
    public void close(){}

    public boolean getError(){return true;}
 
    public byte [] readObjectBytes(int devId, int objId){return null;}
  
    public boolean writeObjectBytes(int devId, int objId, byte [] buffer, int start,
	int count){return false;}

    public int getDevId(){return -1;}
    
    public int getNewObjId(){return -1;}

    public int getRootDevId(){return -1;}
    public int getRootObjId(){return -1;}

    public void setRootDevId(int id){}
    public void setRootObjId(int id){}

    static public void export(Bin b, Vector points){}
}
