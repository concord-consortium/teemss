package org.concord.LabBook;

import waba.util.*;
import waba.ui.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;
import extra.ui.*;
import extra.util.CCUnit;
import org.concord.waba.extra.event.*;
//LabObject implements Storable
public class LObjImage extends LabObject
{

public LObjImageView view = null;

    public LObjImage()
    {
		super(DefaultFactory.IMAGE);
    }
    public LabObjectView getView(ViewContainer vc, boolean edit,LObjDictionary curDict)
    {
    	
 		if(view == null){ 
   			view = new LObjImageView(vc, this, edit);
    	}else{
    		view.container = vc;
    	}
    	view.didLayout = false;
		return view;
    }

    public void writeExternal(DataStream out)
    {
		if(view == null) return;
		view.writeExternal(out);
    }

    public void readExternal(DataStream in)
    {
		if(view == null) view = new LObjImageView(null, this,false);
		view.readExternal(in);
    }
    public boolean equals(TreeNode node){
    	return super.equals(node);
    }

}
