import waba.ui.*;
import waba.fx.*;
import waba.io.*;
import waba.sys.*;
import waba.util.*;
import graph.*;
import extra.ui.*;

public class GraphTool extends Container
{
    AnnotView lg;
    Pushbutton [] modControl;

    int i;
    ProbeManager pm;
    Label tOffVal, tVal;
    LabelBuf curVal, curTime;
    Label localLabel;
    Edit addrEdit;
    Button clearB;
    TextLine convertor = new TextLine("0");

    Transform startingTrans;
 
    int curProbe = 3;

    Label bytesRead = new Label("-2");
    Label line2 = new Label("" + (byte)'C');

    MainWindow mw = null;
    String units = "";

    public GraphTool(AnnotView av, Transform sTrans, int probeMode, 
		     String units, int w, int h) 
    {
	this.units = units;
	lg = av;
	curProbe = probeMode;

	convertor.maxDigits = 2;
	setRect(0,0,w, h);

	String [] names = new String [2];
	names [0] = "Start";
	names [1] = "Stop";
	modControl = Pushbutton.createGroup(names, 1);
	modControl[0].setRect(0,0,25,18);
	modControl[1].setRect(28,0,25,18);
	add(modControl[0]);
	add(modControl[1]);

	clearB = new Button("Clear");
	clearB.setRect(134, 0, 25, 17);
	add(clearB);

	curVal = new LabelBuf("");
	curVal.setRect(55,0,50,10);	
	curVal.setFont(new Font("Helvetica", Font.BOLD, 12));
	add(curVal);

	curTime = new LabelBuf("0.0s");
	curTime.setRect(55,10,50,10);
	curTime.setFont(new Font("Helvetica", Font.BOLD, 12));
	add(curTime);

	pm = new A2DProbeManager("Ultra");
	((A2DProbeManager)pm).gt = this;

	bytesRead.setRect(105, 0, 30, 10);
	add(bytesRead);
	
	line2.setRect(0, 10, 160, 10);
	//add(line2);

	startingTrans = sTrans;

	mw = MainWindow.getMainWindow();
    }

    public void setPos(int x, int y)
    {
	setRect(x,y,width,height);
    }

    Timer timer = null;
    float oldVal;

    public void onExit()
    {
	if(timer != null){
	    pm.stop(curProbe);
	    removeTimer(timer);
	    timer = null;
	    startingTrans.stop();
	}

    }

    void stop()
    {
	modControl[1].setSelected(true);

	if(lg.active){
	    lg.active = false;
	    lg.pause();
	}
	
	if(timer != null){
	    pm.stop(curProbe);

	    removeTimer(timer);
	    timer = null;
	    
	}		
	
	startingTrans.stop();
    
	repaint();
    }

    float curX = 0f;
    float curTemp = 0f;

    public void onEvent(Event e)
    {
	float newVal;

	if(e.type == ControlEvent.PRESSED){
	    Control target = (Control)e.target;
	    int index;
	    if(target == modControl[0] && modControl[0].isSelected()){
		// start
		lg.active = true;
		
		if(timer == null){
		    bytesRead.setText((byte)'C' + "");
		    if(!pm.start(curProbe, startingTrans)){
			
			 bytesRead.setText("Failed start");
			
		    }
		    
		    startingTrans.start();
		    
		    if(timer == null)
			timer = addTimer(100);

		    
		}
		// timer = null;
	    } else if(target == modControl[1] && modControl[1].isSelected()){
		stop();
	    } else if(target == clearB){
		// ask for confirmation
		    lg.active = false;
		    stop();
		    lg.reset();

		    curVal.setText("");
		    curTime.setText("0.0s");

	    } 
	} else if(e.type == 1003){
	    //	    System.out.println("Got 1003");
	    if(lg.lgView.selAnnot != null){
		curVal.setText(convertor.fToString(lg.lgView.selAnnot.value) + units);
		curTime.setText(convertor.fToString(lg.lgView.selAnnot.time) + "s");
	    } else {
		// hack
		curVal.setText("");
		curTime.setText("");
	    }		
	} else if(e.type == ControlEvent.TIMER){
	    pm.step();

	    if(pm.response == pm.ERROR){
		// put up message box
		// These happen rarely
		// mb = new MessageBox("Error", pm.msg);
		// popupModal(mb);			    		
		// System.out.println("Probe error: " + pm.msg);
	    }
	    lg.update();
	    startingTrans.update();
	}  
    }
    
}













