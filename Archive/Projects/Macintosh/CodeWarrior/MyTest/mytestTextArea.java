import waba.ui.*;

import waba.fx.*;

import extra.ui.*;


import org.concord.waba.extra.ui.*;

import java.awt.event.ActionEvent;

import extra.util.*;




public class mytestTextArea extends ExtraMainWindow implements org.concord.waba.extra.event.DialogListener

{

TextArea textArea;

Menu	myEMenu;








public mytestTextArea()

	{

		textArea = new TextArea();

		textArea.setRect(0, 20, this.width, this.height - 40);

		add(textArea);



		MenuBar menubar = new MenuBar();

		myEMenu = new Menu("Edit");

		myEMenu.add("Load Note ...");
		myEMenu.add("-");
		myEMenu.add("Clear");
		myEMenu.add("Paste");
		myEMenu.addActionListener(this);

		menubar.add(myEMenu);


		setMenuBar(menubar);

	}

	

	

    public void dialogClosed(org.concord.waba.extra.event.DialogEvent e){

    	System.out.println("Command "+e.getActionCommand()+" InfoType "+e.getInfoType()+" Info "+e.getInfo());

    	if((e.getInfoType() == org.concord.waba.extra.event.DialogEvent.PROPERTIES) && (e.getInfo() instanceof PropContainer)){

    		System.out.println("PROPERTIES");

    		PropContainer pc = (PropContainer)e.getInfo();

    		int nContainers = pc.getNumbPropContainers();

		for(int i = 0; i < nContainers; i++){

    			System.out.println("Name "+pc.getPropertiesContainerName(i));

			waba.util.Vector prop = pc.getProperties(i);

			if(prop == null) continue;

			int nProperties = prop.getCount();

			for(int j = 0; j < nProperties; j++){

				PropObject po = (PropObject)prop.get(j);

    				System.out.println(po.getName()+" = "+po.getValue());

			}

			System.out.println();

		}

    	}

    }

    

    

   	public void actionPerformed(org.concord.waba.extra.event.ActionEvent e){
		if(e.getActionCommand().equals("Load Note ...")){
			openFileDialog();
		}else if(e.getActionCommand().equals("Paste")){
			if(!CCClipboard.isClipboardEmpty()){
				String str = CCClipboard.getStringContent();
				if(str != null){
					textArea.insertText(str);
				}
			}
		}else if(e.getActionCommand().equals("Clear")){
			textArea.setText("");
			_doPaint(x,y,width,height);//we need to do it for awt
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
    	textArea.setText(new String(chars,0,chars.length));
    	
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e){
		if(e.getSource() == myEMenu){

			if(e.getSource() == myEMenu){

				System.out.println("myEMenu");

			}

			if(e.getActionCommand().equals("Clear")){

				textArea.setText("");

				_doPaint(x,y,width,height);//we need to do it for awt

			}

		}

    }



// Event.java

    public void onEvent(waba.ui.Event event)

   {	

	if (event.type == PenEvent.PEN_DOWN){

		try{

			PenEvent penEvent = (PenEvent)event;

			System.out.println("x "+penEvent.x+" y "+penEvent.y+" this.width "+this.width);

			if(((penEvent.y >= 0 && penEvent.y <= 20) || (penEvent.y > this.height - 40)) && (penEvent.x >= this.width/2 && penEvent.x <= this.width)){

//				pMenu.show(penEvent.x,penEvent.y);

				

//				Dialog.showMessageDialog(this,"Title Title Title Title","Message Message","Button",Dialog.WARN_DIALOG);

				String []confirmButtons = {"Yes","No","Cancel"};

//				Dialog.showConfirmDialog(this,"Title Title Title Title","Message Message",confirmButtons,Dialog.WARN_DIALOG);



				String []choices = {"item1","item2","item3"};

//				Dialog.showInputDialog(this,"Title Title Title Title","Message Message",confirmButtons,Dialog.CHOICE_INP_DIALOG,choices);

				Dialog.showInputDialog(this,"Title Title Title Title","Message Message",confirmButtons,Dialog.EDIT_INP_DIALOG);






				



			}

		}catch(Exception e){}

	}

	if (event.type == ControlEvent.PRESSED){

	}

}


}





