package org.concord.LabBook;

import waba.ui.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import extra.ui.*;
import extra.io.*;

public class LObjCCTextAreaView extends LabObjectView implements ActionListener{
CCTextArea 				tArea;
RelativeContainer 		edit = new RelativeContainer();

LObjCCTextArea 			doc;
Button 					doneButton,doneOutButton;
Button 					insertButton;


Menu 					menu = null;
LabObjectView			addedLabObjectView		= null;
	public LObjCCTextAreaView(ViewContainer vc, LObjCCTextArea d,boolean edit){
		super(vc);
		doc = d;
		lObj = doc;
	}
	public void delMenus(){
		if(container != null){
			container.getMainView().delMenu(this, menu);
		}
	}
	public void addMenus(){
		addMenus(container);
	}
	
	public void addMenus(ViewContainer vc){
		
		if(menu == null){
			menu = new Menu("SNotes");
			menu.add("Load Note ...");
			menu.add("-");
			menu.add("Paste");
			menu.add("Clear");
			menu.add("-");
			menu.add("Insert Object ...");
			menu.add("Delete Current Object");
			menu.add("Delete All Objects");
			menu.addActionListener(this);
		}
		if(vc != null) vc.getMainView().addMenu(this, menu);
	}

    public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equals("Load Note ...")){
			openFileDialog();
		}else if(e.getActionCommand().equals("Insert Object ...")){
			if(tArea != null) tArea.insertObject();
		}else if(e.getActionCommand().equals("Paste")){
			if(!CCClipboard.isClipboardEmpty()){
				String str = CCClipboard.getStringContent();
				if(str != null){
					tArea.insertText(str);
				}
			}
		}else if(e.getActionCommand().equals("Clear")){
			if(tArea != null) tArea.clearAll();
		}else if(e.getActionCommand().equals("Delete Current Object")){
			if(tArea != null) tArea.deleteCurrentObject();
		}else if(e.getActionCommand().equals("Delete All Objects")){
			if(tArea != null) tArea.deleteAllObjects();
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

	public void addChoosenLabObjView(LabObjectView view){
		if(tArea != null){
			remove(edit);
			remove(insertButton);
			if(showDone) remove(doneButton);
			add(view);
//			view.didLayout = false;
			view.layout(false);
			view.setRect(0,0,width,height - 15);
			if(doneOutButton == null){
				doneOutButton = new Button("Done");
			}
			add(doneOutButton);
			doneOutButton.setRect(width-31,height-15,30,15);
			addedLabObjectView = view;
		}
	}

	public void layout(boolean sDone){
		if(didLayout) return;
		didLayout = true;

		showDone = sDone;

		if(tArea == null){
			tArea = new CCTextArea(this,container.getMainView(),doc.curDict,doc);
		}else{
			tArea.mainView 	= container.getMainView();
			tArea.dict 		= doc.curDict;
			tArea.subDictionary = doc;
		}
//		if(doc.text != null)  tArea.setText(doc.text);
		edit.add(tArea, 1, RelativeContainer.BELOW, 
		RelativeContainer.REST, RelativeContainer.REST);
		insertButton = new Button("Insert");
		add(edit);
		add(insertButton);
//		if(doc.text != null)  tArea.setText(doc.text);
		if(showDone){
			doneButton = new Button("Done");
			add(doneButton);
		} 
	}

	public int getHeight(){
//		return (tArea.getFontMetrics().getHeight() + 2) * tArea.getNumLines() + tArea.spacing*2 + 3;
		return 0;
	}

	public void setRect(int x, int y, int width, int height){
		super.setRect(x,y,width,height);
		if(!didLayout) layout(false);

		if(showDone){
			edit.setRect(0,0,width - 1,height-15);
			doneButton.setRect(width-31,height-15,30,15);
		} else {
			edit.setRect(0,0,width - 1,height);
		}
		insertButton.setRect(1,height-15,30,15);
		tArea.setText(tArea.getText());
		tArea.layoutComponents();
	}

	public void close(){
		Debug.println("Got close in document");
//		doc.text = tArea.getText();
		tArea.close();

		super.close();
	}

	public void onEvent(Event e){
		if(e.type == ControlEvent.PRESSED && (addedLabObjectView != null)){
			if(e.target == doneOutButton){
				addedLabObjectView.delMenus();
				addedLabObjectView.close();
				
				remove(addedLabObjectView);
				remove(doneOutButton);
				add(edit);
				add(insertButton);
				if(showDone) add(doneButton);
				tArea.layoutComponents();
				tArea.setText(tArea.getText());
				addedLabObjectView = null;
				tArea.restoreCursor(true);
				return;
			} 
		}
	
		if(e.target == doneButton && e.type == ControlEvent.PRESSED){
			if(container != null){
				container.done(this);
			}	    
		}
		if(e.target == insertButton && e.type == ControlEvent.PRESSED){
			if(tArea != null){
				tArea.insertObject();
/*
				LabBook book = LabObject.lBook;
				LabObjectPtr rootPtr = book.getRoot();
				LObjDictionary loDict = (LObjDictionary)book.load(rootPtr);
				if(loDict != null){
					System.out.println("getChildCount "+loDict.getChildCount());
					int childs = loDict.getChildCount();
					for(int i = 0; i < childs; i++){
						TreeNode node = loDict.getChildAt(i);
						if(node == null) continue;
						LabObject labObj = loDict.getObj(node);
						System.out.println("labObj["+i+"] = "+labObj.name);
					}
				}
				
				if(doc.curDict != null){
					System.out.println("doc.curDict getChildCount "+doc.curDict.getChildCount());
					int childs = doc.curDict.getChildCount();
					for(int i = 0; i < childs; i++){
						TreeNode node = doc.curDict.getChildAt(i);
						if(node == null) continue;
						LabObject labObj = doc.curDict.getObj(node);
						System.out.println("labObj["+i+"] = "+labObj.name);
					}
				}
*/				
				
			}	    
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
}
