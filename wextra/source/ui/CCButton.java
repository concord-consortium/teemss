package org.concord.waba.extra.ui;
import waba.ui.*;
import waba.fx.*;
import waba.sys.Vm;



public class CCButton extends Control
{
boolean 	enabled = true;
String 	text;
Font 		font;
boolean 	armed;
	private static int style = -1;
	final static int PALM_STYLE = 0;
	final static int WIN_STYLE = 1;

	public CCButton(String text){
		if(style == -1){
			if(Vm.getPlatform().equals("PalmOS")){
				style = PALM_STYLE;
			} else {
				style = WIN_STYLE;
			}
		}
		this.text = text;
		this.font = MainWindow.defaultFont;
	}

	
	public void setEnabled(boolean enabled){
		if(enabled == this.enabled) return;
		this.enabled = enabled;
		repaint();
	}
	public boolean isEnabled(){ return enabled;}


	public void setText(String text){
		this.text = text;
		repaint();
	}

	public String getText(){ return text;}
	
	public void onEvent(Event event){
		if(!isEnabled()) return;
		if (event.type == PenEvent.PEN_DOWN){
			armed = true;
			repaint();
		}else if (event.type == PenEvent.PEN_UP){
			armed = false;
			repaint();
			PenEvent pe = (PenEvent)event;
			if (pe.x >= 0 && pe.x < this.width && pe.y >= 0 && pe.y < this.height)
				postEvent(new ControlEvent(ControlEvent.PRESSED, this));
		}else if (event.type == PenEvent.PEN_DRAG){
			PenEvent pe = (PenEvent)event;
			boolean lArmed = false;
			if (pe.x >= 0 && pe.x < this.width && pe.y >= 0 && pe.y < this.height)
				lArmed = true;
			if (armed != lArmed){
				armed = lArmed;
				repaint();
			}
		}
	}
	public static void drawButton(Graphics g, boolean armed, int width, int height){
		boolean isColor = style==WIN_STYLE;

		int x2 = width - 1;
		int y2 = height - 1;
		if (!isColor){
		// draw top, bottom, left and right lines
			g.setColor(0, 0, 0);
			g.drawLine(3, 0, x2 - 3, 0);
			g.drawLine(3, y2, x2 - 3, y2);
			g.drawLine(0, 3, 0, y2 - 3);
			g.drawLine(x2, 3, x2, y2 - 3);
			if (armed) g.fillRect(1, 1, width - 2, height - 2);
			else{
			// draw corners (tl, tr, bl, br)
				g.drawLine(1, 1, 2, 1);
				g.drawLine(x2 - 2, 1, x2 - 1, 1);
				g.drawLine(1, y2 - 1, 2, y2 - 1);
				g.drawLine(x2 - 2, y2 - 1, x2 - 1, y2 - 1);
			// draw corner dots
				g.drawLine(1, 2, 1, 2);
				g.drawLine(x2 - 1, 2, x2 - 1, 2);
				g.drawLine(1, y2 - 2, 1, y2 - 2);
				g.drawLine(x2 - 1, y2 - 2, x2 - 1, y2 - 2);
			}
		}else{
		// top, left
			if (armed)		g.setColor(0, 0, 0);
			else			g.setColor(255, 255, 255);
			g.drawLine(0, 0, x2 - 1, 0);
			g.drawLine(0, 0, 0, y2 - 1);
		// top, left shadow
			if (armed){
				g.setColor(130, 130, 130);
				g.drawLine(1, 1, x2 - 1, 1);
				g.drawLine(1, 1, 1, y2 - 1);
			}
		// bottom, right
			if (armed)		g.setColor(255, 255, 255);
			else			g.setColor(0, 0, 0);
			g.drawLine(0, y2, x2, y2);
			g.drawLine(x2, y2, x2, 0);
		// bottom, right shadow
			if (!armed){
				g.setColor(130, 130, 130);
				g.drawLine(1, y2 - 1, x2 - 1, y2 - 1);
				g.drawLine(x2 - 1, y2 - 1, x2 - 1, 1);
			}
		}
	}

	public void onPaint(waba.fx.Graphics g){
		drawButton(g, armed, this.width, this.height);
		if (armed && style == PALM_STYLE)
			g.setColor(255, 255, 255);
		else
			g.setColor(0, 0, 0);
		g.setFont(font);
		FontMetrics fm = getFontMetrics(font);
		int x = (this.width - fm.getTextWidth(text)) / 2;
		int y = (this.height - fm.getHeight()) / 2;
		if (armed){
			x++;
			y++;
		}
		if(isEnabled())	g.setColor(0, 0, 0);
		else			g.setColor(255, 255, 255);
		g.drawText(text, x, y);
		g.setColor(0, 0, 0);
	}
}
