import waba.ui.*;
import waba.fx.*;
import waba.io.*;
import waba.sys.*;
import waba.util.*;
import graph.*;

public class GraphTool extends Container
{
    AnnotView lg;
    PushButtonGroup modControl, conControl;
    int i;
    ProbeManager pm;
    Label tOffVal, tVal;
    LabelBuf curVal, curTime;
    Label localLabel;
    Button clearB, exitB;
    TextLine convertor = new TextLine("0");

    Transform startingTrans;
 
    int curProbe = 3;

    Label bytesRead = new Label("-2");
    Label line2 = new Label("-1");

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
	modControl = new PushButtonGroup(names, true, 1, 15, 10, 1, true, PushButtonGroup.BUTTON); 
	modControl.setRect(1,1,80,30);
	add(modControl);

	clearB = new Button("Clear");
	clearB.setRect(175, 10, 30, 17);
	add(clearB);

	exitB = new Button("Exit");
	exitB.setRect(206, 10, 30, 17);
	add(exitB);

	names = new String  [2];
	names [0] = "Open";
	names [1] = "Close";
	conControl = new PushButtonGroup(names, true, 1, 4, 4, 2, true, PushButtonGroup.NORMAL); 
	conControl.setRect(200,1,31,30);
	//	add(conControl);


	curVal = new LabelBuf("");
	curVal.setRect(90,1,70,25);	
	curVal.setFont(new Font("Helvetica", Font.BOLD, 16));
	add(curVal);

	curTime = new LabelBuf("0.0s");
	curTime.setRect(90,18,70,23);
	curTime.setFont(new Font("Helvetica", Font.BOLD, 16));
	add(curTime);

	tOffVal = new Label("0.00");
	tOffVal.setRect(100, 240, 60, 20);
	add(tOffVal);

	tVal = new Label("0");
	tVal.setRect(190, 240, 50, 20);
	add(tVal);

	pm = new A2DProbeManager("Ultra");
	((A2DProbeManager)pm).gt = this;

	mw = MainWindow.getMainWindow();

	bytesRead.setRect(0, mw.getRect().height-20, 200, 20);
	add(bytesRead);

	startingTrans = sTrans;

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
	modControl.setSelected(1);

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

    MessageBox confirmClear = null;
    MessageBox confirmExit = null;
    String [] confButtons = {"Yes", "No"};
    float curX = 0f;
    float curTemp = 0f;

    public void onEvent(Event e)
    {
	float newVal;
	MessageBox mb;
	boolean doPlot = true;

	if(e.type == ControlEvent.WINDOW_CLOSED){
	    if(e.target == confirmClear){
		if(confirmClear.getPressedButtonIndex() == 0){
		    // doit
		    lg.active = false;
		    modControl.setSelected(1);
		    stop();
		    lg.reset();
		    
		    curVal.setText("");
		    curTime.setText("0.0s");

		} 
	    } else if(e.target == confirmExit){
		if(confirmExit.getPressedButtonIndex() == 0){
		    // doit
		    mw.exit(0);
		} 
	    }	    	    
	} else if(e.type == ControlEvent.PRESSED){
	    Control target = (Control)e.target;
	    int index;
	    if(target == modControl){
		index = modControl.getSelected();
		if(index == 0){
		    // start
		    lg.active = true;

		    /*
		      int i=0;
		      for(;i<50;i++){
		      lg.addPoint(0, i, i, doPlot);
		      }
		      lg.curView.draw();
		      modControl.setSelected(1);
		      curX = 49f;
		      curTemp = 49f;
		      
		      timer = new Timer();
		    */

		    curX = 0f;

		    if(timer == null){
			bytesRead.setText((byte)'C' + "");
			if(!pm.start(curProbe, startingTrans)){
				
			    bytesRead.setText("Failed start");
			    
			}

			startingTrans.start();

			if(timer == null)
			    timer = addTimer(1);

		    }
		    // timer = null;
		} else {
		    stop();
		}
	    } else if(target == clearB){
		// ask for confirmation
		confirmClear = new MessageBox("Confirm Clear",
						"Are you sure you want to|erase everything?",
						confButtons);
		mw.popupModal(confirmClear);
	    } else if(target == exitB){
		// ask for confirmation
		// ask for confirmation
		confirmExit = new MessageBox("Confirm Exit",
						"Are you sure you want to exit|and lose your work?",
						confButtons);
		mw.popupModal(confirmExit);
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
	    int count = 0;
	    while(pm.step() && count < 10){
		if(pm.probeId == curProbe){
		    //			System.out.println("Got data: " + pm.curData[0] + ", " + 
		    //		   pm.curData[1] + ", ");
		    
		    // startingTrans.transform(pm.curData);
		    
		}
		count++;
		
	    }

	    if(pm.response == pm.ERROR){
		// put up message box
		// These happen rarely
		// mb = new MessageBox("Error", pm.msg);
		// popupModal(mb);			    		
	    }
	    lg.update();
	    startingTrans.update();
	}  
    }
    
}













