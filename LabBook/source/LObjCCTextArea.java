package org.concord.LabBook;

import waba.util.*;
import waba.ui.*;
import org.concord.waba.extra.io.*;
import org.concord.waba.extra.ui.*;

public class LObjCCTextArea extends LObjSubDict{
	public LObjCCTextAreaView view = null;
	LObjDictionary			curDict = null;
	static public	boolean editMode = false;
    public LObjCCTextArea(){
		super(DefaultFactory.CCTEXTAREA);
    }
	public static LabObject makeNewObj(boolean direct){
		return new LObjCCTextArea();
	}

    public LabObjectView getView(ViewContainer vc, boolean edit, LObjDictionary curDict, 
								 LabBookSession session){
    	this.curDict = curDict;
 		if(view == null){ 
 			view = new LObjCCTextAreaView(vc, this, edit, session, curDict);
 		}else {
			if(view.getSession() != null){
				// This is very dangerous if he is keeping things around.
				// We really need to completely loose the reference to this object 
				// every time				
				view.getSession().release();
			}
			view.setSession(session);
			view.localDict = curDict;
    		view.container = vc;
    	}
		return view;
    }
    public LabObjectView getPropertyView(ViewContainer vc, LObjDictionary curDict, 
										 LabBookSession session){
    	LObjCCTextAreaPropView propView = new LObjCCTextAreaPropView(vc, this);
    	propView.setEditMode(editMode);
 		return propView;
    }

    public void writeExternal(DataStream out){
		out.writeBoolean(view != null);
		if(view != null){
			view.writeExternal(out);
		}
    }

    public void readExternal(DataStream in){
		boolean wasView = in.readBoolean();
		if(wasView){
			if(view == null) view = new LObjCCTextAreaView(null, this,false, null, null);
			view.readExternal(in);
		}
    }
}
