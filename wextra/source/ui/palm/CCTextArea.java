package org.concord.waba.extra.ui;

import waba.ui.*;
import waba.fx.*;
import waba.sys.*;
import waba.util.Vector;


public class CCTextArea  extends Control{
CCStringWrapper		[] lines;
FontMetrics 		fm = null;
			
			
	FontMetrics getFontMetrics(){return fm;}
	public void insertObject(){}
	public void insertText(String str){}
	public void setText(String str){}
	public String getText(){return null;}

}

class CCStringWrapper{
String str;
CCTextArea owner = null;
	CCStringWrapper(CCTextArea owner){
		str = "";
		this.owner = owner;
	}
	
	CCStringWrapper(CCTextArea owner,String str){
		this.str = str;
		this.owner = owner;
	}
	String getStr(){return str;}
	void setStr(String str){
		if(owner == null){
			this.str = str;
		}else{
			FontMetrics fm = owner.getFontMetrics();
			if(fm == null) return;
			this.str = str;
		}
	}
	
	String getFullStr(){
		return str;
	}
}
