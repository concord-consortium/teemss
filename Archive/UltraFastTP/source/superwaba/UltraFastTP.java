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

    public void onStart()
    {
	mysize = getRect();

	setupButtons = new Button[Setups.buttonNames.length];
	int buttonY = (mysize.height - Setups.buttonNames.length * 25) / 2;
	int buttonX = (mysize.width - 100) / 2;
	for(int i=0; i< Setups.buttonNames.length; i++){
	    setupButtons[i] = new Button(Setups.buttonNames[i]);
	    setupButtons[i].setRect(buttonX, buttonY, 100, 20);
	    add(setupButtons[i]);
	    buttonY += 25;
	}	
    }

    void setup(float xRange, float yMin, float yMax, Transform sTrans, 
	       Transform eTrans, String units, int probeId)
    {
	for(int i = 0; i < setupButtons.length; i++){
	    remove(setupButtons[i]);
	}

	lg = new AnnotView(mysize.width -2, mysize.height-100);
	lg.setPos(1,40);
	lg.setRange((float)0, (float)xRange, yMin, yMax);
	add(lg);

	gt = new GraphTool(lg, sTrans, probeId, units, mysize.width, 40);
	gt.setPos(1, 1);
	add(gt);

	graphUp = new GraphUpdater(lg, gt);
	digDisp = new DigitalDisplay(gt.curTime, gt.curVal);
	logger = new LogFile(Setups.logName);
	
	eTrans.next = logger;
	logger.next = digDisp;
	digDisp.next = graphUp;
	digDisp.units = units;
    }

    public void onExit()
    {
	if(gt != null)
	    gt.onExit();
    }

    public void exit(int code)
    {
	if(code == 0){
	    if(gt !=  null){
		gt.onExit();
		remove(lg);
		remove(gt);
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













