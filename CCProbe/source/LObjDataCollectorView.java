package org.concord.CCProbe;

import graph.*;
import waba.ui.*;
import waba.fx.*;
import waba.util.*;
import extra.util.*;
import extra.ui.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.probware.probs.*;
import org.concord.waba.extra.probware.*;
import org.concord.LabBook.*;
	
public class LObjDataCollectorView extends LabObjectView
    implements ActionListener, ViewContainer, LabObjListener,
			   GraphTool
{
    LObjDataCollector dc;
    LObjGraphView gv;
    LObjDictionary dataDict = null;
    LObjGraph graph = null;
	Vector rootSources = new Vector();

    Label nameLabel = null;
    Edit nameEdit = null;

    int gt_height = 40;

    Menu menu = new Menu("Edit");

    ToggleButton collectButton;
    Button doneB;

	Label title1Label, title2Label;
    String title1 = "";
	String  title2 = "";

	String [] fileStrings = {"Beam", "Save Data..", "Export Data.."};
	String [] palmFileStrings = {"Save Data.."};

    public LObjDataCollectorView(ViewContainer vc, LObjDataCollector dc, 
							   LObjDictionary curDict, LabBookSession session)
    {
		super(vc, (LabObject)dc, session);

		/*
		for(int i=0; i<60; i++){
			mem[i] = new byte[1000];
		}
		*/

		graph = (LObjGraph)dc.getObj(0, session);

		menu.add("Graph Properties..");
		
		Vector tempRootSources;
		for(int i=0; i<graph.numDataSources; i++){
			DataSource ds = (DataSource)graph.getDataSource(i, session);
			tempRootSources = new Vector();
			ds.getRootSources(tempRootSources, session);
			for(int j=0; j<tempRootSources.getCount(); j++){
				int index = rootSources.find(tempRootSources.get(j));
				if(index < 0){
					if(tempRootSources.get(j) instanceof LObjProbeDataSource){
						rootSources.add(tempRootSources.get(j));
					}
				}
			}
		}
		if(rootSources.getCount() > 0){
			menu.add("Probe Properties..");
		}

		menu.add("Save Profile..");
		menu.addActionListener(this);

		this.dc = dc;
		dataDict = curDict;

    }

	public void addMenus()
	{		
		if(container != null){
			container.getMainView().addMenu(this, menu);
			if(waba.sys.Vm.getPlatform().equals("PalmOS")){
				fileStrings = palmFileStrings;
			}
			container.getMainView().addFileMenuItems(fileStrings, this);
		}
	}

	public void delMenus()
	{
		if(container != null){
			container.getMainView().delMenu(this,menu);
			menu.removeActionListener(this);
			container.getMainView().removeFileMenuItems(fileStrings, this);
		}		
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

	/*
	 *  This is very messy 
	 * we might get stop called several time
	 *  when we stop the data sources
	 * this might trigger a listerner to the 
	 * data source to post a stop event
	 * to us.
	 *  This is a mess.  really
	 *  I'll fix it soon
	 */
	boolean stopping = false;

    void stop(boolean notifyGraph)
    {
		if(stopping) return;
		stopping = true;
		collectButton.setSelected(false);
		collectButton.repaint();
		if(notifyGraph){
			graph.stopAll();
		}
		stopping = false;
    }

	public void graphToolAction(String tool)
	{
		if(tool.equals("Zero Force Probe")){
			if(rootSources != null && rootSources.getCount() > 0 &&
			   rootSources.get(0) instanceof LObjProbeDataSource){
				LObjProbeDataSource pds = 
					(LObjProbeDataSource)rootSources.get(0);
				if(pds.getName() == "Force"){
					pds.zeroForce(session);
				}
			}
		}
	}

    public void layout(boolean sDone)
    {
		if(didLayout) return;
		didLayout = true;

		showDone = sDone;

		collectButton = new ToggleButton("Collect", false);
		add(collectButton);

		graph.addLabObjListener(this);

		gv = (LObjGraphView)graph.getView(this, false, dataDict, session);
		gv.showTitle(false);

		if(rootSources != null && rootSources.getCount() > 0 &&
		   rootSources.get(0) instanceof LObjProbeDataSource){
			LObjProbeDataSource pds = (LObjProbeDataSource)rootSources.get(0);
			if(pds.getName() == "Force"){
				gv.addTool(this, "Zero Force Probe");				
			}
		}

		gv.layout(false);

		if(title1 == null) title1 = "";
		title1Label = new Label(title1);
		add(title1Label);

		String t2 = graph.getTitle(session);
		if(t2 == null) t2 = "";
		title2Label = new Label(t2);
		add(title2Label);

		doneB = new Button("Done");
		if(showDone){
			add(doneB);
		}

		add(gv);
    }

	public void labObjChanged(LabObjEvent e)
	{
		if(e.getObject() == graph &&
		   graph != null){
			setTitle2(graph.getTitle(session));		
			// this used to happen here but I don't think
			// it is needed
			// dc.store();  // maybe
		}
	}

    public void setRect(int x, int y, int width, int height)
    {
		super.setRect(x,y,width,height);
		if(!didLayout) layout(false);

		//		addTimer(100);
	  
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

		setTitle1(dc.getName());		
    }

    public void actionPerformed(ActionEvent e)
    {
		String command;
		Debug.println("Got action: " + e.getActionCommand());

		if(e.getSource() == menu){
			if(e.getActionCommand().equals("Probe Properties..")){
				stop(true);

				LObjProbeDataSource pds = 
					(LObjProbeDataSource)rootSources.get(0);
				pds.showProp();

				Debug.println("Callllll");
			} else if(e.getActionCommand().equals("Graph Properties..")){
				graph.showProp(session);
			} else if(e.getActionCommand().equals("Save Profile...")){
				/*
				LObjDocument dProf = DefaultFactory.createDocument();
				String text = "";
				for(int i=0; i < gv.curPtime; i++){
					for(int j=0; j < gv.pTimes[i].length; j++){
						text += gv.pTimes[i][j] + " ";		
					}
					text += "\n";
				}
				dProf.setText(text);
				dProf.name = "Profile";
		
				if(dataDict != null){
					dataDict.add(dProf);
					dataDict.store();
					dProf.store();
				} 
				*/
			}
		} else {
			if(e.getActionCommand().equals("Save Data..")){
				LObjDictionary gDataDict = graph.getDataDict(session);
				if(gDataDict == null) gDataDict = dataDict;
				graph.saveCurData(gDataDict, session);
			} else if(e.getActionCommand().equals("Export Data..")){
				graph.exportCurData(session);
			}
		}
    }

    public void close()
    {
		Debug.println("Got close in graph");
		stop(true);	

		// need to make sure this unregisters data sources!!
		gv.close();

		if(graph != null) graph.delLabObjListener(this);

		super.close();
    }

	//	int curMemPos = 0;
	// byte [][] mem = new byte [200][];
    public void onEvent(Event e)
    {
		/*
		if(e.target == this && e.type == ControlEvent.TIMER){
			mem[curMemPos] = new byte[1000];
			curMemPos++;
			repaint();
		}
		*/
		if(e.target == gv){			
			if(e.type == 1000){
				// This must have come from the graph so
				// I don't need to notify it
				stop(false);
			} 		
		} else 	if(e.type == ControlEvent.PRESSED){
			Control target = (Control)e.target;
			int index;
			if(target == collectButton && collectButton.isSelected()){
				// need to tell the GraphView to start
				graph.startAll();
			} else if(target == collectButton && ! collectButton.isSelected()){
				// need to tell the GraphView to stop
				stop(true);
			} else if(target == doneB){
				// let our parent know we've been done'd
				if(container != null){
					container.done(this);
				}	    
			}
		} else if(e.target == title2Label && 
				  e.type == PenEvent.PEN_DOWN){
			graph.showProp(session);
		}
    }

	/*
	public void onPaint(Graphics g){
		g.setColor(255,255,255);
		g.fillRect(0,0,100,30);
		g.setColor(0,0,0);
		g.drawText("count: " + curMemPos,0,0);
	}
	*/

	public MainView getMainView()
	{
		if(container != null) return container.getMainView();
		return null;
	}

    public void reload(LabObjectView source){}

    public void done(LabObjectView source) {}

	public int getPreferredWidth(){
		return 100;
	}

	public int getPreferredHeight(){
		return 100;
	}

}
