package org.concord.CCProbe;

import graph.*;
import waba.ui.*;
import waba.util.*;
import waba.fx.*;
import waba.sys.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.util.*;
import org.concord.waba.extra.probware.*;
import org.concord.waba.extra.probware.probs.*;
import extra.util.*;
import org.concord.LabBook.*;

class TimeBin implements DecoratedValue
{
    float time = 0f;
	CCUnit unit = CCUnit.getUnit(CCUnit.UNIT_CODE_S);

    public String getLabel(){return "Time";}
    public float getValue(){return time;}
    public void setValue(float t){time = t;}
    public Color getColor(){return null;}
    public float getTime(){return 0f;}
	public CCUnit getUnit(){return unit;}
}

public class LObjGraphView extends LabObjectView
    implements ActionListener, LabObjListener
{
    LObjGraph graph;
    AnnotView av = null;

	Choice viewChoice = null;
	LineGraphMode lgm = null;
	Button addMark = null;
	Choice toolsChoice = null;
	Button notes = null;
	Button clear = null;

    Button doneButton = null;
    Label titleLabel = null;

    Menu menu = new Menu("Edit");
	int curViewIndex = 1;

    TimeBin timeBin = new TimeBin();

    DigitalDisplay dd;
	DecoratedValue curAnnot = null;

	int dd_height = 20;

	LObjDictionary dataDict;
	boolean instant = false;

	String [] fileStrings = {"Save Data..", "Export Data.."};
	String [] palmFileStrings = {"Save Data.."};

	public static String TOOL_DEL_MARK_NAME = "Delete Mark";
	public static String TOOL_ZOOM_SELECT_NAME =  "Zoom";
	public static String TOOL_AUTO_ZOOM_NAME = "Auto Resize";
	public static String TOOL_ANNOT_MARK_NAME = "Annotate Mark";

    public LObjGraphView(ViewContainer vc, LObjGraph g, LObjDictionary curDict)
    {
		super(vc);

		dataDict = curDict;

		menu.add("Graph Properties..");
		menu.addActionListener(this);

		graph = g;
		lObj = g;	

		graph.addLabObjListener(this);

    }

	public void labObjChanged(LabObjEvent e)
	{
		if(e.getType() == 0){
			if(e.getObject() == graph){
				if(av != null) av.update();
			}
		} else {
			// curGS change
			GraphSettings gs = graph.getCurGraphSettings();
			if(gs != null){
				av.setAxis(gs.xaxis, gs.yaxis);
			}
		}
	}

	public void addMenus()
	{
		if(container == null) return;

		container.getMainView().addMenu(this, menu);
		if(waba.sys.Vm.getPlatform().equals("PalmOS")){
			fileStrings = palmFileStrings;
		}
		container.getMainView().addFileMenuItems(fileStrings, this);
	}

	public void delMenus()
	{
		if(container != null){
			container.getMainView().delMenu(this,menu);
			container.getMainView().removeFileMenuItems(fileStrings, this);
		}
	}

	public void doInstantCollection()
	{
		instant = true;
	}		

    public void actionPerformed(ActionEvent e)
    {
		String command;
		Debug.println("Got action: " + e.getActionCommand());

		if(e.getSource() == menu){
			if(e.getActionCommand().equals("Graph Properties..")){
				graph.showProp();
			}
		} else {
			if(e.getActionCommand().equals("Save Data..")){
				graph.saveCurData(dataDict);
			} else if(e.getActionCommand().equals("Export Data..")){
				graph.exportCurData();
			}
		}
	}

    boolean sTitle = false;

    public void showTitle(boolean doIt)
    {
		sTitle = doIt;
    }

	public void setTitle(String title)
	{
		if(graph != null){
			graph.title = title;
		}

		if(titleLabel != null){
			titleLabel.setText(title);
		}
	}

    public void layout(boolean sDone)
    {
		if(didLayout) return;
		didLayout = true;

		showDone = sDone;

		if(sTitle){
			titleLabel = new Label(graph.name, Label.CENTER);
			add(titleLabel);
		}

		if(showDone){
			doneButton = new Button("Done");
			add(doneButton);
		} 

		lgm = new LineGraphMode();
		add(lgm);

		String [] viewChoices = {"Bar Graph", "Line Graph"};
		viewChoice = new Choice(viewChoices);
		viewChoice.setName("View");
		viewChoice.setSelectedIndex(1);
		//		viewChoice.setRect(0, butStart, 40, 13);
		add(viewChoice);

		addMark = new Button("Mark");

		String [] toolsChoices = {TOOL_DEL_MARK_NAME, "Toggle Scrolling", TOOL_AUTO_ZOOM_NAME, TOOL_ZOOM_SELECT_NAME};
		toolsChoice = new Choice(toolsChoices);
		toolsChoice.setName("Tools");
		add(toolsChoice);
		
		notes = new Button("Notes");
		add(notes);

		clear = new Button("Clear");
		add(clear);
    }

	void setToolBarRect(int x, int y, int width, int height)
	{
		if(width <= 160){
			viewChoice.setRect(0, y, 33, height);
			lgm.setRect(32, y, 30, height);
			addMark.setRect(32, y, 30, height);
			toolsChoice.setRect(63, y, 35, height);
			notes.setRect(100, y, 30, height); 
			clear.setRect(130, y, 30, height);
		} else {
			viewChoice.setRect(0, y, 50, height);
			lgm.setRect(50, y, 42, height);
			addMark.setRect(50, y, 42, height);
			toolsChoice.setRect(100, y, 55, height);
			notes.setRect(160, y, 30, height);
			clear.setRect(200, y, 30, height);
		}

	}
	
    public void setRect(int x, int y, int width, int height)
    {
		super.setRect(x,y,width,height);
		if(!didLayout) layout(false);

		int curY = 0;
		int gHeight = height;
		if(gHeight <= 160){
			dd_height = 10;
		}

		if(sTitle){
			titleLabel.setRect(x, curY, width, 16);
			curY += 16;
			gHeight -= 16;
		} 

		if(width <= 160){
			gHeight -= 12;
		} else {
			gHeight -= 16;
			setToolBarRect(0, curY, width, 16);
			curY += 16;
		}

		// Trying to get 10pt at dd_height = 10 
		//  and 16pt at dd_height = 20
		dd = new DigitalDisplay(new Font("Helvetica", 
										 Font.BOLD, 3*dd_height/5 + 4));		
		gHeight -= dd_height;
		if(showDone) dd.setRect(0,curY, width - 30, dd_height);
		else dd.setRect(0,curY, width, dd_height);
		dd.addBin(timeBin);
		add(dd);
		curY += dd_height;

		if(showDone){
			doneButton.setRect(width-30,0,30,15);
		}


		if(av != null){ 
			remove(av); 
			av.free();
			av = null;
		}
	
		if(graph.graphSettings == null){
			return;
		}
		
		// This is just a hack
		if(gHeight < 1) gHeight = 1;
			
		GraphSettings gs = (GraphSettings)graph.graphSettings.get(0);
		gs.init(this);

		for(int i=1; i<graph.graphSettings.getCount(); i++){
			gs = (GraphSettings)graph.graphSettings.get(i);

			// av.setAxis(xaxis, yaxis);

			gs.init(this);
		}

		gs = graph.getCurGraphSettings();
		av = new AnnotView(width, gHeight, gs.xaxis, gs.yaxis);
		av.setPos(0,curY);
		curY += gHeight;

		if(width <= 160){
			setToolBarRect(0, curY, width, 12);
			curY += 12;
		}

		add(av);

		if(instant){
			graph.startAll();
			instant = false;
		}
    }

    public void close()
    {

		/*
		  Check if the datasource is a dataset
		  Get the annots for each bin
		  clear the datasets old annots
		  add the current annots
		*/

		Debug.println("Got close in graph");

		if(av != null) av.free();
		av = null;
		graph.closeAll();
		graph.saveAllAnnots();
		
		graph.delLabObjListener(this);
		graph.store();
		super.close();
    }

	int numStarted = 0;
	public void startGraph(Bin curBin)
	{
		av.addBin(curBin);
		dd.addBin(curBin);
		numStarted++;
		av.lgView.autoScroll = true;
	}

	public void update(float time){
		av.update();			
		timeBin.setValue(time);
		dd.update();
	}

	// Right this requires the caller to call repaint()

	// This is a mess
	public void stopGraph(Bin curBin) 
	{
		numStarted--;

		av.closeBin(curBin);
		dd.removeBin(curBin);

		if(numStarted == 0){
			av.lgView.autoScroll = false;
			postEvent(new ControlEvent(1000, this));	
		}

		av.update();
	}

	public void clear(Bin curBin)
	{
		av.removeBin(curBin);
		dd.removeBin(curBin);		
	}

    public void onEvent(Event e)
    {
		if(e.target == doneButton &&
		   e.type == ControlEvent.PRESSED){
			if(container != null){
				container.done(this);
			}	    
		} else if(e.target == lgm &&
				  e.type == ControlEvent.PRESSED){
			switch(lgm.getSelectedIndex()){
			case 0:
				av.setViewMode('D');
				break;
			case 1:
				av.setViewMode('Z');
				break;
			case 2:
				av.setViewMode('A');
				break;
			}
		} else if(e.target == addMark &&
				  e.type == ControlEvent.PRESSED){
			av.addAnnot();
		} else if(e.type == 1008){
			// annotation selection change event
			Annotation selAnnot = av.getSelectedAnnot();
			// might need to remove the current probe bin to save space
			if(curAnnot != selAnnot){
				dd.removeBin(curAnnot);
			}

			curAnnot = selAnnot;

			if(curAnnot != null){
				timeBin.setValue(selAnnot.time);
				dd.addBin(curAnnot);
			}
				
			dd.update();
		} else if(e.type == 1003){
			// annotation dragged event
			dd.update();
		} else if(e.type == 1004){
			graph.showAxisProp(2);
		} else if(e.type == 1005){
			graph.showAxisProp(1);
		}else if(e.target == viewChoice){
			if(e.type == ControlEvent.PRESSED){
				int index = viewChoice.getSelectedIndex();
				av.setViewType(index);
				if(curViewIndex != index){
					curViewIndex = index;
					switch(index){
					case 0:
						remove(lgm);
						add(addMark);
						break;
					case 1:
						remove(addMark);
						add(lgm);
						break;
					}
				}
			}
		} else if(e.target == toolsChoice){
			if(e.type == ControlEvent.PRESSED){
				String toolName  = toolsChoice.getSelected();
				if(toolName.equals(TOOL_AUTO_ZOOM_NAME)){
					GraphSettings gs = graph.getCurGraphSettings();
					if(gs.calcVisibleRange()){
						float margin = (gs.maxVisY - gs.minVisY)*0.1f;
						int count=0;
						float ymin, ymax;
						while(margin == 0f && count < 4){
							ymin = gs.minVisY - (-1f)/gs.yaxis.scale;
							ymax = gs.maxVisY + (-1f)/gs.yaxis.scale;
							gs.setYValues(ymin, ymax);
							av.update();

							if(!gs.calcVisibleRange()) return;
							margin = (gs.maxVisY - gs.minVisY)*0.1f;
							count++;
						}
						if(margin < 1.0E-8f) margin = 1.0E-8f; 
						ymin = gs.minVisY - margin;
						ymax = gs.maxVisY + margin;
						gs.setYValues(ymin, ymax);
						av.update();
					}
				} else if(toolName.equals(TOOL_ZOOM_SELECT_NAME)){
					av.lgView.zoomSelect();
				} else if(toolName.equals(TOOL_DEL_MARK_NAME)){
					av.delAnnot(av.getSelectedAnnot());
				} else if(toolName.equals(TOOL_ANNOT_MARK_NAME)){
					Annotation a = av.getSelectedAnnot();
					GraphSettings gs = graph.getCurGraphSettings();
					LObjAnnotation lObjA = gs.findAnnot(a);
				}
			}
		}else if(e.target == clear &&
				 e.type == ControlEvent.PRESSED){
			clear();
		}

    }

    public void clear()
    {
		// ask for confirmation????
		graph.clearAll();

		postEvent(new ControlEvent(1000, this));
		av.reset();
	
		// Clear curBin and set time to 0
		timeBin.setValue(0f);

		numStarted = 0;

		dd.update();
    }
}
