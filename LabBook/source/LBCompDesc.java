package org.concord.LabBook;import waba.ui.*;import org.concord.waba.extra.ui.*;public class LBCompDesc{int 	lineBefore;int 	w, h;int 	alignment;boolean wrapping;Control	control;Menu	menu = null;final static int ALIGNMENT_LEFT = 0;final static int ALIGNMENT_RIGHT = 1;	LBCompDesc(int lineBefore,int w, int h,int alignment, boolean wrapping){		this.lineBefore		= lineBefore;		this.w				= w;		this.h				= h;		this.alignment		= alignment;		this.wrapping		= wrapping;		control				= null;	}		public void setControl(Control	control){this.control = control;}	public Control getControl(){return control;}	public void setMenu(Menu	menu){this.menu = menu;}	public Menu getMenu(){return menu;}}