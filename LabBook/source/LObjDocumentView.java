package org.concord.LabBook;

import waba.ui.*;
import waba.fx.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import extra.ui.*;

public class LObjDocumentView extends LabObjectView implements ActionListener, ScrollListener, TextAreaListener{
TextArea 				tArea;
Edit 					nameEdit;
Label					nameLabel;
boolean					nameEditWasAdded = false;
boolean					doneButtonWasAdded = false;
RelativeContainer 		edit = new RelativeContainer();

LObjDocument 			doc;
Button 					doneButton;

public boolean 			showName = true;

Menu 					menu = null;

CCScrollBar				scrollBar;

	public LObjDocumentView(ViewContainer vc, LObjDocument d,boolean edit){
		super(vc, (LabObject)d, null);
		doc = d;
	}
	public void delMenus(){
		if(!waba.sys.Vm.getPlatform().equals("PalmOS")){
			if(container != null) container.getMainView().delMenu(this, menu);
		}
	}
	public void addMenus(){
		addMenus(container);
	}
	
	public void addMenus(ViewContainer vc){
		
		if(!waba.sys.Vm.getPlatform().equals("PalmOS")){
			if(menu == null){
				menu = new Menu("Notes");
				menu.add("Load Note ...");
				menu.add("-");
				menu.add("Paste");
				menu.addActionListener(this);
			}
			if(vc != null) vc.getMainView().addMenu(this, menu);
		}
	}
	public void setEmbeddedState(boolean embeddedState){
		boolean oldState = getEmbeddedState();
		super.setEmbeddedState(embeddedState);
		if(oldState != getEmbeddedState()){
			if(nameEdit != null){
				if(tArea != null) edit.remove(tArea);
				if(getEmbeddedState()){
					if(nameEditWasAdded){
						remove(nameEdit);
						remove(nameLabel);
						if(tArea != null) edit.add(tArea, 1, RelativeContainer.TOP,RelativeContainer.REST, RelativeContainer.REST);
					}
					nameEditWasAdded = false;
				}else{
					if(!nameEditWasAdded){
						add(nameLabel);
						add(nameEdit);
						if(tArea != null) edit.add(tArea, 1, RelativeContainer.TOP,RelativeContainer.REST, RelativeContainer.REST);
					}
					nameEditWasAdded = true;
				}
			}
		}
	}

    public void actionPerformed(ActionEvent e){
		if(!waba.sys.Vm.getPlatform().equals("PalmOS")){
			if(e.getActionCommand().equals("Load Note ...")){
				openFileDialog();
			}else if(e.getActionCommand().equals("Paste")){
				if(!CCClipboard.isClipboardEmpty()){
					String str = CCClipboard.getStringContent();
					if(str != null){
						tArea.insertText(str);
					}
				}
			}
		}
    }


	public void layout(boolean sDone){
	
		if(sDone != showDone){
			showDone = sDone;
			if(showDone){
				if(doneButton == null){
					doneButton = new Button("Done");
				}
				if(!doneButtonWasAdded && doneButton != null){
					add(doneButton);
					doneButtonWasAdded = true;
				}
				doneButton.setRect(width-30,0,30,15);
			}else{
				if(doneButtonWasAdded && doneButton != null){
					remove(doneButton);
					doneButtonWasAdded = false;
				}
			}
		}
	
		if(didLayout) return;
		didLayout = true;
		showDone = sDone;

		if(showName){
			if(doc.getName() == null) doc.setName("");
			if(nameEdit == null) nameEdit = new Edit();
			nameEdit.setText(doc.getName());
			if(nameLabel == null) nameLabel = new Label("Name");
			if(getEmbeddedState()){
				nameEditWasAdded = false;
			}else{
				add(nameLabel);
				add(nameEdit);
				nameEditWasAdded = true;
			}
		} 
		if(tArea == null) tArea = new TextArea();
		tArea.addTextAreaListener(this);
		if(doc.text != null)  tArea.setText(doc.text);
		edit.add(tArea, 1, RelativeContainer.TOP, 
		RelativeContainer.REST, RelativeContainer.REST);
		add(edit);
		if(doc.text != null)  tArea.setText(doc.text);
		
		if(showDone){
			doneButton = new Button("Done");
			add(doneButton);
			doneButtonWasAdded = true;
		} 
		if(scrollBar == null) scrollBar = new CCScrollBar(this);
		add(scrollBar);
	}

	public int getHeight(){
		return (tArea.getFontMetrics().getHeight() + 2) * tArea.getNumLines() + tArea.spacing*2 + 3;
	}

	public void setRect(int x, int y, int width, int height){
		super.setRect(x,y,width,height);
		int wsb = (waba.sys.Vm.getPlatform().equals("WinCE"))?11:7;
				
		if(!didLayout) layout(showDone);

		if(showDone){
			doneButton.setRect(width-30,0,30,15);
		}
		
		if(getEmbeddedState()){
			edit.setRect(0,0,width-wsb-1,height);
			if(scrollBar != null){
				scrollBar.setRect(width - wsb,0,wsb, height);
			}
		}else{
			edit.setRect(0,17,width-wsb-1,height-19);
			if(scrollBar != null){
				scrollBar.setRect(width - wsb,17,wsb, height-19);
			}
			if(nameLabel != null) nameLabel.setRect(1,1,30,15);
			int editW = (showDone)?width - 62:width - 32;
			if(nameEdit != null) nameEdit.setRect(30, 1, editW, 15);
		}
		redesignScrollBar();
	}

	public void close(){
		Debug.println("Got close in document");
		if(showName){
			doc.setName(nameEdit.getText());
		}
		doc.text = tArea.getText();

		if(scrollBar != null) scrollBar.close();
		super.close();
	}

	public void onEvent(Event e){
		if(e.target == doneButton && e.type == ControlEvent.PRESSED){
			if(container != null){
				container.done(this);
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

	public int getPreferredWidth(){
		return 100;
	}

	public int getPreferredHeight(){
		return 32;
	}

	public void textAreaWasChanged(TextAreaEvent ev){
		redesignScrollBar();
	}
	
	public void redesignScrollBar(){
		if(scrollBar == null) return;
		if(tArea == null) return;
		int allLines = tArea.getNumLines();
		int maxVisLine = tArea.getScreenRows();
		scrollBar.setMinMaxValues(0,allLines - maxVisLine);
		scrollBar.setAreaValues(allLines,maxVisLine);
		scrollBar.setIncValue(1);
		scrollBar.setPageIncValue((int)(0.8f*maxVisLine+0.5f));
		scrollBar.setRValueRect();
		if(allLines > maxVisLine){
			scrollBar.setValue(tArea.getFirstLine());
		}else{
			tArea.setFirstLine(0);
			scrollBar.setValue(0);
		}
		repaint();
	}
	
	public void scrollValueChanged(ScrollEvent se){
		if(se.target != scrollBar) return;
		if(tArea != null) tArea.setFirstLine(se.getScrollValue());
	}
}
