package org.concord.waba.extra.ui;

import waba.ui.*;
import waba.fx.*;
import waba.sys.*;

//###############################################################
public class DragContext {
//###############################################################
static public final int RightButton = 0x1;
static public final int ShiftPressed = 0x2;
static public final int CtrlPressed = 0x4;
static public final int AltPressed = 0x8;
static public final int NoDragOver = 0x10;

public int modifiers = 0;
public Point start = new Point(0,0), curPoint = new Point(0,0);
public Point point1, point2;
public boolean didDrag = false;
public int resolution = 3;
public PenEvent penEvent;

public DragContext(){}
public DragContext(PenEvent ev) {set(ev);}
//==================================================================
public void set(PenEvent ev,Point where)
//==================================================================
{
	start.move(where.x,where.y);
	curPoint.move(where.x,where.y);
	modifiers = 0;
	penEvent = ev;
}
//==================================================================
public void set(PenEvent ev)
//==================================================================
{
	set(ev,new Point(ev.x,ev.y));
}
//==================================================================
public boolean hasDragged(Point newPoint)
//==================================================================
{
	int d = newPoint.x-curPoint.x; if (d < 0) d = -d;
	if (d >= resolution) return true; 
	d = newPoint.y-curPoint.y; if (d < 0) d = -d;
	return (d >= resolution);
}

//###############################################################
}
//###############################################################

