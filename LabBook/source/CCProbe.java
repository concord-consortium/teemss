import waba.ui.*;
import waba.util.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import org.concord.LabBook.*;

public class CCProbe extends ExtraMainWindow
    implements ViewContainer, MainView
{
    LabBook labBook;
    MenuBar menuBar;
    Menu file;
    Menu edit;
    TreeControl treeControl;
    TreeModel treeModel;
    Container me = new Container();
    LabObjectView lObjView = null;
    int myHeight;
	Vector fileMenuStrings = new Vector();

    int newIndex = 0;

    LObjDictionary loDict = null;

    String aboutTitle = "About CCProbe";
	String [] fileStrings;
	Vector fileListeners = new Vector();

    public void onStart()
    {
		// Dialog.showImages = false;
		// ImagePane.showImages = false;

		graph.Bin.START_DATA_SIZE = 25000;
		graph.LargeFloatArray.MaxNumChunks = 25;

		menuBar = new MenuBar();

		// Notice the width and height will change here
		setMenuBar(menuBar);
		waba.fx.Rect myRect = content.getRect();
		myHeight = myRect.height;

		me.setRect(x,y,width,myHeight);

		add(me);

		LabBookDB lbDB;
		String plat = waba.sys.Vm.getPlatform();
		if(plat.equals("PalmOS")){
			graph.Bin.START_DATA_SIZE = 4000;
			graph.LargeFloatArray.MaxNumChunks = 4;
			lbDB = new LabBookCatalog("LabBook");
		} else if(plat.equals("Java")){
			lbDB = new LabBookCatalog("LabBook");
		} else {
			lbDB = new LabBookFile("LabBook");
		}	    

		if(lbDB.getError()){
			// Error;
			exit(0);
		}
		file = new Menu("File");
		
		file.add(aboutTitle);
		if(!plat.equals("PalmOS")){
			file.add("-");
			file.add("Exit");
			fileStrings = new String [3];
			fileStrings[0] = aboutTitle;
			fileStrings[1] = "-";
			fileStrings[2] = "Exit";
		} else {
			fileStrings[0] = aboutTitle;			
		}
		
		file.addActionListener(this);
		menuBar.add(file);

		labBook = new LabBook();
		LabObject.lBook = labBook;

		Debug.println("Openning");
		labBook.open(lbDB);

		loDict = (LObjDictionary)labBook.load(labBook.getRoot());
		if(loDict == null){
			loDict = new LObjDictionary();
			loDict.name = "Root";
			labBook.store(loDict);

		}
		LObjDictionaryView view = (LObjDictionaryView)loDict.getView(this, true);
		view.setRect(x,y,width,myHeight);

		me.add(view);
		lObjView = view;

    }

	public MainView getMainView()
	{
		return this;
	}

    public void addMenu(LabObjectView source, Menu menu)
    {
		menuBar.add(menu);
    }

    public void delMenu(LabObjectView source, Menu menu)
    {
		menuBar.remove(menu);
    }

	void updateFileMenu()
	{		
		int i;
		file.removeAll();
		for(i=0; i < fileMenuStrings.getCount(); i++){
			String [] items = (String [])fileMenuStrings.get(i);
			for(int j=0; j < items.length; j++){
				file.add(items[j]);
			}
				file.add("-");

		}		
		
		for(i = 0; i < fileStrings.length; i++){
			file.add(fileStrings[i]);
		}
	}

	public void addFileMenuItems(String [] items, ActionListener source)
	{
		fileMenuStrings.insert(0, items);
		updateFileMenu();		
		fileListeners.add(source);
	}


	public void removeFileMenuItems(String [] items, ActionListener source)
	{
		int index = fileMenuStrings.find(items);
		if(index < 0) return;
		fileMenuStrings.del(index);
		updateFileMenu();
		index = fileListeners.find(source);
		if(index < 0) return;
		fileListeners.del(index);
	}

    public void done(LabObjectView source){}

    public void reload(LabObjectView source)
    {
		if(source != lObjView) Debug.println("Error source being removed");
		LabObject obj = source.getLabObject();
		source.close();
		me.remove(source);
		LabObjectView replacement = obj.getView(this, true);
		// This automatically does the layout call for us
		replacement.setRect(x,y,width,myHeight);

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
				Debug.println("commiting");
				labBook.store(loDict);
				if(!labBook.commit() ||
				   !labBook.close()){
					//error
				} else {
					labBook = null;
					exit(0);
				}
			}else if(command.equals(aboutTitle)){
				Dialog.showAboutDialog(aboutTitle,AboutMessages.getMessage());
			} else {
				for(int i=0; i<fileListeners.getCount(); i++){
					((ActionListener)fileListeners.get(i)).actionPerformed(e);
				}
			}
		}
    }

    public void onExit()
    {
		Debug.println("closing");
		if(labBook != null){
			labBook.store(loDict);
			labBook.commit();
			labBook.close();
		}

    }

}
