import graph.*;
import waba.ui.*;

public class LObjGraphView extends LabObjectView
{
    LObjGraph graph;
    AnnotView av = null;

    Label nameLabel = null;
    Edit nameEdit = null;

    Button doneButton = null;

    public LObjGraphView(LObjGraph g)
    {
	graph = g;
	lObj = (LabObject)g;	
    }

    public void layout(boolean sDone, boolean sName)
    {
	if(didLayout) return;
	didLayout = true;

	showDone = sDone;
	showName = sName;

	if(showName){
	    nameEdit = new Edit();
	    nameEdit.setText(graph.name);
	    nameLabel = new Label("Name");
	    add(nameLabel);
	    add(nameEdit);
	} 

	if(showDone){
	    doneButton = new Button("Done");
	    add(doneButton);
	} 
    }


    public void setRect(int x, int y, int width, int height)
    {
	super.setRect(x,y,width,height);
	if(!didLayout) layout(false, false);

	int curY = 0;
	int gHeight = height;
	if(showName){
	    nameLabel.setRect(1, 1, 30, 15);
	    nameEdit.setRect(31, 1, 80, 15);
	    curY = 16;
	    gHeight -= 16;
	}

	if(showDone){
	    doneButton.setRect(width-30,height-15,30,15);
	    gHeight -= 16;
	}

	if(av != null){ remove(av); }
	
	av = new AnnotView(width-2, gHeight);
	av.setPos(1,curY);
	av.setRange(graph.xmin, graph.xmax, graph.ymin, graph.ymax);
	add(av);
    }

    public void close()
    {
	System.out.println("Got close in graph");
	if(showName){
	    graph.name = nameEdit.getText();
	}
	graph.ymin = av.getYmin();
	graph.ymax = av.getYmax();
	graph.xmin = av.getXmin();
	graph.xmax = av.getXmax();
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
