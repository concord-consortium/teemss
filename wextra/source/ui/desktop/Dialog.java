
package org.concord.waba.extra.ui;


public class Dialog extends waba.ui.Container{
  extra.ui.Popup popup=null;
  boolean wasDown = false;
  String title;
 waba.fx.Font font;
 private int widthBorder = 3;
 private int heightBorder = 3;
 org.concord.waba.extra.event.ActionListener	listener;//dima

 public Dialog(String title){
  	this.title = title;
  	font = new waba.fx.Font("Helvetica", waba.fx.Font.BOLD, 12);
  }
  public Dialog(){
  	this("");
  }
  public waba.fx.Font getFont(){return font;}
	public void addActionListener(org.concord.waba.extra.event.ActionListener l){
		if(listener == null){
			listener = l;
		}
	}

	public void removeActionListener(org.concord.waba.extra.event.ActionListener l){
		if(listener == l){
			listener = null;
		}
	}
 
  public static void showMessageDialog(org.concord.waba.extra.event.ActionListener l,String title,String message,String buttonTitle){
  	Dialog d = new Dialog(title);
  	waba.fx.FontMetrics fm = d.getFontMetrics(d.getFont());
	int messageWidth 	= fm.getTextWidth(message);
	int titleWidth 		= fm.getTextWidth(title);
	int bWidth = fm.getTextWidth(buttonTitle) + 10;
	int w = (messageWidth > titleWidth)?messageWidth:titleWidth;
	if(w < bWidth) w = bWidth;
	w += 20;
	int bHeight = 20;
	int mHeight = fm.getHeight();
	int h = 15 + bHeight + 10 + (10 + mHeight);
	d.setRect(50,50,w,h);
	waba.ui.Button b = new waba.ui.Button(buttonTitle);
	b.setRect(w/2 - bWidth/2,h - 5 - bHeight,bWidth,bHeight);
	d.add(b);
	waba.ui.Label label = new waba.ui.Label(message,waba.ui.Label.CENTER);
	label.setRect(w/2 - messageWidth/2,20,messageWidth,mHeight);
	d.add(label);
	d.addActionListener(l);
	d.show();
  }
  
   public void drawBorder(waba.fx.Graphics g){
 	if(waba.applet.Applet.currentApplet.isColor)
   		g.setColor(0, 0, 128);
 	else
   		g.setColor(0, 0, 0);
   	for(int i = widthBorder; i >= 0; i--){
		g.drawLine(i, widthBorder - i, width - i, widthBorder - i);
	}
   	for(int i = 0; i <= 15 - widthBorder; i++){
		g.drawLine(0, widthBorder + i, width,  widthBorder + i);
   	}
   	for(int i = widthBorder; i >= 0; i--){
		g.drawLine(i, height - (widthBorder - i), width - i, height - (widthBorder - i));
	}
   	for(int i = 0; i <= widthBorder; i++){
		g.drawLine(i, 15, i, height - widthBorder );
		g.drawLine(width - widthBorder+i, 15, width-widthBorder+i, height - widthBorder );
   	}
   }
   public void drawTitle(waba.fx.Graphics g){
  	waba.fx.FontMetrics fm = getFontMetrics(font);
	int boxWidth = fm.getTextWidth(title) + 8;
	g.setColor(255, 255, 255);
	g.setFont(font);
	g.drawText(title, 4, 2);
 }
   public void onPaint(waba.fx.Graphics g){
 	g.setColor(200, 200, 200);
	g.fillRect(widthBorder,15,width-2*widthBorder,height - 15 - widthBorder);
  	drawBorder(g);
     	drawTitle(g);
	
 }
 
	public void show(){
		if(popup != null) return;
		popup = new extra.ui.Popup(this);
		popup.popup();
		waba.ui.MainWindow mw = waba.ui.MainWindow.getMainWindow();
		if(mw instanceof org.concord.waba.extra.ui.ExtraMainWindow){
			((org.concord.waba.extra.ui.ExtraMainWindow)mw).setDialog(this);
		}
	}
	public void hide(){
		if(popup == null) return;
		popup.unpop();
		popup = null;
		waba.ui.MainWindow mw = waba.ui.MainWindow.getMainWindow();
		if(mw instanceof org.concord.waba.extra.ui.ExtraMainWindow){
			((org.concord.waba.extra.ui.ExtraMainWindow)mw).setDialog(null);
		}
	}
  public void onEvent(waba.ui.Event event){
	if (event.type == waba.ui.ControlEvent.PRESSED){
		if(listener != null){
			String message = "";
			if(event.target instanceof waba.ui.Button){
				message = ((waba.ui.Button)event.target).getText();
			}
			listener.actionPerformed(new org.concord.waba.extra.event.ActionEvent(this,(waba.ui.Control)event.target,message));
		}
		hide();
	}else if(event.type == waba.ui.PenEvent.PEN_UP){
	  	wasDown = false;
	}
  }
}

