package org.concord.LabBook;

import waba.util.*;
import waba.ui.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;
import extra.ui.*;

public class LObjCCTextArea extends LObjSubDict{
public LObjCCTextAreaView view = null;
LObjDictionary			curDict = null;
    public LObjCCTextArea(){
		super(DefaultFactory.CCTEXTAREA);
    }
	public static LabObject makeNewObj(boolean direct){
		return new LObjCCTextArea();
	}

    public LabObjectView getView(ViewContainer vc, boolean edit, LObjDictionary curDict){
    
    	this.curDict = curDict;
 		if(view == null){ 
 			view = new LObjCCTextAreaView(vc, this, edit);
 		}else if(view.container == null){
    		view.container = vc;
    	}
		return view;
    }

    public void writeExternal(DataStream out){
		super.writeExternal(out);
		out.writeBoolean(view != null);
		if(view != null){
			view.writeExternal(out);
		}
    }

    public void readExternal(DataStream in){
		super.readExternal(in);
		boolean wasView = in.readBoolean();
		if(wasView){
			if(view == null) view = new LObjCCTextAreaView(null, this,false);
			view.readExternal(in);
		}
    }
}
