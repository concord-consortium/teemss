import waba.ui.*;
import waba.fx.*;
import waba.util.*;
import waba.sys.*;
import graph.*;

class GraphView extends Control implements PropObject
{
    JGraphics myG;
    final static float DEFAULT_RANGE = (float)500.0;
    final static float DEFAULT_MIN = (float)-200.0;

    Graph2D graph = null;

    public float range, minValue;
    public String units = null;

    int numBars;
    Vector probes = new Vector();
    int length = 0;

    PropWindow prop;
    Edit maxEdit;
    Edit minEdit;

    int count;
    boolean drawGraph;

    public GraphView(int w, int h)
    {
	width = w;
	height = h;

	range = DEFAULT_RANGE;
	minValue = DEFAULT_MIN;

	graph = new LineGraph(w, h);
	graph.setRange(minValue, range);
	
	units = new String("C");
	
	numBars = 0;	
	
	// Make the popup!
	prop = new PropWindow();
	maxEdit = new Edit();
	minEdit = new Edit();

	drawGraph = true;
	count = 0;

    }

    public void reset()
    {
	drawGraph = true;
	graph.reset();
	count = 0;
	
	// repaint
	repaint();
    }


    public void setPos(int x, int y)
    {
	setRect(x,y,width,height);
    }

    public Container setupProp(int w, int h)
    {
	Container properties = new Container();
	Label tmpLabel;

	properties.setRect(0,0,w,h);

	// Max
	tmpLabel = new Label("Max");
	tmpLabel.setRect(2,2,65,17);
	properties.add(tmpLabel);
	maxEdit.setRect(70,2,50,17);
	properties.add(maxEdit);

	// Min
	tmpLabel = new Label("Min");
	tmpLabel.setRect(2,20,65,17);
	properties.add(tmpLabel);
	minEdit.setRect(70,20,50,17);
	properties.add(minEdit);

	return properties;

    }

    public void  refreshProp()
    {
	maxEdit.setText(minValue + range + "");
	minEdit.setText(minValue + "");

    }

    public void setTempColor(Graphics graphics, float temp)
    {
        int percent;
	int r, g, b;
	r = 0;
	b = 0;
	g = 0;

	percent = (int)((temp - minValue)*100/ (range));

	if(percent < 0){
	    b = 255;
	} else if(percent < 25){
	    b = 255;
	    g = (255 * percent) / 25;
	} else if(percent < 50){
	    g = 255;
	    b = (255 * (50 - percent)) / 25;
	} else if(percent < 75){
	    g = 255;
	    r = (255 * (percent - 50)) / 25;
	} else if(percent < 100){
	    r = 255;
	    g = (255 * (100 - percent)) / 25;
	} else {
	    r = 255;
	}
	
	graphics.setColor(r, g, b);

	return;
    }

    public void updateProp()
    {

	minValue = Convert.toFloat(minEdit.getText());
	range = Convert.toFloat(maxEdit.getText()) - minValue;

	graph.setRange(minValue, range);
	length = 0;

	// repaint
	repaint();


    }

    public void addPoint(float x, float []y)
    {
	count++;	    
	    
	if(!enabled){
	    return;
	}

	// Plot data
	if(drawGraph){
	    myG.setColor(0,0,0);
	    graph.addPoint(0,length,y);
	    graph.plot(myG);
	}
	    
	if(count  >= 100){
	    drawGraph = false;
	}
    }


    public void onPaint(Graphics g)
    {
	Rect r = getRect();
	g.getClip(r);
	// redraw graph with latest data
	myG = new JGraphics(this);
	myG.setClip(r.x,r.y,r.width,r.height);
	graph.draw(myG,0,0);
	myG.clearClip();
    }

    public void onEvent(Event e)
    {
	if(e.type == PenEvent.PEN_DOWN && e.target == this){
	    prop.showProp(this);
	}

    }
	
}












