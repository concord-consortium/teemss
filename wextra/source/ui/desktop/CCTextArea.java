package org.concord.waba.extra.ui;

import waba.ui.*;
import waba.fx.*;
import waba.sys.*;
import waba.util.Vector;


public class CCTextArea  extends Control{
CCStringWrapper		[] lines;
FontMetrics 		fm = null;
protected Timer 	caretTimer = null;
protected boolean 	hasCursor = false,cursorOn = false;
protected Font font = new Font("Helvetica",Font.PLAIN,12);

protected	int firstLine = 0;

protected CCTextAreaState curState = new CCTextAreaState();
			
			
	public FontMetrics getFontMetrics() {
		if(fm == null){
			fm = getFontMetrics(font);
		}
		return fm;
	}
	protected int getItemHeight() {
		FontMetrics fm = getFontMetrics(); 
		return (fm == null)?0:fm.getHeight()+2;
	}


	public void insertText(String str){
		if(str == null) return;
		setText(str);//temporary
	}
	
	public String getText(){
		String retValue = "";
		if(lines == null) return retValue;
		for(int i = 0; i < lines.length; i++){
			retValue += (lines[i].getStr() + "\n");
		}
		return retValue;
	}
	
	public void setText(String str){
		lines = null;
		int determinedSystem = -1; //unix - 0; //mac - 1 dos - 2
		if(str == null) return;
		StringBuffer sb = new StringBuffer();
		int i = 0;
		int lastRow = 0;
		int	strLength = str.length();
		while(i < strLength){
			boolean wasEndOfLine = false;
			char c = str.charAt(i);
			if(c == '\n'){
				if(determinedSystem == -1) determinedSystem = 0;
				wasEndOfLine = true;
			}else if(c == '\r'){
				if(determinedSystem <= 0){
					determinedSystem = 1;
					if(i < strLength - 1){
						if(str.charAt(i+1) == '\n'){
							determinedSystem = 2;
							i++;
						}
					}
				}
				wasEndOfLine = true;
			}else{
				sb.append(c);
			}
			if(wasEndOfLine){
				int nLines = (lines == null)?0:lines.length;
				CCStringWrapper []newLines = new CCStringWrapper[nLines+1];
				if(lines != null){
					waba.sys.Vm.copyArray(lines,0,newLines,0,nLines);
				}
				lines = newLines;
				lines[nLines] = new CCStringWrapper(this,sb.toString(),lastRow);
				lastRow += (lines[nLines].getRows());
				sb.setLength(0);
			}
			i++;
		}
		if(lines == null){
			lines = new CCStringWrapper[1];
			lines[0] = new CCStringWrapper(this,sb.toString(),0);
		}else{
			int nLines = lines.length;
			CCStringWrapper []newLines = new CCStringWrapper[nLines+1];
			waba.sys.Vm.copyArray(lines,0,newLines,0,nLines);
			lines = newLines;
			lines[nLines] = new CCStringWrapper(this,sb.toString(),lastRow);
		}
		repaint();
	}
	public void onControlEvent(ControlEvent ev){
		if (ev.type == ev.TIMER && hasCursor) paintCursor(null);
		else if (ev.type == ev.FOCUS_IN) gotFocus();
		else if (ev.type == ev.FOCUS_OUT) lostFocus();
	}
	public void gotFocus(){
		hasCursor =  true;
//		caretTimer = addTimer(500);
	}
	public void lostFocus(){
		clearCursor();
		cursorOn = cursorOn = false;
		removeTimer(caretTimer);
	}
	protected void clearCursor() {
		if (cursorOn) paintCursor(null);
	}
	protected void paintCursor(Graphics gr){
		Graphics g = gr;
		if (g == null) g = createGraphics();
		if (hasCursor){
			Rect r = new Rect(0,0,0,0);
/*
			if (getCharRect(curState.cursorPos,curState.cursorLine,r)){
				g.drawCursor(r.x+spacing-curState.xShift,spacing+r.y-curState.firstLine*getItemHeight(),1,r.height);
				cursorOn = !cursorOn;
			}
*/
//			g.drawCursor(10,10,1,8);
			cursorOn = !cursorOn;
		}
		if (gr == null) g.free();
	}

	public void onEvent(Event ev){
		if (ev instanceof PenEvent)
			onPenEvent((PenEvent)ev);
		else if (ev instanceof KeyEvent){
			onKeyEvent((KeyEvent)ev);
		}else if (ev instanceof ControlEvent){
			onControlEvent((ControlEvent)ev);
		}
		super.onEvent(ev);
	}

	public void onKeyEvent(KeyEvent ev){
		CCTextAreaState tas = curState;
		if (ev.key == IKeys.BACKSPACE) {
		}else if (ev.key == IKeys.DELETE){
		}else if (ev.key == IKeys.ENTER){// && editable(this)){
		}else if (ev.key == IKeys.END){
		}else if (ev.key == IKeys.HOME){
			if(firstLine != 0){
				firstLine = 0;
				repaint();
			}
		}else if (ev.key == IKeys.LEFT){
		}else if (ev.key == IKeys.RIGHT){
		}else if (ev.key == IKeys.UP){
			if(firstLine > 0){
				firstLine--;
				repaint();
			}
		}else if (ev.key == IKeys.DOWN){
			int nRows = getRowsNumber();
			if(lines != null && firstLine < nRows - 2){
				firstLine++;
				repaint();
			}
		}else if (ev.key >= 32 && ev.key <= 255){
			if((ev.modifiers & IKeys.CONTROL) > 0){
				if(ev.key == 'v' || ev.key == 'V'){
					if(!CCClipboard.isClipboardEmpty()){
						String str = CCClipboard.getStringContent();
						if(str != null){
							insertText(str);
						}
					}
				}
			}else{
			}
		}
	}
	
	public int getRowsNumber(){
		int retValue = 0;
		if(lines == null || lines.length < 1) return 0;
		for(int i = 0; i < lines.length; i++){
			retValue += lines[i].getRows();
		}
		return retValue;
	}
	
	public void onPenEvent(PenEvent ev){
	}
	
	public void onPaint(Graphics g){
		Rect r = getRect();
		g.setColor(255,255,255);
		g.fillRect(0,0,r.width,r.height);
		g.setColor(0,0,0);
		g.drawRect(0,0,r.width,r.height);
		doPaintData(g);
	}
	public void doPaintData(Graphics g){
		if(lines == null) return;
		for (int i = 0; i<lines.length; i++){
			(lines[i]).draw(g,firstLine);
		}
	}

}

class CCTextAreaState{
int cursorRow = 0, cursorPos = 0;
	CCTextAreaState(){
	}
}


class CCTARow{
CCStringWrapper owner = null;
	CCTARow(CCStringWrapper owner){
		this.owner = owner;
	}
	public CCStringWrapper getOwner(){
		return owner;
	}
	public void setOwner(CCStringWrapper owner){
		this.owner = owner;
	}
}

class CCStringWrapper{
String str;
CCTextArea owner = null;
int  	beginPos 	= 5;
int		endPos 		= 50;

int		beginRow	= -1;
int		endRow		= -1;
String	[]strings = null;

int insetLeft = 5;
int insetRight = 10;

	CCStringWrapper(CCTextArea owner,String str,int beginRow){
		this.owner = owner;
		Rect r = owner.getRect();
		beginPos 	= r.x + insetLeft;
		endPos 		= r.x + r.width - insetRight;
		setStr(str,beginRow);		
	}
	String getStr(){return str;}
	void setStr(String str,int beginRow){
		this.beginRow 	= beginRow;
		if(owner == null){
			this.str = null;
			return;
		}
		this.str = str;
		FontMetrics fm = owner.getFontMetrics();
		if(fm == null || str == null) return;
		int x 			= beginPos;
		int lastWord 	= 0;
		int	blankWidth = fm.getCharWidth(' ');
		int	delimiterIndex = 0;
		int i = 0;
		while(i < str.length()){
			char c = str.charAt(i);
			if(isWhiteSpace(c)){
				if(c == '\n') break;
				lastWord = i+1;
			}
			int w = fm.getCharWidth(c);
			if(x + w > endPos){
				if(lastWord == delimiterIndex){
					lastWord = i;
				}else{
					i = lastWord;
				}
				int nStrings = 0;
				if(strings != null){
					nStrings = strings.length;
				}
				String []newStrings = new String[nStrings + 1];
				if(strings != null){
					waba.sys.Vm.copyArray(strings,0,newStrings,0,nStrings);
				}
				strings = newStrings;
				strings[nStrings] = str.substring(delimiterIndex,lastWord);
				x = beginPos;
				delimiterIndex = i;
			}else{
				x += w;
			}
			i++;
		}
		if(strings == null){
			strings = new String[1];
			strings[0] = str;
		}else{
			if(delimiterIndex < str.length()){
				int nStrings = strings.length;
				String []newStrings = new String[nStrings + 1];
				waba.sys.Vm.copyArray(strings,0,newStrings,0,nStrings);
				strings = newStrings;
				strings[nStrings] = str.substring(delimiterIndex,str.length());
			}
		}
		this.endRow 	= this.beginRow + strings.length;
	}
	
	public int getRows(){
		return (endRow - beginRow);
	}
	
	String getFullStr(){
		return str;
	}
	
	public void setMargins(int beginPos,int endPos){
		this.beginPos 	= beginPos;
		this.endPos 	= endPos;
	}
	public void setRows(int beginRow,int endRow){
		this.beginRow 	= beginRow;
		this.endRow 	= endRow;
	}
	public void draw(Graphics gr,int firstRow){
		if(gr == null || strings == null || owner == null) return;
		gr.setColor(0,0,0);
		int h = owner.getItemHeight();
		for(int i = beginRow; i < endRow; i++){
			if(i < firstRow) continue;
			int y = (i - firstRow)*h;
			if(i - beginRow < strings.length){
				gr.drawText(strings[i-beginRow],beginPos,y);
			}
		}
	}
	public static boolean isWhiteSpace(char c){
		boolean retValue = false;
		for(int i = 0; i < whiteChars.length; i++){
			if(c == whiteChars[i]){
				retValue = true;
				break;
			}
		}
		return retValue;
	}
	

private static char []whiteChars = {' ','\t','\n','\r'};	

}


