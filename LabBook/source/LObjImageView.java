package org.concord.LabBook;

import waba.util.*;
import waba.ui.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;
import extra.ui.*;
import extra.util.CCUnit;
import org.concord.waba.extra.event.*;
//LabObject implements Storable
public class LObjImageView extends LabObjectView implements ActionListener
{
	Button clearButton,convertButton,doneButton,dirButton;
	Choice catChoice;
	Choice currChoiceFrom,currChoiceTo;
	Edit	numberLeft,numberRight;
	boolean leftToRight = true;
    Menu menu = null;
	ImagePane	imagePane = null;
	//	private 	byte []bytes = null;
	Label	nameLabel;
	Edit 	nameEdit;
	boolean	nameEditWasAdded = false;
	
	LObjImage iObj;
	
	public LObjImageView(ViewContainer vc, LObjImage image,boolean edit){
		super(vc, (LabObject)image, null);
		iObj = image;
	}

	public void delMenus(){
		if(container != null) container.getMainView().delMenu(this, menu);
	}
	public void addMenus(){
		addMenus(container);
	}

	public void addMenus(ViewContainer vc){
		
		if(menu == null){
			menu = new Menu("Image");
			menu.add("Load Image ...");
			menu.addActionListener(this);
		}
		if(vc != null) vc.getMainView().addMenu(this, menu);
	}
	public void setEmbeddedState(boolean embeddedState){
		boolean oldState = getEmbeddedState();
		super.setEmbeddedState(embeddedState);
		if(oldState != getEmbeddedState()){
			if(nameEdit != null){
				if(getEmbeddedState()){
					if(nameEditWasAdded){
						remove(nameEdit);
						if(nameLabel != null) remove(nameLabel);
					}
					nameEditWasAdded = false;
				}else{
					if(!nameEditWasAdded){
						if(nameLabel != null) add(nameLabel);
						add(nameEdit);
					}
					nameEditWasAdded = true;
				}
			}
			if(getEmbeddedState()){
				if(imagePane != null) imagePane.setRect(0,0);
			}else{
				if(imagePane != null) imagePane.setRect(0,17);
			}
		}
	}

	private void removeImagePane()
	{
		if(imagePane != null){
			imagePane.freeImage();
			remove(imagePane);
			imagePane = null;
		}
	}

    public void layout(boolean sDone){
    
		if(didLayout) return;
		didLayout = true;
		if(doneButton != null){
			remove(doneButton);
		}
		
		showDone = sDone;
		if(showDone){
			if(doneButton == null) doneButton = new Button("Done");
			add(doneButton);
		}
		if(nameEdit == null) nameEdit = new Edit();
		nameEdit.setText(getLabObject().getName());
		if(nameLabel == null) nameLabel = new Label("Name");
		if(!getEmbeddedState()){
			add(nameLabel);
			add(nameEdit);
			nameEditWasAdded = true;
		}

		removeImagePane();

		waba.fx.Image wabaImage = iObj.getImage();
		if(wabaImage != null){
			imagePane = new ImagePane(wabaImage);
			add(imagePane);
		}
	}

	public void setRect(int x, int y, int width, int height){
		super.setRect(x,y,width,height);
		if(!didLayout) layout(showDone);

		if(doneButton != null){
			doneButton.setRect(width-30,1,30,15);
		}
		if(!getEmbeddedState() && nameEdit != null && nameEditWasAdded){
			if(nameLabel != null) nameLabel.setRect(1,1,30,15);
			int editW = (showDone)?width - 62:width - 32;
			if(nameEdit != null) nameEdit.setRect(30, 1, editW, 15);
		}

		if(getEmbeddedState()){
			if(imagePane != null) imagePane.setRect(0,0);
		}else{
			if(imagePane != null) imagePane.setRect(0,17);
		}
	}

	public int getPreferredWidth(){
		int iWidth = iObj.getImageWidth();
		if(iWidth <= 0) return 10;
		return iWidth;
	}

	public int getPreferredHeight(){
		int iHeight = iObj.getImageHeight();
		if(iHeight <= 0) return 10;
		return iHeight;
	}

	private extra.ui.Dimension preferrDimension;
	public extra.ui.Dimension getPreferredSize(){
		if(preferrDimension == null){
			preferrDimension = new extra.ui.Dimension(getPreferredWidth(),getPreferredHeight());
		}else{
			preferrDimension.width = getPreferredWidth();
			preferrDimension.height = getPreferredHeight();
		}
		return preferrDimension;
	}

	// override this to not store the image everytime its closed
    public void close(){
		removeImagePane();

		setShowMenus(false);
		if(session != null) session = null;
    	if(nameEdit != null){
    		iObj.setName(nameEdit.getText());
    	}
    }

	public void onEvent(Event e){
		if(e.target == doneButton &&
		   e.type == ControlEvent.PRESSED){
			if(container != null){
				container.done(this);
			}	
		}
	}

    public void actionPerformed(ActionEvent e){
		if(e.getActionCommand().equals("Load Image ...")){
			openFileDialog();
		}
    }
    
    public void openFileDialog(){
    	String []extensions = {".bmp",".BMP"};
    	FileDialog fd = FileDialog.getFileDialog(FileDialog.FILE_LOAD,extensions);
    	if(fd == null) return;
    	fd.show();
    	byte []bytes = fd.getBytesFromFile();
    	if(bytes == null) return;

		removeImagePane();

		iObj.loadImage(bytes);
		
		waba.fx.Image wabaImage = iObj.getImage();
		if(wabaImage == null) return;
		imagePane = new ImagePane(wabaImage);
		add(imagePane);
		if(getEmbeddedState()){
			imagePane.setRect(0,0);
		}else{
			imagePane.setRect(0,17);
		}
		iObj.setName(fd.getFile());
		nameEdit.setText(iObj.getName());    	
    }
}
