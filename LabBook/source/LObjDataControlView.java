package org.concord.LabBook;

import graph.*;
import waba.ui.*;
import extra.util.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.probware.probs.*;


public class LObjDataControlView extends LabObjectView
    implements ActionListener, DialogListener, LObjViewContainer
{
    LObjDataControl dc;
    LObjGraphView gv;
    LObjDictionary dataDict = null;

    Label nameLabel = null;
    Edit nameEdit = null;

    Button doneButton = null;

    int gt_height = 40;
    GraphTool gt = null;

    Menu menu = new Menu("Probe");

    public LObjDataControlView(LObjViewContainer vc, LObjDataControl dc, 
			       LObjDictionary curDict)
    {
	super(vc);

	menu.add("Properties...");
	menu.add("Save Data...");
	menu.add("Export Data");
	menu.add("Save Profile...");
	menu.addActionListener(this);
	if(vc != null){
	    vc.addMenu(this, menu);
	}

	this.dc = dc;
	lObj = dc;
	dataDict = curDict;
    }

    public void dialogClosed(DialogEvent e)
    {
	Debug.println("Got closed");
	dc.portId = dc.getProbe().getInterfacePort();
	LObjGraph graph = (LObjGraph)dc.getObj(0);
	CCProb p = dc.getProbe();
	graph.name = p.getName() + "(";
	PropObject [] props = p.getProperties();
	int i;
	for(i=0; i < props.length-1; i++){
	    graph.name += props[i].getName() + "- " + props[i].getValue() + "; ";
	}
	graph.name += props[i].getName() + "- " + props[i].getValue() + ")";

	gv.titleLabel.setText(graph.name);
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

	LObjGraph graph = (LObjGraph)dc.getObj(0);
	if(graph.name.equals("Graph")){
	    CCProb p = dc.getProbe();
	    graph.name = p.getName() + "(";
	    PropObject [] props = p.getProperties();
	    int i;
	    for(i=0; i < props.length-1; i++){
		graph.name += props[i].getName() + "- " + props[i].getValue() + "; ";
	    }
	    graph.name += props[i].getName() + "- " + props[i].getValue() + ")";
	}
	gv = (LObjGraphView)graph.getView(this, false);
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

	if(e.getSource() == menu){
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
		LObjGraph graph = (LObjGraph)dc.getObj(0);
		dSet.dict.name = graph.name;
		if(dataDict != null){
		    dataDict.add(dSet.dict);
		    dSet.writeChunks();
		} else {
		    // for now it is an error
		    // latter it should ask the user for the name
		}
	    } else if(e.getActionCommand().equals("Export Data")){
		if(gt.bins != null ||
		   gt.bins.getCount() > 0){
		    if(gv.av.curView instanceof GraphViewLine){
			LabBookFile.export((Bin)gt.bins.get(0), null);
		    } else {
			LabBookFile.export((Bin)gt.bins.get(0), gv.av.lGraph.annots);
		    }

		}
	    }else if(e.getActionCommand().equals("Save Profile...")){
		LObjDocument dProf = new LObjDocument();
		dProf.text = "";
		for(int i=0; i < gt.curPtime; i++){
		    for(int j=0; j < gt.pTimes[i].length; j++){
			dProf.text += gt.pTimes[i][j] + " ";		
		    }
		    dProf.text += "\n";
		}
		dProf.name = "Profile";
		
		if(dataDict != null){
		    dataDict.add(dProf);
		    dataDict.store();
		    dProf.store();
		} 
		
	    }
	} 
    }

    public void close()
    {
	Debug.println("Got close in graph");
	gv.close();
	if(container != null){
	    container.delMenu(this,menu);
	}

	gt.onExit();
	super.close();
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

    public void reload(LabObjectView source){}

    public void addMenu(LabObjectView source, org.concord.waba.extra.ui.Menu menu)
    {
	if(container != null) container.addMenu(this, menu);
    }
    
    public void delMenu(LabObjectView source, org.concord.waba.extra.ui.Menu menu)
    {
	if(container != null) container.delMenu(this, menu);
    }

    public void done(LabObjectView source) {}

    public LObjDictionary getDict(){return null;}

}
