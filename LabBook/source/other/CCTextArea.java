package org.concord.LabBook;

import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import waba.ui.*;
import waba.fx.*;
import waba.sys.*;
import waba.util.Vector;
import extra.io.*;


class CCTextAreaChooser extends LabBookChooser{
Choice	alignmentChoice;
Check	wrapCheck,linkCheck;
Edit	widthEdit,heightEdit;
Label	alignmentLabel,wrapLabel,widthLabel,heightLabel;


	public CCTextAreaChooser(LObjDictionary dict,ViewContainer viewContainer,DialogListener l){
		super(dict,viewContainer,l);
	}
	public void setContent(){
  		waba.fx.FontMetrics fm = getFontMetrics(getFont()); 
		boolean firstTime = (view == null);
		super.setContent();
		if(view == null && viewContainer != null && dict != null){
			view = (LObjDictionaryView)dict.getView(viewContainer, true);
		}
		Rect r = getContentPane().getRect();
		if(view != null){
			if(firstTime){
				view.viewFromExternal = true;
				view.layout(false);
				getContentPane().add(view);
			}
			view.setRect(0,0,r.width,r.height - 30);
		}
		
		
		int xStart = 2;
		int cLength = (fm == null)?40:(2 + fm.getTextWidth("Align"));
		if(alignmentLabel == null){
			alignmentLabel = new Label("Align");
			getContentPane().add(alignmentLabel);
		}
		
		alignmentLabel.setRect(xStart,r.height - 50, cLength, 15);
		if(alignmentChoice == null){
			String choices[] = {"Left","Right"};
			alignmentChoice = new Choice(choices);
			getContentPane().add(alignmentChoice);
		}
		
		xStart += (cLength);
		cLength = 40;
		alignmentChoice.setRect(xStart,r.height - 50, cLength, 15);
		
		
		if(wrapCheck == null){
			wrapCheck = new Check("Wrap");
			getContentPane().add(wrapCheck);
			wrapCheck.setChecked(true);
		}
		xStart += (5+cLength);
		cLength = 40;
		wrapCheck.setRect(xStart,r.height - 50, cLength, 15);


		if(linkCheck == null){
			linkCheck = new Check("Link ");
			getContentPane().add(linkCheck);
		}
		xStart += (5+cLength);
		linkCheck.setRect(xStart,r.height - 50, cLength, 15);

		
		if(widthLabel == null){
			widthLabel = new Label("Width");
			getContentPane().add(widthLabel);
		}
		xStart = 2;
		cLength = (fm == null)?30:(2 + fm.getTextWidth("Width"));
		widthLabel.setRect(xStart,r.height - 35, cLength, 15);

		if(widthEdit == null){
			widthEdit = new Edit();
			widthEdit.setText("10");
			getContentPane().add(widthEdit);
		}
		xStart += (cLength);
		cLength = 30;
		widthEdit.setRect(xStart,r.height - 35, cLength, 15);
		
		
		if(heightLabel == null){
			heightLabel = new Label("Height");
			getContentPane().add(heightLabel);
		}
		xStart += (cLength);
		cLength = (fm == null)?30:(2 + fm.getTextWidth("Height"));
		heightLabel.setRect(xStart,r.height - 35, cLength, 15);

		if(heightEdit == null){
			heightEdit = new Edit();
			heightEdit.setText("10");
			getContentPane().add(heightEdit);
		}
		xStart += (cLength);
		cLength = 30;
		heightEdit.setRect(xStart,r.height - 35, cLength, 15);
		
		if(choiceButton == null){
			choiceButton = new Button("Choose");
			getContentPane().add(choiceButton);
		}
		choiceButton.setRect(r.width - 55,r.height - 18, 40, 16);
		
		
	}
    public void onEvent(Event e){
    	LabObject  obj = null;
    	boolean	   doNotify = false;
		if(e.type == TreeControl.DOUBLE_CLICK){
			if(e.target instanceof TreeControl){
				TreeControl tc = (TreeControl)e.target;
			    TreeNode curNode = tc.getSelected();
				obj = dict.getObj(curNode);	
				doNotify = true;
			}

		}else if(e.type == ControlEvent.PRESSED && e.target == choiceButton){
			if(view != null){
				TreeNode curNode = view.treeControl.getSelected();
				if(curNode != null){
					obj = dict.getObj(curNode);	
				}		
			}
			doNotify = true;
		}
		if(doNotify){
			if(obj != null && listener != null){
				boolean wrap = wrapCheck.getChecked();
				int alighn = LBCompDesc.ALIGNMENT_LEFT;
				if(alignmentChoice != null){
					if(alignmentChoice.getSelected().equals("Right")) alighn = LBCompDesc.ALIGNMENT_RIGHT;
				}
				int wc = 10;
				if(widthEdit != null){
					wc = Convert.toInt(widthEdit.getText());
				}
				int hc = 10;
				if(heightEdit != null){
					hc = Convert.toInt(heightEdit.getText());
				}
	 	  		LBCompDesc cdesc = new LBCompDesc(0,wc,hc,alighn,wrap,linkCheck.getChecked());
	 	  		cdesc.setObject(obj);
				listener.dialogClosed(new DialogEvent(this,null,null,cdesc,DialogEvent.OBJECT));
			}
			hide();
		}
	}
}



public class CCTextArea  extends Container implements ViewContainer, MainView, DialogListener{
//CCStringWrapper		[] lines;
Vector				lines = null;
FontMetrics 		fm = null;
int 				insetLeft = 5;
int 				insetRight = 10;
protected Timer 	caretTimer = null;
protected boolean 	hasCursor = false,cursorOn = false;
protected Font font = new Font("Helvetica",Font.PLAIN,12);

protected	int firstLine = 0;

protected CCTextAreaState curState = new CCTextAreaState();
			
LBCompDesc			[]components = null;
Vector				rows = null;		
MainView 			mainView = null;
LObjDictionary 		dict = null;
LBCompDesc			currObjectViewDesc = null;


CCTextAreaChooser		labBookDialog = null;
LObjSubDict				subDictionary;
LObjCCTextAreaView		owner;
String				text;


	public CCTextArea(LObjCCTextAreaView owner,MainView mainView,LObjDictionary dict,LObjSubDict subDictionary){
		super();
		this.mainView = mainView;
		this.dict = dict;
		this.subDictionary = subDictionary;
		this.owner = owner;
	}
	
	public FontMetrics getFontMetrics() {
		if(fm == null){
			fm = getFontMetrics(font);
		}
		return fm;
	}

	public MainView getMainView(){
		return this;
	}
    public void addMenu(LabObjectView source, Menu menu){
    	if(mainView != null) mainView.addMenu(source,menu);
    }

    public void delMenu(LabObjectView source, Menu menu){
    	if(mainView != null) mainView.delMenu(source,menu);
    }

	public void addFileMenuItems(String [] items, org.concord.waba.extra.event.ActionListener source){
    	if(mainView != null) mainView.addFileMenuItems(items,source);
	}

	public void removeFileMenuItems(String [] items, org.concord.waba.extra.event.ActionListener source){
    	if(mainView != null) mainView.removeFileMenuItems(items,source);
	}

	public String [] getCreateNames(){
		return null;
	}

	public void createObj(String name, LObjDictionaryView dView){
	}
	
	public void showFullWindowView(LabObjectView view){
	}


    public void dialogClosed(DialogEvent e){
		if(e.getInfoType() != DialogEvent.OBJECT || e.getInfo() == null) return;
		if(!(e.getInfo() instanceof LBCompDesc)) return;
		LBCompDesc obj = (LBCompDesc)e.getInfo();
		if(obj == null) return;
		Object o = obj.getObject();
		if(o == null || !(o instanceof LabObject)) return;
		LabObject labObject = (LabObject)o;
		int nComponents = (components == null)?0:components.length;
		LBCompDesc []newComponents = new LBCompDesc[nComponents+1];
		if(components != null){
			waba.sys.Vm.copyArray(components,0,newComponents,0,nComponents);
		}
		components = newComponents;
		LabObjectView view = labObject.getView(this,false);
		view.setEmbeddedState(true);
		components[nComponents] = obj;
		components[nComponents].setObject(view);
		components[nComponents].lineBefore = getLineIndex(curState.cursorRow + firstLine);
		if(subDictionary != null) subDictionary.setObj(labObject,nComponents);
		view.layout(false);
		add(view);
		layoutComponents();
		setText(getText());
    }
    public void writeExternal(DataStream out){
    	out.writeString(getText());
    	out.writeBoolean(components != null);
    	if(components == null) return;
    	out.writeInt(components.length);
    	for(int i = 0; i < components.length; i++){
    		LBCompDesc d = components[i];
    		out.writeBoolean(d != null);
    		if(d == null) continue;
    		d.writeExternal(out);
    	}
    }

    public void readExternal(DataStream in){
		setText(in.readString(),false);
		boolean wasComponents = in.readBoolean();
		if(!wasComponents) return;
		int nComp = in.readInt();
		if(nComp < 1) return;
		components = new LBCompDesc[nComp];
		for(int i = 0; i < nComp; i++){
			boolean wasPart = in.readBoolean();
			if(!wasPart) 	components[i] = null;
			else			components[i] = new LBCompDesc(in);
		}
    }



    public void done(LabObjectView source){
		if(labBookDialog != null){
			labBookDialog.hide();
			labBookDialog = null;
		}
    }

    public void reload(LabObjectView source){
		LabObject obj = source.getLabObject();
		source.close();
		remove(source);
		LabObjectView replacement = obj.getView(this, true);
//		replacement.setRect(x,y,width,myHeight);

		add(replacement);
//		lObjView = replacement;
    }

	public void close(){
		if(labBookDialog != null){
			labBookDialog.hide();
			labBookDialog = null;
		}
		if(currObjectViewDesc != null){
			if(currObjectViewDesc.getObject() instanceof LabObjectView){
				LabObjectView objView = (LabObjectView)currObjectViewDesc.getObject();
				objView.close();
			}
			currObjectViewDesc = null;
		}
	}



	protected int getItemHeight() {
		FontMetrics fm = getFontMetrics(); 
		return (fm == null)?0:fm.getHeight()+2;
	}


	public void clearAll(){
		setText("");
		deleteAllObjects(false);
		layoutComponents();
	}
	public void deleteCurrentObject(boolean doLayout){
		if(currObjectViewDesc != null){
			if(currObjectViewDesc.getObject() instanceof LabObjectView){
				LabObjectView objView = (LabObjectView)currObjectViewDesc.getObject();
				objView.close();
			}
			if(components != null){
				for(int i = 0; i < components.length; i++){
					if(currObjectViewDesc == components[i]){
						LabObjectView view = (LabObjectView)components[i].getObject();
						if(view != null){
							remove(view);
						}
						if(subDictionary != null) subDictionary.setObj(null,i);
						for(int j = i; j < components.length - 1; j++){
							components[j] = components[j+1];
							LabObjectView 	oView = (LabObjectView)components[j].getObject();
							LabObject 		tempObj = (oView == null)?null:oView.getLabObject();
							if(subDictionary != null) subDictionary.setObj(tempObj,j);
						}
						int nComp = components.length - 1;
						if(nComp < 1){
							components = null;
						}else{
							LBCompDesc []newComp = new LBCompDesc[nComp];
							waba.sys.Vm.copyArray(components,0,newComp,0,nComp);
							components = newComp;
						}
						break;
					}
				}
			}
			currObjectViewDesc = null;
			if(doLayout) layoutComponents();
			setText(getText());
		}
	}
	public void deleteCurrentObject(){
		deleteCurrentObject(true);
	}
	public void deleteAllObjects(){
		deleteAllObjects(true);
	}
	public void deleteAllObjects(boolean doLayout){
		deleteCurrentObject(false);
		if(components != null){
			for(int i = 0; i < components.length; i++){
				LabObjectView view = (LabObjectView)components[i].getObject();
				if(view != null) remove(view);
				if(subDictionary != null) subDictionary.setObj(null,i);
			}
			components = null;
			if(doLayout) layoutComponents();
			setText(getText());
		}
	}

	public void insertText(String str){
		if(str == null) return;
		layoutComponents();
		setText(str);//temporary
	}
	
	public String getText(){
/*		String retValue = "";
		if(lines == null) return retValue;
		for(int i = 0; i < lines.length; i++){
			retValue += (lines[i].getStr() + "\n");
		}
		return retValue;*/
		return text;
	}
	public void insertObject(){
		if(labBookDialog != null){
			labBookDialog.hide();
			labBookDialog = null;
		}
		if(dict != null){
			labBookDialog = new CCTextAreaChooser(dict,this,this);
			labBookDialog.show();
		}



	}
	
	public void layoutComponents(){
		if(components != null){
			for(int i = 0; i < components.length; i++){
				LBCompDesc c = components[i];
				if(c == null) continue;
				Control cntrl = (Control)c.getObject();
				if(cntrl != null){//?
					remove(cntrl);
				}else{
					LabObject lobj = subDictionary.getObj(i);
					if(lobj != null){
						cntrl = lobj.getView(this,false);
						((LabObjectView)cntrl).setEmbeddedState(true);

						c.setObject(cntrl);
					}
				}
				if(cntrl != null) add(cntrl);//?
//				int yTop = y;
				int yTop = 0;
				int line = c.lineBefore;
				if(line > 0){
					if(lines != null && line < lines.getCount()){
//						yTop += (lines[line - 1].endRow - firstLine)*getItemHeight();
						yTop += (((CCStringWrapper)lines.get(line - 1)).endRow - firstLine)*getItemHeight();
					}
				}else if(line == 0){
					yTop -= firstLine*getItemHeight();
				}	
				if(cntrl != null){
					if(c.alignment == LBCompDesc.ALIGNMENT_RIGHT){
						cntrl.setRect(x+width-insetRight-c.w,yTop,c.w,c.h);
					}else{
						cntrl.setRect(x+insetLeft,yTop,c.w,c.h);
					}
				}
			}
		}
	}
	
	public void setRect(int x, int y, int width, int height){
		super.setRect(x,y,width,height);
		layoutComponents();
	}
	
	
	public void test(){
		setText(getText());
		repaint();
	}
	public void setText(String str){
		setText(str,true);
	}
	
	public void setText(String str, boolean forceAction){
		int determinedSystem = -1; //unix - 0; //mac - 1 dos - 2
		if(str == null) return;
		text = str;
		if(!forceAction) return;
		lines = new Vector();
		rows = new Vector();
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
				int nLines = lines.getCount();
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
				if(compDesc == null){
					lines.add(new CCStringWrapper(this,sb.toString(),lastRow));
				}else{
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
					
					lines.add(new CCStringWrapper(this,sb.toString(),lastRow));
					int nRows = rows.getCount();
					if(addRows > 0){
						for(int k = nRows; k < nRows + addRows; k++){
							CCTARow newRow = new CCTARow((CCStringWrapper)lines.get(nLines));
							newRow.setMargins(leftMargin,rightMargin);
							rows.add(newRow);
						}
					}
				}
				int nRows = rows.getCount();
				int lastLineRow = ((CCStringWrapper)lines.get(nLines)).endRow;
				int addRows = lastLineRow - nRows;
				if(addRows > 0){
					for(int k = nRows; k < nRows + addRows; k++){
						CCTARow newRow = new CCTARow((CCStringWrapper)lines.get(nLines));
						newRow.setMargins(r.x + insetLeft,r.x + r.width - insetRight);
						rows.add(newRow);
					}
				}				
				lastRow += (((CCStringWrapper)lines.get(nLines)).getRows());
				sb.setLength(0);
			}
			i++;
		}
		if(lines.getCount() < 1){
			lines.add(new CCStringWrapper(this,sb.toString(),0));
		}else{
			lines.add(new CCStringWrapper(this,sb.toString(),lastRow));
		}
		repaint();
	}
	public void onControlEvent(ControlEvent ev){
		if (ev.type == ev.TIMER && hasCursor){
			paintCursor(null);
		}
		else if (ev.type == ev.FOCUS_IN){
			if(ev.target  == this){
				gotFocus();
			}else if(components != null){
				LBCompDesc cntrlDesc = findComponentDesc(ev.target);
				if(cntrlDesc != null){
					Control cntrl = (Control)cntrlDesc.getObject();
					if(cntrl instanceof LabObjectView){
						LabObjectView object = (LabObjectView)cntrl;
						object.setShowMenus(true);
						currObjectViewDesc = cntrlDesc;
						removeCursor();
						if(cntrlDesc.link){
							object.setShowMenus(true);
							if(owner != null){
								owner.addChoosenLabObjView(object);
							}
						}
					}
				}
			}
		}else if (ev.type == ev.FOCUS_OUT){
			if(ev.target  == this){
				lostFocus();
			}else if(currObjectViewDesc != null){
				Control cntrl = (Control)currObjectViewDesc.getObject();
				if(cntrl instanceof LabObjectView){
					LabObjectView object = (LabObjectView)cntrl;
					object.close();
					currObjectViewDesc = null;
				}
			}
		}
	}
	public void gotFocus(){
		restoreCursor();
	}
	public void lostFocus(){
		removeCursor();
	}
	
	LBCompDesc findComponentDesc(Object o){
		if(components == null || components.length < 1) return null;
		for(int i = 0; i < components.length; i++){
			LabObjectView view = getParentLabObjectView(o);
			if(view == null) continue;
			if(components[i].getObject() == view) return components[i];
		}
		return null;
	}
	
	public static LabObjectView getParentLabObjectView(Object o){
		if(!(o instanceof Control)) return null;
		LabObjectView view = null;
		Control c = (Control)o;
		while(view == null && c != null){
			if(c instanceof LabObjectView){
				view = (LabObjectView)c;
				break;
			}
			c = c.getParent();
		}
		return view;
	}
	public static LObjDictionaryView getParentLObjDictionaryView(Object o){
		if(!(o instanceof Control)) return null;
		LObjDictionaryView view = null;
		Control c = (Control)o;
		while(view == null && c != null){
			if(c instanceof LObjDictionaryView){
				view = (LObjDictionaryView)c;
				break;
			}
			c = c.getParent();
		}
		return view;
	}
	
	public void restoreCursor(){
		restoreCursor(false);
	}
	public void restoreCursor(boolean forseAction){
		if(!forseAction && currObjectViewDesc != null) return;
		hasCursor =  true;
		if(caretTimer == null) caretTimer = addTimer(500);
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
		if (g == null) return;
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

	public void moveUp(){
		if(firstLine > 0){
			removeCursor();
			firstLine--;
			layoutComponents();
			repaint();
		}
	}
	public void moveDown(){
		int nRows = getRowsNumber();
		if(lines != null && firstLine < nRows - 2){
			removeCursor();
			firstLine++;
			layoutComponents();
			repaint();
		}
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
				layoutComponents();
				repaint();
			}
		}else if (ev.key == IKeys.LEFT){
		}else if (ev.key == IKeys.RIGHT){
		}else if (ev.key == IKeys.UP || ev.key == IKeys.PAGE_UP){
			moveUp();
		}else if (ev.key == IKeys.DOWN || ev.key == IKeys.PAGE_DOWN){
			moveDown();
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
		if(lines == null || lines.getCount() < 1) return 0;
		for(int i = 0; i < lines.getCount(); i++){
			retValue += ((CCStringWrapper)lines.get(i)).getRows();
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
			if(lineIndex >= 0 && lineIndex < lines.getCount()){
				CCStringWrapper sw = (CCStringWrapper)lines.get(lineIndex);
				int rIndex = row - 1 - sw.beginRow;
				if(rIndex >= 0 && rIndex < sw.delimiters.length / 2){
					String str = sw.getSubString(rIndex);
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
		if(lines == null || lines.getCount() < 1) return retValue;
		if(row > getRowsNumber()) return lines.getCount() - 1;
		int ind = 0;
		for(int i = 0; i < lines.getCount(); i++){
			int swRows = ((CCStringWrapper)lines.get(i)).getRows();
			if(row < ind + swRows){
				retValue = i;
				break;
			}
			ind += swRows;
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
		for (int i = 0; i<lines.getCount(); i++){
			((CCStringWrapper)lines.get(i)).draw(g,firstLine);
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
char 	[]chars = null;
int		delimiters[] = null;
static int	[]charWidthMappers = null;


	CCStringWrapper(CCTextArea owner,String str,int beginRow){
		this.owner = owner;
		Rect r = owner.getRect();
		beginPos 	= r.x + owner.insetLeft;
		endPos 		= r.x + r.width - owner.insetRight;
		setStr(str,beginRow);		
	}
	
	private static void createCharWidthMappers(CCTextArea owner){
		if(charWidthMappers != null || owner == null) return;
		FontMetrics fm = owner.getFontMetrics();
		if(fm == null) return;
		charWidthMappers = new int[96];
		for(int i = 0; i < 96; i++){
			charWidthMappers[i] = fm.getCharWidth((char)(i+32));
		}
	}
	
	String getStr(){return str;}
	void setStr(String str,int beginRow){
		this.beginRow 	= beginRow;
		if(owner == null){
			this.str = null;
			return;
		}
		
		int    numbTotalRows = (owner.rows == null)?0:owner.rows.getCount();
		this.str = str;
		FontMetrics fm = owner.getFontMetrics();
		if(fm == null || str == null) return;
		int currRow = beginRow;
		int x 			= (currRow >= numbTotalRows || owner.rows == null)?beginPos:((CCTARow)owner.rows.get(currRow)).beginPos;
		int lastWord 	= 0;
		int	blankWidth = fm.getCharWidth(' ');
		int	delimiterIndex = 0;
		int i = 0;
		
		int nLines = 0;
		delimiters = null;
		chars = str.toCharArray();
		while(i < str.length()){
			char c = str.charAt(i);
			if(isWordDelimiter(c)){
				if(c == '\n') break;
				lastWord = i+1;
			}
			if(charWidthMappers == null){
				createCharWidthMappers(owner);
			}
			int w = 0;
			if(c >= ' ' && c < (char)128){
				w = (charWidthMappers != null)?charWidthMappers[(int)c - 32]:fm.getCharWidth(c);
			}else{
				w = fm.getCharWidth(c);
			}
			
			
			int limitX = (currRow >= numbTotalRows || owner.rows == null)?endPos:((CCTARow)owner.rows.get(currRow)).endPos;
			if(x + w > limitX){
				if(lastWord == delimiterIndex){
					lastWord = i;
				}else{
					i = lastWord;
				}
				int nInt = 0;
				if(delimiters != null){
					nInt = delimiters.length;
				}
				int	[]newDelimiters = new int[nInt + 2];
				if(delimiters != null){
					waba.sys.Vm.copyArray(delimiters,0,newDelimiters,0,nInt);
				}
				delimiters = newDelimiters;
				delimiters[nInt] = delimiterIndex;
				delimiters[nInt+1] = lastWord;
				
				currRow++;
				x = (currRow >= numbTotalRows || owner.rows == null)?beginPos:((CCTARow)owner.rows.get(currRow)).beginPos;
				delimiterIndex = i;
			}else{
				x += w;
			}
			i++;
		}
		if(delimiters == null){
			delimiters = new int[2];
			delimiters[0] = 0;
			delimiters[1] = chars.length;
		}else{
			if(delimiterIndex < str.length()){
				int nInt = delimiters.length;
				int	[]newDelimiters = new int[nInt + 2];
				waba.sys.Vm.copyArray(delimiters,0,newDelimiters,0,nInt);
				delimiters = newDelimiters;
				delimiters[nInt] = delimiterIndex;
				delimiters[nInt+1] = chars.length;
			}
		}
		this.endRow 	= this.beginRow + (delimiters.length / 2);
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
	
	public String getSubString(int index){
		if(chars == null || delimiters == null) return null;
		if(index < 0 || index >= delimiters.length / 2) return null;
		return new String(chars,delimiters[2*index],delimiters[2*index+1]-delimiters[2*index]);
	}
	
	public void draw(Graphics gr,int firstRow){
		if(gr == null || delimiters == null || owner == null) return;
		gr.setColor(0,0,0);
		int    numbTotalRows = (owner.rows == null)?0:owner.rows.getCount();
		int h = owner.getItemHeight();
		int limitRow = delimiters.length / 2;
		for(int i = beginRow; i < endRow; i++){
			if(i < firstRow) continue;
			int y = (i - firstRow)*h;
			if(i - beginRow < limitRow ){
				int x = (i >= numbTotalRows || owner.rows == null)?beginPos:((CCTARow)owner.rows.get(i)).beginPos;
				int index = (i - beginRow)*2;
				gr.drawText(chars,delimiters[index],delimiters[index+1] - delimiters[index],x,y);
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
/*
		switch(c){
			case ' ':
			case '\t':
			case ';':
			case '.':
			case ',':
				retValue = true;
				break;
		}
*/
		return retValue;
	}
	

private static char []wordDelimChars = {' ','\t',';','.',','};	

}

