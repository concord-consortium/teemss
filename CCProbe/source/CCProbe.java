import waba.ui.*;
import waba.util.*;
import waba.fx.*;

import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;

import org.concord.LabBook.*;
import org.concord.CCProbe.*;

public class CCProbe extends MainView
{

	LabBookSession mainSession;
    Menu edit;
    Title 		title;
	int yOffset = 0;
    int newIndex = 0;

    String aboutTitle = "About CCProbe";
    String [] creationTypes = {"Folder", "Notes", "Data Collector", 
							   "Drawing","UnitConvertor","Image"};

	int		[]creationID = {0x00010100};
	
    public void onStart()
    {
		super.onStart();
    	
		LObjDictionary loDict = null;

		LabBook.registerFactory(new DataObjFactory());

		// Dialog.showImages = false;
		// ImagePane.showImages = false;

		graph.Bin.START_DATA_SIZE = 25000;
		graph.LargeFloatArray.MaxNumChunks = 25;

		int dictHeight = myHeight;

		LabBookDB lbDB;
		String plat = waba.sys.Vm.getPlatform();
		if(plat.equals("PalmOS")){
			graph.Bin.START_DATA_SIZE = 4000;
			graph.LargeFloatArray.MaxNumChunks = 4;
			GraphSettings.MAX_COLLECTIONS = 1;
			graph.GraphViewLine.scrollStepSize = 0.45f;
			lbDB = new LabBookCatalog("LabBook");
			CCTextArea.INTER_LINE_SPACING = 0;
		} else if(plat.equals("Java")){
			/*
			graph.Bin.START_DATA_SIZE = 4000;
			graph.LargeFloatArray.MaxNumChunks = 4;
			GraphSettings.MAX_COLLECTIONS = 1;
			*/
			lbDB = new LabBookCatalog("LabBook");
			// lbDB = new LabBookFile("LabBook");
		} else {
			lbDB = new LabBookFile("LabBook");
			GraphSettings.MAX_COLLECTIONS = 4;
		}

		if(myHeight < 180){
			yOffset = 13;
			dictHeight -= 13;
			if(title == null) title = new Title("CCProbe");
			title.setRect(0,0,width, 13);
			me.add(title);
		}

		if(lbDB.getError()){
			// Error;
			exit(0);
		}
		
		String [] fileStrings;
		if(!plat.equals("PalmOS")){
			int i=0;
			fileStrings = new String [4];
			fileStrings[i++] = aboutTitle;
			if(plat.equals("Java")){
				fileStrings[i++] = "Serial Port Setup..";
			}
			fileStrings[i++] = "-";
			fileStrings[i++] = "Exit";
		} else {
			fileStrings = new String [1];
			fileStrings[0] = aboutTitle;			
		}
		
		addFileMenuItems(fileStrings, null);

		Debug.println("Openning");
		labBook.open(lbDB);
		LabObjectPtr rootPtr = labBook.getRoot();
		
		mainSession = rootPtr.getSession();

		loDict = (LObjDictionary)mainSession.getObj(rootPtr);
		if(loDict == null){
			loDict = DefaultFactory.createDictionary();
			loDict.setName("Home");
			mainSession.storeNew(loDict);
			labBook.store(loDict);

		}
		LabObjectView view = (LabObjectView)loDict.getView(this, true, mainSession);
		
		view.setRect(x,yOffset,width,dictHeight);
		view.setShowMenus(true);
		me.add(view);
		lObjView = view;
		if(loDict != null){
			newIndex = loDict.getChildCount();
		}

    }

	public String [] getCreateNames()
	{
	String  []createNames = creationTypes;
		if(creationID != null){
			for(int i = 0; i < creationID.length; i++){
				int factoryType = (creationID[i] & 0xFFFF0000);
				factoryType >>>= 16;
				int objID = creationID[i] & 0xFFFF;
				LabObjectFactory factory = null;
				for(int f = 0; f < LabBook.objFactories.length; f++){
					if(LabBook.objFactories[f] == null) continue;
					if(LabBook.objFactories[f].getFactoryType() == factoryType){
						factory = LabBook.objFactories[f];
						break;
					}
				}
				if(factory != null){
					LabObjDescriptor []desc = factory.getLabBookObjDesc();
					if(desc != null){
						for(int d = 0; d < desc.length; d++){
							if(desc[d] == null) continue;
							if(desc[d].objType == objID){
								String name = desc[d].name;
								if(name != null){
									String []newNames = new String[createNames.length+1];
									waba.sys.Vm.copyArray(createNames,0,newNames,0,createNames.length);
									newNames[createNames.length] = name;
									createNames = newNames;
								}
							}
						}
					}
				}
			}
		}	
		return createNames;
	}

	public void createObj(String objType, LObjDictionaryView dView)
	{
		LabObject newObj = null;
//		boolean autoEdit = false;
		boolean autoEdit = true;
		boolean autoProp = true;
		
		for(int f = 0; f < LabBook.objFactories.length; f++){
			if(LabBook.objFactories[f] == null) continue;
			LabObjDescriptor []desc = LabBook.objFactories[f].getLabBookObjDesc();
			if(desc == null) continue;
			boolean doExit = false;
			for(int d = 0; d < desc.length; d++){
				if(desc[d] == null) continue;
				if(objType.equals(desc[d].name)){
					newObj = LabBook.objFactories[f].makeNewObj(desc[d].objType);
					if(objType.equals("Folder")){
						autoEdit = false;
					} else if(objType.equals("Data Collector")){	       
						autoEdit = false;
					}
					doExit = true;
					break;
				}
			}
			if(doExit) break;
		}

		if(newObj != null){
			dView.getSession().storeNew(newObj);
			if(newIndex == 0){
				newObj.setName(objType);		    
			} else {
				newObj.setName(objType + " " + newIndex);		    
			}			
			newIndex++;
			
			dView.insertAtSelected(newObj);

			// The order seems to matter here.  
			// insert and selected for some reason nulls the pointer.
			// perhaps by doing a commit?
			// newObj.store();
			
			if(autoEdit){
				dView.openSelected(true);
			} else if(autoProp){
				dView.showProperties(newObj);
			} 
			
		}
	}

    public void reload(LabObjectView source)
    {
		if(source != lObjView) Debug.println("Error source being removed");
		LabObject obj = source.getLabObject();
		LabBookSession oldSession = source.getSession();
		source.close();
		me.remove(source);
		if(title != null){
			me.remove(title);
		}
		LabObjectView replacement = obj.getView(this, true, oldSession);
		// This automatically does the layout call for us

		waba.fx.Rect myRect = content.getRect();
		myHeight = myRect.height;
		int dictHeight = myHeight;
		if(myHeight < 180){
			yOffset = 13;
			dictHeight -= 13;
			if(title == null) title = new Title("CCProbe");
			title.setRect(0,0,width, 13);
			me.add(title);
		}
		replacement.setRect(x,yOffset,width,dictHeight);
		replacement.setShowMenus(true);
		me.add(replacement);
		lObjView = replacement;
    }

    public void actionPerformed(ActionEvent e)
    {
		String command;
		Debug.println("Got action: " + e.getActionCommand());

		if(e.getSource() == file){
			command = e.getActionCommand();
			if(command.equals("Exit")){
				handleQuit();
			}else if(command.equals(aboutTitle)){
				handleAbout();
			} else if(command.equals("Serial Port Setup..")){
				DataExport.showSerialDialog();
			} else {
				super.actionPerformed(e);
			}
		}
    }

    public void onExit()
    {
		Debug.println("closing");
		if(labBook != null){
			if(curFullView != null){
				curFullView.close();
			} else {
				lObjView.close();
			}
			labBook.commit();
			labBook.close();
		}
    }

	public void handleQuit(){
		Debug.println("commiting");
		if(curFullView != null) curFullView.close();
		else lObjView.close();
		if(!labBook.commit() ||
		   !labBook.close()){
			//error
		} else {
			labBook = null;
			exit(0);
		}
	}

	public void handleAbout(){
		Dialog.showAboutDialog(aboutTitle,AboutMessages.getMessage());
	}
}
