package org.concord.LabBook;

import waba.io.*;
import waba.util.*;
import waba.sys.*;
import org.concord.waba.extra.io.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;

public class LabBookFile extends LabBookDB
{

    public LabBookFile(String dud){}

    public boolean save(){return false;}
    
    public void close(){}

    public boolean getError(){return true;}
 
    public byte [] readObjectBytes(LabObjectPtr ptr, int numBytes){return null;}
  
    public boolean writeObjectBytes(LabObjectPtr ptr, byte [] buffer, int start,
									int count){return false;}

    public int getDevId(){return -1;}
    
    public int getNewObjId(){return -1;}

	public LabObjectPtr getRootPtr(){ return null; }

	public void setRootPtr(LabObjectPtr ptr){}
}
