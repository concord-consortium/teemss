import waba.ui.*;
import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;
import extra.ui.*;

public class LObjDrawingView extends LabObjectView
{


    LObjDrawing draw;
    CCScrible scribble;

    Button doneButton = null;

    public LObjDrawingView(LObjViewContainer vc, LObjDrawing d)
    {
	super(vc);

	draw = d;
	lObj = (LabObject)d;	
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

	if(scribble != null){ remove(scribble); }
	
	scribble = new CCScrible(MainWindow.getMainWindow(),1,curY,width-2, dHeight);

	add(scribble);
    }

    public void close()
    {
    	scribble.destroy();
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
