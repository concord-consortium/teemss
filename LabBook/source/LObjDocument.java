package org.concord.LabBook;

import waba.util.*;
import waba.ui.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;
import extra.ui.*;

public class LObjDocument extends LabObject{
String 					text = null;
public LObjDocumentView view = null;

    public LObjDocument(){
		super(DefaultFactory.DOCUMENT);
    }
	public static LabObject makeNewObj(boolean direct){
		return new LObjDocument();
	}

    public void setText(String t){
		text = t;
    }

    public LabObjectView getView(ViewContainer vc, boolean edit, LObjDictionary curDict){
 		if(view == null){ 
 			view = new LObjDocumentView(vc, this, edit);
 		}else if(view.container == null){
    		view.container = vc;
    		if(view.menu == null/* && edit*/){
    			view.addMenus(vc);
    		}
    	}
		return view;
    }

    public void writeExternal(DataStream out){
		super.writeExternal(out);
		out.writeString(text);
    }

    public void readExternal(DataStream in){
		super.readExternal(in);
	
		text = in.readString();
    }
}
