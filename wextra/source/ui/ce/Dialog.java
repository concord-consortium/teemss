
package org.concord.waba.extra.ui;


public class Dialog extends waba.ui.Container{
  extra.ui.Popup popup=null;
  boolean wasDown = false;
  String title;
 waba.fx.Font font;
 protected int widthBorder = 3;
 protected int heightBorder = 3;
 protected org.concord.waba.extra.event.DialogListener	listener;//dima
 waba.ui.Control	inpControl = null;
 public final static  int  DEF_DIALOG = 0;
 public final static  int  ERR_DIALOG = 1;
 public final static  int  WARN_DIALOG = 2;
 public final static  int  INFO_DIALOG = 3;
 public final static  int  QUEST_DIALOG = 4;
 public final static  int  EDIT_INP_DIALOG = 5;
 public final static  int  CHOICE_INP_DIALOG = 6;

 public Dialog(String title){
  	this.title = title;
  	font = new waba.fx.Font("Helvetica", waba.fx.Font.BOLD, 12);
  }
  public Dialog(){
  	this("");
  }
  
  public void setTitle(String title){
  	this.title = title;
  	repaint();
  }
  
  public waba.fx.Font getFont(){return font;}
	public void addDialogListener(org.concord.waba.extra.event.DialogListener l){
		if(listener == null){
			listener = l;
		}
	}

	public void removeDialogListener(org.concord.waba.extra.event.DialogListener l){
		if(listener == l){
			listener = null;
		}
	}
  public static void showConfirmDialog(org.concord.waba.extra.event.DialogListener l,String title,String message,String []buttonTitles,int messageType){
  	if(buttonTitles == null) return;
  	Dialog d = new Dialog(title);
  	waba.fx.FontMetrics fm = d.getFontMetrics(d.getFont());
	int messageWidth 	= fm.getTextWidth(message);
	int titleWidth 		= fm.getTextWidth(title);
	int bWidth = 0;
	for(int i = 0; i < buttonTitles.length; i++){
		bWidth += (fm.getTextWidth(buttonTitles[i]) + 12);
	}
	int w = (messageWidth > titleWidth)?messageWidth:titleWidth;
	if(w < bWidth) w = bWidth;
	w += 20 + 20;//space + image
	int bHeight = 20;
	int mHeight = fm.getHeight();
	int h = 15 + bHeight + 10 + (10 + mHeight);
	d.setRect(50,50,w,h);
	int xButtonCurr = w/2 - bWidth/2;
	for(int i = 0; i < buttonTitles.length; i++){
		waba.ui.Button b = new waba.ui.Button(buttonTitles[i]);
		int bW = fm.getTextWidth(buttonTitles[i]) + 6;
		b.setRect(xButtonCurr+3,h - 5 - bHeight,bW ,bHeight);
		xButtonCurr += (bW + 6);
		d.add(b);
	}
	waba.ui.Label label = new waba.ui.Label(message,waba.ui.Label.CENTER);
	label.setRect(10 + w/2 - messageWidth/2,20,messageWidth,mHeight);
	d.add(label);
	String imagePath = "cc_extra/icons/";
	switch(messageType){
		default:
		case DEF_DIALOG:
		case INFO_DIALOG:
			imagePath += "InformSmall.bmp";
			break;
		case ERR_DIALOG:
			imagePath += "ErrorSmall.bmp";
			break;
		case WARN_DIALOG:
			imagePath += "WarnSmall.bmp";
			break;
		case QUEST_DIALOG:
			imagePath += "QuestionSmall.bmp";
			break;
	}
	ImagePane ip = new ImagePane(imagePath);
	ip.setRect(d.widthBorder + 2,17,16,16);
	d.add(ip);
	d.addDialogListener(l);
	d.show();
  }
 
  public static void showMessageDialog(org.concord.waba.extra.event.DialogListener l,String title,String message,String buttonTitle,int messageType){
  	Dialog d = new Dialog(title);
  	waba.fx.FontMetrics fm = d.getFontMetrics(d.getFont());
	int messageWidth 	= fm.getTextWidth(message);
	int titleWidth 		= fm.getTextWidth(title);
	int bWidth = fm.getTextWidth(buttonTitle) + 10;
	int w = (messageWidth > titleWidth)?messageWidth:titleWidth;
	if(w < bWidth) w = bWidth;
	w += 20 + 20;//space + image
	int bHeight = 20;
	int mHeight = fm.getHeight();
	int h = 15 + bHeight + 10 + (10 + mHeight);
	d.setRect(50,50,w,h);
	waba.ui.Button b = new waba.ui.Button(buttonTitle);
	b.setRect(w/2 - bWidth/2,h - 5 - bHeight,bWidth,bHeight);
	d.add(b);
	waba.ui.Label label = new waba.ui.Label(message,waba.ui.Label.CENTER);
	label.setRect(10 + w/2 - messageWidth/2,20,messageWidth,mHeight);
	d.add(label);
	String imagePath = "cc_extra/icons/";
	switch(messageType){
		default:
		case DEF_DIALOG:
		case INFO_DIALOG:
			imagePath += "InformSmall.bmp";
			break;
		case ERR_DIALOG:
			imagePath += "ErrorSmall.bmp";
			break;
		case WARN_DIALOG:
			imagePath += "WarnSmall.bmp";
			break;
		case QUEST_DIALOG:
			imagePath += "QuestionSmall.bmp";
			break;
	}
	ImagePane ip = new ImagePane(imagePath);
	ip.setRect(d.widthBorder + 2,17,16,16);
	d.add(ip);
	d.addDialogListener(l);
	d.show();
  }
  public static void showInputDialog(org.concord.waba.extra.event.DialogListener l,String title,String message,String []buttonTitles,int messageType){
  	showInputDialog(l,title,message,buttonTitles,messageType,null);
  }
  public static void showInputDialog(org.concord.waba.extra.event.DialogListener l,String title,String message,String []buttonTitles,int messageType,String []choices){
   	if(buttonTitles == null) return;
 	Dialog d = new Dialog(title);
  	waba.fx.FontMetrics fm = d.getFontMetrics(d.getFont());
	int messageWidth 	= fm.getTextWidth(message);
	int titleWidth 		= fm.getTextWidth(title);
	int bWidth = 0;
	for(int i = 0; i < buttonTitles.length; i++){
		bWidth += (fm.getTextWidth(buttonTitles[i]) + 12);
	}
	int w = (messageWidth > titleWidth)?messageWidth:titleWidth;
	if(w < bWidth) w = bWidth;
	w += 20 + 20;//space + image
	int bHeight = 20;
	int mHeight = fm.getHeight();
	int h = 15 + bHeight + 10 + (15 + 2*mHeight);
	d.setRect(50,50,w,h);
	int xButtonCurr = w/2 - bWidth/2;
	for(int i = 0; i < buttonTitles.length; i++){
		waba.ui.Button b = new waba.ui.Button(buttonTitles[i]);
		int bW = fm.getTextWidth(buttonTitles[i]) + 6;
		b.setRect(xButtonCurr+3,h - 5 - bHeight,bW ,bHeight);
		xButtonCurr += (bW + 6);
		d.add(b);
	}

	waba.ui.Label label = new waba.ui.Label(message,waba.ui.Label.CENTER);
	label.setRect(10 + w/2 - messageWidth/2,20,messageWidth,mHeight);
	d.add(label);

	int editWidth =w - 10 - 10;
	if(messageType == EDIT_INP_DIALOG){
		d.inpControl = new waba.ui.Edit();
		d.inpControl.setRect(20,25 + mHeight ,d.width - 24,mHeight+5);
		d.add(d.inpControl);
	}else if(messageType == CHOICE_INP_DIALOG){
		d.inpControl = new Choice(choices);
		d.inpControl.setRect(20,25 + mHeight ,d.width - 24,mHeight+5);
		d.add(d.inpControl);
	}
	ImagePane ip = new ImagePane("cc_extra/icons/QuestionSmall.bmp");
	ip.setRect(d.widthBorder + 2,17,16,16);
	d.add(ip);
	d.addDialogListener(l);
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
			Object info = null;
			int infoType = org.concord.waba.extra.event.DialogEvent.UNKNOWN;

			if(event.target instanceof waba.ui.Button){
				message = ((waba.ui.Button)event.target).getText();
			}else if(event.target instanceof extra.ui.List){
				return;
			}
			if(inpControl != null){
				if(inpControl instanceof waba.ui.Edit){
					info = ((waba.ui.Edit)inpControl).getText();
					infoType = org.concord.waba.extra.event.DialogEvent.EDIT;
				}else if(inpControl instanceof Choice){
					info = ((extra.ui.List)inpControl).getSelected();
					infoType = org.concord.waba.extra.event.DialogEvent.CHOICE;
				}
			}
			listener.dialogClosed(new org.concord.waba.extra.event.DialogEvent(this,(waba.ui.Control)event.target,message,info,infoType));
		}
		hide();
	}else if(event.type == waba.ui.PenEvent.PEN_UP){
	  	wasDown = false;
	}
  }
}