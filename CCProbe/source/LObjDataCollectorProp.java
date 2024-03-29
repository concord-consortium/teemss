package org.concord.CCProbe;

import waba.ui.*;
import waba.util.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.util.*;

import org.concord.LabBook.*;

public class LObjDataCollectorProp extends LabObjectView
	implements ActionListener, PropOwner
{
    LObjDataCollector dc;
	PropContainer mainProps;
	PropContainer dsProps;
	PropertyView propView = null;
	PropObject probeType = null;
	PropObject [] probeQuantities = null;
	PropObject nameProp;
	
    public LObjDataCollectorProp(ViewContainer vc, LObjDataCollector d,
								   LObjDictionary curDict, LabBookSession session)
    {
		super(vc, (LabObject)d, session);
		dc = d;

		String [] probeNames = LObjProbeDataSource.getProbeNames();
		probeQuantities = new PropObject[probeNames.length];

		mainProps = new PropContainer("Main");
		nameProp = new PropObject("Name", "Name", 0, dc.getName());
		nameProp.prefWidth = 120;
		mainProps.addProperty(nameProp);

		LObjGraph graph = dc.getGraph(session);
		int index = -1;
		Vector rootSources = null;
		if(graph != null &&
		   graph.getDataSource(0, session) != null){
			rootSources = new Vector();
			graph.getDataSource(0, session).getRootSources(rootSources, session);
			if(rootSources.getCount() > 0 &&
			   rootSources.get(0) instanceof LObjProbeDataSource){
				String curName = ((LObjProbeDataSource)rootSources.get(0)).getProbeName();
				for(int i=0; i<probeNames.length; i++){
					if(curName.equals(probeNames[i])){
						index = i;
					}
				}
			}
		} 
		if(rootSources == null || index != -1){
			if(index == -1) index = 0;
			probeType = new PropObject("Probe", "Probe", 1, probeNames, index);
			probeType.prefWidth = 120;
			mainProps.addProperty(probeType, this);
			LObjProbeDataSource probeDS =  
				LObjProbeDataSource.getProbeDataSource(probeType.getValue());

			if(probeDS == null) return;
			String [] quantityNames = probeDS.getQuantityNames();
			if(quantityNames == null || quantityNames.length <= 1) return;
			
			setupOutputPane(quantityNames);
		}

		propView.updateView();
    }
    
	public void layout(boolean sDone)
	{
		if(didLayout) return;
		didLayout = true;

		propView = new PropertyView(this);
		propView.setAlignment(PropertyView.ALIGN_TOP);
		propView.addContainer(mainProps);
		if(dsProps != null){
			propView.addContainer(dsProps);
		}
		add(propView);
	}

    public void setRect(int x, int y, int width, int height)
    {
		super.setRect(x,y,width,height);
		if(!didLayout) layout(false);

		propView.setRect(0,0,width,height);
	}

	public boolean visValueChanged(PropObject po)
	{		
		if(po == probeType){
			if(probeQuantities[probeType.getVisIndex()] != null){
				if(dsProps == null){
					dsProps = new PropContainer("Outputs");
					propView.addContainer(dsProps);
				} else {
					PropObject oldQuantProp = dsProps.findProperty(0);
					dsProps.removeProperty(oldQuantProp);
				}
				dsProps.addProperty(probeQuantities[probeType.getVisIndex()]);
				propView.updateView();
			} else {
				LObjProbeDataSource probeDS =  
					LObjProbeDataSource.getProbeDataSource(probeType.getVisValue());
				if(probeDS == null) return false;
				String [] quantityNames = probeDS.getQuantityNames();
				if(quantityNames == null) return false;
				if(quantityNames.length <= 1){
					if(dsProps != null){
						propView.removeContainer(dsProps);
						dsProps = null;						
						propView.updateView();
					}
				} else {
					setupOutputPane(quantityNames);
					propView.updateView();
				}
			}
		}
		return true;
	}

	private void setupOutputPane(String [] quantityNames)
	{
		if(dsProps == null){
			dsProps = new PropContainer("Outputs");
			propView.addContainer(dsProps);
		} else {
			PropObject oldQuantProp = dsProps.findProperty(0);
			dsProps.removeProperty(oldQuantProp);
		}
		PropObject quantityProp = new PropObject("", "DS", 0, quantityNames);
		quantityProp.setType(PropObject.MULTIPLE_SEL_LIST);
		quantityProp.setRadio(false);
		if(probeType != null){
			probeQuantities[probeType.getVisIndex()] = quantityProp;
		}
		for(int i=0; i<quantityNames.length; i++){
			quantityProp.setCheckedValue(i, true);
		}
		dsProps.addProperty(quantityProp);
	}

	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("Apply")){
			LObjGraph graph = dc.getGraph(session);
			
			if(probeType != null){
				Vector dataSources = new Vector(1);
				LObjProbeDataSource probeDS = 
					LObjProbeDataSource.getProbeDataSource(probeType.getValue());
				session.storeNew(probeDS);
				if(dsProps == null){
					dataSources.add(probeDS);
				} else {
					PropObject quantProp  = dsProps.findProperty(0);
					if(quantProp == null){
						dataSources.add(probeDS);
					} else {
						String [] quantNames = quantProp.getPossibleValues();
						for(int i=0; i<quantNames.length; i++){
							if(quantProp.getCheckedValue(i)){
								DataSource newDS = 
									probeDS.getQuantityDataSource(quantNames[i], session);
								dataSources.add(newDS);
							}
						}
					}
				}
				if(dataSources.getCount() < 1){
					// error
					return;
				}


				//			dc.setDataSources(dataSources);
				
				
				graph.clear();
				graph.createDefaultAxis();
				graph.addDataSource((DataSource)dataSources.get(0),true,0,0, session);
				for(int i=1; i<dataSources.getCount(); i++){
					graph.addYAxis();
					graph.addDataSource((DataSource)dataSources.get(i),true,0,i, session);
				}
				graph.store();
				
				// to be safe
				((DataSource)dataSources.get(0)).closeEverything();

			}

			dc.setName(nameProp.getValue());
				
		} else if(e.getActionCommand().equals("Close")){
			if(container != null){
				container.done(this);
			}	    
		}
	}

}
