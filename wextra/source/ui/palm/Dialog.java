package org.concord.waba.extra.ui;

import waba.ui.*;
import waba.util.*;
import waba.fx.*;

import org.concord.waba.extra.event.*;

public class Dialog extends waba.ui.Container{
	Popup popup=null;
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
	
	static waba.util.Vector openDialogs = new waba.util.Vector();


	private waba.ui.Container		contentPane;

    public static boolean showImages = true;

	public Dialog(String title){
		this.title = title;
		font = waba.ui.MainWindow.defaultFont;
		contentPane = null;
	}
	public Dialog(){
		this("");
	}
  
	public void setRect(int x,int y,int width,int height){
		super.setRect(x,y,width,height);
		boolean doSetContent = false;
		if(contentPane == null){
			contentPane = new waba.ui.Container();
			add(contentPane);
			doSetContent = true;
		}
		contentPane.setRect(widthBorder+1,17,width-1-(widthBorder)*2,height-17-heightBorder);
		if(doSetContent) setContent();
	}

	public void setContent(){}
  
	public waba.ui.Container getContentPane(){return contentPane;}
  
	public void setTitle(String title){
		this.title = title;
		repaint();
	}
  
	public Font getFont(){return font;}
	public void addDialogListener(DialogListener l){
		if(listener == null){
			listener = l;
		}
	}

	public void removeDialogListener(org.concord.waba.extra.event.DialogListener l){
		if(listener == l){
			listener = null;
		}
	}
	public static Dialog showAboutDialog(String title,String messages[]){
		Dialog d = new Dialog(title);
		waba.fx.FontMetrics fm = d.getFontMetrics(d.getFont());
		int bHeight = 15;
		int h = bHeight+2;
		int maxWith = 10;
		String	bstring = "Done";
		int mHeight = fm.getHeight();
		if(messages != null){
			for(int i = 0; i < messages.length; i++){
				if(maxWith < (fm.getTextWidth(messages[i]) + 1)){
					maxWith = fm.getTextWidth(messages[i]) + 1;
				}
				h += (2+mHeight);
			}
		}
		int w = maxWith;
		d.setRect(0,0,w+d.widthBorder*2,h+15+d.heightBorder);
		waba.ui.Container cp = d.getContentPane();
		waba.ui.Button b = new waba.ui.Button(bstring);
		cp.add(b);
		int bW = fm.getTextWidth(bstring) + 6;
		b.setRect(w/2 - bW/2,h - bHeight-1,bW ,bHeight);
		if(messages != null){
			int yLabel = 2;
			for(int i = 0; i < messages.length; i++){
				waba.ui.Label label = new waba.ui.Label(messages[i],waba.ui.Label.CENTER);
				int messageWidth 	= fm.getTextWidth(messages[i]);
				cp.add(label);
				label.setRect(w/2 - messageWidth/2,yLabel,messageWidth,mHeight);
				yLabel += (2 + mHeight);
			}
		}
		d.show();
		return d;
	}

	public static Dialog showConfirmDialog(org.concord.waba.extra.event.DialogListener l,
										   String title,String message,String []buttonTitles,int messageType)
	{
		if(buttonTitles == null) return null;

		Dialog d = new Dialog(title);
		FontMetrics fm = d.getFontMetrics(d.getFont());

		Vector lines = new Vector();
		int messageWidth = parseMessage(message, fm, lines);

		int titleWidth 		= fm.getTextWidth(title);
		int bWidth = 0;
		for(int i = 0; i < buttonTitles.length; i++){
			bWidth += (fm.getTextWidth(buttonTitles[i]) + 12);
		}
		int w = (messageWidth > titleWidth)?messageWidth:titleWidth;
		if(w < bWidth) w = bWidth;
		if(showImages){
			w += 20 + 20;//space + image
		} else {
			w += 20;
		}

		int bHeight = 15;
		int mHeight = fm.getHeight();

		int h = 15 + bHeight + 10 + (15 + mHeight*(lines.getCount() + 1));
		d.setRect(1,50,w,h);
		Container cp = d.getContentPane();
		Rect cpRect = cp.getRect();
		h = cpRect.height;
		w = cpRect.width;

		int xButtonCurr = w/2 - bWidth/2;
		for(int i = 0; i < buttonTitles.length; i++){
			Button b = new Button(buttonTitles[i]);
			int bW = fm.getTextWidth(buttonTitles[i]) + 6;
			b.setRect(xButtonCurr+3,h - 5 - bHeight,bW ,bHeight);
			xButtonCurr += (bW + 6);
			cp.add(b);
		}

		int imageOffset = 0;
		if(showImages){
			imageOffset = 10;
		}
		for(int i=0; i<lines.getCount(); i++){
			Label label = new waba.ui.Label((String)lines.get(i),Label.CENTER);
			label.setRect(imageOffset + w/2 - messageWidth/2,
						  20+i*mHeight,
						  messageWidth,mHeight);
			cp.add(label);		
		}

		String imagePath = "";
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
		if(showImages){
			ImagePane ip = (ImagePane)new ImagePane(imagePath);
			ip.setRect(d.widthBorder + 2,17,16,16);
			cp.add(ip);
		}
		d.addDialogListener(l);
		d.show();
		return d;
	}
 
	static int parseMessage(String message, FontMetrics fm, Vector lines)
	{
		// split message into lines;
		char [] mChars = message.toCharArray();
		int lineStart = 0;
		int messageWidth = 0;
		String newLine;
		int newMWidth;
		for(int i=0; i<mChars.length; i++){
			if(mChars[i] == '|'){
				newLine = new String(mChars, lineStart, i-lineStart);
				newMWidth = fm.getTextWidth(newLine);
				if(newMWidth > messageWidth) messageWidth = newMWidth;
				lines.add(newLine);
				lineStart = i+1;
			}
		}

		newLine = new String(mChars, lineStart, mChars.length-lineStart);
		newMWidth = fm.getTextWidth(newLine);
		if(newMWidth > messageWidth) messageWidth = newMWidth;
		lines.add(newLine);
		
		return messageWidth;
	}

	public static Dialog showMessageDialog(DialogListener l, String title,
										   String message,String buttonTitle,int messageType)
	{
		String [] buttons = {buttonTitle};

		return showConfirmDialog(l, title, message, buttons, messageType);
	}

	public static Dialog showInputDialog(org.concord.waba.extra.event.DialogListener l,
										 String title,String message,String []buttonTitles,int messageType){
		return showInputDialog(l,title,message,buttonTitles,messageType,null,null);
	}
	public static Dialog showInputDialog(org.concord.waba.extra.event.DialogListener l,String title,
										 String message,String []buttonTitles,int messageType,String []choices){
		return showInputDialog(l,title,message,buttonTitles,messageType,choices,null);
	}

	public static Dialog showInputDialog(org.concord.waba.extra.event.DialogListener l,String title,String message,
										 String []buttonTitles,int messageType,String []choices, String defStr){
		if(buttonTitles == null) return null;
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
		int bHeight = 15;
		int mHeight = fm.getHeight();
		int h = 15 + bHeight + 10 + (15 + 2*mHeight);
		if(choices != null &&
		   choices.length > 2) h += mHeight*(choices.length-2);

		d.setRect(5,5,w,h);
		waba.ui.Container cp = d.getContentPane();
		h -= 15;

		int xButtonCurr = w/2 - bWidth/2;
		for(int i = 0; i < buttonTitles.length; i++){
			waba.ui.Button b = new waba.ui.Button(buttonTitles[i]);
			int bW = fm.getTextWidth(buttonTitles[i]) + 6;
			b.setRect(xButtonCurr+3,h - 5 - bHeight,bW ,bHeight);
			xButtonCurr += (bW + 6);
			cp.add(b);
		}

		waba.ui.Label label = new waba.ui.Label(message,waba.ui.Label.CENTER);
		label.setRect(10 + w/2 - messageWidth/2,2,messageWidth,mHeight);
		cp.add(label);

		int editWidth =w - 10 - 10;
		if(messageType == EDIT_INP_DIALOG){
			d.inpControl = new waba.ui.Edit();
			cp.add(d.inpControl);
			d.inpControl.setRect(20,7 + mHeight ,d.width - 24,mHeight+5);
			if(defStr != null) ((waba.ui.Edit)d.inpControl).setText(defStr);
		}else if(messageType == CHOICE_INP_DIALOG){
			d.inpControl = new Choice(choices);
			d.inpControl.setRect(20,7 + mHeight ,d.width - 24,mHeight+5);
			if(defStr != null) ((Choice)d.inpControl).setSelectedIndex(defStr);
			cp.add(d.inpControl);
		}
		if(showImages){
			ImagePane ip = (ImagePane)new ImagePane("QuestionSmall.bmp");
			ip.setRect(d.widthBorder + 2,17,16,16);
			cp.add(ip);
		}
		d.addDialogListener(l);
		d.show();
		return d;
	}
  
	public void drawBorder(waba.fx.Graphics g){
		// palm style
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
		// palm style
		g.setColor(255,255,255);

		g.fillRect(widthBorder,15,width-2*widthBorder,height - 15 - widthBorder);
		drawBorder(g);
     	drawTitle(g);
	}
 

 	public waba.ui.Window getWabaWindow(){return waba.ui.MainWindow.getMainWindow();}
	public void show(){
		if(popup != null) return;
		popup = new Popup(this);
		popup.popup();
		waba.ui.MainWindow mw = waba.ui.MainWindow.getMainWindow();
		if(mw instanceof ExtraMainWindow){
			((ExtraMainWindow)mw).setDialog(this);
			openDialogs.add(this);
		}
		/*
		  if(inpControl != null &&
		  inpControl instanceof waba.ui.Edit){
		  getWabaWindow().setFocus(inpControl);
		  }
		*/
	}
	public void hide(){
		if(popup == null) return;
		popup.unpop();
		popup = null;
		waba.ui.MainWindow mw = waba.ui.MainWindow.getMainWindow();
		if(mw instanceof org.concord.waba.extra.ui.ExtraMainWindow){
			((org.concord.waba.extra.ui.ExtraMainWindow)mw).setDialog(null);
			int index = openDialogs.find(this);
			if(index >= 0) openDialogs.del(index);
			if(openDialogs.getCount() > 0){
				Dialog topD = (Dialog)openDialogs.get(openDialogs.getCount() -1);
				((org.concord.waba.extra.ui.ExtraMainWindow)mw).setDialog(topD);
			} 
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
				}else if(event.target instanceof List){
					return;
				}
				if(inpControl != null){
					if(inpControl instanceof waba.ui.Edit){
						info = ((waba.ui.Edit)inpControl).getText();
						infoType = org.concord.waba.extra.event.DialogEvent.EDIT;
					}else if(inpControl instanceof Choice){
						info = ((List)inpControl).getSelected();
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
