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
	menuBar = new MenuBar();
	setMenuBar(menuBar);
	file = new Menu("File");
	file.add("Exit");
	file.addActionListener(this);
	menuBar.add(file);

	me.setRect(x,y,width,height);
	add(me);

	LabBookDB lbDB = (LabBookDB)new LabBookFile("LabBook.ccp");
	labBook = new LabBook();
	LabObject.lBook = labBook;

	System.out.println("Openning");
	labBook.open(lbDB);

	loDict = (LObjDictionary)labBook.load(new LabObjectPtr(0,0,null));
	if(loDict == null){
	    loDict = new LObjDictionary();
	    loDict.name = "Root";
	    labBook.store(loDict);

	}
	LObjDictionaryView view = (LObjDictionaryView)loDict.getView(true);
	view.setRect(x,y,width,height);
	me.add(view);
	lObjView = (LabObjectView)view;
	lObjView.addViewContainer(this);


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
	if(source != lObjView) System.out.println("Error source being removed");
	LabObject obj = source.getLabObject();
	source.close();
	me.remove(source);
	LabObjectView replacement = obj.getView(true);
	// This automatically does the layout call for us
	replacement.setRect(x,y,width,height);
	me.add(replacement);
	lObjView = replacement;
	replacement.addViewContainer(this);
    }

    public void actionPerformed(ActionEvent e)
    {
	String command;
	System.out.println("Got action: " + e.getActionCommand());

	if(e.getSource() == file){
	    command = e.getActionCommand();
	    if(command.equals("Exit")){
		java.awt.Window awtWindow = (java.awt.Window)getAWTCanvas().getParent().getParent();
		java.awt.Insets insets 	= awtWindow.getInsets();
		System.out.println(insets);

		System.out.println("commiting");
		labBook.commit();
		labBook.close();
		exit(0);
	    }
	} 
    }

    public void onExit(int code)
    {
	System.out.println("closing");
	labBook.close();
    }

}
