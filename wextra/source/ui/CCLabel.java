package org.concord.waba.extra.ui;
import waba.ui.MainWindow;
import waba.ui.Control;
import waba.ui.Label;
import waba.fx.Font;

public class CCLabel extends Control
{
String text;
Font font;
int align;

int bRed = 255,bGreen = 255,bBlue = 255;
int tRed = 0,tGreen = 0,tBlue = 0;
	public CCLabel(String text){
		this(text, Label.LEFT);
	}
	public CCLabel(String text, int align){
		this.text = (text == null)?"":text;
		this.align = align;
		this.font = MainWindow.defaultFont;
	}
	
	public void setFont(Font font){
		this.font = (font == null)?MainWindow.defaultFont:font;
	}
	public Font getFont(){ return font;}
	
	public void setBackColor(int r,int g,int b){
		bRed = r;
		bGreen = g;
		bBlue = b;
	}
	public void setTextColor(int r,int g,int b){
		tRed = r;
		tGreen = g;
		tBlue = b;
	}
	public void setText(String text){
		this.text = (text == null)?"":text;
		repaint();
	}
	public void setText(String text,boolean redraw){
		this.text = (text == null)?"":text;
		if(redraw) repaint();
	}
	
	public String getText(){ return text;}
	
	public void onPaint(waba.fx.Graphics g){
		g.setColor(bRed, bGreen, bBlue);
		g.fillRect(0,0,width,height);
		g.setColor(tRed, tGreen, tBlue);
		g.setFont(font);
		waba.fx.FontMetrics fm = getFontMetrics(font);
		int x = 0;
		int y = (this.height - fm.getHeight()) / 2;
		if (align == Label.CENTER)		x = (this.width - fm.getTextWidth(text)) / 2;
		else if (align == Label.RIGHT)	x = this.width - fm.getTextWidth(text);
		g.drawText(text, x, y);
		g.setColor(0, 0, 0);
	}
}
