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

    Menu pMenu = new Menu("Probe");
    Menu gMenu = new Menu("Graph");

    public LObjDataControlView(LObjViewContainer vc, LObjDataControl dc)
    {
	super(vc);

	pMenu.add("Properties...");
	pMenu.add("Save Data...");
	pMenu.addActionListener(this);
	gMenu.add("Change Axis...");
	gMenu.addActionListener(this);
	if(vc != null){
	    vc.addMenu(this, pMenu);
	    vc.addMenu(this, gMenu);
	}

	this.dc = dc;
	lObj = dc;
    }

    public void dialogClosed(DialogEvent e)
    {
	Debug.println("Got closed");
	dc.portId = dc.getProbe().getInterfacePort();
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
	
	gt = new GraphTool(gv.av, dc, "C", width, gt_height);
	
	gt.setPos(0, curY);
	add(gt);
	
    }

    public void actionPerformed(ActionEvent e)
    {
	String command;
	Debug.println("Got action: " + e.getActionCommand());

	if(e.getSource() == pMenu){
	    if(e.getActionCommand().equals("Properties...")){
		gt.stop();
		dc.getProbe().calibrateMe((ExtraMainWindow)(MainWindow.getMainWindow()), this, dc.interfaceId);

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
	} else if(e.getSource() == gMenu){
	    if(e.getActionCommand().equals("Change Axis...")){
		gt.showAxisProp();
	    } 
	}
    }

    public void close()
    {
	Debug.println("Got close in graph");
	gv.close();
	if(container != null){
	    container.delMenu(this,pMenu);
	    container.delMenu(this,gMenu);
	}

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
