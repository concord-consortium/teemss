import waba.ui.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import extra.util.*;
import extra.ui.*;

public class LObjOutputSetChoice extends LabObjectView
{
    LObjOutputSet output;
    

    TreeControl treeControl;
    TreeModel treeModel;

    GridContainer buttons = null; 
    Button cancelButton = new Button("Cancel");
    Button newButton = new Button("New");
    Button openButton = new Button("Open");

    public LObjOutputSetChoice(LObjOutputSet o)
    {
	lObj = (LabObject)o;
	output = o;
    }

    public void layout(boolean sDone, boolean sName)
    {
	if(didLayout) return;
	didLayout = true;

	showDone = sDone;
	showName = sName;

	treeModel = new TreeModel(output.outputDict);
	treeControl = new TreeControl(treeModel);
	treeControl.showRoot(false);
	add(treeControl);

	buttons = new GridContainer(3,1);
	buttons.add(newButton, 0, 0);
	buttons.add(openButton, 1, 0);
	buttons.add(cancelButton, 2,0);

	add(buttons);
    }
    

    public void setRect(int x, int y, int width, int height)
    {
	super.setRect(x,y,width,height);
	if(!didLayout) layout(false, false);
	
	treeControl.setRect(0,0, width, height-20);
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
		output.skipChoice = true;
		output.setCurOutput(null);
		// reload ...
		if(container != null){
		    container.reload(this);
		}
	    }else if(e.target == openButton){
		curNode = treeControl.getSelected();
		if(curNode == null) return;
		output.setCurOutput((LabObject)curNode);
		output.skipChoice = true;
		// reload...
		if(container != null){
		    container.reload(this);
		}
	    } else if(e.target == cancelButton){
		if(container != null){
		    container.done(this);
		}
	    }	    
	}
    }

    public void close(){}

}
