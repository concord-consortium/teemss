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
Button 					upButton;
Button 					downButton;


Menu 					menu = null;
Menu 					menuEdit = null;

LabObjectView			addedLabObjectView		= null;


String [] fileStrings = {"Load Note..."};



	public LObjCCTextAreaView(ViewContainer vc, LObjCCTextArea d,boolean edit){
		super(vc);
		doc = d;
		lObj = doc;
	}
	public void delMenus(){
		if(container != null){
			if(menu != null) container.getMainView().delMenu(this, menu);
			if(menuEdit != null) container.getMainView().delMenu(this, menuEdit);
			container.getMainView().removeFileMenuItems(fileStrings, this);
		}
	}
	public void addMenus(){
		addMenus(container);
	}
	
	public void addMenus(ViewContainer vc){
		if(menuEdit == null){
			menuEdit = new Menu("Edit");
			menuEdit.add("Paste");
			menuEdit.add("Clear");
			menuEdit.add("-");
			menuEdit.add("Properties");
			menuEdit.addActionListener(this);
		}	
		if(vc != null) vc.getMainView().addMenu(this, menuEdit);
		if(menu == null){
			menu = new Menu("Object");
		}else{
			menu.removeAll();
		}
		menu.add("Insert Object ...");
		menu.add("-");
		menu.add("Delete Current Object");
		menu.add("Delete All Objects");
		menu.add("-");
		menu.addActionListener(this);
		if(vc != null) vc.getMainView().addMenu(this, menu);
		container.getMainView().addFileMenuItems(fileStrings, this);
	}
	
	public void numbObjectChanged(){
		if(menu == null || tArea == null) return;
		menu.removeAll();
		menu.add("Insert Object ...");
		menu.add("-");
		menu.add("Delete Current Object");
		menu.add("Delete All Objects");
		menu.add("-");
		LBCompDesc []components = tArea.components;
		if(components == null || components.length < 1) return;
		for(int i = 0; i < components.length; i++){
			LBCompDesc cdesc = components[i];
			if(cdesc == null) continue;
			String item = "Object "+(i+1)+" (";
			LabObjectView 	oView = (LabObjectView)components[i].getObject();
			if(oView != null){
				LabObject 		tempObj = (oView == null)?null:oView.getLabObject();
				if(tempObj != null){
					item += (tempObj.name);
				}
			}
			item += ")...";
			menu.add(item);
		}
	}

    public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equals("Load Note...")){
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
		}else if(e.getActionCommand().equals("Test")){
			tArea.test();
		}else if(e.getActionCommand().equals("Clear")){
			if(tArea != null) tArea.clearAll();
			numbObjectChanged();
		}else if(e.getActionCommand().equals("Delete Current Object")){
			if(tArea != null) tArea.deleteCurrentObject();
			numbObjectChanged();
		}else if(e.getActionCommand().equals("Delete All Objects")){
			if(tArea != null) tArea.deleteAllObjects();
			numbObjectChanged();
		}else if(e.getSource() == menu){
			System.out.println("OBJECTS");
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
			remove(upButton);
			remove(downButton);
			if(showDone) remove(doneButton);
			add(view);
//			view.didLayout = false;
			view.layout(false);
			view.setEmbeddedState(false);
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

		edit.add(tArea, 1, RelativeContainer.BELOW, 
		RelativeContainer.REST, RelativeContainer.REST);
		insertButton = new Button("Insert");
		add(edit);
		add(insertButton);
		upButton = new Button("Up");
		downButton = new Button("Down");
		add(upButton);
		add(downButton);

		
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
		upButton.setRect(35,height-15,20,15);
		downButton.setRect(60,height-15,30,15);
		tArea.setText(tArea.getText());
		tArea.layoutComponents();
	}

	public void close(){
		Debug.println("Got close in document");
		tArea.close();

		super.close();
	}

	public void onEvent(Event e){
		if(e.type == ControlEvent.PRESSED && (addedLabObjectView != null)){
			if(e.target == doneOutButton){
				addedLabObjectView.setEmbeddedState(true);
				addedLabObjectView.setShowMenus(false);
				remove(addedLabObjectView);
				remove(doneOutButton);
				add(edit);
				add(insertButton);
				add(upButton);
				add(downButton);
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
		
		if(e.target == upButton && e.type == ControlEvent.PRESSED){
			if(tArea != null){
				tArea.moveUp();
			}	    
		}
		
		if(e.target == downButton && e.type == ControlEvent.PRESSED){
			if(tArea != null){
				tArea.moveDown();
			}	    
		}
		
		if(e.target == insertButton && e.type == ControlEvent.PRESSED){
			if(tArea != null){
				tArea.insertObject();
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
