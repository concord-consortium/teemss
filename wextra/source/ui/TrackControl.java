package org.concord.waba.extra.ui;

import waba.ui.*;
import waba.fx.*;

//##################################################################
public abstract class TrackControl extends AdvControl implements IScroll{
//##################################################################
{
	modify(WantDrag|WantHoldDown/*|FollowTracking*/,0);
}
//==================================================================
public abstract Rect getThumbArea();
public abstract void setThumbPos(int x,int y);
public abstract int getTrackTo();
//==================================================================

protected static final int LessThanThumb = 1, MoreThanThumb = 2, OnThumb = 3;
public int type = Horizontal;
//==================================================================
public int whichArea(Point p)
//==================================================================
{
	Rect t = getThumbArea();
	if (type == Horizontal){
		if (p.x < t.x) return LessThanThumb;
		else if (p.x >= t.x+t.width) return MoreThanThumb;
		else return OnThumb;
	}else{
		if (p.y < t.y) return LessThanThumb;
		else if (p.y >= t.y+t.height) return MoreThanThumb;
		else return OnThumb;
	}
}
int pressedArea;
boolean amTracking = false;

//==================================================================
public void generate(int what,int value){}
//==================================================================

//==================================================================
public void generatePage()
//==================================================================
{
	if (pressedArea == LessThanThumb) generate(PageLower,1);
	else if (pressedArea == MoreThanThumb) generate(PageHigher,1);
}
//==================================================================
public void penPressed(Point p)
//==================================================================
{
	pressedArea = whichArea(p);
	generatePage();
}
//==================================================================
public void penHeld(Point p)
//==================================================================
{
	int nowPressed = whichArea(p);
	if (nowPressed != pressedArea) return;
	generatePage();
}
//==================================================================
public void startDragging(DragContext dc)
//==================================================================
{
	if (pressedArea != OnThumb) return;
	Rect r = getThumbArea();
	dc.start.translate(-r.x,-r.y);
	amTracking = true;
}
//==================================================================
public void stopDragging(DragContext dc)
//==================================================================
{
	generate(TrackTo,getTrackTo());
	amTracking = false;
}
//==================================================================
public void dragged(DragContext dc)
//==================================================================
{
	if (pressedArea != OnThumb) return;
	//if (!isOnMe(dc.curPoint)) return;
	Rect r = getThumbArea();
	int x = r.x, y = r.y;
	setThumbPos(dc.curPoint.x-dc.start.x,dc.curPoint.y-dc.start.y);
	r = getThumbArea();
	if (r.x == x && r.y == y) return;
	if (hasModifier(FollowTracking)) generate(TrackTo,getTrackTo());
	repaintNow();
}


//##################################################################
}
//##################################################################

