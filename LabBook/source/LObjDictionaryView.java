package org.concord.LabBook;

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
    GridContainer buttons = null; 
 
    Button doneButton = new Button("Done");
    Button newButton = new Button("New");
    Button openButton = new Button("Open");
    Button editButton = new Button("Edit");
    Button delButton = new Button("Del");

    Menu editMenu = new Menu("Edit");
    Menu viewMenu = new Menu("View");

    PropContainer creationProps = new PropContainer();
    PropContainer subCreateProps = creationProps.createSubContainer("Sub");
    String [] creationTypes = {"Dictionary", "Document", "Questions", "Data Collector", "Drawing"};
    PropObject newObjType = new PropObject("Type", creationTypes);

    boolean editStatus = false;

    public LObjDictionaryView(LObjViewContainer vc, LObjDictionary d)
    {
	super(vc);
	dict = d;
	lObj =dict;
	add(me);
	editMenu.add("Rename...");
	viewMenu.add("Paging View");
	editMenu.addActionListener(this);
	viewMenu.addActionListener(this);

	if(vc != null){
	    vc.addMenu(this, editMenu);
	    vc.addMenu(this, viewMenu);
	}
   	
	creationProps.addProperty(newObjType, "Sub");
    }

    public void layout(boolean sDone)
    {
	if(didLayout) return;
	didLayout = true;

	showDone = sDone;

	treeModel = new TreeModel(dict);
	treeControl = new TreeControl(treeModel);
	treeControl.showRoot(false);
	me.add(treeControl);

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
	if(!didLayout) layout(false);
	
	me.setRect(0,0, width, height);
	treeControl.setRect(1,1,width-2, height-22);
	Debug.println("Setting grid size: " + width + " " + height);
	buttons.setRect(0,height-20,width,20);
    }

    Dialog newDialog = null;

    public void onEvent(Event e)
    {
	if(e.type == ControlEvent.PRESSED){
	    TreeNode curNode;
	    TreeNode parent;

	    LabObject newObj;
	    if(e.target == newButton){
		String [] buttons = {"Cancel", "Create"};
		newDialog = Dialog.showInputDialog(this, "Create", "Create a new Object",
						      buttons,Dialog.CHOICE_INP_DIALOG, creationTypes);

	    } else if(e.target == delButton){
		curNode = treeControl.getSelected();
		if(curNode == null) return;
		parent = treeControl.getSelectedParent();
		treeModel.removeNodeFromParent(curNode, parent);
	    } else if(e.target == openButton){
		curNode = treeControl.getSelected();
		showPage((LabObject)curNode, false);
	    } else if(e.target == editButton){
		curNode = treeControl.getSelected();
		showPage((LabObject)curNode, true);
	    } else if(e.target == doneButton){
		if(container != null){
		    container.done(this);
		}
	    }	    
	}
    }

    Dialog rnDialog = null;

    public void dialogClosed(DialogEvent e)
    {
	String command = e.getActionCommand();
	if(e.getSource() == newDialog){
	    if(command.equals("Create")){
		String objType = (String)e.getInfo();
		LabObject newObj = null;
		if(objType.equals("Dictionary")){
		    newObj = new LObjDictionary();
		} else if(objType.equals("Document")){
		    newObj = new LObjDocument();
		} else if(objType.equals("Questions")){
		    newObj = LObjQuestion.makeNewQuestionSet();

		} else if(objType.equals("Data Collector")){	       
		    LObjDataControl dc = LObjDataControl.makeNew();
		    newObj = dc.dict;
		    dc.dict.hideChildren = true;
		} else if(objType.equals("Drawing")){
		    newObj = new LObjDrawing();
		}
		if(newObj != null){
		    TreeNode curNode = treeControl.getSelected();
		    TreeNode parent = treeControl.getSelectedParent();
		    newObj.name = "New" + newIndex;
		    newIndex++;
		    if(curNode == null){
			treeModel.insertNodeInto((TreeNode)newObj, treeModel.getRoot(), treeModel.getRoot().getChildCount());
		    } else {
			treeModel.insertNodeInto((TreeNode)newObj, parent, parent.getIndex(curNode)+1);
		    }
		}
	    }
	} else if(e.getSource() == rnDialog){
	    if(command.equals("Ok")){
		LabObject selObj = (LabObject)treeControl.getSelected();
		if(selObj != null){
		    selObj.name = (String)e.getInfo();
		    treeControl.repaint();
		    // repaint??
		} else {
		    dict.name = (String)e.getInfo();
		    treeControl.repaint();
		}
	    }
	}		   
    }

    public void actionPerformed(ActionEvent e)
    {
	String command;
	Debug.println("Got action: " + e.getActionCommand());

	if(e.getSource() == lObjView){
	    if(e.getActionCommand().equals("Done")){
		lObjView.close();
		remove(lObjView);
		add(me);
	    }
	} else if(e.getSource() == viewMenu){
	    if(e.getActionCommand().equals("Paging View")){
		dict.viewType = dict.PAGING_VIEW;
		if(container != null){
		    container.reload(this);
		}
	    }
	} else if(e.getSource() == editMenu){	    
	    if(e.getActionCommand().equals("Rename...")){
		LabObject selObj = (LabObject)treeControl.getSelected();
		String [] buttons = {"Cancel", "Ok"};
		if(selObj != null){
		    rnDialog = Dialog.showInputDialog(this, "Rename Object", "Old Name was " + selObj.name,
						      buttons,Dialog.EDIT_INP_DIALOG);
		} else {
		    rnDialog = Dialog.showInputDialog(this, "Rename Parent", "Old Name was " + dict.name,
						      buttons,Dialog.EDIT_INP_DIALOG);
		}		    
	    }
	}
    }

    public void showPage(LabObject obj, boolean edit)
    {
	if(obj == null) return;

	delMenu(this, viewMenu);
	delMenu(this, editMenu);

	editStatus = edit;
	lObjView = obj.getView(this, edit);

	if(lObjView == null){
	    addMenu(this, editMenu);
	    addMenu(this, viewMenu);
	    return;
	}
	remove(me);
        lObjView.layout(true);
	lObjView.setRect(x,y,width,height);
	add(lObjView);

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
	    addMenu(this, editMenu);
	    addMenu(this, viewMenu);
	    lObjView = null;
	}
	//	System.gc();
    }

    public void reload(LabObjectView source)
    {
	if(source == lObjView){
	    LabObject obj = source.getLabObject();
	    lObjView.close();
	    remove(lObjView);
	    
	    lObjView = obj.getView(this, editStatus);
	    lObjView.layout(true);
	    lObjView.setRect(x,y,width,height);
	    add(lObjView);
	}
    }

    public LObjDictionary getDict()
    {
	return dict;
    }

    public void close()
    {
	
	if(container != null){
	    container.delMenu(this,editMenu);
	    container.delMenu(this,viewMenu);
	}

	// Commit ???
	// Store ??
    }

}
