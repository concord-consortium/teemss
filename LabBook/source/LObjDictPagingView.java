package org.concord.LabBook;

import waba.ui.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import extra.ui.*;

public class LObjDictPagingView extends LabObjectView 
    implements ActionListener, ViewContainer
{
    LabObjectView lObjView = null;

    int newIndex = 0;

    LObjDictionary dict;
 
    Button doneButton = new Button("Done");
    Button backButton = new Button("<-");
    Button nextButton = new Button("->");
    Label statusLabel = new Label("* of 0", Label.CENTER);
    Button delButton = new Button("Del");

    TreeNode [] childArray;

    int index;

    LabObject curObj;
    boolean editStatus = true;
    int defaultNewObjectType = LabObject.DOCUMENT;

    org.concord.waba.extra.ui.Menu menu = new org.concord.waba.extra.ui.Menu("View");

    public LObjDictPagingView(ViewContainer vc, LObjDictionary d, boolean edit)
    {
	super(vc);

	dict = d;
	lObj = dict;
	childArray = dict.childArray();
	index = 0;
	menu.add("Tree View");
	menu.addActionListener(this);
	editStatus = edit;

	if(vc != null) vc.getMainView().addMenu(this, menu);
	
    }

    public void layout(boolean sDone)
    {
	if(didLayout) return;
	didLayout = true;

	showDone = sDone;

	if(showDone){
	    add(doneButton);
	} 

	add(nextButton);
	add(backButton);
	add(statusLabel);
	if(editStatus) add(delButton);
	
	showObject();
    }

    public void setRect(int x, int y, int width, int height)
    {
	super.setRect(x,y,width,height);
	if(!didLayout) layout(false);
	
	backButton.setRect(0, height-15, 15, 15);
	statusLabel.setRect(15, height-15, 40, 15);
	nextButton.setRect(55, height-15, 15, 15);
	if(editStatus) delButton.setRect(100, height-15, 25, 15);
	if(showDone){
	    doneButton.setRect(130, height-15, 30, 15);
	}
	if(lObjView != null){
	    lObjView.setRect(0,0,width, height-15);
	    Debug.println("Adding lObjView at: " + x + ", " + y +
			       ", " + width + ", " + height);
	}
    }

    public void onEvent(Event e)
    {
	if(e.type == ControlEvent.PRESSED){
	    TreeNode curNode;
	    TreeNode parent;

	    LabObject newObj;
	    if(e.target == nextButton){
		index++;
		if(!editStatus && 
		   (index >= childArray.length)){
		    // If edit is turned off make sure they can't add objects
		    index = childArray.length - 1;
		} else if(index < childArray.length){
		    repaint();
		} else {
		    if(dict.hasObjTemplate){
			newObj = dict.getObjTemplate().copy();
		    } else {
			newObj = LabObject.getNewObject(defaultNewObjectType);
		    }
		    newObj.name = "New" + newIndex;
		    newIndex++;
		    dict.insert(newObj, index);
		    childArray = dict.childArray();
		    if(index >= childArray.length) index = childArray.length - 1;
		}
		showObject();
		
	    } else if(e.target == delButton){
		if(childArray == null) return;
		if(childArray[index] == null) return;
		dict.remove(childArray[index]);
		childArray = dict.childArray();
		if(index >= childArray.length) index = childArray.length - 1;
		showObject();
	    } else if(e.target == backButton){
		index--;
		if(index < 0) index = 0;
		showObject();
	    } else if(e.target == doneButton){
		if(container != null){
		    container.done(this);
		}
	    }	    
	}
    }

    public void actionPerformed(ActionEvent e)
    {
	if(e.getSource() == menu){
	    if(e.getActionCommand().equals("Tree View") &&
	       container != null){
		dict.viewType = dict.TREE_VIEW;
		container.reload(this);
	    }

	}		
    }

	public MainView getMainView()
	{
		if(container != null) return container.getMainView();
		else return null;
	}

    public void done(LabObjectView source) {}

    public void reload(LabObjectView source)
    {
	if(source == lObjView){
	    LabObject obj = source.getLabObject();
	    lObjView.close();
	    remove(lObjView);
	    
	    lObjView = obj.getView(this, editStatus);
	    lObjView.layout(false);
	    lObjView.setRect(x,y,width,height-15);
	    add(lObjView);
	}
    }

    public void showObject()
    {
	if(childArray == null) return;
	if(index < 0 || index >= childArray.length) return;

	TreeNode curNode = childArray[index];
	LabObject obj = null;
	if(index == 0 &&
	   curNode.toString().equals("..empty..")){
	    if(dict.hasObjTemplate){
		obj = dict.getObjTemplate().copy();
	    } else {
		obj = LabObject.getNewObject(defaultNewObjectType);
	    }
	    
	    obj.name = "New" + newIndex;
	    newIndex++;
	    dict.insert(obj, 0);
	    childArray = dict.childArray();
	} else {
	    obj = dict.getObj(curNode);
	    if(obj == null) Debug.println("showPage: object not in database: " +
					  ((LabObjectPtr)curNode).debug());
	    
	} 

	if(obj == null) return;
       	if(obj == curObj) return;

	if(lObjView != null){
	    lObjView.close();
	    remove(lObjView);
	}
		
	curObj = obj;

	statusLabel.setText((index + 1) + " of " + childArray.length);

	lObjView = obj.getView(this, editStatus);

	lObjView.layout(false);
	if(width > 0 || height > 15){
	    lObjView.setRect(0,0,width,height-15);
	    Debug.println("Adding lObjView at: " + x + ", " + y +
			       ", " + width + ", " + height);
	}
	add(lObjView);

	// do I need this
	// repaint();
    }

    public void close()
    {
	if(container != null)  container.getMainView().delMenu(this,menu);

	if(lObjView != null){
	    lObjView.close();
	}

	super.close();
	// Commit ???
	// Store ??
    }

}
