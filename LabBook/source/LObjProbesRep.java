package org.concord.LabBook;

import waba.util.*;
import waba.ui.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;
import extra.ui.*;
import extra.util.CCUnit;
import org.concord.waba.extra.event.*;
//LabObject implements Storable
public class LObjProbesRep extends LObjSubDict
{

public LObjProbesRepView view = null;

    public LObjProbesRep()
    {
		objectType = PROBESREPOBJ;
    }

	public static LObjProbesRep makeNew()
	{
		LObjProbesRep me = new LObjProbesRep();
		me.initSubDict();
		me.setObj(new LObjProbe("1"),0);
		me.setObj(new LObjProbe("2"),1);
		me.setObj(new LObjProbe("3"),2);
		me.setObj(new LObjProbe("4"),3);
		
		return me;
	}

    public LabObjectView getView(LObjViewContainer vc, boolean edit,LObjDictionary curDict)
    {
    	
 		if(view == null){ 
   			view = new LObjProbesRepView(vc, this, edit);
    	}else if(view.container == null){
    		view.container = vc;
    		if(view.menu == null/* && edit*/){
    			view.addMenus(vc);
    		}
    	}
		return view;
    }

    public void writeExternal(DataStream out)
    {
		super.writeExternal(out);
		if(view == null) return;
		view.writeExternal(out);
    }

    public void readExternal(DataStream in)
    {
		super.readExternal(in);
		if(view == null) view = new LObjProbesRepView(null, this,false);
		view.readExternal(in);
    }
    public boolean equals(TreeNode node){
    	return super.equals(node);
    }

}
class LObjProbesRepView extends LabObjectView implements ActionListener
{
	Button doneButton;
    Menu menu = null;
	
	
	public LObjProbesRepView(LObjViewContainer vc, LObjProbesRep d,boolean edit){
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
