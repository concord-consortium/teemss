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
class LObjDrawingView extends LabObjectView implements ScrollListener
{
	Edit 					nameEdit;
	Label					nameLabel;
	boolean					nameEditWasAdded = false;
    CCScrible 				scribble;

    Button doneButton = null;
	boolean	scribbleWasAdded = false;
	
	CCScrollBar				scrollBar;
	
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
		if(scrollBar == null) scrollBar = new CCScrollBar(this);
		if(scrollBar != null){
			scrollBar.setMinMaxValues(20,100);
			scrollBar.setAreaValues(200,100);
			scrollBar.setIncValue(5);
			scrollBar.setPageIncValue(40);
			scrollBar.setValue(20);
		}
		
		add(scrollBar);
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
//			scribble.setRect(2,curY,width-4, dHeight);
			scribble.setRect(2,curY,width-11, dHeight);
		}
		if(scrollBar != null){
			scrollBar.setRect(width-7,curY + 19,7, dHeight - 20);
		}
    }

    public void close()
    {
    	scribble.close();
		super.close();
		
		if(scrollBar != null) scrollBar.close();
		
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

	public int getPreferredWidth(waba.fx.FontMetrics fm){
		return 100;
	}

	public int getPreferredHeight(waba.fx.FontMetrics fm){
		return 32;
	}

	private extra.ui.Dimension preferrDimension;
	public extra.ui.Dimension getPreferredSize(){
		if(preferrDimension == null){
			preferrDimension = new extra.ui.Dimension(getPreferredWidth(null),getPreferredHeight(null));
		}else{
			preferrDimension.width = getPreferredWidth(null);
			preferrDimension.height = getPreferredHeight(null);
		}
		return preferrDimension;
	}
	
	public void scrollValueChanged(ScrollEvent se){
		if(se.target != scrollBar) return;
	}
}

