package org.concord.LabBook;

import waba.ui.*;
import org.concord.waba.extra.ui.*;
import extra.io.*;

public class LBCompDesc{
public int 	lineBefore;
public int 	w, h;
public int 	alignment;
public boolean wrapping;
public boolean link = false;
public Object	control;
//Control	control;
public final static int ALIGNMENT_LEFT = 0;
public final static int ALIGNMENT_RIGHT = 1;
public int	linkColor = 0x0000FF;

	public LBCompDesc(int lineBefore,int w, int h,int alignment, boolean wrapping,boolean link){
		this.lineBefore		= lineBefore;
		this.w				= w;
		this.h				= h;
		this.alignment		= alignment;
		this.wrapping		= wrapping;
		this.link			= link;
		control				= null;
	}
	public LBCompDesc(DataStream in){
		readExternal(in);
	}
	
	public void setObject(Object	control){this.control = control;}
	public Object getObject(){return control;}

    public void writeExternal(DataStream out){
    	out.writeInt(lineBefore);
    	out.writeInt(w);
    	out.writeInt(h);
    	out.writeInt(alignment);
    	out.writeBoolean(wrapping);
    	out.writeBoolean(link);
		out.writeInt(linkColor);
    }

    public void readExternal(DataStream in){
    	lineBefore = in.readInt();
    	w = in.readInt();
    	h = in.readInt();
    	alignment = in.readInt();
    	wrapping = in.readBoolean();
    	link 	= in.readBoolean();
		linkColor = in.readInt();
    }



	
	public String toString(){
		String str = "LBCompDesc ";
		str += ("w "+w+"; h "+h+"; alignment "+alignment+"; wrapping "+wrapping+"; obj "+control);
		return str;
	}
	
}
