import waba.ui.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import extra.ui.*;

public class LObjDictPagingView extends LabObjectView 
    implements ActionListener, LObjViewContainer
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

    org.concord.waba.extra.ui.Menu menu = new org.concord.waba.extra.ui.Menu("Dict");

    public LObjDictPagingView(LObjDictionary d, boolean edit)
    {
	dict = d;
	lObj = (LabObject)dict;
	childArray = dict.childArray();
	index = 0;
	menu.add("*Tree View");
	menu.addActionListener(this);
	editStatus = edit;
    }

    public void layout(boolean sDone, boolean sName)
    {
	if(didLayout) return;
	didLayout = true;

	showDone = sDone;
	showName = sName;

	if(showDone){
	    add(doneButton);
	} 

	add(nextButton);
	add(backButton);
	add(statusLabel);
	add(delButton);
	
	showObject();
    }

    public void setRect(int x, int y, int width, int height)
    {
	super.setRect(x,y,width,height);
	if(!didLayout) layout(false, false);
	
	backButton.setRect(0, height-15, 15, 15);
	statusLabel.setRect(15, height-15, 40, 15);
	nextButton.setRect(55, height-15, 15, 15);
	delButton.setRect(100, height-15, 25, 15);
	if(showDone){
	    doneButton.setRect(130, height-15, 30, 15);
	}
	if(lObjView != null){
	    lObjView.setRect(0,0,width, height-15);
	    System.out.println("Adding lObjView at: " + x + ", " + y +
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
		if(index < childArray.length){
		    repaint();
		} else {
		    if(dict.newObjectTemplate != null){
			newObj = dict.newObjectTemplate.copy();
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
	    if(e.getActionCommand().equals("*Tree View") &&
	       container != null){
		dict.viewType = dict.TREE_VIEW;
		container.reload(this);
	    }

	}		
    }

    public void addViewContainer(LObjViewContainer vc)
    {
	container = vc;
	vc.addMenu(this, menu);
    }

    public void addMenu(LabObjectView source, org.concord.waba.extra.ui.Menu menu)
    {
	if(container != null) container.addMenu(this, menu);
    }
    
    public void delMenu(LabObjectView source, org.concord.waba.extra.ui.Menu menu)
    {
	if(container != null) container.delMenu(this, menu);
    }

    public void done(LabObjectView source) {}

    public void reload(LabObjectView source)
    {
	if(source == lObjView){
	    LabObject obj = source.getLabObject();
	    lObjView.close();
	    remove(lObjView);
	    
	    lObjView = obj.getView(editStatus);
	    lObjView.layout(false, true);
	    lObjView.setRect(x,y,width,height-15);
	    lObjView.addViewContainer(this);
	    add(lObjView);
	}
    }

    public LObjDictionary getDict()
    {
	return dict;
    }

    public void showObject()
    {
	if(childArray == null) return;
	if(index < 0 || index >= childArray.length) return;


	LabObject obj = (LabObject)(childArray[index]);
	if(obj == null) return;
       	if(obj == curObj) return;

	if(lObjView != null){
	    lObjView.close();
	    remove(lObjView);
	}
		
	curObj = obj;
	if(index == 0 &&
	   obj.name.equals("..empty..")){
	    if(dict.newObjectTemplate != null){
		obj = dict.newObjectTemplate.copy();
	    } else {
		obj = LabObject.getNewObject(defaultNewObjectType);
	    }

	    obj.name = "New" + newIndex;
	    newIndex++;
	    dict.insert(obj, 0);
	    childArray = dict.childArray();
	}

	statusLabel.setText((index + 1) + " of " + childArray.length);

	lObjView = obj.getView(editStatus);

	lObjView.layout(false, true);
	if(width > 0 || height > 15){
	    lObjView.setRect(0,0,width,height-15);
	    System.out.println("Adding lObjView at: " + x + ", " + y +
			       ", " + width + ", " + height);
	}
	lObjView.addViewContainer(this);
	add(lObjView);

	// do I need this
	// repaint();
    }

    public void close()
    {
	if(container != null)  container.delMenu(this,menu);

	if(lObjView != null){
	    lObjView.close();
	}

	// Commit ???
	// Store ??
    }

}
