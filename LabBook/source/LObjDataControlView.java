package org.concord.LabBook;

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

    Menu menu = new Menu("Probe");

    public LObjDataControlView(LObjViewContainer vc, LObjDataControl dc)
    {
	super(vc);

	menu.add("Settings...");
	menu.add("Save Data...");
	menu.addActionListener(this);
	if(vc != null){
	    vc.addMenu(this, menu);
	}

	this.dc = dc;
	lObj = dc;
    }

    public void dialogClosed(DialogEvent e)
    {
	Debug.println("Got closed");
    }

    public void layout(boolean sDone)
    {
	if(didLayout) return;
	didLayout = true;

	showDone = sDone;

	if(showDone){
	    doneButton = new Button("Done");
	    add(doneButton);
	} 

	gv = (LObjGraphView)dc.getObj(0).getView(null, false);
	gv.layout(false);
	add(gv);
    }


    public void setRect(int x, int y, int width, int height)
    {
	super.setRect(x,y,width,height);
	if(!didLayout) layout(false);

	int curY = 0;
	int gHeight = height;

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
	Debug.println("Got action: " + e.getActionCommand());

	if(e.getSource() == menu){
	    if(e.getActionCommand().equals("Settings...")){
		gt.stop();
		gt.curProbe.calibrateMe((ExtraMainWindow)(MainWindow.getMainWindow()), this);

		Debug.println("Callllll");
	    } else if(e.getActionCommand().equals("Save Data...")){
		LObjDataSet dSet = LObjDataSet.makeNewDataSet();
		dSet.setDataViewer(gv.graph);
		for(int i=0; i<gt.bins.getCount(); i++){
		    dSet.addBin((Bin)gt.bins.get(i));
		}
		LObjDictionary dataDict = (LObjDictionary)dc.getObj(1);
		if(dataDict != null){
		    dataDict.add(dSet.dict);
		    dSet.writeChunks();
		} else {
		    // for now it is an error
		    // latter it should ask the user for the name
		}
	    }
	}
    }

    public void close()
    {
	Debug.println("Got close in graph");
	gv.close();
	if(container != null)  container.delMenu(this,menu);
	gt.onExit();

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
