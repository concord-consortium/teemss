package org.concord.waba.extra.ui;

import org.concord.waba.extra.io.*;

public class CCPen{
	public byte w = 1;
	public byte h = 1;
	public int red = 0;
	public int green = 0;
	public int blue = 0;
    public final static boolean isColor = true;
	public final static int BEGIN_PEN_ITEM = 10010;
	public final static int END_PEN_ITEM = 10011;

	public CCPen(){
		w = 1;
		h = 1;
		red 		= 0;
		green 	= 0;
		blue 		= 0;
	}
	public void writeExternal(DataStream out){
		out.writeInt(BEGIN_PEN_ITEM);
		out.writeByte(w);
		out.writeByte(h);
		out.writeInt(red);
		out.writeInt(green);
		out.writeInt(blue);
		out.writeInt(0);//reserved
		out.writeInt(END_PEN_ITEM);
	}
	
	public void readExternal(DataStream in){
		int temp = in.readInt();
		if(temp != BEGIN_PEN_ITEM) return;
		w = in.readByte();
		h = in.readByte();
		red = in.readInt();
		green = in.readInt();
		blue = in.readInt();
		in.readInt();//reserved
		in.readInt();//END_PEN_ITEM
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
