import waba.ui.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import extra.util.*;
import extra.ui.*;

public class LObjDictionaryView extends LabObjectView 
    implements ActionListener, LObjViewContainer, DialogListener
{
    TreeControl treeControl;
    TreeModel treeModel;
    RelativeContainer me = new RelativeContainer();
    LabObjectView lObjView = null;

    int newIndex = 0;

    LObjDictionary dict;
    Edit nameEdit;
    RelativeContainer edit = new RelativeContainer();
    GridContainer buttons = null; 
 
    Button doneButton = new Button("Done");
    Button newButton = new Button("New");
    Button openButton = new Button("Open");
    Button editButton = new Button("Edit");
    Button delButton = new Button("Del");

    org.concord.waba.extra.ui.Menu menu = new org.concord.waba.extra.ui.Menu("Dict");

    PropContainer creationProps = new PropContainer();
    PropContainer subCreateProps = creationProps.createSubContainer("Sub");
    String [] creationTypes = {"Dictionary", "Document", "Questions", "DataControl"};
    PropObject newObjType = new PropObject("Type", creationTypes);

    boolean editStatus = false;

    public LObjDictionaryView(LObjDictionary d)
    {
	dict = d;
	lObj = (LabObject)dict;
	add(me);
	menu.add("*Pager View");
	menu.addActionListener(this);
	
	creationProps.addProperty(newObjType, "Sub");
    }

    public void layout(boolean sDone, boolean sName)
    {
	if(didLayout) return;
	didLayout = true;

	showDone = sDone;
	showName = sName;

	if(showName){
	    nameEdit = new Edit();
	    nameEdit.setText(dict.name);
	    edit.add(new Label("Name"), 1, 1, 30, 15);
	    edit.add(nameEdit, 30, 1, 50, 15);
	} 
	treeModel = new TreeModel(dict);
	treeControl = new TreeControl(treeModel);
	edit.add(treeControl, 1, RelativeContainer.BELOW, 
		 RelativeContainer.REST, RelativeContainer.REST);
	me.add(edit);

	if(showDone){
	    buttons = new GridContainer(5,1);
	    buttons.add(doneButton, 4, 0);
	} else {
	    buttons = new GridContainer(4,1);
	}
	buttons.add(newButton, 0, 0);
	buttons.add(openButton, 1, 0);
	buttons.add(editButton, 2, 0);
	buttons.add(delButton, 3, 0);
	me.add(buttons);
    }

    public void setRect(int x, int y, int width, int height)
    {
	super.setRect(x,y,width,height);
	if(!didLayout) layout(false, false);
	
	me.setRect(0,0, width, height);
	edit.setRect(0,0,width, height-20);
	System.out.println("Setting grid size: " + width + " " + height);
	buttons.setRect(0,height-20,width,20);
    }

    public void onEvent(Event e)
    {
	if(e.type == ControlEvent.PRESSED){
	    TreeNode curNode;
	    TreeNode parent;

	    LabObject newObj;
	    if(e.target == newButton){
		if(treeControl.getSelected() == null) return;
		PropertyDialog pd = new PropertyDialog((org.concord.waba.extra.ui.ExtraMainWindow)MainWindow.getMainWindow(),
						       this, "Create new lab object", 
						       creationProps);
		pd.setRect(50,50, 140,140);
		//		pd.setContent();
		pd.show();
	    } else if(e.target == delButton){
		curNode = treeControl.getSelected();
		if(curNode == null) return;
		parent = treeControl.getSelectedParent();
		treeModel.removeNodeFromParent(curNode, parent);
	    } else if(e.target == openButton){
		curNode = treeControl.getSelected();
		if(curNode == null) return;
		editStatus = false;
		lObjView = ((LabObject)curNode).getView(false);
		showPage(lObjView);
	    } else if(e.target == editButton){
		curNode = treeControl.getSelected();
		if(curNode == null) return;
		editStatus = true;
		lObjView = ((LabObject)curNode).getView(true);
		showPage(lObjView);
	    } else if(e.target == doneButton){
		if(container != null){
		    container.done(this);
		}
	    }	    
	}
    }

    public void dialogClosed(DialogEvent e)
    {
	String command = e.getActionCommand();
	if(!command.equals("Cancel")){
	    String objType = newObjType.getValue();
	    LabObject newObj = null;
	    if(objType.equals("Dictionary")){
		newObj = (LabObject)new LObjDictionary();
	    } else if(objType.equals("Document")){
		newObj = (LabObject)new LObjDocument();
	    } else if(objType.equals("Questions")){
		newObj = LObjQuestion.makeNewQuestionSet();

	    } else if(objType.equals("DataControl")){	       
		LObjDataControl dc = LObjDataControl.makeNew();
		newObj = (LabObject)dc.dict;
	    }
	    if(newObj != null){
		TreeNode curNode = treeControl.getSelected();
		TreeNode parent = treeControl.getSelectedParent();
		if(curNode == null) return;
		newObj.name = "New" + newIndex;
		newIndex++;
		treeModel.insertNodeInto((TreeNode)newObj, parent, parent.getIndex(curNode)+1);
	    }
	}
	if(command.equals("Apply")){
	    ((Dialog)(e.getSource())).hide();
	    ((org.concord.waba.extra.ui.ExtraMainWindow)(MainWindow.getMainWindow())).setDialog(null);
	}	   	
    }

    public void actionPerformed(ActionEvent e)
    {
	String command;
	System.out.println("Got action: " + e.getActionCommand());

	if(e.getSource() == lObjView){
	    if(e.getActionCommand().equals("Done")){
		lObjView.close();
		remove(lObjView);
		add(me);
	    }
	} else if(e.getSource() == menu){
	    if(e.getActionCommand().equals("*Pager View")){
		dict.viewType = dict.PAGING_VIEW;
		if(container != null){
		    container.reload(this);
		}
	    }

	}
    }

    public void addViewContainer(LObjViewContainer vc)
    {
	container = vc;
	vc.addMenu(this, menu);
    }

    public void showPage(LabObjectView page)
    {
	if(page == null) return;

	remove(me);
	if(container != null){
	    container.delMenu(this, menu);
	}
	page.layout(true, true);
	page.setRect(x,y,width,height);
	page.addViewContainer(this);
	add(page);
	// do I need this
	// repaint();
    }

    public void addMenu(LabObjectView source, org.concord.waba.extra.ui.Menu menu)
    {
	if(container != null) container.addMenu(this, menu);
    }
    
    public void delMenu(LabObjectView source, org.concord.waba.extra.ui.Menu menu)
    {
	if(container != null) container.delMenu(this, menu);
    }

    public void done(LabObjectView source)
    {
	if(source == lObjView){
	    lObjView.close();
	    remove(lObjView);
	    add(me);
	    if(container != null) container.addMenu(this, menu);
	    lObjView = null;
	}
    }

    public void reload(LabObjectView source)
    {
	if(source == lObjView){
	    LabObject obj = source.getLabObject();
	    lObjView.close();
	    remove(lObjView);
	    
	    lObjView = obj.getView(editStatus);
	    lObjView.layout(true, true);
	    lObjView.setRect(x,y,width,height);
	    lObjView.addViewContainer(this);
	    add(lObjView);
	}
    }

    public LObjDictionary getDict()
    {
	return dict;
    }

    public void close()
    {
	if(showName){
	    dict.name = nameEdit.getText();
	}
	
	if(container != null)  container.delMenu(this,menu);

	// Commit ???
	// Store ??
    }

}
