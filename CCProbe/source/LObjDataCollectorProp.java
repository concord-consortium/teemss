package org.concord.LabBook;

import waba.ui.*;
import waba.util.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.probware.probs.*;
import org.concord.waba.extra.probware.*;
import extra.ui.*;
import extra.util.*;

public class LObjDataCollectorProp extends LabObjectView
{
    LObjDataCollector dc;
    Button doneButton;
    Choice probeChoice = null;
    Edit nameEdit = null;
    Label nameLabel = null;

    public LObjDataCollectorProp(ViewContainer vc, LObjDataCollector d,
								   LObjDictionary curDict)
    {
		super(vc);

		dc = d;
		lObj = dc;
    }
    
    public void layout(boolean sDone)
    {
		if(didLayout) return;
		didLayout = true;

		showDone = sDone;

		nameLabel = new Label("Name");
		nameEdit = new Edit();
		nameEdit.setText("" + dc.name);
		add(nameLabel);
		add(nameEdit);

		probeChoice = new Choice(ProbFactory.getProbNames());	
		add(probeChoice);

		if(showDone){
			doneButton = new Button("Done");
			add(doneButton);
		} 
    }

    public void setRect(int x, int y, int width, int height)
    {
		super.setRect(x,y,width,height);
		if(!didLayout) layout(false);

		nameLabel.setRect(1, 2, 30, 15);
		nameEdit.setRect(35, 2, width-40, 15);

		probeChoice.setRect(3,30, width-7, 15);

		if(showDone){
			doneButton.setRect(width-30,height-15,30,15);
		} 
    }

    public void close()
    {
		Debug.println("Got close in document");
		int probeId = ProbFactory.getIndex(probeChoice.getSelected());
		LObjGraph graph = dc.getGraph();

		Vector dataSources = new Vector(1);
		LObjProbeDataSource newDS = LObjProbeDataSource.getProbeDataSource(probeId, dc.interfaceId,
																		   CCProb.INTERFACE_PORT_A);
		dataSources.add(newDS);
		dc.setDataSources(dataSources);

		graph.xUnit = CCUnit.getUnit(CCUnit.UNIT_CODE_S);
		graph.xLabel = "Time";
		graph.yLabel = newDS.getLabel();
		graph.yUnit = newDS.getUnit();
		graph.store();

		if(nameEdit.getText() != "" && nameEdit.getText() != null){
			dc.name = nameEdit.getText();
		}

		dc.closeSources();

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
