package org.concord.waba.extra.ui;

import waba.ui.*;
import waba.fx.*;
import waba.sys.*;
import waba.util.Vector;

/**
* TextArea by Michael L Brereton, November 1999.
* This code may be freely used, modified and redistributed, as long
* as credit is given to the original author in any such modification
* or distribution.
*/
//##################################################################
public class TextArea extends AdvControl {
//##################################################################

//protected String [] lines;
StringWrapper		[] lines;
/**
* The space between the text and the border of the control.
*/
public int spacing = 3;
/**
* This is the minimum distance to scroll horizontally. It is a percent figure, defaults to 30%
*/
public int minXScroll = 30;
/**
* This is the minimum distance to scroll Vertically. It is a percent figure, defaults to 80%
*/
public int minYScroll = 80;

//==================================================================
/**
* If the control should reset the cursor position to row 0 and column 0 when it loses focus.
*/
public boolean homeCursorOnLostFocus = true;
//==================================================================



protected boolean forceClearCursor = false;


public int	getTextAreaWidth(){
	int w = getRect().width-spacing*2-2;
	return w;
}

//==================================================================
protected int getItemHeight() 
//==================================================================
{
	FontMetrics fm = getFontMetrics(); 
	return fm.getHeight()+2;
}
//==================================================================
protected int getTextWidth()
//==================================================================
{
	FontMetrics fm = getFontMetrics();
	int w = 0;
	for (int i = 0; i<getNumLines(); i++) {
		int ww = fm.getTextWidth(lines[i].getStr());
		if (ww > w) w = ww;
	}
	return w;
}
//------------------------------------------------------------------
protected StringWrapper [] split(String what,char separator)
//------------------------------------------------------------------
{
	if (what == null) return new StringWrapper[0];
	if (what.length() == 0) return new StringWrapper[0];
	Vector dest = new Vector();
	StringBuffer sb = new StringBuffer();
	for (int i = 0;i<what.length(); i++){
		char c = what.charAt(i);
		if (c == separator) {
			dest.add(sb.toString());
			sb = new StringBuffer();
		}else 
			sb.append(c);
	}
	dest.add(sb.toString());
	StringWrapper [] st = new StringWrapper[dest.getCount()];
	for (int i = 0; i<dest.getCount(); i++)
		st[i] = new StringWrapper(this,(String)dest.get(i));
	return st;
}
//------------------------------------------------------------------
protected void splitLines()
//------------------------------------------------------------------
{
	lines = split(text,'\n');
	if (lines.length == 0) {
		lines = new StringWrapper[1];
		lines[0] = new StringWrapper(this);
	}
}
//==================================================================
public int getNumLines()
//==================================================================
{
	if (lines == null) splitLines();
	return lines.length;
}
//------------------------------------------------------------------
protected void insertLine(int index)
//------------------------------------------------------------------
{
	if (lines == null) lines = new StringWrapper[0];
	if (index > lines.length) index = lines.length;
	StringWrapper [] nl = new StringWrapper[lines.length+1];
	for (int i = 0; i<index; i++) nl[i] = lines[i];
	nl[index] = new StringWrapper(this);
	for (int i = index; i<lines.length; i++) nl[i+1] = lines[i];
	lines = nl;
}
//------------------------------------------------------------------
protected boolean getCharRect(int ch,int ln,Rect dest)
//------------------------------------------------------------------
{
	dest.width = dest.x = 0;
	dest.height = getItemHeight();
	dest.y = dest.height*ln;
	FontMetrics fm = getFontMetrics();
//..................................................................
	if (ln >= lines.length || ln < 0) return false;
	String s = lines[ln].getStr();
	if (ch > s.length() || ch < 0) return false;
	if (ch == 0) dest.x = 0;
	else dest.x = fm.getTextWidth(s.substring(0,ch));
	if (ch == s.length()) dest.width = 5;
	else dest.width = fm.getCharWidth(s.charAt(ch));
//..................................................................
	return true;
}
/**
* Sets the text for the control. Use a '\n' separates lines in the text.
**/
//==================================================================
public void setText(String what)
//==================================================================
{
	text = what;
	if (text == null) text = "";
	splitLines();
	fix();
	repaintDataNow();
	checkScrolls();
}
/**
* Returns the text of the control.
**/
//==================================================================
public String getText()
//==================================================================
{
	if (lines == null) return "";
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i<lines.length; i++){
		if (lines[i] != null) {
			if (i != 0) sb.append("\n");
			sb.append(lines[i].getFullStr());
		}
	}
	return sb.toString();
}
//------------------------------------------------------------------
protected void fixText()
//------------------------------------------------------------------
{
	setText(getText());
}
/**
* Returns the number of complete rows displayable on the control.
**/
//==================================================================
public int getScreenRows()
//==================================================================
{
	int h = getItemHeight();
	if (h == 0) return 0;
	return (height-spacing*2)/h;
}
//------------------------------------------------------------------
protected ImageBuffer itemBuffer = new ImageBuffer();
protected ImageBuffer blockBuffer = new ImageBuffer();
//------------------------------------------------------------------
//------------------------------------------------------------------
protected void paintLastChar(Graphics g) {paintLastChar(g,false);}
protected void paintLastChar(Graphics gr,boolean eraseIt)
//------------------------------------------------------------------
{
	Graphics g = gr;
	if (g == null) g = createGraphics();
	Rect r = getRect();
	g.setClip(spacing,spacing,r.width-spacing*2,r.height-spacing*2);
	FontMetrics fm = getFontMetrics();
	String line = getLine(curState.cursorLine);
	int x = spacing-curState.xShift;
	int w = 0;
	int h = getItemHeight(),y = h*(curState.cursorLine-curState.firstLine)+spacing;
	g.setDrawOp(g.DRAW_OVER);
	if (line.length() != 0) {
		x += fm.getTextWidth(line.substring(0,line.length()-1));
		if (!eraseIt){
			g.setColor(0,0,0);
			g.setFont(font);
			g.drawText(""+line.charAt(line.length()-1),x,y);
		}else {
			g.setColor(255,255,255);
			w = fm.getCharWidth(line.charAt(line.length()-1));
			g.fillRect(x,y,w,h);
		}
	}
	if (gr == null) g.free();
}
/**
* Delete the current selection.
*/
//==================================================================
public boolean deleteSelection()
//==================================================================
{
	clearCursor();
	if (curState.selStartLine == -1) return false;
	int sl = curState.selStartLine, sp = curState.selStartPos;
	int el = curState.selEndLine;
	if (sl != el) {
		lines[sl].setStr(lines[sl].getStr().substring(0,curState.selStartPos)+lines[el].getStr().substring(curState.selEndPos,lines[el].getStr().length()));
		for (int i = curState.selStartLine+1; i<=curState.selEndLine && i<lines.length; i++) lines[i] = null;
		curState.selStartLine = -1;
		fixText();
	}else {
		String s = lines[sl].getStr();
		lines[sl].setStr(s.substring(0,curState.selStartPos)+s.substring(curState.selEndPos,s.length()));
		curState.selStartLine = -1;
		paintLine(null,sl);
	}
	newCursorPos(sp,sl,false);
	return true;
}
//------------------------------------------------------------------
protected void paintLine(Graphics gr,int index)
//------------------------------------------------------------------
{
	Graphics g = gr;
	if (g == null) g = createGraphics();
	int num = getScreenRows();
	if (index < curState.firstLine || index >= curState.firstLine+getScreenRows()) {
		if (gr == null) g.free();
		return;
	}
	String line = getLine(index);
	int h = getItemHeight();
	int y = spacing+(index-curState.firstLine)*h;
	int x = 0;
	int w = width-spacing*2;
	Graphics gr2 = itemBuffer.get(w,h);
	Image img = itemBuffer.image;
	gr2.setDrawOp(gr.DRAW_OVER);
//..................................................................
// Fill background.
//..................................................................
	gr2.setColor(255,255,255);
	gr2.fillRect(0,0,w,h);
//..................................................................
// Do text.
//..................................................................
	gr2.setColor(0,0,0);
	gr2.setFont(font);
	gr2.translate(-curState.xShift,0);
	gr2.drawText(line,x,0);//+1);
	if (curState.isInSelection(index) && curState.selectionEnabled) {
		int sp = 0, ep = line.length();
		if (curState.selStartLine == index) sp = curState.selStartPos;
		if (curState.selEndLine == index) ep = curState.selEndPos;
		Graphics bg = blockBuffer.get(w,h);
		bg.setColor(255,255,255);
		bg.fillRect(0,0,w,h);
		bg.translate(-curState.xShift,0);
		FontMetrics fm = getFontMetrics();
		int leftWidth = fm.getTextWidth(line.substring(0,sp));
		int myWidth = fm.getTextWidth(line.substring(sp,ep));
		bg.setColor(0,0,0);
		bg.fillRect(leftWidth,0,myWidth,h);
		bg.translate(curState.xShift,0);
		gr2.setDrawOp(gr2.DRAW_XOR);
		gr2.translate(curState.xShift,0);
		gr2.drawImage(blockBuffer.image,0,0);
	}else 
		gr2.translate(curState.xShift,0);
	gr2.free();
	g.drawImage(img,spacing,y);
	if (gr == null) g.free();
}
//==================================================================
public void doPaint(Graphics g,Rect area)
//==================================================================
{
	Rect r = getRect();
	g.setColor(255,255,255);
	g.fillRect(0,0,r.width,r.height);
	g.setColor(0,0,0);
	g.drawRect(0,0,r.width,r.height);
	doPaintData(g,area);
}


//------------------------------------------------------------------
protected void paintCursor(Graphics gr)
//------------------------------------------------------------------
{
	Graphics g = gr;
	if (g == null) g = createGraphics();
	if (hasCursor){
		Rect r = new Rect(0,0,0,0);
		if (getCharRect(curState.cursorPos,curState.cursorLine,r)){
/*
//			Image i = new Image(2,r.height);
			Image i = new Image(1,r.height);//dima
			Graphics gi = new Graphics(i);
			if(cursorOn){//dima
				gi.setColor(0,0,0);//mColor.setColor(gi,getForeground());
			}else{
				gi.setColor(255,255,255);//mColor.setColor(gi,getForeground());
			}
			gi.fillRect(0,0,r.width,r.height);
			gi.free();
//			g.setDrawOp(g.DRAW_XOR);
			g.drawImage(i,r.x+spacing-curState.xShift,spacing+r.y-curState.firstLine*getItemHeight());
			g.setDrawOp(g.DRAW_OVER);
			i.free();
*/			
			if(g != null) g.drawCursor(r.x+spacing-curState.xShift,spacing+r.y-curState.firstLine*getItemHeight(),1,r.height);

			
			cursorOn = !cursorOn;
		}
	}
	if (gr == null && g != null) g.free();
}
//==================================================================
public void doPaintData(Graphics g) {doPaintData(g,getRect());}
//==================================================================
public void doPaintData(Graphics g,Rect area)
//==================================================================
{
	if (lines == null) splitLines();
	for (int i = 0; i<getScreenRows(); i++) 
		paintLine(g,i+curState.firstLine);
	cursorOn = false;
}
//------------------------------------------------------------------
protected Timer blink = null;
protected boolean hasCursor, cursorOn;
//------------------------------------------------------------------

//==================================================================
public void onControlEvent(ControlEvent ev)
//==================================================================
{
	if (ev.type == ev.TIMER && hasCursor) paintCursor(null);
	else if (ev.type == ev.FOCUS_IN) gotFocus();
	else if (ev.type == ev.FOCUS_OUT) lostFocus();
}
//==================================================================
public void selectAll()
//==================================================================
{
	clearSelection();
	curState.selStartLine = curState.selStartPos = 0;
	curState.selEndLine = lines.length-1;
	curState.selEndPos = lines[lines.length-1].getStr().length();
	if (!newCursorPos(curState.selEndPos,curState.selEndLine,true))
		repaintDataNow();
}
protected boolean justGotFocus = false;

//==================================================================
protected void checkScrolls(){}
protected void updateScrolls(){}
//==================================================================

//==================================================================
public void gotFocus()
//==================================================================
{
	justGotFocus = true;
	hasCursor =  true;
	blink = addTimer(500);
	checkScrolls();
}
//==================================================================
public void lostFocus()
//==================================================================
{
	clearSelection();
	if (homeCursorOnLostFocus) newCursorPos(0,0,false);
	checkScrolls();
	clearCursor();
	cursorOn = hasCursor = false;
	removeTimer(blink);
}

//------------------------------------------------------------------
protected textAreaState curState = new textAreaState();
//------------------------------------------------------------------
//------------------------------------------------------------------
protected boolean fix()
//------------------------------------------------------------------
{
	if (width == 0 || height == 0) return false;
	textAreaState tas = curState.getCopy();
//	Rect _rect = getRect();
	FontMetrics fm = getFontMetrics();
	if (!hasCursor) tas.cursorLine = tas.cursorPos = tas.firstLine = tas.xShift = 0;
//..................................................................
	if (tas.cursorLine >= lines.length) {
		tas.cursorLine = lines.length-1;
		tas.cursorPos = lines[tas.cursorLine].getStr().length();
	}
	if (tas.cursorLine < 0) tas.cursorLine = 0;
	String ln = lines[tas.cursorLine].getStr();
	if (tas.cursorPos > ln.length()) tas.cursorPos = ln.length(); 
	if (tas.cursorPos < 0) tas.cursorPos = 0;
//..................................................................
	int sr = getScreenRows();
	int ys = (minYScroll*sr)/100;
	if (ys < 1) ys = 1;
	if (ys > sr) ys = sr;
	while (tas.cursorLine >= sr+tas.firstLine) tas.firstLine += ys;
	while (tas.cursorLine < tas.firstLine) tas.firstLine -= ys;
	if (tas.firstLine < 0) tas.firstLine = 0;
//..................................................................
//	int w = _rect.width-spacing*2-2;
	int w = getTextAreaWidth();
	String cl = ln.substring(0,tas.cursorPos);
	int cw = fm.getTextWidth(cl);
	if (cw < tas.xShift) tas.xShift = 0;
	int extra = (minXScroll*width)/100;
	if (extra < 4) extra = 4;
	if (cw > tas.xShift+w-4) tas.xShift = cw-w+extra;
	textAreaState t2 = curState;
	curState = tas;
	return curState.displayChanged(t2);
}

//------------------------------------------------------------------
protected boolean newCursorPos(int ch,int ln,boolean takeSel)
//------------------------------------------------------------------
{
	boolean repainted = false;
	cursorOn = false;//dima
	clearCursor();
	curState.cursorLine = ln;
	curState.cursorPos = ch;
	if (fix()) {
		repaintDataNow();
		updateScrolls();
		repainted = true;
	}
	return repainted;
}
//------------------------------------------------------------------
protected void newText(String txt,int newCp,boolean redoData)
//------------------------------------------------------------------
{
	FontMetrics fm = getFontMetrics();
	int oldw = fm.getTextWidth(lines[curState.cursorLine].getStr());
	lines[curState.cursorLine].setStr(txt);
	int w = getFontMetrics().getTextWidth(txt);
	int tw = width-spacing*2;
	if ((w > tw && oldw <= tw) || (w < tw && oldw >=tw)) checkScrolls();
	boolean rp = newCursorPos(newCp,curState.cursorLine,false);
	if (!rp) {
		clearCursor();
		paintLine(null,curState.cursorLine);
	}
}
//------------------------------------------------------------------
protected Point getPenChar(Point onControl)
//------------------------------------------------------------------
{
	FontMetrics fm = getFontMetrics();
	Point p = new Point(0,0);
	if (lines == null) lines = new StringWrapper[0];
	int h = getItemHeight();
	int px = onControl.x-spacing+curState.xShift;
	int py = onControl.y-spacing+curState.firstLine*h;
	p.y = py/h;
	p.x = 0;
	String s = getLine(p.y);
	int i = 0, w = 0;
	for (; i<s.length(); i++){
		w += fm.getCharWidth(s.charAt(i));
		if (w >= px) break;
	}
	p.x = i;
	return p;
}
//------------------------------------------------------------------
protected void clearCursor() {
	if (cursorOn) paintCursor(null);
}
//------------------------------------------------------------------
//------------------------------------------------------------------
protected String getLine(int index)
//------------------------------------------------------------------
{
	if (lines == null) return "";
	if (index < 0 || index >= lines.length) return "";
	return lines[index].getStr();
}
//------------------------------------------------------------------
protected String getLine() {return getLine(curState.cursorLine);}
//------------------------------------------------------------------
protected Point pressPoint = new Point(0,0);
//==================================================================
public void clearSelection()
//==================================================================
{
	clearCursor();
	curState.selectionEnabled = false;
	for (int i = 0; i<getScreenRows(); i++)
		if (curState.isInSelection(i+curState.firstLine)) paintLine(null,i+curState.firstLine);
	curState.selectionEnabled = true;
	curState.selStartLine = -1;
}
//==================================================================
public void penPressed(Point where)
//==================================================================
{
	Point loc = getPenChar(where);
	if (loc == null) return;
	clearSelection();
	newCursorPos(loc.x,loc.y,false);
	lastDrag = pressPoint = loc;
}
//==================================================================
public void penDoubleClicked(Point where) {selectAll();}
//==================================================================
protected Point lastDrag;
//==================================================================
public void dragged(DragContext dc)
//==================================================================
{
	Point now = getPenChar(dc.curPoint);
	if (now == null) return;
	if (lastDrag != null)
		if (now.x == lastDrag.x && now.y == lastDrag.y) return;
	boolean [] wasIn = new boolean[lines.length];
	for (int i = 0; i<lines.length; i++)
		wasIn[i] = curState.isInSelection(i);
	lastDrag = now;
	if (now.y < pressPoint.y) {
		curState.selStartLine = now.y;
		curState.selEndLine = pressPoint.y;
		curState.selStartPos = now.x;
		curState.selEndPos = pressPoint.x;
	}else if (now.y > pressPoint.y) {
		curState.selStartLine = pressPoint.y;
		curState.selEndLine = now.y;
		curState.selStartPos = pressPoint.x;
		curState.selEndPos = now.x;
	}else {
		curState.selStartLine = curState.selEndLine = pressPoint.y;
		if (now.x < pressPoint.x) {
			curState.selStartPos = now.x;
			curState.selEndPos = pressPoint.x;
		}else{
			curState.selStartPos = pressPoint.x;
			curState.selEndPos = now.x;
		}
	}
	curState.fixSel(lines);
	if (!newCursorPos(now.x,now.y,false)) {
		Graphics g = createGraphics();
		for (int i = curState.firstLine; i<curState.firstLine+getScreenRows() && i<lines.length; i++) {
			if (curState.isInSelection(i) || wasIn[i])
				paintLine(g,i);
		}
		g.free();
	}
}



public void insertText(String str){//dima
	if(str == null) return;
	int cl = curState.cursorLine;
	if(cl < 0) return;
	String s = getLine();
	int cp = curState.cursorPos;
	String suffix = s.substring(cp);
	lines[cl].setStr(s.substring(0,cp) + str + suffix);
	fixText();
}


	public void scrollUp(){
		curState.firstLine -= (int)(0.8f*getScreenRows()+0.5f);
		if(curState.firstLine < 0) curState.firstLine = 0;
		repaintDataNow();
		updateScrolls();
	}
	public void scrollDown(){
		curState.firstLine += (int)(0.8f*getScreenRows()+0.5f);
		if(curState.firstLine > getNumLines() - 3) curState.firstLine = getNumLines() - 3;
		repaintDataNow();
		updateScrolls();
	}


//==================================================================
public void onKeyEvent(KeyEvent ev)
//==================================================================
{
	textAreaState tas = curState;
	String s = getLine();
	int sl = s.length();
	int cl = curState.cursorLine;
	int cp = curState.cursorPos;
	if (ev.key == IKeys.BACKSPACE) {
		if (deleteSelection()) return;
		if (cp > 0 && cp == s.length()) {
			paintLastChar(null,true);
			lines[cl].setStr(s.substring(0,sl-1));
			newCursorPos(cp-1,cl,false);
		}else if (cp > 0)
			newText(s.substring(0,cp-1)+s.substring(cp,s.length()),cp-1,false);
		else if (cl > 0) {
			String pl = getLine(cl-1);
			lines[cl-1].setStr(pl+s);
			lines[cl] = null;
			curState.cursorPos = pl.length();
			curState.cursorLine--;
			fixText();
		}
	}else if (ev.key == IKeys.DELETE){
			if (deleteSelection()) return;
			if (cp < s.length()) {
				newText(s.substring(0,cp)+s.substring(cp+1,s.length()),cp,false);
			}else {
				if (cl < lines.length-1){
					lines[cl].setStr(lines[cl].getStr()+lines[cl+1].getStr());
					lines[cl+1] = null;
					fixText();
				}
			}
	}else if (ev.key == IKeys.ENTER){// && editable(this)){
		insertLine(cl+1);
		
		
		lines[cl].setStr(s.substring(0,cp));
		lines[cl+1].setStr(s.substring(cp,s.length()));
		curState.cursorPos = 0;
		curState.cursorLine++;
		fixText();
	}else if (ev.key == IKeys.END){
		checkScrolls();
		newCursorPos(getLine().length(),tas.cursorLine,false);
	}else if (ev.key == IKeys.HOME){
		checkScrolls();
		newCursorPos(0,tas.cursorLine,false);
	}else if (ev.key == IKeys.LEFT){
		clearCursor();//dima
		newCursorPos(tas.cursorPos-1,tas.cursorLine,false);
	}else if (ev.key == IKeys.RIGHT){
		clearCursor();//dima
		newCursorPos(tas.cursorPos+1,tas.cursorLine,false);
	}else if (ev.key == IKeys.UP){
		clearCursor();//dima
		checkScrolls();
		newCursorPos(tas.cursorPos,tas.cursorLine-1,false);
	}else if (ev.key == IKeys.DOWN){
		clearCursor();//dima
		checkScrolls();
		newCursorPos(tas.cursorPos,tas.cursorLine+1,false);
	}else if (ev.key >= 32 && ev.key <= 255){
		/*if (!editable(this)){
			Sound.beep();
			return;
		}*/
		boolean redoData = deleteSelection();
		s = getLine();
		cl = curState.cursorLine;
		cp = curState.cursorPos;
		if ((ev.modifiers & IKeys.SHIFT) == IKeys.SHIFT)
			if (ev.key >= 'a' && ev.key <= 'z') 
				ev.key = 'A'+ev.key-'a';
		if (redoData || cp != s.length())
			newText(s.substring(0,curState.cursorPos)+(char)ev.key+s.substring(curState.cursorPos,s.length()),curState.cursorPos+1,redoData);
		else {
			String temp = lines[cl].getStr();
			lines[cl].setStr(temp + (char)ev.key);
			paintLastChar(null);
			newCursorPos(cp+1,cl,false);
		}
	}
}
/**
* This scrolls the display. 
* "which" should be IScroll.Vertical or IScroll.Horizontal.
* "action" should be IScroll.ScrollHigher, IScroll.ScrollLower, IScroll.PageHigher, IScroll.PageLower, IScroll.TrackTo
* "value" is valid for only for action = IScroll.TrackTo and represents either the line to scroll vertically to
* or the x coordinate (in pixels) to scroll horizontally to.
*/
//==================================================================
public void doScroll(int which,int action,int value)
//==================================================================
{
	if (which == IScroll.Vertical) {
		if (action == IScroll.ScrollHigher) curState.firstLine++;
		else if (action == IScroll.ScrollLower) curState.firstLine--;
		else if (action == IScroll.PageHigher) curState.firstLine += getScreenRows();
		else if (action == IScroll.PageLower) curState.firstLine -= getScreenRows();
		else if (action == IScroll.TrackTo) curState.firstLine = value;
		if (curState.firstLine > getNumLines()-getScreenRows()) curState.firstLine = getNumLines()-getScreenRows();
		if (curState.firstLine < 0) curState.firstLine = 0;
	}else {
		int w = getRect().width-spacing*2;
		int sh = (minXScroll*width)/100;
		if (sh < 10) sh = 10;
		if (action == IScroll.ScrollHigher) curState.xShift += sh;
		else if (action == IScroll.ScrollLower) curState.xShift -= sh;
		else if (action == IScroll.PageHigher) curState.xShift += w-10;
		else if (action == IScroll.PageLower) curState.xShift -= w-10;
		else if (action == IScroll.TrackTo) curState.xShift = value;
		int mw = getTextWidth();
		if (curState.xShift+w > mw) curState.xShift = mw-w;  
		if (curState.xShift < 0) curState.xShift = 0;
	}	
	repaintDataNow();
	updateScrolls();
}

//##################################################################
}
//##################################################################
//##################################################################
class textAreaState{
//##################################################################
int cursorLine, cursorPos;
int firstLine, xShift;
int selStartLine = -1, selStartPos;
int selEndLine, selEndPos;

boolean selectionEnabled = true;

public boolean isInSelection(int line)
{
	if (selStartLine == -1) return false;
	return (selStartLine <= line && selEndLine >= line);
}
public textAreaState(){}
public textAreaState getCopy()
{
	textAreaState tas = new textAreaState();
	tas.cursorLine = cursorLine;
	tas.cursorPos = cursorPos;
	tas.xShift = xShift;
	tas.firstLine = firstLine;
	tas.selStartLine = selStartLine;
	tas.selStartPos = selStartPos;
	tas.selEndLine = selEndLine;
	tas.selEndPos = selEndPos;
	return tas;
}
public boolean hasSelection()
{
	return selStartLine != -1;
}
public boolean displayChanged(textAreaState other)
{
	if (other.firstLine != firstLine) return true;
	if (other.xShift != xShift) return true;
	return false;
}
public void fixSel(StringWrapper [] lines)
{
	if (selStartLine < 0) selStartLine = 0;
	if (selEndLine < 0) selEndLine = 0;
	if (selStartLine >= lines.length) selStartLine = lines.length-1;
	if (selEndLine >= lines.length) {
		selEndLine = lines.length-1;
		selEndPos = lines[selEndLine].getStr().length();
	}
	String s = lines[selStartLine].getStr();
	if (selStartPos < 0) selStartPos = 0;	
	if (selStartPos > s.length()) selStartPos = s.length();	
	s = lines[selEndLine].getStr();
	if (selEndPos < 0) selEndPos = 0;	
	if (selEndPos > s.length()) selEndPos = s.length();	
}
//##################################################################
}
//##################################################################

class StringWrapper{
String str;
TextArea owner = null;
	StringWrapper(TextArea owner){
		str = "";
		this.owner = owner;
	}
	
	StringWrapper(TextArea owner,String str){
		this.str = str;
		this.owner = owner;
	}
	String getStr(){return str;}
	void setStr(String str){
		if(owner == null){
			this.str = str;
		}else{
			FontMetrics fm = owner.getFontMetrics();
			if(fm == null) return;
			int ww = fm.getTextWidth(str);
			int wControl = owner.getTextAreaWidth();
/*
			if(wControl - 5 < ww){
				System.out.println("need Split");
			}else{
				System.out.println("doesn't need Split");
			}
*/
			this.str = str;
		}
	}
	
	String getFullStr(){
		return str;
	}
}
