package org.concord.waba.extra.ui;

import waba.ui.*;
import waba.fx.*;
import waba.sys.*;
import waba.util.Vector;


public class CCTextArea  extends Container{
CCStringWrapper		[] lines;
FontMetrics 		fm = null;
int 				insetLeft = 5;
int 				insetRight = 10;
protected Timer 	caretTimer = null;
protected boolean 	hasCursor = false,cursorOn = false;
protected Font font = new Font("Helvetica",Font.PLAIN,12);

protected	int firstLine = 0;

protected CCTextAreaState curState = new CCTextAreaState();
			
LBCompDesc	[]components = null;
CCTARow		[]rows = null;		

	public CCTextArea(){
		super();
		components = new LBCompDesc[1];
		components[0] = new LBCompDesc(2,20,50,LBCompDesc.ALIGNMENT_LEFT,true);
	}
	
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
	public void insertObject(){
		System.out.println("insertObject");
	}
	
	public void layoutComponents(){
		if(components != null){
			for(int i = 0; i < components.length; i++){
				LBCompDesc c = components[i];
				Button b = new Button(""+i);
				add(b);
				int yTop = y;
				int line = c.lineBefore;
				if(line > 0){
					if(lines != null && lines.length > line){
						yTop += lines[line - 1].endRow*getItemHeight();
					}
				}	
				if(c.alignment == LBCompDesc.ALIGNMENT_RIGHT){
					b.setRect(x+width-insetRight-c.w,yTop,c.w,c.h);
				}else{
					b.setRect(x+insetLeft,yTop,c.w,c.h);
				}
			}
		}
	}
	
	public void setRect(int x, int y, int width, int height){
		super.setRect(x,y,width,height);
		layoutComponents();
	}
	
	public void setText(String str){
		lines = null;
		rows = null;
		int determinedSystem = -1; //unix - 0; //mac - 1 dos - 2
		if(str == null) return;
		removeCursor();
		Rect r = getRect();
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
				LBCompDesc compDesc = null;
				if(components != null){
					for(int k = 0; k < components.length; k++){
						LBCompDesc cDesc = components[k];
						if(cDesc.lineBefore == nLines){
							compDesc = components[k];
						}
					}
				}
				int leftMargin = r.x + insetLeft;
				int rightMargin = r.x + r.width - insetRight;
				int skipRows = 0;
				if(compDesc != null){
					int addH = compDesc.h;
					int addRows = 1 + (addH / getItemHeight());
					if(compDesc.alignment == LBCompDesc.ALIGNMENT_LEFT){
						leftMargin = r.x + insetLeft + compDesc.w;
					}else{
						rightMargin = r.x + r.width - insetRight - compDesc.w;
					}
					if(!compDesc.wrapping){
						lastRow += addRows;
					}
					
					int nRows = (rows == null)?0:rows.length;
					CCTARow []newRows = new CCTARow[nRows + addRows];
					if(addRows > 0){
						waba.sys.Vm.copyArray(rows,0,newRows,0,nRows);
						for(int k = nRows; k < nRows + addRows; k++){
							newRows[k] = new CCTARow(lines[nLines]);
							newRows[k].setMargins(leftMargin,rightMargin);
						}
					}
					rows = newRows;
				}
				lines[nLines] = new CCStringWrapper(this,sb.toString(),lastRow);
				int nRows = (rows == null)?0:rows.length;
				int lastLineRow = lines[nLines].endRow;
				int addRows = lastLineRow - nRows;
				if(addRows > 0){
					CCTARow []newRows = new CCTARow[nRows + addRows];
					waba.sys.Vm.copyArray(rows,0,newRows,0,nRows);
					for(int k = nRows; k < nRows + addRows; k++){
						newRows[k] = new CCTARow(lines[nLines]);
						newRows[k].setMargins(r.x + insetLeft,r.x + r.width - insetRight);
					}
					rows = newRows;
				}				
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
		restoreCursor();
	}
	public void lostFocus(){
		removeCursor();
	}
	
	public void restoreCursor(){
		hasCursor =  true;
		caretTimer = addTimer(500);
	}
	
	
	public void removeCursor(){
		clearCursor();
		hasCursor = false;
		cursorOn = false;
		removeTimer(caretTimer);
		caretTimer = null;
	}
	
	protected void clearCursor() {
		if (cursorOn) paintCursor(null);
	}
	protected void paintCursor(Graphics gr){
		Graphics g = gr;
		if (g == null) g = createGraphics();
		if (hasCursor){
			Rect r = new Rect(0,0,0,0);

			if (getCharRect(r)){
				g.drawCursor(r.x,r.y,r.width,r.height);
				cursorOn = !cursorOn;
			}
		}
		if (gr == null) g.free();
	}
	public boolean getCharRect(Rect r){
		if(r == null) return false;
		r.width 	= 1;
		r.height 	= getItemHeight();
		r.x = curState.cursorPos;
		r.y = curState.cursorRow*getItemHeight();
		return true;
	}

/*
	public boolean getCharRect(int charPos,Rect r){
		if(r == null) return false;
		r.width 	= 1;
		r.height 	= getItemHeight();
		if(lines == null) return false;
		int ind = 0;
		boolean doExit = false;
		for(int i = 0; i < lines.length; i++){
			ind += lines[i].getStr().length();
			if(charPos < ind + lines[i].getStr().length()){
				String []strings = lines[i].strings;
				if(strings != null){
					int iPos = ind;
					for(int j = 0; j < strings.length; j++){
						if(charPos < iPos + strings[j].length()){
							r.x = lines[i].insetLeft + fm.getTextWidth(strings[j].substring(0,(charPos - iPos)));
							r.y = (lines[i].beginRow + j - firstLine)*r.height;
							doExit = true;
							break;
						}
						iPos += strings[j].length();
					}
					if(doExit) break;
				}
			}
			ind += lines[i].getStr().length();
		}
		return true;
	}
*/
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
				removeCursor();
				firstLine = 0;
				repaint();
			}
		}else if (ev.key == IKeys.LEFT){
		}else if (ev.key == IKeys.RIGHT){
		}else if (ev.key == IKeys.UP){
			if(firstLine > 0){
				removeCursor();
				firstLine--;
				repaint();
			}
		}else if (ev.key == IKeys.DOWN){
			int nRows = getRowsNumber();
			if(lines != null && firstLine < nRows - 2){
				removeCursor();
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
		if(ev.type == PenEvent.PEN_DOWN){
			int x = 0;
			int h = getItemHeight();
			int row = 1 + firstLine + (ev.y / h);
			if(row > getRowsNumber()) row = getRowsNumber();
			int lineIndex = getLineIndex(row - 1);
			if(lineIndex >= 0 && lineIndex < lines.length){
				CCStringWrapper sw = lines[lineIndex];
				int rIndex = row - 1 - sw.beginRow;
				if(rIndex >= 0 && rIndex < sw.strings.length){
					String str = sw.strings[rIndex];
					if(str != null){
						int lastPos = insetLeft + fm.getTextWidth(str);
						if(ev.x < insetLeft) x = insetLeft;
						else if(ev.x > lastPos) x = lastPos;
						else{
							int xp = insetLeft;
							x = ev.x;
							for(int c = 0; c < str.length(); c++){
								int cw = fm.getCharWidth(str.charAt(c));
								if(x < xp + cw){
									x = xp + cw;
									row = sw.endRow + 1;
									x = insetLeft;
									break;
								}
								xp += cw;
							}
						}
					}
				}
			}
			if(hasCursor) clearCursor();
			else		  restoreCursor();
			curState.cursorPos = x;
			curState.cursorRow = row - 1 - firstLine;
			paintCursor(null);
		}
	}
	
	public int getLineIndex(int row){
		int retValue = 0;
		if(lines == null || lines.length < 1) return retValue;
		if(row > getRowsNumber()) return lines.length - 1;
		int ind = 0;
		for(int i = 0; i < lines.length; i++){
			if(row < ind + lines[i].getRows()){
				retValue = i;
				break;
			}
			ind += lines[i].getRows();
		}
		return retValue;
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
public int cursorRow = 0, cursorPos = 0;
public int	cursorChar = 0;
	CCTextAreaState(){
	}
}


class CCTARow{
CCStringWrapper owner = null;
int  	beginPos,endPos;
	CCTARow(CCStringWrapper owner){
		this.owner = owner;
	}
	public CCStringWrapper getOwner(){
		return owner;
	}
	public void setOwner(CCStringWrapper owner){
		this.owner = owner;
	}
	public void setMargins(int beginPos,int endPos){
		this.beginPos 	= beginPos;
		this.endPos 	= endPos;
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


	CCStringWrapper(CCTextArea owner,String str,int beginRow){
		this.owner = owner;
		Rect r = owner.getRect();
		beginPos 	= r.x + owner.insetLeft;
		endPos 		= r.x + r.width - owner.insetRight;
		setStr(str,beginRow);		
	}
	String getStr(){return str;}
	void setStr(String str,int beginRow){
		this.beginRow 	= beginRow;
		if(owner == null){
			this.str = null;
			return;
		}
		
		int    numbTotalRows = (owner.rows == null)?0:owner.rows.length;
		this.str = str;
		FontMetrics fm = owner.getFontMetrics();
		if(fm == null || str == null) return;
		int currRow = beginRow;
		int x 			= (currRow >= numbTotalRows || owner.rows == null)?beginPos:owner.rows[currRow].beginPos;
		int lastWord 	= 0;
		int	blankWidth = fm.getCharWidth(' ');
		int	delimiterIndex = 0;
		int i = 0;
		while(i < str.length()){
			char c = str.charAt(i);
			if(isWordDelimiter(c)){
				if(c == '\n') break;
				lastWord = i+1;
			}
			int w = fm.getCharWidth(c);
			int limitX = (currRow >= numbTotalRows || owner.rows == null)?endPos:owner.rows[currRow].endPos;
			if(x + w > limitX){
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
				currRow++;
				x = (currRow >= numbTotalRows || owner.rows == null)?beginPos:owner.rows[currRow].beginPos;
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
		int    numbTotalRows = (owner.rows == null)?0:owner.rows.length;
		int h = owner.getItemHeight();
		for(int i = beginRow; i < endRow; i++){
			if(i < firstRow) continue;
			int y = (i - firstRow)*h;
			if(i - beginRow < strings.length){
				int x = (i >= numbTotalRows || owner.rows == null)?beginPos:owner.rows[i].beginPos;
				gr.drawText(strings[i-beginRow],x,y);
			}
		}
	}
	public static boolean isWordDelimiter(char c){
		boolean retValue = false;
		for(int i = 0; i < wordDelimChars.length; i++){
			if(c == wordDelimChars[i]){
				retValue = true;
				break;
			}
		}
		return retValue;
	}
	

private static char []wordDelimChars = {' ','\t','\n','\r',':',';','.',',','/','\\','@'};	

}

class LBCompDesc{
int 	lineBefore;
int 	w, h;
int 	alignment;
boolean wrapping;

final static int ALIGNMENT_LEFT = 0;
final static int ALIGNMENT_RIGHT = 1;



	LBCompDesc(int lineBefore,int w, int h,int alignment, boolean wrapping){
		this.lineBefore		= lineBefore;
		this.w				= w;
		this.h				= h;
		this.alignment		= alignment;
		this.wrapping		= wrapping;
	}
}
