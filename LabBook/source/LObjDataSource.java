package org.concord.LabBook;

import waba.util.*;
import waba.ui.*;
import extra.io.*;
import extra.util.*;
import org.concord.waba.extra.probware.Transform;
import org.concord.waba.extra.probware.CCInterfaceManager;

import org.concord.waba.extra.ui.*;
import extra.ui.*;
import extra.util.CCUnit;
import org.concord.waba.extra.event.*;
//LabObject implements Storable
public class LObjDataSource extends LObjSubDict implements Transform
{

public LObjDataSourceView view = null;
CCUnit		currentUnit = null;
public 		waba.util.Vector 	dataListeners = null;
    public LObjDataSource()
    {
		objectType = DATASOURCEOBJ;
    }
    public LabObjectView getView(LObjViewContainer vc, boolean edit,LObjDictionary curDict)
    {
    	
 		if(view == null){ 
   			view = new LObjDataSourceView(vc, this, edit);
    	}else if(view.container == null){
    		view.container = vc;
    		if(view.menu == null/* && edit*/){
    			view.addMenus(vc);
    		}
    	}
		return view;
    }
	public void addDataListener(DataListener l){
		if(dataListeners == null) dataListeners = new waba.util.Vector();
		if(dataListeners.find(l) < 0) dataListeners.add(l);
	}
	public void removeDataListener(DataListener l){
		int index = dataListeners.find(l);
		if(index >= 0) dataListeners.del(index);
	}
	public void notifyDataListeners(DataEvent e){
		if(dataListeners == null) return;
		for(int i = 0; i < dataListeners.getCount(); i++){
			DataListener l = (DataListener)dataListeners.get(i);
			l.dataReceived(e);
		}
	}


	public void startDataDelivery(){}
	public void stopDataDelivery(){}

	public CCUnit 	getUnit(){return currentUnit;}

    public void writeExternal(DataStream out)
    {
		super.writeExternal(out);
		if(view == null) return;
		view.writeExternal(out);
    }

    public void readExternal(DataStream in)
    {
		super.readExternal(in);
		if(view == null) view = new LObjDataSourceView(null, this,false);
		view.readExternal(in);
    }
    public boolean equals(TreeNode node){
    	return super.equals(node);
    }
	public boolean dataArrived(org.concord.waba.extra.event.DataEvent e){
		return false;
	}
	
	public boolean idle(org.concord.waba.extra.event.DataEvent e){
		return false;
	}
	
	public boolean startSampling(org.concord.waba.extra.event.DataEvent e){
		return false;
	}

}
class LObjDataSourceView extends LabObjectView implements ActionListener
{
	Button doneButton;
    Menu menu = null;
	
	
	public LObjDataSourceView(LObjViewContainer vc, LObjDataSource d,boolean edit){
		super(vc);
		if(edit) addMenus(vc);
		lObj = d;	
	}

	public void addMenus(LObjViewContainer vc){
		
	}

    public void onPaint(waba.fx.Graphics g){
    	super.onPaint(g);
    }
    public void writeExternal(DataStream out){
    }

    public void readExternal(DataStream in){
   }
    public void layout(boolean sDone){
		if(didLayout) return;
		didLayout = true;

		showDone = sDone;
		if(showDone){
			doneButton = new Button("Done");
			add(doneButton);
		}
	}

	public void setRect(int x, int y, int width, int height){
		super.setRect(x,y,width,height);
		if(!didLayout) layout(true);

		if(doneButton != null){
			doneButton.setRect(width/2 - 20, height - 17, 40, 15);
		}

	}

    public void close(){
		if(container != null && menu != null){
		    container.delMenu(this,menu);
		}
		super.close();
    }

	public void onEvent(Event e){
		if(e.target == doneButton &&
			e.type == ControlEvent.PRESSED){
			if(container != null){
				container.done(this);
			}	
		}
	}

    public void actionPerformed(ActionEvent e){
    }



}
