package org.concord.waba.extra.ui;

import waba.ui.*;
import waba.fx.*;
import waba.sys.*;

//##################################################################
public class AdvControl extends Control{
//##################################################################
public String name = "";
public String text = "";
//==================================================================
// Most of these modifiers are not used in this AdvControl.
// They are all used in mControl in the mWaba toolkit.
//==================================================================
public static final int
	Disabled = 0x1, NotEditable = 0x2, Invisible = 0x4, PreferredSizeOnly = 0x8,
	NoFocus = 0x10, NotAnEditor = 0x20, AlwaysEnabled = 0x40, CapturesPen = 0x80,
	WantHoldDown = 0x100, WantDrag = 0x200;
//------------------------------------------------------------------
// These are used for PenEvents
//------------------------------------------------------------------
protected static final int
	GotPenDown = 0x400, DidHoldDown = 0x800;
//==================================================================
protected int modifiers = WantDrag;
//==================================================================
public void modify(int flagsToSet,int flagsToClear)
//==================================================================
{
	modifiers |= flagsToSet;
	modifiers &= ~flagsToClear;
}
//==================================================================
public boolean hasModifier(int flag) {return (modifiers & flag) != 0;}
//==================================================================

//==================================================================
public void onEvent(Event ev)
//==================================================================
{
	if (ev instanceof PenEvent)
		onPenEvent((PenEvent)ev);
	else if (ev instanceof KeyEvent){
		onKeyEvent((KeyEvent)ev);
	}else if (ev instanceof ControlEvent){
		onControlEvent((ControlEvent)ev);
	}
	super.onEvent(ev);
}
//==================================================================
public void onTimerEvent(Timer t){}
public void onKeyEvent(KeyEvent ev){}
public void onControlEvent(ControlEvent ev){}
//==================================================================

//..................................................................
Point curPoint = new Point(0,0), pressPoint = new Point(0,0);
boolean firstPress = true;
DragContext dragging;
public int dragResolution = 3, startDragResolution  = 3;
//..................................................................

//==================================================================
public void onPenEvent(PenEvent ev)
//==================================================================
{
	curPoint.move(ev.x,ev.y);
//..................................................................
// Pen down.
//..................................................................
	if (ev.type == ev.PEN_DOWN) {
		if (!firstPress) return;
		firstPress = false;
		modify(GotPenDown,DidHoldDown);
		//..................................................................
		// Get ready for dragging if necessary.
		//..................................................................
		dragging = new DragContext(ev);
		dragging.set(ev);
		dragging.resolution = startDragResolution;
		penPressed(curPoint);
		//if (hasModifier(WantHoldDown)) holdId = mApp.requestTick(this,holdDownPause);
//..................................................................
// Pen up.
//..................................................................
	}else if (ev.type == ev.PEN_UP) {
		firstPress = true;
		if (!hasModifier(GotPenDown)) return;
		modify(0,GotPenDown);
		//..................................................................
		// Insert code for checking for click or double-click here.
		//..................................................................
		
		// click check
		
		//..................................................................
		// If we were dragging, then it is time to stop.
		//..................................................................
		if (dragging.didDrag && hasModifier(WantDrag)){
			dragging.curPoint.move(curPoint.x,curPoint.y);
			dragging.penEvent = ev;
			stopDragging(dragging);
		}else {
		//..................................................................
		// If we were not dragging, then do penClicked() or penReleased()
		//..................................................................
			if (hasModifier(DidHoldDown)) penReleased(curPoint);
			else penClicked(curPoint);
		}
		//if (doDouble) penDoubleClicked(curPoint);
//..................................................................
// Pen drag.
//..................................................................
	}else if (ev.type == ev.PEN_DRAG){
		if (!hasModifier(GotPenDown)) return;
		if (hasModifier(WantDrag)){
			if (!dragging.hasDragged(curPoint)) return;
			dragging.resolution = dragResolution;
			dragging.curPoint.move(curPoint.x,curPoint.y);
			dragging.penEvent = ev;
			if (!dragging.didDrag) startDragging(dragging);
			else dragged(dragging);
		}
		dragging.didDrag = true;
	}
}
//==================================================================
public void onPaint(Graphics g) 
//==================================================================
{
	doPaint(g,getRect());
}
//==================================================================
public void penPressed(Point p) {/*System.out.println(name+" Pressed: "+p);*/}
public void penClicked(Point p) {penReleased(p);}
public void penDoubleClicked(Point p) {penClicked(p);}
//==================================================================
public void startDragging(DragContext dc) {dragged(dc);}
public void dragged(DragContext dc) {/*System.out.println(name+" Dragged: "+dc.curPoint);*/}
public void stopDragging(DragContext dc) {penReleased(dc.curPoint);}
//==================================================================
public void penHeld(Point p){}
public void penReleased(Point p) {/*System.out.println(name+" Released: "+p);*/}
//==================================================================
//==================================================================
protected boolean isOnMe(Point p)
//==================================================================
{
	Rect _rect = getRect();
	if (p.x<0 || p.x>=_rect.width) return false;
	return !(p.y<0 || p.y>=_rect.height);
}

//==================================================================
public Font font = new Font("Helvetica",Font.PLAIN,12);
public FontMetrics getFontMetrics() {return getFontMetrics(font);}
//==================================================================
//==================================================================
public void repaintDataNow() 
//==================================================================
{
	Graphics g = createGraphics();
	if(g == null) return;
	doPaintData(g);
	g.free();
}
//==================================================================
public void doPaintData(Graphics g){}
//==================================================================
//==================================================================
public void repaintNow() {repaintNow(null,null);}
//==================================================================
public void repaintNow(Graphics gr,Rect where)
//==================================================================
{
	Graphics g = gr;
	if (g == null) g = createGraphics();
	if (g == null) return;
	if (where == null) where = getRect();
	doPaint(g,where);
	if (gr == null) g.free();
}
//==================================================================
public void doPaint(Graphics g,Rect area)
//==================================================================
{
	Rect r = getRect();
	g.setColor(0,0,0); 
	g.drawRect(0,0,r.width,r.height);
	g.drawLine(0,0,r.width,r.height);
	g.drawLine(0,r.height,r.width,0);
}
//##################################################################
}
//##################################################################

