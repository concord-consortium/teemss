package org.concord.waba.extra.ui;

import waba.ui.*;
import waba.fx.*;
import waba.sys.*;
import waba.util.Vector;


public class CCTextArea  extends Control{
CCStringWrapper		[] lines;
FontMetrics 		fm = null;
protected Timer 	caretTimer = null;
protected boolean 	hasCursor = false,cursorOn = false;
			
			
	FontMetrics getFontMetrics(){return fm;}


	public void insertText(String str){
		if(str == null) return;
	}
	public void setText(String str){
		if(str == null) return;
	}
	public void onControlEvent(ControlEvent ev){
		if (ev.type == ev.TIMER && hasCursor) paintCursor(null);
		else if (ev.type == ev.FOCUS_IN) gotFocus();
		else if (ev.type == ev.FOCUS_OUT) lostFocus();
	}
	public void gotFocus(){
		hasCursor =  true;
		caretTimer = addTimer(500);
	//	checkScrolls();
	}
	public void lostFocus(){
//		clearSelection();
//		if (homeCursorOnLostFocus) newCursorPos(0,0,false);
//		checkScrolls();
		clearCursor();
		cursorOn = cursorOn = false;
		removeTimer(caretTimer);
	}
	protected void clearCursor() {
		if (cursorOn) paintCursor(null);
	}
	protected void paintCursor(Graphics gr){
		Graphics g = gr;
		if (g == null) g = createGraphics();
		if (hasCursor){
			Rect r = new Rect(0,0,0,0);
/*
			if (getCharRect(curState.cursorPos,curState.cursorLine,r)){
				g.drawCursor(r.x+spacing-curState.xShift,spacing+r.y-curState.firstLine*getItemHeight(),1,r.height);
				cursorOn = !cursorOn;
			}
*/
			g.drawCursor(10,10,1,8);
			cursorOn = !cursorOn;
		}
		if (gr == null) g.free();
	}

	public void onEvent(Event ev){
		if (ev instanceof PenEvent)
			onPenEvent((PenEvent)ev);
		else if (ev instanceof KeyEvent){
			onKeyEvent((KeyEvent)ev);
		}else if (ev instanceof ControlEvent){
			onControlEvent((ControlEvent)ev);
		}
		super.onEvent(ev);
	}

	public void onKeyEvent(KeyEvent ev){
	}
	
	public void onPenEvent(PenEvent ev){
	}
	
	public void onPaint(Graphics g){
		Rect r = getRect();
		g.setColor(255,255,255);
		g.fillRect(0,0,r.width,r.height);
		g.setColor(0,0,0);
		g.drawRect(0,0,r.width,r.height);
//		doPaintData(g,area);
	}

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
