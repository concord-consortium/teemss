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
    implements ActionListener, DialogListener, DataListener

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
    Vector bins = new Vector();

    Menu menu = new Menu("Graph");
	int curViewIndex = 1;

    PropertyDialog pDialog = null;
    PropContainer props = null;
	PropObject propTitle;
    PropObject propXmin;
    PropObject propXmax;
	PropObject propXlabel;
    PropObject propYmin;
    PropObject propYmax;
	PropObject propYlabel;

	boolean autoTitle = false;

    TimeBin timeBin = new TimeBin();

    DigitalDisplay dd;
    Bin curBin = null;
	DecoratedValue curAnnot = null;

	int dd_height = 20;

	LObjDataCollector dc = null;
	LObjDictionary dataDict;
	boolean instant = false;

	String [] fileStrings = {"Save Data..", "Export Data.."};
	String [] palmFileStrings = {"Save Data.."};

    public LObjGraphView(ViewContainer vc, LObjGraph g, LObjDictionary curDict)
    {
		super(vc);

		dataDict = curDict;

		menu.add("Properties..");
		menu.addActionListener(this);

		if(vc != null){
			vc.getMainView().addMenu(this, menu);
			if(waba.sys.Vm.getPlatform().equals("PalmOS")){
				fileStrings = palmFileStrings;
			}
			vc.getMainView().addFileMenuItems(fileStrings, this);
		}

		graph = g;
		lObj = g;	

		props = new PropContainer();
		props.createSubContainer("Graph");
		props.createSubContainer("YAxis");	
		props.createSubContainer("XAxis");

		propTitle = new PropObject("Title", graph.title);
		propTitle.prefWidth = 100;

		propXmin = new PropObject("Min", graph.xmin + "");
		propXmax = new PropObject("Max", graph.xmax + "");
		propXlabel = new PropObject("Label", graph.xLabel);
		propYmin = new PropObject("Min", graph.ymin + "");
		propYmax = new PropObject("Max", graph.ymax + "");
		propYlabel = new PropObject("Label", graph.yLabel);

		props.addProperty(propTitle, "Graph");

		props.addProperty(propXmax, "XAxis");
		props.addProperty(propXmin, "XAxis");
		props.addProperty(propXlabel, "XAxis");

		props.addProperty(propYmax, "YAxis");
		props.addProperty(propYmin, "YAxis");
		props.addProperty(propYlabel, "YAxis");
    }

	DataSource curDS;
	public void addDataSource(DataSource ds)
	{
		// need to pass in object at this point to identify which 
		// data source is which
		ds.addDataListener(this);
		curDS = ds;
		if(curDS != null){			
			graph.yUnit = curDS.getUnit();
			if(graph.name.equals("..auto_title..")){
				graph.yLabel = curDS.getLabel();
				autoTitle = true;
			} 			
		}
	}

	public void doInstantCollection()
	{
		instant = true;
	}		

	public void setDC(LObjDataCollector dataC)
	{
		dc = dataC;
		if(graph.name.equals("..auto_title..")){
			graph.title = dc.getSummaryTitle();		   
			autoTitle = true;
		} 			
	}

	public void updateProp()
	{
		if(curDS != null){
			graph.yUnit = curDS.getUnit();
			if(autoTitle){
				graph.yLabel = curDS.getLabel();
				if(dc != null){
					graph.title = dc.getSummaryTitle();
				}
			}

		}
		av.setYLabel(graph.yLabel, graph.yUnit);
		av.setXLabel(graph.xLabel, graph.xUnit);		
		av.setRange(graph.xmin, graph.xmax, graph.ymin, graph.ymax);

		curBin.setUnit(graph.yUnit);
	}

    public void dialogClosed(DialogEvent e)
    {
		if(!e.getActionCommand().equals("Cancel")){
			graph.xmin = propXmin.createFValue();
			graph.xmax = propXmax.createFValue();
			graph.ymin = propYmin.createFValue();
			graph.ymax = propYmax.createFValue();
			graph.xLabel = propXlabel.getValue();

			String newTitle = propTitle.getValue();
			String newYLabel = propYlabel.getValue();

			if(!autoTitle && 
			   ((newTitle.length() > 0 && 
				 newTitle.charAt(0) == '*') ||
				(newYLabel.length() > 0 &&
				 newYLabel.charAt(0) == '*')) && 
			   dc != null){
				autoTitle = true;
				graph.name = "..auto_title..";
			} else if(autoTitle && 
			   ((newTitle.length() > 0 && 
				 newTitle.charAt(0) != '*') ||
				(newYLabel.length() > 0 &&
				 newYLabel.charAt(0) != '*'))){
				autoTitle = false;
			}

			if(!autoTitle){
				graph.title = newTitle;
				graph.yLabel = newYLabel;
			}

			updateProp();

			if(autoTitle && dc != null){
				propTitle.setValue("*" + graph.title);
				propYlabel.setValue("*" + graph.yLabel);
			} 

			postEvent(new ControlEvent(1001, this));
		}

		if(e.getActionCommand().equals("Close")){
			av.repaint();
		}
    }

	void updateAv2Graph()
	{
		graph.ymin = av.getYmin();
		graph.ymax = av.getYmax();
		graph.xmin = av.getXmin();
		graph.xmax = av.getXmax();		
	}

    public void showAxisProp()
    {
		MainWindow mw = MainWindow.getMainWindow();
		if(mw instanceof ExtraMainWindow){
			updateAv2Graph();

			if(autoTitle) propTitle.setValue("*" + graph.title);
			else propTitle.setValue(graph.title);

			propXmin.setValue("" + graph.xmin);
			propXmax.setValue("" + graph.xmax);
			propXlabel.setValue(graph.xLabel);

			propYmin.setValue("" + graph.ymin);
			propYmax.setValue("" + graph.ymax);
			propYlabel.setValue(graph.yLabel);

			pDialog = new PropertyDialog((ExtraMainWindow)mw, this, "Properties", props);
			pDialog.setRect(0,0, 140,140);
			pDialog.show();
		}
    }

    float val= 0f;
    float time = 0f;

    int numVals = 0;

    //    int [] [] pTimes = new int [1000][];
    int [] [] pTimes = null;

    public void dataReceived(DataEvent dataEvent)
    {

		if(dataEvent.type == DataEvent.DATA_READY_TO_START){
			numVals = 0;
			curPtime = 0;
			return;
		}

		if(dataEvent.type == DataEvent.DATA_RECEIVED){
			if(av.active){
				int startPTime = Vm.getTimeStamp();
				if(!curBin.dataReceived(dataEvent)){
					postEvent(new ControlEvent(1000, this));
					// stopGraph();
					// av.curView.draw();
					return;		
				}
				if(pTimes != null){
					dataEvent.pTimes[dataEvent.numPTimes++] = Vm.getTimeStamp() - startPTime;		   
					savePTimes(dataEvent);
				}
			}	
			numVals += dataEvent.numbSamples;

			val = dataEvent.data[dataEvent.dataOffset];
		} else {
			int startPTime = Vm.getTimeStamp();
			if(pTimes != null){
				pTimes [curPtime] = new int [6];
				pTimes[curPtime][0] = 1;
				pTimes[curPtime][1] = startPTime;
				pTimes[curPtime][2] = numVals;
			}

			//		if(lg.active){
			av.update();
			
			int newTime = Vm.getTimeStamp();
			if(pTimes != null){
				pTimes[curPtime][3] = (newTime - startPTime);		
			}

			startPTime = Vm.getTimeStamp();
			if(pTimes != null){
				pTimes[curPtime][4] = (startPTime - newTime);
			}

			timeBin.setValue(dataEvent.getTime());
			dd.update();
			//		curVal.setText(output1);
			// curTime.setText(output2);

			if(pTimes != null){
				pTimes[curPtime][5] = (Vm.getTimeStamp() - startPTime);
			}
		
			numVals = 0;
			curPtime++;
		}
    }
    
    public String pTimeText = ""; 
    int curPtime = 0;

    public void savePTimes(DataEvent dEvent)
    {
		if(pTimes != null){
			pTimes [curPtime] = new int [dEvent.numPTimes + 1];
			pTimes [curPtime][0] = 0;
			for(int i=0; i< dEvent.numPTimes; i++){
				pTimes [curPtime][i+1] = dEvent.pTimes[i];
			}
			curPtime++;
		}
    }

    public void actionPerformed(ActionEvent e)
    {
		String command;
		Debug.println("Got action: " + e.getActionCommand());

		if(e.getSource() == menu){
			if(e.getActionCommand().equals("Properties..")){
				showAxisProp();
			}
		} else {
			if(e.getActionCommand().equals("Save Data..")){
				LObjDataSet dSet = LObjDataSet.makeNewDataSet();
				updateAv2Graph();
				LObjGraph dsGraph = (LObjGraph)graph.copy();
				dsGraph.name = "Graph";
				dSet.setDataViewer(dsGraph);
				dSet.setUnit(graph.yUnit);
				for(int i=0; i<bins.getCount(); i++){
					dSet.addBin((Bin)bins.get(i));
				}

				if(dataDict != null){
					dataDict.add(dSet);
					dSet.store();
				} else {
					// for now it is an error
					// latter it should ask the user for the name
				}
			} else if(e.getActionCommand().equals("Export Data..")){
				if(bins != null ||
				   bins.getCount() > 0){
					if(dc != null){
						((Bin)bins.get(0)).description = dc.getSummaryTitle();
					} else {
						((Bin)bins.get(0)).description = graph.title;
					}

					DataExport.export((Bin)bins.get(0), av.lGraph.annots);

				}
			}
		}
	}

    boolean sTitle;

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

		String [] toolsChoices = {"Delete Mark", "Toggle Scrolling", "Auto Resize"};
		toolsChoice = new Choice(toolsChoices);
		toolsChoice.setName("Tools");
		add(toolsChoice);
		
		notes = new Button("Notes");
		add(notes);

		clear = new Button("Clear");
		add(clear);
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

		// Trying to get 10pt at dd_height = 10 
		//  and 16pt at dd_height = 20
		dd = new DigitalDisplay(new Font("Helvetica", 
										 Font.BOLD, 3*dd_height/5 + 4));		
		gHeight -= dd_height;
		dd.setRect(0,curY, width, dd_height);
		dd.addBin(timeBin);
		add(dd);
		curY += dd_height;

		if(showDone){
			doneButton.setRect(width-30,height-15,30,15);
			gHeight -= 16;
		}

		if(width <= 160){
			gHeight -= 12;
			viewChoice.setRect(0, height-12, 33, 12);
			lgm.setRect(32, height -12, 30, 12);
			addMark.setRect(32, height-12, 30, 12);
			toolsChoice.setRect(63, height-12, 35, 12);
			notes.setRect(100, height-12, 30, 12); 
			clear.setRect(130, height-12, 30, 12);
		} else {
			gHeight -= 16;
			viewChoice.setRect(0, height-16, 50, 16);
			lgm.setRect(50, height -16, 42, 16);
			addMark.setRect(50, height-16, 42, 16);
			toolsChoice.setRect(100, height-16, 55, 16);
			notes.setRect(160, height-16, 30, 16);
			clear.setRect(200, height-16, 30, 16);
		}


		if(av != null){ remove(av); }
	
		av = new AnnotView(width, gHeight, 6);
		av.setPos(0,curY);
		av.setRange(graph.xmin, graph.xmax, graph.ymin, graph.ymax);
		if(bins != null){
			for(int i = 0; i < bins.getCount(); i++){
				av.addBin((Bin)bins.get(i));
			}
			if(bins.getCount() > 0){
				av.lgView.autoScroll = false;
			}
		}
		av.setYLabel(graph.yLabel, graph.yUnit);
		av.setXLabel(graph.xLabel, graph.xUnit);
		curBin = av.getBin();
		curBin.setUnit(graph.yUnit);

		add(av);

		if(instant){
			startGraph();
			curDS.startDataDelivery();
			stopGraph();
			instant = false;
		}
    }

    public void close()
    {
		Debug.println("Got close in graph");
		graph.ymin = av.getYmin();
		graph.ymax = av.getYmax();
		graph.xmin = av.getXmin();
		graph.xmax = av.getXmax();
		if(autoTitle) graph.name = "..auto_title..";

		graph.store();
		if(container != null){
			container.getMainView().delMenu(this,menu);
			container.getMainView().removeFileMenuItems(fileStrings, this);
		}

		av.free();

		if(curDS != null){
			curDS.removeDataListener(this);
		}

		super.close();
    }

	public void startGraph(){
		if(bins.getCount() == 0){
			av.active = true;
			bins.add(curBin);
			curBin.time = new Time();
			dd.addBin(curBin);

			// Don't quite know what to do here
			// this should be taken care of by DataSources
			curBin.description = "";
		}
	}

	// Right this requires the caller to call repaint()
	public void stopGraph()
	{
		if(av.active){
			av.active = false;
			dd.removeBin(curBin);
			curBin = av.pause();
			curBin.setUnit(graph.yUnit);
		}
	
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
				break;
			case 2:
				av.setViewMode('A');
				break;
			}
		} else if(e.target == addMark &&
				  e.type == ControlEvent.PRESSED){
			av.addAnnot();
		} else if(e.type == 1003){
			//	    System.out.println("Got 1003");
			if(av.lgView.selAnnot != null){
				timeBin.setValue(av.lgView.selAnnot.time);
				// need to make sure lg.lgView.selAnnot has been added to dd
				if(curAnnot == null){
					// might need to remove the curBin to save space
					curAnnot = av.lgView.selAnnot;
					dd.addBin(curAnnot);
				} else if(curAnnot != av.lgView.selAnnot){
					dd.removeBin(curAnnot);
					curAnnot = av.lgView.selAnnot;
					dd.addBin(curAnnot);
				}

				dd.update();

				// curVal.setText(convertor.fToString(lg.lgView.selAnnot.value) + units);
				// curTime.setText(convertor.fToString() + "s");
			} else {
				// hack
				// Need to remove annot bin from dd
				// set value of timeBin to last time
				if(curAnnot != null){
					dd.removeBin(curAnnot);
					curAnnot = null;
				}
				dd.update();
		
				// curVal.setText("");
				// curTime.setText("");
			}		
		} else if(e.target == viewChoice){
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
		} else if(e.target == clear &&
				  e.type == ControlEvent.PRESSED){
			clear();
		}

    }

    public void clear()
    {
		// ask for confirmation????


		postEvent(new ControlEvent(1000, this));

		av.reset();
	
		// Clear curBin and set time to 0
		timeBin.setValue(0f);
		dd.update();
	
		// curVal.setText("");
		// curTime.setText("0.0s");
		bins = new Vector();	
    }
}
