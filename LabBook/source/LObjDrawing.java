package org.concord.LabBook;

import waba.util.*;
import waba.ui.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;
import extra.ui.*;
//LabObject implements Storable
public class LObjDrawing extends LabObject
{

public LObjDrawingView view = null;

    public LObjDrawing()
    {
	objectType = DRAWING;

    }
    public LabObjectView getView(ViewContainer vc, boolean edit, LObjDictionary curDict)
    {
    	
    	if(view == null){
    		view = new LObjDrawingView(vc, this);
    	}else if(view.container == null){
    		view.container = vc;
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
	if(view == null){
		view = new LObjDrawingView(null, this);
	}
	view.readExternal(in);
	
    }
}
class LObjDrawingView extends LabObjectView
{
    CCScrible scribble;

    Button doneButton = null;

    public LObjDrawingView(ViewContainer vc, LObjDrawing d)
    {
	super(vc);

	lObj = d;	
    }

    public void writeExternal(DataStream out)
    {
	if(scribble == null) return;
	scribble.writeExternal(out);
    }

    public void readExternal(DataStream in)
    {
	if(scribble == null){
		scribble = new CCScrible(MainWindow.getMainWindow());
	}
	scribble.readExternal(in);
	
    }
    public void layout(boolean sDone)
    {
	if(didLayout) return;
	didLayout = true;

	showDone = sDone;

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
	int dHeight = height;

	if(showDone){
	    doneButton.setRect(width-30,height-15,30,15);
	    dHeight -= 16;
	}

	if(scribble != null){ 
		if(!scribble.isAddComponent()) add(scribble);
		scribble.setRect(1,curY,width-2, dHeight);
	}else{
		scribble = new CCScrible(MainWindow.getMainWindow(),1,curY,width-2, dHeight);
		add(scribble);
	}
    }

    public void close()
    {
    	scribble.close();
		super.close();
    }

    public void onEvent(Event e)
    {
	if(e.target == doneButton &&
	   e.type == ControlEvent.PRESSED){
	   if(scribble.isChooserUp()){
	   	scribble.closeChooser();
	   }else{
		    if(container != null){
			container.done(this);
		    }	
	   }    
	}
    }



}
