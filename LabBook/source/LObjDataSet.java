package org.concord.LabBook;

import extra.io.*;
import waba.util.*;

public class LObjDataSet extends LabObject
{
    Vector bins = new Vector();
    

    public LObjDataSet()
    {
	objectType = DATA_SET;
    }

    public void readExternal(DataStream ds)
    {
	super.readExternal(ds);

    }

    public void writeExternal(DataStream ds)
    {
	super.writeExternal(ds);

    }

}
