package org.concord.LabBook;

import graph.*;
import waba.ui.*;
import waba.fx.*;
import extra.util.*;
import extra.ui.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.probware.probs.*;
import org.concord.waba.extra.probware.*;

public class LObjDataControlView extends LabObjectView
    implements ActionListener, DialogListener, LObjViewContainer
{
    LObjDataControl dc;
    LObjGraphView gv;
    LObjDictionary dataDict = null;

    Label nameLabel = null;
    Edit nameEdit = null;

    int gt_height = 40;

    Menu menu = new Menu("Probe");

    ProbManager pm = null;
    ToggleButton collectButton;
    Button doneB;

    CCProb curProbe = null;

	Label title1Label, title2Label;
    String title1 = "";
	String  title2 = "";

    public LObjDataControlView(LObjViewContainer vc, LObjDataControl dc, 
							   LObjDictionary curDict)
    {
		super(vc);

		menu.add("Properties...");
		menu.add("Save Profile...");
		menu.addActionListener(this);
		if(vc != null){
			vc.addMenu(this, menu);
		}

		this.dc = dc;
		lObj = dc;
		dataDict = curDict;

		pm = ProbManager.getProbManager(dc.interfaceId);
    }

	public void setTitle1(String t1)
	{
		if(t1 == null) t1 = "";
		title1Label.setText(t1);
	}

	public void setTitle2(String t2)
	{
		if(t2 == null) t2 = "";
		title2Label.setText(t2);
	}

    void stop()
    {
		pm.stop();
		collectButton.setSelected(false);    
		gv.stopGraph();

		repaint();
    }
    
    public void dialogClosed(DialogEvent e)
    {
		Debug.println("Got closed");	
		gv.setDC(dc);
		dc.updateGraphProp();
		gv.updateProp();
		setTitle2(graph.title);
		dc.store();
		repaint();
    }

    LObjGraph graph = null;

    public void layout(boolean sDone)
    {
		if(didLayout) return;
		didLayout = true;

		showDone = sDone;

		collectButton = new ToggleButton("Collect", false);
		add(collectButton);

		graph = (LObjGraph)dc.getObj(0);
		dc.updateGraphProp();
		gv = (LObjGraphView)graph.getView(this, false, dataDict);
		gv.showTitle(false);
		gv.setDC(dc);

		gv.layout(false);

		title1Label = new Label(title1);
		add(title1Label);

		title2Label = new Label(graph.title);
		add(title2Label);

		doneB = new Button("Done");
		add(doneB);


		add(gv);

		curProbe = dc.getProbe();
		pm.registerProb(curProbe);
		pm.addDataListenerToProb(curProbe.getName(),gv);

    }


    public void setRect(int x, int y, int width, int height)
    {
		super.setRect(x,y,width,height);
		if(!didLayout) layout(false);

		int curY = 0;
		int gHeight = height;

		if(gHeight <= 160){
			gt_height = 22;
		}
         
		gv.setRect(0, curY+gt_height, width, gHeight-gt_height);
	
		int buttonWidth = gt_height;
		if(gt_height < 30) buttonWidth = 35;

		int xPos = 0;
        collectButton.setRect(xPos,0,buttonWidth,gt_height);
		xPos += buttonWidth+2;

		title1Label.setRect(xPos, 0, width-xPos-27, gt_height/2);
		title2Label.setRect(xPos, gt_height/2, width-xPos, gt_height/2);
		doneB.setRect(width-27, 0, 27, gt_height/2);

		setTitle1(dc.name);

    }

    public void actionPerformed(ActionEvent e)
    {
		String command;
		Debug.println("Got action: " + e.getActionCommand());

		if(e.getSource() == menu){
			if(e.getActionCommand().equals("Properties...")){
				stop();
				dc.getProbe().calibrateMe((ExtraMainWindow)(MainWindow.getMainWindow()), this, dc.interfaceId);

				Debug.println("Callllll");
			} else if(e.getActionCommand().equals("Save Profile...")){
				LObjDocument dProf = new LObjDocument();
				dProf.text = "";
				for(int i=0; i < gv.curPtime; i++){
					for(int j=0; j < gv.pTimes[i].length; j++){
						dProf.text += gv.pTimes[i][j] + " ";		
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
		if(container != null){
			container.delMenu(this,menu);
		}

		stop();	

		gv.close();

		if(curProbe != null){
			pm.unRegisterProb(curProbe);
		}
		super.close();
    }

    public void onEvent(Event e)
    {
		if(e.target == gv){
			if(e.type == 1000){
				stop();
			} else if(e.type == 1001){
				setTitle2(gv.graph.title);
			}			
		} else 	if(e.type == ControlEvent.PRESSED){
			Control target = (Control)e.target;
			int index;
			if(target == collectButton && collectButton.isSelected()){
				// need to tell the GraphView to start
				gv.startGraph();
				pm.start();
			} else if(target == collectButton && ! collectButton.isSelected()){
				// need to tell the GraphView to stop
				stop();
			} else if(target == doneB){
				// let our parent know we've been done'd
				if(container != null){
					container.done(this);
				}	    
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
