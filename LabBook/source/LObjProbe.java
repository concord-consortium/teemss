package org.concord.LabBook;

import waba.util.*;
import waba.ui.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;
import extra.ui.*;
import extra.util.CCUnit;
import org.concord.waba.extra.event.*;

public class LObjProbe extends LabObject
{
    public LObjProbe()
    {
    	this("Probe");
    }
    public LObjProbe(String name)
    {
    	this.name = name;
		objectType = PROBEOBJ;
    }

    public void writeExternal(DataStream out)
    {
		super.writeExternal(out);
    }

    public void readExternal(DataStream in)
    {
		super.readExternal(in);
    }
    
    public String toString(){
    	return name;
    }

}
