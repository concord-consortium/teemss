import waba.ui.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;

public class Tester extends ExtraMainWindow
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

    int newIndex = 0;

    LObjDictionary loDict = null;

    public void onStart()
    {
	Dialog.showImages = false;

	if(waba.sys.Vm.getPlatform().equals("PalmOS")){
	    graph.Bin.START_DATA_SIZE = 1000;
	}
	menuBar = new MenuBar();
	setMenuBar(menuBar);
	file = new Menu("File");
	file.add("Exit");
	file.addActionListener(this);
	menuBar.add(file);

	me.setRect(x,y,width,height);
	add(me);

	LabBookDB lbDB = new LabBookFile("LabBook");
	if(lbDB.getError()){
	    // Error;
	    exit(0);
	}

	labBook = new LabBook();
	LabObject.lBook = labBook;

	Debug.println("Openning");
	labBook.open(lbDB);

	loDict = (LObjDictionary)labBook.load(new LabObjectPtr(0,0,null));
	if(loDict == null){
	    loDict = new LObjDictionary();
	    loDict.name = "Root";
	    labBook.store(loDict);

	}
	LObjDictionaryView view = (LObjDictionaryView)loDict.getView(this, true);
	view.setRect(x,y,width,height);
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
	replacement.setRect(x,y,width,height);
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
		labBook.commit();
		if(!labBook.close()){
		
		} else {
		    labBook = null;
		    exit(0);
		}
	    }
	} 
    }

    public void onExit(int code)
    {
	Debug.println("closing");
	if(labBook != null)
	    labBook.close();
    }

}
