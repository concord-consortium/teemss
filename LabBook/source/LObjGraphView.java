package org.concord.LabBook;

import graph.*;
import waba.ui.*;
import waba.util.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.ui.*;

public class LObjGraphView extends LabObjectView
    implements ActionListener
{
    LObjGraph graph;
    AnnotView av = null;

    Button doneButton = null;
    Label titleLabel = null;
    Vector bins = null;

    Menu menu = new Menu("Graph");

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
    }

    public void actionPerformed(ActionEvent e)
    {
	String command;
	Debug.println("Got action: " + e.getActionCommand());

	if(e.getSource() == menu){
	    if(e.getActionCommand().equals("Change Axis...")){
		av.lgView.showAxisProp();
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

    public void layout(boolean sDone)
    {
	if(didLayout) return;
	didLayout = true;

	if(bins != null){
	    menu.add("Export Data");	    
	}
	showDone = sDone;

	titleLabel = new Label(graph.name, Label.CENTER);
	add(titleLabel);

	if(showDone){
	    doneButton = new Button("Done");
	    add(doneButton);
	} 
    }


    public void setRect(int x, int y, int width, int height)
    {
	super.setRect(x,y,width,height);
	if(!didLayout) layout(false);

	int curY = 1;
	int gHeight = height;

	if(height > 160){
	    titleLabel.setRect(x, curY, width, 16);
	    curY += 16;
	    gHeight -= 16;
	} else {
	    titleLabel.setRect(0,0,0,0);
	}

	if(showDone){
	    doneButton.setRect(width-30,height-15,30,15);
	    gHeight -= 16;
	}

	if(av != null){ remove(av); }
	
	av = new AnnotView(width-2, gHeight);
	av.setPos(1,curY);
	av.setRange(graph.xmin, graph.xmax, graph.ymin, graph.ymax);
	if(bins != null){
	    for(int i = 0; i < bins.getCount(); i++){
		av.addBin((Bin)bins.get(i));
	    }
	    av.lgView.autoScroll = false;
	}
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
	}
    }

}
