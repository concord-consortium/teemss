package org.concord.waba.extra.ui;

import com.apple.mrj.MRJApplicationUtils;
import com.apple.mrj.MRJQuitHandler;
//import com.apple.mrj.MRJPrefsHandler;
import com.apple.mrj.MRJAboutHandler;


public class CCMacApplHandler implements CCApplHandler, MRJQuitHandler, MRJAboutHandler/*, MRJPrefstHandler*/
{
CCApplHandlerListener	listener;
	public void registerHandlers(CCApplHandlerListener	listener){
    	MRJApplicationUtils.registerQuitHandler(this);
//    	MRJApplicationUtils.registerPrefsHandler(this);
    	MRJApplicationUtils.registerAboutHandler(this);
    	addCCApplHandlerListener(listener);
	}
	public void addCCApplHandlerListener(CCApplHandlerListener	l){
		if(l == null) return;
		if(l != listener) listener = l;
	}
	public void removeCCApplHandlerListener(CCApplHandlerListener	l){
		if(l == null) return;
		if(l == listener) listener = null;
	}



	public void handleQuit() throws IllegalStateException{
		if(listener != null) listener.handleQuit();
	}
	public void handlePrefs(){
		if(listener != null) listener.handlePrefs();
	}
	public void handleAbout(){
		if(listener != null) listener.handleAbout();
	}
}

