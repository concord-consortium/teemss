package org.concord.LabBook;

import graph.*;
import waba.ui.*;
import waba.util.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.util.*;
import org.concord.waba.extra.probware.*;
import org.concord.waba.extra.probware.probs.*;
import extra.util.*;

public class LObjGraphView extends LabObjectView
    implements ActionListener, DialogListener

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
    Vector bins = null;
	GraphTool gTool = null;

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

    public LObjGraphView(LObjViewContainer vc, LObjGraph g)
    {
		super(vc);

		menu.add("Change Axis...");
		menu.addActionListener(this);

		if(vc != null){
			vc.addMenu(this, menu);
		}

		graph = g;
		lObj = g;	

		props = new PropContainer();
		props.createSubContainer("Graph");
		props.createSubContainer("Y Axis");	
		props.createSubContainer("X Axis");

		propTitle = new PropObject("Title", graph.title);

		propXmin = new PropObject("Min", graph.xmin + "");
		propXmax = new PropObject("Max", graph.xmax + "");
		propXlabel = new PropObject("Label", graph.xLabel);
		propYmin = new PropObject("Min", graph.ymin + "");
		propYmax = new PropObject("Max", graph.ymax + "");
		propYlabel = new PropObject("Label", graph.yLabel);

		props.addProperty(propTitle, "Graph");

		props.addProperty(propXmax, "X Axis");
		props.addProperty(propXmin, "X Axis");
		props.addProperty(propXlabel, "X Axis");

		props.addProperty(propYmax, "Y Axis");
		props.addProperty(propYmin, "Y Axis");
		props.addProperty(propYlabel, "Y Axis");
    }

    public void dialogClosed(DialogEvent e)
    {
		if(!e.getActionCommand().equals("Cancel")){
			graph.xmin = propXmin.createFValue();
			graph.xmax = propXmax.createFValue();
			graph.ymin = propYmin.createFValue();
			graph.ymax = propYmax.createFValue();
			av.setRange(graph.xmin, graph.xmax, graph.ymin, graph.ymax);

			graph.yLabel = propYlabel.getValue();
			graph.xLabel = propXlabel.getValue();
			av.setYLabel(graph.yLabel, graph.yUnit);
			av.setXLabel(graph.xLabel, graph.xUnit);

			String newTitle = propTitle.getValue();
			if(newTitle.charAt(0) == '*' && gTool != null){
				autoTitle = true;
			} else {
				autoTitle = false;
			}

			if(autoTitle && gTool != null){
		
				gTool.dc.portId = gTool.dc.getProbe().getInterfacePort();
				CCProb p = gTool.dc.getProbe();
				graph.title = p.getName() + "(";
				PropObject [] props = p.getProperties();
				int i;
				for(i=0; i < props.length-1; i++){
					graph.title += props[i].getName() + "- " + props[i].getValue() + "; ";
				}
				graph.title += props[i].getName() + "- " + props[i].getValue() + ")";
				propTitle.setValue("*" + graph.title);
			} else {
				graph.title = newTitle;
			}

			if(gTool != null) gTool.setTitle2(graph.title);
		}

		if(e.getActionCommand().equals("Close")){
			av.repaint();
		}
    }

    public void showAxisProp()
    {
		MainWindow mw = MainWindow.getMainWindow();
		if(mw instanceof ExtraMainWindow){
			graph.ymin = av.getYmin();
			graph.ymax = av.getYmax();
			graph.xmin = av.getXmin();
			graph.xmax = av.getXmax();

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

    public void actionPerformed(ActionEvent e)
    {
		String command;
		Debug.println("Got action: " + e.getActionCommand());

		if(e.getSource() == menu){
			if(e.getActionCommand().equals("Change Axis...")){
				showAxisProp();
			} else if(e.getActionCommand().equals("Export Data")){
				if(bins.getCount() > 0 && bins.get(0) != null){
					LabBookFile.export((Bin)bins.get(0), null);
				}
			}
		}
    }

    public void addBins(Vector bs)
    {
		bins = bs;
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

	public void setGraphTool(GraphTool gt)
	{
		gTool = gt;
		gTool.setTitle2(graph.title);
	}

    public void layout(boolean sDone)
    {
		if(didLayout) return;
		didLayout = true;

		if(bins != null){
			menu.add("Export Data");	    
		}
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

		if(sTitle){
			titleLabel.setRect(x, curY, width, 16);
			curY += 16;
			gHeight -= 16;
		} 

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
			av.lgView.autoScroll = false;
		}
		av.setYLabel(graph.yLabel, graph.yUnit);
		av.setXLabel(graph.xLabel, graph.xUnit);

		add(av);
    }

    public void close()
    {
		Debug.println("Got close in graph");
		graph.ymin = av.getYmin();
		graph.ymax = av.getYmax();
		graph.xmin = av.getXmin();
		graph.xmax = av.getXmax();
		graph.store();
		if(container != null){
			container.delMenu(this,menu);
		}

		super.close();
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
			if(gTool != null) gTool.clear();
			else av.reset();
		}

    }

}
