import waba.ui.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import org.concord.LabBook.*;

public class CCProbe extends ExtraMainWindow
    implements LObjViewContainer
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

    int newIndex = 0;

    LObjDictionary loDict = null;

    String aboutTitle = "About CCProbe";

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

	
		file = new Menu("File");
		file.add(aboutTitle);
		file.add("-");
		file.add("Exit");
		file.addActionListener(this);
		menuBar.add(file);

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

    public void addMenu(LabObjectView source, Menu menu)
    {
		menuBar.add(menu);
    }

    public void delMenu(LabObjectView source, Menu menu)
    {
		menuBar.remove(menu);
    }

    public void done(LabObjectView source){}

    public LObjDictionary getDict() {return null;}

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
