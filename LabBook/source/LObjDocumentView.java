package org.concord.LabBook;

import waba.ui.*;
import waba.fx.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import extra.ui.*;

public class LObjDocumentView extends LabObjectView implements ActionListener{
TextArea 				tArea;
Edit 					nameEdit;
Label					nameLabel;
boolean					nameEditWasAdded = false;
boolean					buttonsWasAdded = false;
boolean					doneButtonWasAdded = false;
RelativeContainer 		edit = new RelativeContainer();

LObjDocument 			doc;
Button 					doneButton;
TimerButton 			upButton,downButton;

public boolean 			showName = true;

Menu 					menu = null;


	public LObjDocumentView(ViewContainer vc, LObjDocument d,boolean edit){
		super(vc);
		doc = d;
		lObj = doc;
	}
	public void delMenus(){
		if(container != null) container.getMainView().delMenu(this, menu);
	}
	public void addMenus(){
		addMenus(container);
	}
	
	public void addMenus(ViewContainer vc){
		
		if(menu == null){
			menu = new Menu("Notes");
			menu.add("Load Note ...");
			menu.add("-");
			menu.add("Paste");
			menu.addActionListener(this);
		}
		if(vc != null) vc.getMainView().addMenu(this, menu);
	}
	public void setEmbeddedState(boolean embeddedState){
		boolean oldState = getEmbeddedState();
		super.setEmbeddedState(embeddedState);
		if(oldState != getEmbeddedState()){
			if(nameEdit != null){
				if(tArea != null) edit.remove(tArea);
				if(getEmbeddedState()){
					if(buttonsWasAdded){
						remove(upButton);
						remove(downButton);
					}
					if(nameEditWasAdded){
						remove(nameEdit);
						remove(nameLabel);
						if(tArea != null) edit.add(tArea, 1, RelativeContainer.TOP,RelativeContainer.REST, RelativeContainer.REST);
					}
					nameEditWasAdded = false;
					buttonsWasAdded = false;
				}else{
					if(!buttonsWasAdded){
						add(upButton);
						add(downButton);
					}
					if(!nameEditWasAdded){
						add(nameLabel);
						add(nameEdit);
						if(tArea != null) edit.add(tArea, 1, RelativeContainer.TOP,RelativeContainer.REST, RelativeContainer.REST);
					}
					buttonsWasAdded = true;
					nameEditWasAdded = true;
				}
			}
		}
	}

    public void actionPerformed(ActionEvent e){
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
		if(upButton == null) 	upButton 	= new TimerButton("Up");
		if(downButton == null) 	downButton 	= new TimerButton("Down");


		if(showName){
			if(doc.name == null) doc.name = "";
			if(nameEdit == null) nameEdit = new Edit();
			nameEdit.setText(doc.name);
			if(nameLabel == null) nameLabel = new Label("Name");
			if(getEmbeddedState()){
				nameEditWasAdded = false;
			}else{
				add(nameLabel);
				add(nameEdit);
				nameEditWasAdded = true;
			}
		} 
		if(getEmbeddedState()){
			buttonsWasAdded = false;
		}else{
			add(upButton);
			add(downButton);
			buttonsWasAdded = true;
		}

		if(tArea == null) tArea = new TextArea();
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
	}

	public int getHeight(){
		return (tArea.getFontMetrics().getHeight() + 2) * tArea.getNumLines() + tArea.spacing*2 + 3;
	}

	public void setRect(int x, int y, int width, int height){
		super.setRect(x,y,width,height);
				
		if(!didLayout) layout(showDone);

		if(showDone){
			doneButton.setRect(width-30,0,30,15);
		}
		
		if(getEmbeddedState()){
			edit.setRect(0,0,width,height);
		}else{
			edit.setRect(0,34,width,height-36);
			if(upButton != null)    upButton.setRect(1,17,30,15);
			if(downButton != null)  downButton.setRect(35,17,30,15);
			if(nameLabel != null) nameLabel.setRect(1,1,30,15);
			int editW = (showDone)?width - 62:width - 32;
			if(nameEdit != null) nameEdit.setRect(30, 1, editW, 15);
		}
	}

	public void close(){
		Debug.println("Got close in document");
		if(showName){
			doc.name = nameEdit.getText();
		}
		doc.text = tArea.getText();

		super.close();
	}

	public void onEvent(Event e){
		if(e.target == doneButton && e.type == ControlEvent.PRESSED){
			if(container != null){
				container.done(this);
			}	    
		}else if(e.target == upButton && e.type == ControlEvent.PRESSED){
			if(tArea != null) tArea.scrollUp();
		}else if(e.target == downButton && e.type == ControlEvent.PRESSED){
			if(tArea != null) tArea.scrollDown();
		}else if(e instanceof ControlEvent && e.type == ControlEvent.TIMER){
			if(e.target == upButton){
				if(tArea != null)	tArea.scrollUp();
			}else if(e.target == downButton){
				if(tArea != null)	tArea.scrollDown();
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

	public int getPreferredWidth(waba.fx.FontMetrics fm){
		return 16;
	}

	public int getPreferredHeight(waba.fx.FontMetrics fm){
		return 16;
	}

	private extra.ui.Dimension preferrDimension;
	public extra.ui.Dimension getPreferredSize(){
		if(preferrDimension == null){
			preferrDimension = new extra.ui.Dimension(getPreferredWidth(null),getPreferredHeight(null));
		}else{
			preferrDimension.width = getPreferredWidth(null);
			preferrDimension.height = getPreferredHeight(null);
		}
		return preferrDimension;
	}

}
