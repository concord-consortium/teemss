
package org.concord.waba.extra.ui;


public class Dialog extends java.awt.Dialog implements java.awt.event.ActionListener{
  extra.ui.Popup popup=null;
  boolean wasDown = false;
  String title;
 waba.fx.Font font;
private int widthBorder = 3;
private int heightBorder = 3;
 org.concord.waba.extra.event.DialogListener	listener;//dima
 java.awt.Component	inpControl = null;
 public final static  int  DEF_DIALOG = 0;
 public final static  int  ERR_DIALOG = 1;
 public final static  int  WARN_DIALOG = 2;
 public final static  int  INFO_DIALOG = 3;
 public final static  int  QUEST_DIALOG = 4;
 public final static  int  EDIT_INP_DIALOG = 5;
 public final static  int  CHOICE_INP_DIALOG = 6;

 public Dialog(String title){
 	super((java.awt.Frame)waba.applet.Applet.currentApplet.getParent(),title,false);
 	setResizable(false);
	setLayout(null);
  }
  public Dialog(){
  	this("");
  }
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
  	java.awt.FontMetrics fm = d.getFontMetrics(d.getFont());
	int messageWidth 	= fm.stringWidth(message);
	int titleWidth 		= fm.stringWidth(title);
	int bWidth = 0;
	for(int i = 0; i < buttonTitles.length; i++){
		bWidth += (fm.stringWidth(buttonTitles[i]) + 12);
	}
	int w = (messageWidth > titleWidth)?messageWidth:titleWidth;
	if(w < bWidth) w = bWidth;
	w += 20 + 20;//space + image
	int bHeight = 20;
	int mHeight = fm.getHeight();
	int h = 15 + bHeight + 10 + (10 + mHeight);
	d.setSize(w,h);
	d.setLocation(50,50);
	int xButtonCurr = w/2 - bWidth/2;
	for(int i = 0; i < buttonTitles.length; i++){
		java.awt.Button b = new java.awt.Button(buttonTitles[i]);
		int bW = fm.stringWidth(buttonTitles[i]) + 6;
		b.setSize(bW ,bHeight);
		b.setLocation(xButtonCurr+3,h - 5 - bHeight);
		xButtonCurr += (bW + 6);
		b.setFont(d.getFont());
		b.addActionListener(d);
		d.add(b);
	}
	java.awt.Label label = new java.awt.Label(message,java.awt.Label.CENTER);
	label.setSize(messageWidth,mHeight);
	label.setLocation(10 + w/2 - messageWidth/2,20);
	label.setFont(d.getFont());
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
	ip.setSize(16,16);
	ip.setLocation(d.widthBorder + 2,17);
	d.add(ip);
	d.addDialogListener(l);
	d.show();
  }
 
  public static void showMessageDialog(org.concord.waba.extra.event.DialogListener l,String title,String message,String buttonTitle,int messageType){
  	Dialog d = new Dialog(title);
  	java.awt.FontMetrics fm = d.getFontMetrics(d.getFont());
	int messageWidth 	= fm.stringWidth(message);
	int titleWidth 		= fm.stringWidth(title);
	int bWidth = fm.stringWidth(buttonTitle) + 10;
	int w = (messageWidth > titleWidth)?messageWidth:titleWidth;
	if(w < bWidth) w = bWidth;
	w += 20 + 20;//space + image
	int bHeight = 20;
	int mHeight = fm.getHeight();
	int h = 15 + bHeight + 10 + (10 + mHeight);
	d.setSize(w,h);
	d.setLocation(50,50);
	java.awt.Button b = new java.awt.Button(buttonTitle);
	b.setFont(d.getFont());
	d.add(b);
	b.setSize(bWidth,bHeight);
	b.setLocation(w/2 - bWidth/2,h - 5 - bHeight);
	b.addActionListener(d);
	
	java.awt.Label label = new java.awt.Label(message,java.awt.Label.CENTER);
	d.add(label);
	label.setSize(messageWidth,mHeight);
	label.setLocation(10 + w/2 - messageWidth/2,20);
	label.setFont(d.getFont());
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
	d.add(ip);
	ip.setSize(16,16);
	ip.setLocation(d.widthBorder + 2,17);
	d.addDialogListener(l);
	d.show();
  }
  public static void showInputDialog(org.concord.waba.extra.event.DialogListener l,String title,String message,String []buttonTitles,int messageType){
	showInputDialog(l,title,message,buttonTitles,messageType,null);
 }
  public static void showInputDialog(org.concord.waba.extra.event.DialogListener l,String title,String message,String []buttonTitles,int messageType,String []choices){
   	if(buttonTitles == null) return;
 	Dialog d = new Dialog(title);
  	java.awt.FontMetrics fm = d.getFontMetrics(d.getFont());
	int messageWidth 	= fm.stringWidth(message);
	int titleWidth 		= fm.stringWidth(title);
	int bWidth = 0;
	for(int i = 0; i < buttonTitles.length; i++){
		bWidth += (fm.stringWidth(buttonTitles[i]) + 12);
	}
	int w = (messageWidth > titleWidth)?messageWidth:titleWidth;
	if(w < bWidth) w = bWidth;
	w += 20 + 20;//space + image
	int bHeight = 20;
	int mHeight = fm.getHeight();
	int h = 15 + bHeight + 10 + (15 + 2*mHeight);
	d.setSize(w,h);
	d.setLocation(50,50);
	int xButtonCurr = w/2 - bWidth/2;
	for(int i = 0; i < buttonTitles.length; i++){
		java.awt.Button b = new java.awt.Button(buttonTitles[i]);
		int bW = fm.stringWidth(buttonTitles[i]) + 6;
		b.setSize(bW ,bHeight);
		b.setLocation(xButtonCurr+3,h - 5 - bHeight);
		xButtonCurr += (bW + 6);
		b.setFont(d.getFont());
		b.addActionListener(d);
		d.add(b);
	}

	java.awt.Label label = new java.awt.Label(message,java.awt.Label.CENTER);
	label.setSize(messageWidth,mHeight);
	label.setLocation(10 + w/2 - messageWidth/2,20);
	label.setFont(d.getFont());
	d.add(label);

	int editWidth =w - 10 - 10;
	if(messageType == EDIT_INP_DIALOG){
		d.inpControl = new java.awt.TextField();
		d.inpControl.setSize(d.getSize().width - 24,mHeight+5);
		d.inpControl.setLocation(20,25 + mHeight);
		d.inpControl.setFont(d.getFont());
		d.add(d.inpControl);
	}else if(messageType == CHOICE_INP_DIALOG){
		d.inpControl = new java.awt.Choice();
		if(choices != null){
			for(int c = 0; c < choices.length; c++){
				((java.awt.Choice)d.inpControl).addItem(choices[c]);
			}
		}
		d.inpControl.setSize(d.getSize().width - 24,mHeight+5);
		d.inpControl.setLocation(20,25 + mHeight);
		d.inpControl.setFont(d.getFont());
		d.add(d.inpControl);
	}
	ImagePane ip = new ImagePane("cc_extra/icons/QuestionSmall.bmp");
	ip.setSize(16,16);
	ip.setLocation(d.widthBorder + 2,17);
	d.add(ip);
	d.addDialogListener(l);
	d.show();
  }
  
 
/*
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

			if(event.target instanceof java.awt.Button){
				message = ((java.awt.Button)event.target).getText();
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
  */
  public void actionPerformed(java.awt.event.ActionEvent event){
		if(listener != null){
			String message = "";
			Object info = null;
			int infoType = org.concord.waba.extra.event.DialogEvent.UNKNOWN;
			message = event.getActionCommand();
			if(inpControl != null){
				if(inpControl instanceof java.awt.TextField){
					info = ((java.awt.TextField)inpControl).getText();
					infoType = org.concord.waba.extra.event.DialogEvent.EDIT;
				}else if(inpControl instanceof java.awt.Choice){
					info = ((java.awt.Choice)inpControl).getSelectedItem();
					infoType = org.concord.waba.extra.event.DialogEvent.CHOICE;
				}
			}
			listener.dialogClosed(new org.concord.waba.extra.event.DialogEvent(this,null,message,info,infoType));
		}
		hide();
		dispose();
  }
}
