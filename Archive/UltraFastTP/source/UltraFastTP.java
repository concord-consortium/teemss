import waba.ui.*;
import waba.fx.*;
import waba.io.*;
import waba.sys.*;
import waba.util.*;
import graph.*;

public class UltraFastTP extends MainWindow
{
    AnnotView lg;

    Transform startingTrans;
    GraphUpdater graphUp;
    DigitalDisplay digDisp;

    GraphTool gt = null;

    LogFile logger = null;
    Button [] setupButtons = null;
    Rect mysize;

    int gt_height = 40;
    int av_height = 0;

    	public UltraFastTP(){
		if(Vm.getPlatform().equals("Java")){//dima
		 	waba.io.impl.SerialManager.checkAvailableSerialPorts();
			waba.io.impl.SerialPortDesc sPortDesc = waba.io.impl.SerialManager.getAssignedPort();
			if(sPortDesc == null){
				waba.io.impl.SerialManager.showSetupDialog();
			}
		 }
	}
    public void onStart()
    {
	mysize = getRect();
	if(mysize.height == mysize.width){
	    gt_height = 20;
	}


	av_height = mysize.height-gt_height;
	if(Vm.getPlatform().equals("WinCE")){
	    av_height -= 40;
	} 

	if(Vm.getPlatform().equals("PalmOS")){
	    Bin.START_DATA_SIZE = 2500;
	}
	
	setupButtons = new Button[Setups.buttonNames.length];
	int buttonY = (mysize.height - Setups.buttonNames.length * 16) / 2;
	int buttonX = (mysize.width - 100) / 2;
	for(int i=0; i< Setups.buttonNames.length; i++){
	    setupButtons[i] = new Button(Setups.buttonNames[i]);
	    setupButtons[i].setRect(buttonX, buttonY, 100, 15);
	    add(setupButtons[i]);
	    buttonY += 16;
	}
    }

    Container me;

    void setup(float xRange, float yMin, float yMax, Transform sTrans, 
	       Transform eTrans, String units, int probeId)
    {
	removeAll();

	me = new Container();
	me.setRect(0,0,mysize.width,mysize.height);
	PropWindow.topContainer = me;
	add(me);

	lg = new AnnotView(mysize.width, av_height);
	lg.setPos(1,gt_height);
	lg.setRange((float)0, (float)xRange, yMin, yMax);
	me.add(lg);

	gt = new GraphTool(lg, sTrans, probeId, units, 
			   mysize.width, gt_height);
	gt.setPos(0, 0);
	me.add(gt);

	graphUp = new GraphUpdater(lg, gt);
	digDisp = new DigitalDisplay(gt.curTime, gt.curVal);
	logger = new LogFile(Setups.logName);
	
	eTrans.next = logger;
	logger.next = digDisp;
	digDisp.next = graphUp;
	digDisp.units = units;

    }

    public void exit(int code)
    {
	if(code == 0){
	    if(gt != null){
		gt.onExit();
		remove(me);
	    }
	    for(int i=0; i< Setups.buttonNames.length; i++){
		add(setupButtons[i]);
	    }	
	} else {
	    super.exit(code);
	}
    }

    void removeAll()
    {
	for(int i = 0; i < setupButtons.length; i++){
	    remove(setupButtons[i]);
	}
    }

    public void onExit()
    {
	if(gt != null)
	    gt.onExit();
    }


    public void onEvent(Event e)
    {
	if(e.type > 400){
	    gt.onEvent(e);
	} else if(e.type == ControlEvent.PRESSED){
	    for(int i=0; i<setupButtons.length; i++){
		if(e.target == setupButtons[i]){
		    Setups.setup(i, this);
		}
	    }
	}
    }

}













