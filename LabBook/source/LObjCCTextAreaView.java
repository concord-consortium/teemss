package org.concord.LabBook;

import waba.ui.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import extra.ui.*;
import extra.io.*;

public class LObjCCTextAreaView extends LabObjectView 
	implements ActionListener,DialogListener, ScrollListener, TextAreaListener,
	ViewContainer
{
CCTextArea 				tArea;
//RelativeContainer 		edit = new RelativeContainer();
Container 				edit = new Container();

LObjCCTextArea 			doc;
Button 					doneButton;
Button 					insertButton;
boolean					insertButtonAdded;
//TimerButton 			upButton;
//TimerButton 			downButton;


Menu 					menu = null;
Menu 					menuEdit = null;



String [] fileStrings = {"Load Note..."};

Edit 					nameEdit;
Label					nameLabel;
boolean					nameEditWasAdded = false;


CCScrollBar				scrollBar;

	public LObjCCTextAreaView(ViewContainer vc, LObjCCTextArea d,boolean edit){
		super(vc);
		doc = d;
		lObj = doc;
	}

	LabObjectView lObjView = null;
    public void done(LabObjectView source)
	{
		// This would be called after a object was popped up and then
		// it wants to be closed
		
		// check if this object is the one we popped up
		if(source == lObjView){
			lObjView.setContainer(tArea);
			lObjView.setEmbeddedState(true);
			lObjView.setShowMenus(false);
			tArea.layoutComponents();
			tArea.setText(tArea.getText());
			tArea.removeCursor();
			// If we are embedded this will be a problem
			getMainView().closeTopWindowView();
			if(showMenus) addMenus();
			lObjView.didLayout = false;
			lObjView.layout(false);
			lObjView = null;
		}
	}

    public void reload(LabObjectView source)
	{
		// don't know what to do here
	}

	public void delMenus(){
		if(container != null){
			if(menu != null) container.getMainView().delMenu(this, menu);
			if(menuEdit != null) container.getMainView().delMenu(this, menuEdit);			
			container.getMainView().removeFileMenuItems(fileStrings, this);
			if(tArea != null) tArea.delMenus();
		}
	}
	public void addMenus(){
		addMenus(container);
	}
	
	public void addMenus(ViewContainer vc){
		if(menuEdit == null){
			menuEdit = new Menu("Edit");
			menuEdit.add("Paste");
			menuEdit.add("Clear...");
			menuEdit.add("-");
			menuEdit.add("Insert Empty Line");
			menuEdit.add("-");
			menuEdit.add("Properties...");
			menuEdit.addActionListener(this);
		}	
		if(vc != null) vc.getMainView().addMenu(this, menuEdit);
		if(menu == null){
			menu = new Menu("Object");
			menu.add("Object's Propertry...");
			menu.add("Open Object");
			menu.add("-");
			menu.add("Insert Object...");
			menu.add("-");
			menu.add("Delete Current Object...");
			menu.add("Delete All Objects...");
		}
		menu.addActionListener(this);
		if(vc != null) vc.getMainView().addMenu(this, menu);
		container.getMainView().addFileMenuItems(fileStrings, this);
	}
	

    public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equals("Load Note...")){
			openFileDialog();
		}else if(e.getActionCommand().equals("Insert Object...")){
			if(tArea != null) tArea.insertObject();
		}else if(e.getActionCommand().equals("Paste")){
			if(!CCClipboard.isClipboardEmpty()){
				String str = CCClipboard.getStringContent();
				if(str != null){
					tArea.insertText(str);
				}
			}
		}else if(e.getActionCommand().equals("Test")){
			tArea.test();
		}else if(e.getActionCommand().equals("Clear...")){
			if(tArea != null) tArea.requireClearingAll();
		}else if(e.getActionCommand().equals("Delete Current Object...")){
			if(tArea != null) tArea.requireDeleteCurrentObject();
		}else if(e.getActionCommand().equals("Delete All Objects...")){
			if(tArea != null) tArea.requireDeleteAllObjects();
		}else if(e.getActionCommand().equals("Properties...")){
			showProperties();
		}else if(e.getActionCommand().equals("Insert Empty Line")){
			if(tArea != null) tArea.insertEmptyLine();
		}else if(e.getActionCommand().equals("Object's Propertry...")){
			if(tArea != null) tArea.openCurrentObjectPropertiesDialog();
		}else if(e.getActionCommand().equals("Open Object")){
			if(tArea != null) tArea.openCurrentObject();
		}

    }



	public void showProperties(){
		LObjCCTextAreaPropView propView = (LObjCCTextAreaPropView)doc.getPropertyView(null, null);
		if(propView == null) return;
		propView.setTextArea(tArea);
		MainWindow mw = MainWindow.getMainWindow();
		if(!(mw instanceof ExtraMainWindow)) return;
		ViewDialog vDialog = new ViewDialog((ExtraMainWindow)mw, this, "Properties", propView);
		vDialog.setRect(0,0,150,150);
		if(tArea != null) propView.setEditMode(tArea.getEditMode());
		vDialog.addDialogListener(this);
		vDialog.show();		
	}
	
	
	public void dialogClosed(DialogEvent e){
		if(tArea == null) return;
		boolean editMode = tArea.getEditMode();
		if(insertButtonAdded != editMode){
			didLayout = false;
			waba.fx.Rect r = getRect();
			setRect(r.x,r.y,r.width,r.height);
		}
	}
	
	
   public void writeExternal(DataStream out){
    	out.writeBoolean(tArea != null);
    	if(tArea != null){
    		tArea.writeExternal(out);
    	}
    }

    public void readExternal(DataStream in){
		boolean wasText = in.readBoolean();
		if(wasText){
			tArea = new CCTextArea(this,null,doc.curDict,doc);
			tArea.readExternal(in);
		}
    }

	public MainView getMainView()
	{
		if(container != null) return container.getMainView();
		return null;
	}

	public void addChoosenLabObjView(LabObjectView view)
	{
	    delMenus();
		lObjView = view;
		view.setContainer(this);
		getMainView().showFullWindowView(view);

	}

	public void layout(boolean sDone){
		if(didLayout) return;
		didLayout = true;

		showDone = sDone;
		if(nameEdit == null) nameEdit = new Edit();
		nameEdit.setText(getLabObject().name);
		if(nameLabel == null) nameLabel = new Label("Name");
		add(nameLabel);
		add(nameEdit);
		nameEditWasAdded = true;

		if(tArea == null){
			tArea = new CCTextArea(this,container.getMainView(),doc.curDict,doc);
		}else{
			tArea.mainView 	= container.getMainView();
			tArea.dict 		= doc.curDict;
			tArea.subDictionary = doc;
		}
		tArea.addTextAreaListener(this);
		if(insertButton == null) insertButton = new Button("Insert");
		if(tArea == null || tArea.getEditMode()){
			if(!insertButtonAdded) add(insertButton);
			insertButtonAdded = true;
		}else if(insertButtonAdded && insertButton != null){
			remove(insertButton);
			insertButtonAdded = false;
		}
//		upButton = new TimerButton("Up");
//		downButton = new TimerButton("Down");
//		add(upButton);
//		add(downButton);

		
		if(showDone){
			if(doneButton == null){
				doneButton = new Button("Done");
				add(doneButton);
			}
		} 
		add(edit);
		edit.add(tArea);
		
		if(scrollBar == null) scrollBar = new CCScrollBar(this);
		edit.add(scrollBar);
	}

	public int getHeight(){
//		return (tArea.getFontMetrics().getHeight() + 2) * tArea.getNumLines() + tArea.spacing*2 + 3;
		return 0;
	}

	public void setRect(int x, int y, int width, int height){
		super.setRect(x,y,width,height);
		if(!didLayout) layout(showDone);
		boolean needInserButton = (tArea == null || tArea.getEditMode());
		if(showDone){
			doneButton.setRect(width-31,0,30,15);
		}
		
		int yStart = (needInserButton)?34:17;
		edit.setRect(1,yStart,width - 2,height - 2 - yStart);

		if(nameLabel != null) nameLabel.setRect(1,1,30,15);
		int editW = (showDone)?width - 62:width - 32;
		if(nameEdit != null) nameEdit.setRect(30, 1, editW, 15);



		if(tArea != null){
			waba.fx.Rect rEdit = edit.getRect();
			int wsb = (waba.sys.Vm.getPlatform().equals("WinCE"))?11:7;
			int delta = (scrollBar != null)?wsb:0;
			tArea.setRect(1,1,rEdit.width - 2 - delta, rEdit.height - 2);
			if(scrollBar != null){
				scrollBar.setRect(rEdit.width - wsb - 1,1,wsb, rEdit.height - 2);
			}
		}
		if(needInserButton)	insertButton.setRect(1,17,30,15);
//		upButton.setRect(35,17,20,15);
//		downButton.setRect(60,17,30,15);
		tArea.setText(tArea.getText());
		tArea.layoutComponents();
		redesignScrollBar();
	}

	public void close(){
		Debug.println("Got close in document");
		tArea.close();
    	if(nameEdit != null){
    		getLabObject().name = nameEdit.getText();
    	}
		if(scrollBar != null) scrollBar.close();
		super.close();
	}

	public void onPaint(waba.fx.Graphics g){
		if(g != null && edit != null){
			waba.fx.Rect r = edit.getRect();
			g.setColor(0,0,0);
			g.drawRect(r.x, r.y, r.width, r.height);
		}
	}
	public void onEvent(Event e){
	
/*
		if(e instanceof ControlEvent && e.type == ControlEvent.TIMER){
			if(e.target == upButton){
				if(tArea != null)	tArea.moveUp();
			}else if(e.target == downButton){
				if(tArea != null)	tArea.moveDown();
			}
		}
*/	
		if( e.type == ControlEvent.PRESSED){
			if(e.target == insertButton){
				if(tArea != null){
					tArea.insertObject();
				}	    
			}else if(e.target == doneButton){
				if(container != null){
					container.done(this);
				}	    
			}/*else if(e.target == upButton){
				if(tArea != null){
					tArea.moveUp();
				}	    
			}else if(e.target == downButton){
				if(tArea != null){
					tArea.moveDown();
				}	    
			}*/ 
		}
	}
    public void openFileDialog(){
    	String []extensions = {".txt",".TXT"};
    	FileDialog fd = FileDialog.getFileDialog(FileDialog.FILE_LOAD,null);
    	if(fd == null) return;
    	fd.show();
    	byte []bytes = fd.getBytesFromFile();
    	if(bytes == null || bytes.length < 1) return;
    	char []chars = new char[bytes.length];
    	int	currChars = 0;
    	for(int i = 0; i < bytes.length; i++){
    		if(bytes[i] == '\r'){//MAC or DOS
    			if((i < bytes.length - 1) && (bytes[i+1] == '\n')){//DOS
					continue;
    			}
    			chars[currChars++] = '\n';
    		}else{//
    			chars[currChars++] = (char)bytes[i];
    		}
    	}
    	tArea.setText(new String(chars,0,chars.length));
    	
    }
	public int getPreferredWidth(){
		return 100;
	}

	public int getPreferredHeight(){
		return 100;
	}
	
	public void textAreaWasChanged(TextAreaEvent textAreaEvent){
		redesignScrollBar();
	}
	
	public void redesignScrollBar(){
		if(scrollBar == null) return;
		if(tArea == null) return;
		int allLines = tArea.getRowsNumber();
		int maxVisLine = tArea.getVisRows();
				
		scrollBar.setMinMaxValues(0,allLines - maxVisLine);
		scrollBar.setAreaValues(allLines,maxVisLine);
		scrollBar.setIncValue(1);
		scrollBar.setPageIncValue((int)(0.8f*maxVisLine+0.5f));
		scrollBar.setRValueRect();
		if(allLines > maxVisLine){
			scrollBar.setValue(tArea.firstLine);
		}else{
			tArea.setFirstLine(0,false);
			scrollBar.setValue(0);
		}
		repaint();
	}
	
	public void scrollValueChanged(ScrollEvent se){
		if(se.target != scrollBar) return;
		if(tArea != null) tArea.setFirstLine(se.getScrollValue());
	}
}
