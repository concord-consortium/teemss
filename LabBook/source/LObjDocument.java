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

    public void setText(String t){
		text = t;
    }

    public LabObjectView getView(ViewContainer vc, boolean edit, LObjDictionary curDict){
 		if(view == null){ 
 			view = new LObjDocumentView(vc, this, edit);
 		}else{
    		view.container = vc;
    	}
		return view;
    }

    public void writeExternal(DataStream out){
		out.writeString(text);
    }

    public void readExternal(DataStream in){
		text = in.readString();
    }
}
