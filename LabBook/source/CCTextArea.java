package org.concord.LabBook;

import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import waba.ui.*;
import waba.fx.*;
import waba.sys.*;
import waba.util.Vector;
import extra.io.*;

public class CCTextArea  extends Container 
	implements ViewContainer, DialogListener
{
	public static final int	version = 12;

	public Vector		lines = null;
	FontMetrics 		fm = null;
	int 				insetLeft = 5;
	int 				insetRight = 10;
	protected Timer 	caretTimer = null;
	protected boolean 	hasCursor = false,cursorOn = false;
	protected Font font = new Font("Helvetica",Font.PLAIN,12);

	public	int firstLine = 0;

	protected CCTextAreaState curState = new CCTextAreaState();
			
	LBCompDesc			[]components = null;
	Vector				rows = null;		
	MainView 			mainView = null;
	LObjDictionary 		dict = null;
	LBCompDesc			currObjectViewDesc = null;

	CCTextAreaChooser		labBookDialog = null;
	LObjSubDict				subDictionary;
	LabBookSession          session;
	LObjCCTextAreaView		owner;
	String				text;

	boolean		needNotifyAboutMenu = true;

	EmbedObjectPropertyControl currObjPropControl;

	ObjPropertyViewDialog 	currObjPropDialog = null;

	String []dialogButtonTitles = {"Yes","No"};
	Dialog					confirmDialogClear = null;
	Dialog					confirmDialogDeleteAll = null;
	Dialog					confirmDialogDeleteCurrent = null;
	Dialog					confirmDialogDeleteChosenParagraph = null;


	Vector	listeners;

	public final static int	yTextBegin = 2;
	public static int INTER_LINE_SPACING = 2;

	LObjDictionary		objDictionary = null;

	private final static EmptyLabObject emptyObject = new EmptyLabObject();

	private CCStringWrapper textWasChosen = null;

	public CCTextArea(LObjCCTextAreaView owner,MainView mainView,LObjDictionary dict,
					  LObjSubDict subDictionary, LabBookSession sess){
		super();
		this.mainView = mainView;
		this.dict = dict;
		this.subDictionary = subDictionary;
		this.owner = owner;
		session = sess;
	}

	public void setSession(LabBookSession sess)
	{
		session = sess;
	}

	public void addTextAreaListener(TextAreaListener l){
		if(listeners == null){
			listeners = new Vector();
			listeners.add(l);
		}else{
			int index = listeners.find(l);
			if(index < 0) listeners.add(l);
		}
	}
	public void removeTextAreaListener(TextAreaListener l){
		if(listeners == null) return;
		int index = listeners.find(l);
		if(index >= 0) listeners.del(index);
	}
	
	public void notifyListeners(int  type){
		if(listeners == null || listeners.getCount() < 1) return;
		for(int i = 0; i < listeners.getCount(); i++){
			TextAreaListener l = (TextAreaListener)listeners.get(i);
			if(l != null) l.textAreaWasChanged(null);
		}
	}
	
	public FontMetrics getFontMetrics() {
		if(fm == null){
			fm = getFontMetrics(font);
		}
		return fm;
	}

	public MainView getMainView()
	{		
		return owner.getMainView();
	}

	public void delMenus(){
    	if(mainView != null){
			if(currObjectViewDesc != null){
				Object o = currObjectViewDesc.getObject();
				if(o != null && o instanceof LabObjectView){
					LabObjectView ov = (LabObjectView)o;
					ov.setShowMenus(false);
				}
			}
		}
	}


	public boolean getEditMode(){
		if(owner != null && owner.doc != null) return owner.doc.editMode;
		return false;
	}

    public void dialogClosed(DialogEvent e){
    	if(e.getSource() == currObjPropDialog){
    		if(currObjectViewDesc != null){

 
 				boolean wrap = currObjPropControl.wrapCheck.getChecked();
				EmbedObjectPropertyControl.lastWrap = wrap;
				int alighn = LBCompDesc.ALIGNMENT_LEFT;
				if(currObjPropControl.alignmentChoice != null){
					EmbedObjectPropertyControl.lastAlighnLeft = true;
					if(currObjPropControl.alignmentChoice.getSelected().equals("Right")){
						EmbedObjectPropertyControl.lastAlighnLeft = false;
					}
				}
				EmbedObjectPropertyControl.lastW = 10;
				if(currObjPropControl.widthEdit != null){
					EmbedObjectPropertyControl.lastW = Convert.toInt(currObjPropControl.widthEdit.getText());
				}
				EmbedObjectPropertyControl.lastH = 10;
				if(currObjPropControl.heightEdit != null){
					EmbedObjectPropertyControl.lastH = Convert.toInt(currObjPropControl.heightEdit.getText());
				}
	 	  		EmbedObjectPropertyControl.lastLink = currObjPropControl.linkCheck.getChecked();
    			currObjectViewDesc.alignment 	= (EmbedObjectPropertyControl.lastAlighnLeft)?
					LBCompDesc.ALIGNMENT_LEFT:LBCompDesc.ALIGNMENT_RIGHT;
    			currObjectViewDesc.wrapping 	= EmbedObjectPropertyControl.lastWrap;
    			currObjectViewDesc.link 		= EmbedObjectPropertyControl.lastLink;
    			currObjectViewDesc.w 			= EmbedObjectPropertyControl.lastW;
    			currObjectViewDesc.h 			= EmbedObjectPropertyControl.lastH;
    			
				if(currObjectViewDesc.getObject() instanceof LabObjectView){
					LabObjectView objView = (LabObjectView)currObjectViewDesc.getObject();
					boolean needChangeView = ((currObjectViewDesc.link && !(objView instanceof LObjMinimizedView)) ||
											  (!currObjectViewDesc.link && (objView instanceof LObjMinimizedView)));					
					if(needChangeView){
						LabObjectPtr lObjPtr = null;
						if(objView instanceof LObjMinimizedView){
							lObjPtr = ((LObjMinimizedView)objView).getPtr();
						} else {
							LabObject lobj = objView.getLabObject();
							lObjPtr = lobj.getVisiblePtr();
						} 							
 
						objView.close();
						remove(objView);
						objView = setCompDescView(currObjectViewDesc, lObjPtr);
						add(objView);
					}
				}

				initLines();
   			}
    		currObjPropDialog = null;
    		return;
    	}else if(e.getSource() == labBookDialog){
			if(e.getInfoType() != DialogEvent.OBJECT || e.getInfo() == null) return;
			if(!(e.getInfo() instanceof LBCompDesc)) return;
			LBCompDesc obj = (LBCompDesc)e.getInfo();
			if(obj == null) return;
			Object o = obj.getObject();
			if((o instanceof LObjCCTextArea) || (o instanceof LObjCCTextAreaView)){
				Sound.beep();
				return;
			}
			if(o == null || !(o instanceof LabObject)) return;
			LabObject labObject = (LabObject)o;
			int nComponents = (components == null)?0:components.length;
			LBCompDesc []newComponents = new LBCompDesc[nComponents+1];
			if(components != null){
				waba.sys.Vm.copyArray(components,0,newComponents,0,nComponents);
			}
			components = newComponents;
			LabObjectView view = setCompDescView(obj, labObject.getVisiblePtr());

			components[nComponents] = obj;
			components[nComponents].lineBefore = getLineIndex(curState.cursorRow + firstLine);
			
			setObj(labObject,nComponents);
			view.layout(false);
			add(view);
			initLines();
		}else if(e.getSource() == confirmDialogClear){
			if(e.getActionCommand().equals("Yes")){
				clearAll();
				notifyListeners(0);
			}
			confirmDialogClear = null;
		}else if(e.getSource() == confirmDialogDeleteCurrent){
			if(e.getActionCommand().equals("Yes")){
				deleteCurrentObject();
				notifyListeners(0);
			}
			confirmDialogDeleteCurrent = null;
		}else if(e.getSource() == confirmDialogDeleteAll){
			if(e.getActionCommand().equals("Yes")){
				deleteAllObjects();
				notifyListeners(0);
			}
			confirmDialogDeleteAll = null;
		}else if(e.getSource() == confirmDialogDeleteChosenParagraph){
			if(e.getActionCommand().equals("Yes")){
				deleteChosenParagraph();
				notifyListeners(0);
			}
			confirmDialogDeleteChosenParagraph = null;
		}
    }
    
    public void initLineDictionary(){
    	if(subDictionary == null) return;

    	LabObject zeroObject = subDictionary.getObj(0, session);
		if(objDictionary == null){
			if(zeroObject instanceof LObjDictionary){
				objDictionary = (LObjDictionary)zeroObject;
			}else{
				objDictionary = DefaultFactory.createDictionary();
				session.storeNew(objDictionary);
				if(objDictionary == null) return;
				if(subDictionary.getNumObjs() > 0){
					for(int i = subDictionary.getNumObjs(); i >= 0; i--){
						LabObject o = subDictionary.getObj(i, session);
						subDictionary.setObj(o,i+1);
					}
				}
				subDictionary.setObj(objDictionary,0);
			}
		}
    }
    
    public void setObj(LabObject lobj,int index){
    
    
		if(subDictionary == null) return;
    	LabObject zeroObject = subDictionary.getObj(0, session);
    	if(zeroObject instanceof LObjDictionary){
			subDictionary.setObj(lobj,index + 1);
    	}else{
			subDictionary.setObj(lobj,index);
		}
    }
    public LabObject getObj(int index){
    	LabObject retObject = null;
    	if(subDictionary == null) return retObject;
    	LabObject zeroObject = subDictionary.getObj(0, session);
    	if(zeroObject instanceof LObjDictionary){
    		retObject = subDictionary.getObj(index + 1, session);
    	}else{
    		retObject = subDictionary.getObj(index, session);
    	}
    	return retObject;
    }

    public LabObjectPtr getPtr(int index){
    	LabObjectPtr retPtr = null;
    	if(subDictionary == null) return retPtr;
    	LabObject zeroObject = subDictionary.getObj(0, session);
    	if(zeroObject instanceof LObjDictionary){
    		retPtr = subDictionary.getPtr(index + 1);
    	}else{
    		retPtr = subDictionary.getPtr(index);
    	}
    	return retPtr;
	}


    public void writeExternal(DataStream out){
		if(lines == null){
			out.writeInt(0);
		} else {
			out.writeInt(lines.getCount());
		}
		if(components == null){
			out.writeInt(0);
		} else {
			out.writeInt(components.length);
		}
		if(lines != null){
			for(int i=0; i<lines.getCount(); i++){
				CCStringWrapper sWrap = (CCStringWrapper)lines.get(i);
				if(sWrap == null){
					out.writeBoolean(false);
				} else {
					out.writeBoolean(true);
					sWrap.writeExternal(out);
				}
			}
		}
		if(components != null){
			for(int i=0; i<components.length; i++){
	    		LBCompDesc d = components[i];
				if(d == null){
					out.writeBoolean(false);
				} else {
					out.writeBoolean(true);
					d.writeExternal(out);
				}
			}
		}
    }

	boolean needInitLines = false;

    public void readExternal(DataStream in){
		int nLines = in.readInt();
		int nComp = in.readInt();
		lines = new Vector();
		for(int i=0; i<nLines; i++){
			if(in.readBoolean()){
				lines.add(new CCStringWrapper(in));
			} else {
				lines.add(null);
			}
		}

		components = new LBCompDesc[nComp];
		for(int i = 0; i < nComp; i++){
			boolean wasPart = in.readBoolean();
			if(!wasPart) 	components[i] = null;
			else			components[i] = new LBCompDesc(in);
		}

		needInitLines = true;
    }

    public void done(LabObjectView source){
		if(labBookDialog != null){
			labBookDialog.hide();
			labBookDialog = null;
		}				
    }

    public void reload(LabObjectView source){ }

	public void close(){
		textWasChosen = null;
		if(labBookDialog != null){
			labBookDialog.hide();
			labBookDialog = null;
		}
		if(caretTimer != null){
			removeTimer(caretTimer);
			caretTimer = null;
		}
		if(loadingTimer != null){
			removeTimer(loadingTimer);
			loadingTimer = null;
		}

		if(components == null || components.length < 1) return;
		for(int i = 0; i < components.length; i++){
			 LBCompDesc c = components[i];
			 if(c != null && c.getObject() != null){
				 ((LabObjectView)c.getObject()).close();
			 }
		}
	}

	protected int getItemHeight() {
		FontMetrics fm = getFontMetrics(); 
		return (fm == null)?0:fm.getHeight()+INTER_LINE_SPACING;
	}

	public int getVisRows(){
		int h = getItemHeight();
		if(h == 0) return 0;
		return height / h;
	}

	public void requireClearingAll(){
		if(!getEditMode()){
			Sound.beep();
			return;
		}
		confirmDialogClear = 
			Dialog.showConfirmDialog(this,"Clearing All","Are you sure? ",
									 dialogButtonTitles,Dialog.QUEST_DIALOG);
	}

	// hmmm...
	public void clearAll(){
		setText("");
		deleteAllObjects(false);
		layoutComponents();
	}
	
	public void requireDeleteCurrentObject(){
		if(!getEditMode()){
			Sound.beep();
			return;
		}
		confirmDialogDeleteCurrent = 
			Dialog.showConfirmDialog(this,"Delete Object","Are you sure? ",
									 dialogButtonTitles,Dialog.QUEST_DIALOG);
	}
	
	public void deleteCurrentObject(boolean doLayout){
		if(!getEditMode()){
			Sound.beep();
			return;
		}
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
						setObj(null,i);
						for(int j = i; j < components.length - 1; j++){
							components[j] = components[j+1];
							LabObject 		tempObj = null;
							if(components[j].getObject() instanceof LabObjectView){
								LabObjectView 	oView = (LabObjectView)components[j].getObject();
								if(oView instanceof LObjMinimizedView){
									tempObj = session.load(((LObjMinimizedView)oView).getPtr());
								} else if(oView != null){
									tempObj = oView.getLabObject();
								}
									
							}
							setObj(tempObj,j);
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
			initLines();
		}
	}
	public void deleteCurrentObject(){
		deleteCurrentObject(true);
	}

	public void requireDeleteAllObjects(){
		if(!getEditMode()){
			Sound.beep();
			return;
		}
		confirmDialogDeleteAll = 
			Dialog.showConfirmDialog(this,"Delete All Objects","Are you sure? ",
									 dialogButtonTitles,Dialog.QUEST_DIALOG);
	}
	
	public void requireDeleteChosenParagraph(){
		if(!getEditMode()){
			Sound.beep();
			return;
		}
		confirmDialogDeleteChosenParagraph = 
			Dialog.showConfirmDialog(this,"Delete Paragraph","Are you sure? ",
									 dialogButtonTitles,Dialog.QUEST_DIALOG);
	}
	
	public void deleteAllObjects(){
		deleteAllObjects(true);
	}
	public void deleteAllObjects(boolean doLayout){
		if(!getEditMode()){
			Sound.beep();
			return;
		}
		deleteCurrentObject(false);
		if(components != null){
			for(int i = 0; i < components.length; i++){
				LabObjectView view = (LabObjectView)components[i].getObject();
				if(view != null) remove(view);
				setObj(null,i);
			}
			components = null;
			initLines();
		}
	}

	public void deleteChosenParagraph(){
		if(!getEditMode() || (textWasChosen == null) || lines == null){
			Sound.beep();
			return;
		}
		int index = lines.find(textWasChosen);
		if(index >= 0) lines.del(index);

		String str = "";
		for(int i = 0; i < lines.getCount(); i++){
			str += (((CCStringWrapper)lines.get(i)).getStr() + "\n");
		}
		textWasChosen = null;
		initLines();
	}

	public void setup(waba.util.Vector linesVector,
					  waba.util.Vector linkComponents,
					  waba.util.Vector embedComponents){
		if(linesVector == null) return;
		int i;
		textWasChosen = null;
		int linkObjectIndex = 0;
		initLineDictionary();
		for(i = 0; i < linesVector.getCount(); i++){
			CCStringWrapper wrapper = (CCStringWrapper)linesVector.get(i);
			wrapper.owner = this;
			if(wrapper.link && linkComponents != null){
				LabObject lObject = (LabObject)linkComponents.get(linkObjectIndex);
				if(lObject != null){
					objDictionary.add(lObject);
					wrapper.indexInDict = (linkObjectIndex++);
				}
			}
		}
		lines = linesVector;

		if(embedComponents != null && embedComponents.getCount() > 0){
			for(i = 0; i < embedComponents.getCount(); i++){
				LBCompDesc objDesc = (LBCompDesc)embedComponents.get(i);
				Object o = objDesc.getObject();

				if(o == null || !(o instanceof LabObject)) continue;

				LabObject labObject = (LabObject)o;

				int nComponents = (components == null)?0:components.length;
				LBCompDesc []newComponents = new LBCompDesc[nComponents+1];
				if(components != null){
					waba.sys.Vm.copyArray(components,0,newComponents,0,nComponents);
				}
				components = newComponents;

				components[nComponents] = objDesc;

				setObj(labObject,nComponents);
			}
		}
		needInitLines = true;
	}

	public void insertText(String iStr){
		if(!getEditMode()){
			Sound.beep();
			return;
		}
		Vector oldVectorLines = lines;
		removeCursor();
		if(iStr == null) return;
		int nStr = iStr.length();
		if(nStr < 1){
			iStr += "\n";
		}else{
			char c = iStr.charAt(nStr - 1);
			boolean wasEOL = false;
			if(c == '\n'){
				wasEOL = true;
			}else if(c == '\r'){
				wasEOL = true;
			}
			if(!wasEOL){
				iStr += "\n";
			}
		}
		int lineIndex = getLineIndex(curState.cursorRow + firstLine);
		int oldLines = (lines == null)?0:lines.getCount();
		String str = "";
		if(lines == null){
			str = iStr;
		}else{
			boolean wasAdded = false;
			for(int i = 0; i < lines.getCount(); i++){
				if(i == lineIndex){
					wasAdded = true;
					str += iStr;
				}
				str += (((CCStringWrapper)lines.get(i)).getStr() + "\n");
			}
			if(!wasAdded) str += iStr;
		}
		
		int oldRows = getRowsNumber();
		setText(str);//temporary
		int newRows = getRowsNumber();
		if(newRows > oldRows){
			curState.cursorRow += (newRows - oldRows);
		}
		restoreTextProperty(oldVectorLines,lineIndex,(newRows - oldRows));
		/*
		  int newLines = (lines == null)?0:lines.getCount();
		  int addLines = newLines - oldLines;
		  if(addLines > 0 && components != null){
		  for(int i = 0; i < components.length; i++){
		  LBCompDesc c = components[i];
		  int lineBefore = c.lineBefore;
		  if(lineIndex < lineBefore){
		  c.lineBefore += addLines;
		  }
		  }
		  }
		*/
		layoutComponents();
		notifyListeners(0);
		restoreCursor(true);
	}
	public void insertEmptyLine(){
		if(!getEditMode()){
			Sound.beep();
			return;
		}
		if(lines == null){
			setText("");
		}else{
			Vector oldLines = lines;
			int lineIndex = getLineIndex(curState.cursorRow + firstLine);
			String str = "";
			for(int i = 0; i < lines.getCount(); i++){
				if(i == lineIndex) str += " \n";
				str += (((CCStringWrapper)lines.get(i)).getStr() + "\n");
			}
			setText(str);//temporary
			restoreTextProperty(oldLines,lineIndex,1);
		}
		layoutComponents();
		notifyListeners(0);
	}
	
	public String getText(){
		return text;
	}
	public void insertObject(){
		if(!getEditMode()){
			Sound.beep();
			return;
		}
		if(labBookDialog != null){
			labBookDialog.hide();
			labBookDialog = null;
		}
		if(dict != null){
			MainWindow mw = MainWindow.getMainWindow();
			if(!(mw instanceof ExtraMainWindow)) return;
			labBookDialog = new CCTextAreaChooser((ExtraMainWindow)mw,
												  (LObjDictionary)LabObject.lBook.load(LabObject.lBook.getRoot()),
												  this,this, session);
			labBookDialog.setRect(0,0,150,150);
			labBookDialog.show();
		}
	}
	
	public void layoutComponents(){
		if(components != null){
			for(int i = 0; i < components.length; i++){
				LBCompDesc c = components[i];
				if(c == null) continue;
				Control cntrl = (Control)c.getObject();
				if(cntrl == null){
					LabObjectPtr lObjPtr = getPtr(i);
					if(lObjPtr != null){
						cntrl = setCompDescView(c, lObjPtr);
						if(cntrl != null) add(cntrl);
					}
				}

				int yTop = CCTextArea.yTextBegin;
				int line = c.lineBefore;
				if(line > 0){
					if(lines != null && line < lines.getCount()){
						yTop += (((CCStringWrapper)lines.get(line - 1)).endRow - firstLine)*getItemHeight();
					}else if(lines != null && line >= lines.getCount()){
						yTop += (((CCStringWrapper)lines.get(lines.getCount() - 1)).endRow - firstLine)*getItemHeight();
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
			if(needNotifyAboutMenu){
				needNotifyAboutMenu = false;
			}
		}
	}
	
	
	public void setRect(int x, int y, int width, int height){
		super.setRect(x,y,width,height);

		if(needInitLines && loadingTimer == null){
			// seems like add timer calls onPaint (ick)
			loadingTimer = addTimer(10);
		}
	}
	
	
	public void test(){
		setText(getText());
		repaint();
	}

	public void restoreTextProperty(Vector oldLines,int insertAtIndex,int addLines){
		if(lines == null) return;
		if(oldLines == null) return;
		if(oldLines.getCount() >= lines.getCount()) return;
		for(int i = 0; i < lines.getCount(); i++){
			if(i >= insertAtIndex && i < insertAtIndex + addLines) continue;
			int oldIndex = (i < insertAtIndex)?i:i-addLines;
			CCStringWrapper oldWrapper = (CCStringWrapper)oldLines.get(oldIndex);
			CCStringWrapper newWrapper = (CCStringWrapper)lines.get(i);
			newWrapper.rColor = oldWrapper.rColor;
			newWrapper.gColor = oldWrapper.gColor;
			newWrapper.bColor = oldWrapper.bColor;
			newWrapper.link = oldWrapper.link;
			newWrapper.indexInDict = oldWrapper.indexInDict;
		}
	}

	public void restoreTextProperty(Vector oldLines){
		if(lines == null) return;
		if(oldLines == null) return;
		if(oldLines.getCount() != lines.getCount()) return;
		for(int i = 0; i < lines.getCount(); i++){
			CCStringWrapper oldWrapper = (CCStringWrapper)oldLines.get(i);
			CCStringWrapper newWrapper = (CCStringWrapper)lines.get(i);
			newWrapper.rColor = oldWrapper.rColor;
			newWrapper.gColor = oldWrapper.gColor;
			newWrapper.bColor = oldWrapper.bColor;
			newWrapper.link = oldWrapper.link;
			newWrapper.indexInDict = oldWrapper.indexInDict;
		}
	}

	public void changeLineContent(String str,CCStringWrapper stringWrapper){
		if(str == null || stringWrapper == null) return;
		if(lines == null) return;

		initLines();
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
						if(cDesc != null && cDesc.lineBefore == nLines){
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
					
					int nRows = rows.getCount();
					if(addRows > 0){
						for(int k = nRows; k < nRows + addRows; k++){
							CCTARow newRow = new CCTARow();
							newRow.setMargins(leftMargin,rightMargin);
							rows.add(newRow);
						}
					}
					lines.add(new CCStringWrapper(this,sb.toString(),lastRow));
				}
				int nRows = rows.getCount();
				int lastLineRow = ((CCStringWrapper)lines.get(nLines)).endRow;
				int addRows = lastLineRow - nRows;
				if(addRows > 0){
					for(int k = nRows; k < nRows + addRows; k++){
						CCTARow newRow = new CCTARow();
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
			String s = sb.toString();
			if(s.length() > 1){
				lines.add(new CCStringWrapper(this,s,lastRow));
			}
		}
		if(components != null){
			int nLines = (lines == null)?0:lines.getCount();
			for(int k = 0; k < components.length; k++){
				LBCompDesc cDesc = components[k];
				if(cDesc != null && cDesc.lineBefore == nLines){
					int addH = cDesc.h;
					int addRows = 1 + (addH / getItemHeight());
					int lastLineRow = (nLines < 1)?0:((CCStringWrapper)lines.get(nLines - 1)).endRow;
					if(rows == null)  rows = new waba.util.Vector();
					int nRows = rows.getCount();
					if(addRows > 0){
						for(int rw = nRows; rw < nRows + addRows; rw++){
							CCTARow newRow = new CCTARow();
							newRow.setMargins(r.x + insetLeft,r.x + r.width - insetRight);
							rows.add(newRow);
						}
					}		
					break;
				}
			}
		}
		repaint();		
	}

	public void initLines()
	{
		needInitLines = false;

		Rect r = getRect();
		int lastRow = 0;
		rows = new Vector();

		for(int i=0; i<lines.getCount(); i++){
			int nLines = i;
			LBCompDesc compDesc = null;
			if(components != null){
				for(int k = 0; k < components.length; k++){
					LBCompDesc cDesc = components[k];
					if(cDesc != null && cDesc.lineBefore == nLines){
						compDesc = components[k];
					}
				}
			}
			int leftMargin = r.x + insetLeft;
			int rightMargin = r.x + r.width - insetRight;
			int skipRows = 0;
			CCStringWrapper curLine = (CCStringWrapper)lines.get(nLines); 
			if(compDesc == null){
				curLine.init(this, lastRow);
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
					
				int nRows = rows.getCount();
				if(addRows > 0){
					for(int k = nRows; k < nRows + addRows; k++){
						CCTARow newRow = new CCTARow();
						newRow.setMargins(leftMargin,rightMargin);
						rows.add(newRow);
					}
				}
				curLine.init(this, lastRow);
			}
			int nRows = rows.getCount();
			int lastLineRow = curLine.endRow;
			int addRows = lastLineRow - nRows;
			if(addRows > 0){
				for(int k = nRows; k < nRows + addRows; k++){
					CCTARow newRow = new CCTARow();
					newRow.setMargins(r.x + insetLeft,r.x + r.width - insetRight);
					rows.add(newRow);
				}
			}				
			lastRow += curLine.getRows();
		}

		if(lines.getCount() < 1){
			lines.add(new CCStringWrapper(this,"",0));
		}

		if(components != null){
			int nLines = (lines == null)?0:lines.getCount();
			for(int k = 0; k < components.length; k++){
				LBCompDesc cDesc = components[k];
				if(cDesc != null && cDesc.lineBefore == nLines){
					int addH = cDesc.h;
					int addRows = 1 + (addH / getItemHeight());
					int lastLineRow = (nLines < 1)?0:((CCStringWrapper)lines.get(nLines - 1)).endRow;
					if(rows == null)  rows = new waba.util.Vector();
					int nRows = rows.getCount();
					if(addRows > 0){
						for(int rw = nRows; rw < nRows + addRows; rw++){
							CCTARow newRow = new CCTARow();
							newRow.setMargins(r.x + insetLeft,r.x + r.width - insetRight);
							rows.add(newRow);
						}
					}		
					break;
				}
			}
		}
		layoutComponents();
		notifyListeners(0);
	}


	public void onControlEvent(ControlEvent ev){
		if(ev.type == EmbedObjectPropertyControl.NEED_DEFAULT_SIZE && ev.target == currObjPropControl){
			LabObjectView oView = null;
			if(currObjPropDialog != null) oView = currObjPropDialog.getCheckView();
			if(oView == null) return;


			boolean isLink = currObjPropControl.linkCheck.getChecked();
			LabObject obj = oView.getLabObject();
			LabObjectView objView = null;
			if(isLink){
				objView = (LabObjectView)new LObjMinimizedView(obj.getVisiblePtr());
			} else {
				objView = obj.getView(null, false, session);
			}

			extra.ui.Dimension d = objView.getPreferredSize();
			if(d == null) return;
			if(d.width > 0){
				currObjPropControl.widthEdit.setText(""+d.width);
				currObjPropControl.lastW = d.width;
			}
			if(d.height > 0){
				currObjPropControl.heightEdit.setText(""+d.height);
				currObjPropControl.lastH = d.height;
			}
			return;
		}

		if (ev.type == ev.TIMER){
			if(loadingTimer != null){
				removeTimer(loadingTimer);
				loadingTimer = null;
				if(needInitLines){
					initLines();
				}
				repaint();
			}
			if(hasCursor){
				paintCursor(null);
			}
		} else if (ev.type == ev.FOCUS_IN){
			if(ev.target  == this){
				gotFocus();
				/*  I don't understand why we'd do this???
				if(currObjectViewDesc != null){
					Control cntrl = (Control)currObjectViewDesc.getObject();
					if((cntrl != null) && (cntrl instanceof LabObjectView)){
						LabObjectView object = (LabObjectView)cntrl;
						object.close();
					}
					//				currObjectViewDesc = null;
				}
				*/
			} else if(components != null){
				textWasChosen = null;
				if(currObjectViewDesc != null){
					if(currObjectViewDesc.getObject() instanceof LabObjectView){
						((LabObjectView)currObjectViewDesc.getObject()).setShowMenus(false);
					}
				}
				LBCompDesc cntrlDesc = findComponentDesc(ev.target);
				if(cntrlDesc != null){
					Control cntrl = (Control)cntrlDesc.getObject();
					if(cntrl instanceof LabObjectView){
						LabObjectView object = (LabObjectView)cntrl;
						object.setShowMenus(true);
						currObjectViewDesc = cntrlDesc;
						removeCursor();
						repaint();
					}
				}
			}
		}else if (ev.type == ev.FOCUS_OUT){
			if(ev.target  == this){
				lostFocus();
			}
		}
	}
	public void gotFocus(){
		restoreCursor();
	}
	public void lostFocus(){
		removeCursor();
		textWasChosen = null;
	}
	
	LBCompDesc findComponentDesc(Object o){
		if(o == this) return null;
		if(components == null || components.length < 1) return null;
		for(int i = 0; i < components.length; i++){
			LabObjectView view = getRelevantEmbeddedObject(o);
			if(view == null) continue;
			if(components[i].getObject() == view) return components[i];
		}
		return null;
	}

	LBCompDesc findComponent(PenEvent e){
		if(components == null || components.length < 1) return null;
		for(int i = 0; i < components.length; i++){
			if(components[i] == null) continue;
			Object o = components[i].getObject();
			if(o == null) continue;
			if(o instanceof LabObjectView){
				LabObjectView view = (LabObjectView)o;
				Rect r = view.getRect();
				if(r == null) continue;
				if(e.x <  r.x) continue;
				if(e.y <  r.y) continue;
				if(e.x >  r.x+r.width) continue;
				if(e.y >  r.y+r.height) continue;
				return components[i];
			}
		}
		return null;
	}

	private void openSWObject(CCStringWrapper sw)
	{
		if(sw != null && currObjPropDialog == null &&
		   objDictionary != null && sw.link &&
		   sw.indexInDict >= 0){

			LabObjectPtr ptr = objDictionary.getChildAt(sw.indexInDict);
			if(ptr == null) return;
			if(owner != null){
				owner.gotoChoosenLabObject(ptr);
			}
		}
	}

	public void openCurrentObject(){
		if(!getEditMode()){
			Sound.beep();
			return;
		}
		if(currObjPropDialog == null && currObjectViewDesc != null){
			Sound.beep();
		}else {
			openSWObject(textWasChosen);
		}
	}
	
	public void openCurrentObjectPropertiesDialog(){
		if(!getEditMode()){
			Sound.beep();
			return;
		}
		if(currObjPropDialog == null){
			openCompProp(currObjectViewDesc);
		}
	}

	void openCompProp(LBCompDesc compDesc){
		if(compDesc == null){
			if(textWasChosen != null){
				MainWindow mw = MainWindow.getMainWindow();
				if((mw instanceof ExtraMainWindow)){
					TextObjPropertyView tPropView = 
						new TextObjPropertyView((ExtraMainWindow)mw,
												(LObjDictionary)LabObject.lBook.load(LabObject.lBook.getRoot()), 
												null,textWasChosen, session);
					ViewDialog dialog = new ViewDialog((ExtraMainWindow)mw, this,"Properties",tPropView);
					dialog.setRect(0,0,150,150);
					dialog.show();		
				}
			}
			return;
		}
		LabObjectView objView = (LabObjectView)compDesc.getObject();
		if(objView == null) return;
		String name = "";
		if(objView instanceof LObjMinimizedView){
			name = ((LObjMinimizedView)objView).getPtr().toString();
		} else {
			name = objView.getLabObject().getName();
		}
		if(currObjPropControl == null){
			currObjPropControl = new EmbedObjectPropertyControl(null,name);
			currObjPropControl.layout(true);
		}
		if(currObjPropControl != null){
			currObjPropControl.setName(name);
			EmbedObjectPropertyControl.lastAlighnLeft 	= (compDesc.alignment == LBCompDesc.ALIGNMENT_LEFT);
			EmbedObjectPropertyControl.lastWrap 		= compDesc.wrapping;
			EmbedObjectPropertyControl.lastLink 		= compDesc.link;
			EmbedObjectPropertyControl.lastW			= compDesc.w;
			EmbedObjectPropertyControl.lastH			= compDesc.h;
			MainWindow mw = MainWindow.getMainWindow();
			if(!(mw instanceof ExtraMainWindow)) return;
			currObjPropDialog = new ObjPropertyViewDialog((ExtraMainWindow)mw, this, "Properties", currObjPropControl,objView);
			currObjPropDialog.setRect(0,0,150,150);
			currObjPropDialog.show();		
		}
	}
	
	public LabObjectView getRelevantEmbeddedObject(Object o){
		if(!(o instanceof Control)) return null;
		LabObjectView view = null;
		Control c = (Control)o;
		Control prevControl = null;
		while(view == null && c != null){
			if(c == this && (prevControl instanceof LabObjectView)){
				view = (LabObjectView)prevControl;
				break;
			}
			prevControl = c;
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
		if(!getEditMode()){
			hasCursor = false;
			cursorOn = false;
			if(caretTimer != null){
				removeTimer(caretTimer);
				caretTimer = null;
			}
			return;
		}
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
		r.y = CCTextArea.yTextBegin + curState.cursorRow*getItemHeight();
		if(r.y > height){
			removeCursor();
			return false;
		}
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


	public void setFirstLine(int fl){
		setFirstLine(fl,true);
	}
	public void setFirstLine(int fl,boolean dorepaint){
		int nRows = getRowsNumber();
		if(lines != null && fl < nRows - 1 && fl >= 0){
			removeCursor();
			firstLine = fl;
			if(dorepaint){
				layoutComponents();
				repaint();
			}
		}
	}

	public void onKeyEvent(KeyEvent ev){
		CCTextAreaState tas = curState;
		if(ev.target != this) return;
		if (ev.key == IKeys.BACKSPACE) {
		}else if (ev.key == IKeys.DELETE){
		}else if (ev.key == IKeys.ENTER){// && editable(this)){
			insertEmptyLine();
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
	
	/*
	  public int getRowsNumber(){
	  int retValue = 0;
	  if(lines == null || lines.getCount() < 1) return 0;
	  for(int i = 0; i < lines.getCount(); i++){
	  retValue += ((CCStringWrapper)lines.get(i)).getRows();
	  }
	  return retValue;
	  }
	*/
	public int getRowsNumber(){
		int retValue = 0;
		int nLines = (lines == null)?0:lines.getCount();
		int addRow = 0;
		if(components != null){
			for(int i = 0; i < components.length; i++){
	    		LBCompDesc d = components[i];
	    		if(d == null) continue;
	    		if(d.lineBefore == nLines){
	    			addRow = 1 + (d.h / getItemHeight());
	    			break;	
	    		}
			}
		}
		retValue = addRow;
		if(nLines < 1) return retValue;
		retValue = addRow + ((CCStringWrapper)lines.get(lines.getCount() - 1)).endRow + 1;
		return retValue;
	}
	
	public void onPenEvent(PenEvent ev){
		if(ev.type == PenEvent.PEN_DOWN){
			LBCompDesc compDesc = findComponentDesc(ev.target);
			textWasChosen = null;
			if(compDesc == null && currObjectViewDesc != null){
				currObjectViewDesc = null;
				repaint();
			}

			int x = 0;
			int h = getItemHeight();
			if(ev.y < CCTextArea.yTextBegin) ev.y = CCTextArea.yTextBegin;
			int row = 1 + firstLine + ((ev.y - CCTextArea.yTextBegin) / h);
			if(row > getRowsNumber()) row = getRowsNumber();
			int lineIndex = getLineIndex(row - 1);
			if(lines == null){
				row = 1;
			}else if(lineIndex >= 0 && lineIndex < lines.getCount()){
				CCStringWrapper sw = (CCStringWrapper)lines.get(lineIndex);

				if(getEditMode() && sw != null){
					textWasChosen = sw;
					repaint();
				}else{
					openSWObject(sw);
					return;
				}
				
				
				int rIndex = row - 1 - sw.beginRow;
				if(rIndex >= 0 && rIndex < sw.delimiters.getCount()){
					
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
			if(!getEditMode()){
				return;
			}
			if(hasCursor) clearCursor();
			else		  restoreCursor();
			curState.cursorPos = x;
			curState.cursorRow = row - 1 - firstLine;
			paintCursor(null);
		} else if(ev.type == PenEvent.PEN_UP){
			LBCompDesc compDesc = findComponentDesc(ev.target);

			if(compDesc != null){
				Control cntrl = (Control)compDesc.getObject();
				if(!getEditMode() && compDesc.link && cntrl instanceof LabObjectView){
					
					LabObjectView view = (LabObjectView)cntrl;
					
					// need to fix this!!!
					view.setShowMenus(false);
					LabObjectPtr lObjPtr = null;
					if(view instanceof LObjMinimizedView){
						lObjPtr = ((LObjMinimizedView)view).getPtr();
					} else {
						lObjPtr = view.getLabObject().getVisiblePtr();
					}
					if(owner != null){
						owner.gotoChoosenLabObject(lObjPtr);
					}
				}
				return;
			}

		}
	}

	public int getLineIndex(int row){
		int retValue = 0;
		if(lines == null || lines.getCount() < 1){
			return 0;
		}
		retValue = lines.getCount();
		if(row >= getRowsNumber()){
			return retValue;
		}
		int ind = 0;
		for(int i = 0; i < lines.getCount(); i++){
			ind = ((CCStringWrapper)lines.get(i)).beginRow;
			int swRows = ((CCStringWrapper)lines.get(i)).getRows();
			if(row < ind + swRows){
				retValue = i;
				break;
			}
			//			ind += swRows;
		}
		return retValue;
	}
	

	Timer loadingTimer = null;
	public void onPaint(Graphics g){
		Rect r = getRect();
		if(loadingTimer != null){
			g.drawText("Loading", 20,20);
			return;
		}
		g.setColor(255,255,255);
		g.fillRect(0,0,r.width,r.height);
		g.setColor(0,0,0);
		doPaintData(g);				
	}
	public void doPaintData(Graphics g){
		if(lines == null) return;
		for (int i = 0; i<lines.getCount(); i++){
			((CCStringWrapper)lines.get(i)).draw(g,firstLine);
		}
	}
	public void paintChildren(Graphics g, int x, int y, int width, int height){
		super.paintChildren(g,x,y,width,height);
		if(getEditMode() && g != null){
			if(currObjectViewDesc != null){
				if(currObjectViewDesc.getObject() instanceof LabObjectView){
					LabObjectView objView = (LabObjectView)currObjectViewDesc.getObject();
					Rect rClip = getRect();
					g.setClip(0,0,rClip.width,rClip.height);
					Rect rObj = objView.getRect();
					boolean isColor = waba.sys.Vm.isColor();
					if(!isColor){
						g.setColor(0,0,0);
						g.drawRect(rObj.x-1,rObj.y-1,rObj.width+2,rObj.height+2);
					}else{
						g.setColor(0,0,255);
					}	
					g.drawRect(rObj.x,rObj.y,rObj.width,rObj.height);
					
				
				
					g.setColor(0,0,0);
					g.clearClip();
				}
			}else if(textWasChosen != null){
				Rect rClip = getRect();
				g.setClip(0,0,rClip.width,rClip.height);
				boolean isColor = waba.sys.Vm.isColor();
				
				int yText = yTextBegin + (textWasChosen.beginRow - firstLine)*getItemHeight();
				int hText = (textWasChosen.endRow - textWasChosen.beginRow)*getItemHeight();
				Rect rText = new Rect(textWasChosen.beginPos,yText,textWasChosen.endPos - textWasChosen.beginPos,hText);
				if(!isColor){
					g.setColor(0,0,0);
					g.drawRect(rText.x-1,rText.y-1,rText.width+2,rText.height+2);
				}else{
					g.setColor(0,0,255);
				}
				g.drawRect(rText.x,rText.y,rText.width,rText.height);
					
				g.setColor(0,0,0);
				g.clearClip();
			}
		}
	}

	public static CCStringWrapper createCCStringWrapper(String str,String color,boolean link,LabObject labObj){
		CCStringWrapper wrapper = new CCStringWrapper(null,null,0);
		wrapper.str = str;
		wrapper.link = link;
		wrapper.rColor = TextObjPropertyView.byteFromHexa(color.substring(0,2));
		wrapper.gColor = TextObjPropertyView.byteFromHexa(color.substring(2,4));
		wrapper.bColor = TextObjPropertyView.byteFromHexa(color.substring(4,6));
		return wrapper;
	}

	static byte [] charWidthMappers = null;
	byte [] getCharWidths()
	{
		if(charWidthMappers != null) return charWidthMappers;

		FontMetrics fm = getFontMetrics();
		if(fm == null) return null;
		charWidthMappers = new byte[256];
		for(int i = 0; i < 256; i++){
			charWidthMappers[i] = (byte)fm.getCharWidth((char)(i));
		}
		
		return charWidthMappers;
	}

	private LabObjectView setCompDescView(LBCompDesc compDesc, LabObjectPtr lObjPtr){
		LabObjectView  objView = null;
		if(compDesc.link){
			LObjMinimizedView minView  = new LObjMinimizedView(lObjPtr);
			minView.rColor = ((compDesc.linkColor & 0xFF0000) >> 16);
			minView.rColor &= 0xFF;
			minView.gColor = ((compDesc.linkColor & 0xFF00) >> 8);
			minView.gColor &= 0xFF;
			minView.bColor &= compDesc.linkColor & 0xFF;
			objView = minView;
		} else {
			LabObject lobj = session.load(lObjPtr);
			
			objView = lobj.getView(this,false, session);
			objView.setEmbeddedState(true);
			objView.layout(false);
		}
		compDesc.setObject(objView);
		return objView;
	}
}

class CCTextAreaState{
	public int cursorRow = 0, cursorPos = 0;
	public int	cursorChar = 0;
	CCTextAreaState(){
	}
}


class CCTARow{
	int  	beginPos,endPos;
	CCTARow(){
	}
	public void setMargins(int beginPos,int endPos){
		this.beginPos 	= beginPos;
		this.endPos 	= endPos;
	}
}


class ObjPropertyViewDialog extends ViewDialog{
	CCTextArea		textArea;
	LabObjectView	checkView;
	public 	ObjPropertyViewDialog(ExtraMainWindow owner,CCTextArea textArea,String title, 
								  LabObjectView view,LabObjectView checkView){
		super(owner,textArea,title,view);
		this.textArea = textArea;
		this.checkView = checkView;
	}
	public void onEvent(Event e){
		if(textArea == null) return;
		if(e instanceof ControlEvent && e.type == EmbedObjectPropertyControl.NEED_DEFAULT_SIZE){
			textArea.onEvent(e);
		}
	}
	public LabObjectView getCheckView(){return checkView;}
}



class TextObjPropertyView extends LabObjectView{
	CCStringWrapper stringWrapper;
	public Edit		strEdit;
	public Button	doneButton;
	public Button	cancelButton;
	public Edit		colorEdit;
	public Label	colorLabel;
	public Check	linkCheck;
	ExtraMainWindow owner;
	LObjDictionary dict;

	LObjDictionaryView	view;

	LabBookSession session;


	public TextObjPropertyView(ExtraMainWindow owner,LObjDictionary dict,
							   ViewContainer vc, CCStringWrapper stringWrapper, 
							   LabBookSession session){
		super(vc, (LabObject)dict, session);
		this.stringWrapper 	= stringWrapper;
		this.dict 	= dict;
		this.owner 	= owner;
		this.session = session;
	}
	public void layout(boolean sDone){
		if(didLayout) return;
		didLayout = true;
		if(strEdit == null){
			strEdit = new Edit();
			if(stringWrapper != null){
				strEdit.setText(stringWrapper.getStr());
			}
			add(strEdit);
		}
		if(colorEdit == null){
			colorEdit = new Edit();
			add(colorEdit);
			if(stringWrapper != null){
				colorEdit.setText(hexaFromColor(stringWrapper.rColor,stringWrapper.gColor,stringWrapper.bColor));
			}
		}
		if(colorLabel == null){
			colorLabel = new Label("Color 0x");
			add(colorLabel);
		}
		if(linkCheck == null){
			linkCheck = new Check("Link");
			add(linkCheck);
			if(stringWrapper != null){
				linkCheck.setChecked(stringWrapper.link);
			}
		}
		if(view == null && container != null && dict != null){
			view = (LObjDictionaryView)dict.getView(container, true, session);
			view.viewFromExternal = true;
			view.layout(false);
			add(view);
		}
		if(cancelButton == null){
			cancelButton = new Button("Cancel");
			add(cancelButton);
		}
		if(doneButton == null){
			doneButton = new Button("Done");
			add(doneButton);
		}
	}
	public void setRect(int x, int y, int width, int height){
		super.setRect(x,y,width,height);
		if(!didLayout) layout(false);
		if(doneButton != null)	doneButton.setRect(width-31,height-15,30,15);
		if(cancelButton != null) cancelButton.setRect(2,height-15,40,15);

		if(strEdit != null){
			strEdit.setRect(2,2, width - 4, 15);
		}
		
		if(colorLabel != null){
			colorLabel.setRect(2,20,36,15);
		}
		if(colorEdit != null){
			colorEdit.setRect(40,20,40,15);
		}
		if(linkCheck != null){
			linkCheck.setRect(2,40,50,15);
		}
		if(view != null){
			view.setRect(2,60,width - 4,height - 77);
		}
		
	}
	
	static char hexaFromDigit(int d){
		if(d >=0 && d <= 9){
			return (char)(d + '0');
		}else if(d >=10 && d <= 15){
			return (char)(d - 10 + 'A');
		}
		return '0';
	}
	static String hexaFromColor(int r, int g, int b){
		if(r < 0) 	r = 0;
		if(r > 255) r = 255;
		if(g < 0) 	g = 0;
		if(g > 255) g = 255;
		if(b < 0) 	b = 0;
		if(b > 255) b = 255;
		String str = "";
		
		str += hexaFromDigit(r >>> 4);
		str += hexaFromDigit(r & 0xF);
		str += hexaFromDigit(g >>> 4);
		str += hexaFromDigit(g & 0xF);
		str += hexaFromDigit(b >>> 4);
		str += hexaFromDigit(b & 0xF);
		
		return str;		
	}
	
	static int byteFromHexa(String str){
		int retValue = 0;
		if(str == null) return retValue;
		int base = 1;
		int curCharInd = str.length() - 1;
		while(curCharInd >= 0){
			char c = str.charAt(curCharInd);
			if(c >= '0' && c <= '9'){
				retValue += base*(int)(c - '0');
			}else if(c >= 'A' && c <= 'F'){
				retValue += base*(int)(c - 'A' + 10);
			}else if(c >= 'a' && c <= 'f'){
				retValue += base*(int)(c - 'a' + 10);
			}
			curCharInd--;
			base <<= 4;
		}
		
		return retValue;
		
	}

	public void onEvent(Event e){
		if(e.target == cancelButton && e.type == ControlEvent.PRESSED){
			if(container != null) container.done(this);
		}else if(e.target == doneButton && e.type == ControlEvent.PRESSED){
			if(container != null){
				container.done(this);
				if(stringWrapper == null) return;
				if(linkCheck != null){
					stringWrapper.link = linkCheck.getChecked();
				}
				if(colorEdit != null){
					String strColor = colorEdit.getText();
					int n = strColor.length();
					if(n < 6){
						for(int i = 0; i < 6-n; i++) strColor += "0";
					}else{
						strColor = strColor.substring(0,6);
					}
					stringWrapper.rColor = byteFromHexa(strColor.substring(0,2));
					stringWrapper.gColor = byteFromHexa(strColor.substring(2,4));
					stringWrapper.bColor = byteFromHexa(strColor.substring(4,6));
					
				}
				int oldIndex = stringWrapper.indexInDict;
				stringWrapper.indexInDict = -1;
				if(stringWrapper.link && (view != null) && (stringWrapper.owner != null) && (stringWrapper.owner.objDictionary != null)){
					TreeNode curNode = view.treeControl.getSelected();
					if(curNode == null){
						stringWrapper.indexInDict = oldIndex;
					}else{
						LabObject obj = ((DictTreeNode)(view.treeControl.getRootNode())).getObj(curNode);
						if(obj != null){
							int dIndex = stringWrapper.owner.objDictionary.getIndex(obj);
							if(dIndex >= 0){
								stringWrapper.indexInDict = dIndex;
							}else{
								stringWrapper.owner.objDictionary.add(obj);
								stringWrapper.indexInDict = stringWrapper.owner.objDictionary.getChildCount() - 1;
							}
						}
					}
				}		


				if(strEdit != null && stringWrapper.owner != null){
					stringWrapper.owner.changeLineContent(strEdit.getText(),stringWrapper);
				}
				stringWrapper.owner.repaint();
			}	 
		}
	}
}

final class EmptyLabObject extends LabObject{
	public EmptyLabObject(){super(-1);}
	public void readExternal(DataStream in){}
	public void writeExternal(DataStream out){}
}
