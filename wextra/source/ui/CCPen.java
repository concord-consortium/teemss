package org.concord.waba.extra.ui;

public class CCPen{
public byte w = 1;
public byte h = 1;
public int red = 0;
public int green = 0;
public int blue = 0;
    public final static boolean isColor = true;

	public CCPen(){
		w = 1;
		h = 1;
		red 		= 0;
		green 	= 0;
		blue 		= 0;
	}
	public void setPenSize(byte w,byte h){
		this.w = w;
		this.h = h;
		if(this.w < 1) this.w = 1;
		if(this.h < 1) this.h = 1;
	}
	public void setPenColor(int red,int green,int blue){
		if(isColor){
			this.red = red;
			this.green = green;
			this.blue = blue;
		}else{
			this.red = (red > 0)?255:0;
			this.green = (red > 0)?255:0;
			this.blue = (red > 0)?255:0;
		}
	}
}
