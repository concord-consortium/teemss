package org.concord.LabBook;

import waba.ui.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import extra.ui.*;

public class LObjDocumentView extends LabObjectView implements ActionListener{
TextArea 				tArea;
Edit 					nameEdit;
RelativeContainer 		edit = new RelativeContainer();

LObjDocument 			doc;
Button 					doneButton;

public boolean 			showName = true;

Menu 					menu = null;

	public LObjDocumentView(LObjViewContainer vc, LObjDocument d,boolean edit){
		super(vc);
		doc = d;
		lObj = doc;
		/*if(edit) */addMenus(vc);
	}
	public void addMenus(LObjViewContainer vc){
		
		if(menu != null || vc == null) return;
		menu = new Menu("Notes");
		menu.add("Load Note ...");
		menu.add("-");
		menu.add("Paste");
		menu.addActionListener(this);
		vc.addMenu(this, menu);
	}

    public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equals("Load Note ...")){
			openFileDialog();
		}else if(e.getActionCommand().equals("Paste")){
			if(!CCClipboard.isClipboardEmpty()){
				String str = CCClipboard.getStringContent();
				if(str != null){
//    				tArea.setText(str);
					tArea.insertText(str);
				}
			}
		}
    }


	public void layout(boolean sDone){
		if(didLayout) return;
		didLayout = true;

		showDone = sDone;

		if(showName){
			if(doc.name == null) doc.name = "";
			nameEdit = new Edit();
			nameEdit.setText(doc.name);
			edit.add(new Label("Name"), 1, 1, 30, 15);
			edit.add(nameEdit, 30, 1, 50, 15);
		} 
		tArea = new TextArea();
		if(doc.text != null)  tArea.setText(doc.text);
		edit.add(tArea, 1, RelativeContainer.BELOW, 
		RelativeContainer.REST, RelativeContainer.REST);
		add(edit);
		if(showDone){
			doneButton = new Button("Done");
			add(doneButton);
		} 
	}

	public int getHeight(){
		return (tArea.getFontMetrics().getHeight() + 2) * tArea.getNumLines() + tArea.spacing*2 + 3;
	}

	public void setRect(int x, int y, int width, int height){
		super.setRect(x,y,width,height);
		if(!didLayout) layout(false);

		if(showDone){
			edit.setRect(0,0,width,height-15);
			doneButton.setRect(width-30,height-15,30,15);
		} else {
			edit.setRect(0,0,width,height);
		}
	}

	public void close(){
		Debug.println("Got close in document");
		if(showName){
			doc.name = nameEdit.getText();
		}
		doc.text = tArea.getText();
		if(container != null && menu != null){
		    container.delMenu(this,menu);
		}

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
}
