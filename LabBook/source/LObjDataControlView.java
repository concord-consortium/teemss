import graph.*;
import waba.ui.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;


public class LObjDataControlView extends LabObjectView
    implements ActionListener, DialogListener
{
    LObjDataControl dc;
    LObjGraphView gv;

    Label nameLabel = null;
    Edit nameEdit = null;

    Button doneButton = null;

    int gt_height = 40;
    GraphTool gt = null;

    org.concord.waba.extra.ui.Menu menu = new org.concord.waba.extra.ui.Menu("Probe");

    public LObjDataControlView(LObjDataControl dc)
    {
	menu.add("Calibrate...");
	menu.addActionListener(this);

	this.dc = dc;
	lObj = dc;
    }

    public void dialogClosed(DialogEvent e)
    {
	System.out.println("Got closed");
    }

    public void layout(boolean sDone, boolean sName)
    {
	if(didLayout) return;
	didLayout = true;

	showDone = sDone;
	showName = sName;

	if(showName){
	    nameEdit = new Edit();
	    nameEdit.setText(dc.dict.name);
	    nameLabel = new Label("Name");
	    add(nameLabel);
	    add(nameEdit);
	} 

	if(showDone){
	    doneButton = new Button("Done");
	    add(doneButton);
	} 

	gv = (LObjGraphView)dc.graph.getView(false);
	gv.layout(false,false);
	add(gv);
    }


    public void setRect(int x, int y, int width, int height)
    {
	super.setRect(x,y,width,height);
	if(!didLayout) layout(false, false);

	int curY = 0;
	int gHeight = height;
	if(showName){
	    nameLabel.setRect(1, 1, 30, 15);
	    nameEdit.setRect(31, 1, 80, 15);
	    curY = 16;
	    gHeight -= 16;
	}

	if(showDone){
	    doneButton.setRect(width-30,height-15,30,15);
	    gHeight -= 16;
	}

	if(gHeight <= 160){
	    gt_height = 20;
	}
         
	gv.setRect(0, curY+gt_height, width, gHeight-gt_height);

	gt = new GraphTool(gv.av, dc.probeId, "C", 
			   width, gt_height);

	gt.setPos(0, curY);
	add(gt);

    }

    public void actionPerformed(ActionEvent e)
    {
	String command;
	System.out.println("Got action: " + e.getActionCommand());

	if(e.getSource() == menu){
	    if(e.getActionCommand().equals("Calibrate...")){
		gt.stop();
		gt.curProbe.calibrateMe((ExtraMainWindow)(MainWindow.getMainWindow()), this);

		System.out.println("Callllll");
	    }

	}
    }

    public void addViewContainer(LObjViewContainer vc)
    {
	container = vc;
	vc.addMenu(this, menu);
    }

    public void close()
    {
	System.out.println("Got close in graph");
	if(showName){
	    dc.dict.name = nameEdit.getText();
	}
	gv.close();
	if(container != null)  container.delMenu(this,menu);

    }

    public void onEvent(Event e)
    {
	if(e.target == doneButton &&
	   e.type == ControlEvent.PRESSED){
	    if(container != null){
		container.done(this);
	    }	    
	}
    }
}
