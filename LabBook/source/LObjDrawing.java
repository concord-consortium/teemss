package org.concord.LabBook;

import waba.util.*;
import waba.ui.*;
import extra.io.*;
import org.concord.waba.extra.ui.*;
import extra.ui.*;
import org.concord.waba.extra.event.*;
//LabObject implements Storable
public class LObjDrawing extends LabObject
{

	public LObjDrawingView view = null;

    public LObjDrawing()
    {
		super(DefaultFactory.DRAWING);
    }
    public LabObjectView getView(ViewContainer vc, boolean edit, LObjDictionary curDict)
    {
    	
    	if(view == null){
    		view = new LObjDrawingView(vc, this);
    	}else{
    		view.container = vc;
    	}
    	view.didLayout = false;
		return view;
    }

    public void writeExternal(DataStream out)
    {
		if(view == null) return;
		view.writeExternal(out);
    }

    public void readExternal(DataStream in)
    {
		if(view == null){
			view = new LObjDrawingView(null, this);
		}
		view.readExternal(in);
	
    }
}
class LObjDrawingView extends LabObjectView
{
	Edit 					nameEdit;
	Label					nameLabel;
	boolean					nameEditWasAdded = false;
    CCScrible 				scribble;

    Button doneButton = null;
	boolean	scribbleWasAdded = false;
	
	
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

	public void setEmbeddedState(boolean embeddedState){
		boolean oldState = getEmbeddedState();
		super.setEmbeddedState(embeddedState);
		if(oldState != getEmbeddedState()){
			if(nameEdit != null){
				if(scribble != null) remove(scribble);
				if(getEmbeddedState()){
					if(nameEditWasAdded){
						remove(nameEdit);
						remove(nameLabel);
						add(scribble);
					}
					nameEditWasAdded = false;
				}else{
					if(!nameEditWasAdded){
						add(nameLabel);
						add(nameEdit);
						add(scribble);
					}
					nameEditWasAdded = true;
				}
			}
			if(scribble != null) scribble.setEmbeddedState(embeddedState);
		}
	}
    public void layout(boolean sDone)
    {
		if(didLayout) return;
		didLayout = true;
		if(nameEdit == null) nameEdit = new Edit();
		nameEdit.setText(getLabObject().name);
		if(nameLabel == null) nameLabel = new Label("Name");
		if(getEmbeddedState()){
			nameEditWasAdded = false;
		}else{
			add(nameLabel);
			add(nameEdit);
			nameEditWasAdded = true;
		}
		if(scribble != null){ 
			if(!scribbleWasAdded){
				add(scribble);
				scribbleWasAdded = true;
			}
		}else{
			scribble = new CCScrible(MainWindow.getMainWindow());
			add(scribble);
			scribbleWasAdded = true;
		}
		if(doneButton != null){
			remove(doneButton);
		}
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

		int curY = 2;
		int dHeight = height - 2*curY;



		if(showDone){
		    doneButton.setRect(width-32,curY+2,30,15);
		}
		if(!getEmbeddedState() && nameEdit != null && nameEditWasAdded){
			waba.fx.Rect r = getRect();
			nameLabel.setRect(1, curY+2, 30, 15);
			int editW = (showDone)?r.width - 62:r.width - 32;
			nameEdit.setRect(30, curY+2, editW, 15);
			
			curY += 15;
			dHeight -= 15;
			
		}
		
		if(scribble != null){ 
			scribble.setRect(2,curY,width-4, dHeight);
		}
    }

    public void close()
    {
    	scribble.close();
		super.close();
		
		
    	if(nameEdit != null){
    		getLabObject().name = nameEdit.getText();
    	}
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

	public int getPreferredWidth(){
		return 100;
	}

	public int getPreferredHeight(){
		return 32;
	}
}

