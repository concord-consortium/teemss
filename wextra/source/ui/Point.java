package org.concord.waba.extra.ui;

//##################################################################
public class Point {
	//##################################################################
	public int x,y;
	public Point() {this(0,0);}
	public Point(int xx,int yy) {x = xx; y = yy;}
	public void move(int toX,int toY) {x = toX; y = toY;}
	public void translate(int dx,int dy) {x += dx; y += dy;}
	public String toString() {return "("+x+","+y+")";}
	//##################################################################
}
//##################################################################

