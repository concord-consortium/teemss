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

    public void onStart()
    {
	Bin.START_DATA_SIZE = 3000;

	setupButtons = new Button[Setups.buttonNames.length];
	int buttonY = (160 - Setups.buttonNames.length * 16) / 2;
	for(int i=0; i< Setups.buttonNames.length; i++){
	    setupButtons[i] = new Button(Setups.buttonNames[i]);
	    setupButtons[i].setRect(20, buttonY, 100, 15);
	    add(setupButtons[i]);
	    buttonY += 16;
	}
    }

    public void exit(int code)
    {
	if(code == 0){
	    if(gt != null){
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

    Container me = new Container();

    void setup(float xRange, float yMin, float yMax, Transform sTrans, 
	       Transform eTrans, String units, int probeId)
    {
	me.setRect(0,0,160,160);
	PropWindow.topContainer = me;
	add(me);

	removeAll();

	lg = new AnnotView(160,140);
	lg.setPos(1,20);
	lg.setRange((float)0, (float)xRange, yMin, yMax);
	me.add(lg);

	gt = new GraphTool(lg, sTrans, probeId, units, 160, 20);
	gt.setPos(0, 0);
	me.add(gt);

	graphUp = new GraphUpdater(lg, gt);
	digDisp = new DigitalDisplay(gt.curTime, gt.curVal);
	
	eTrans.next = digDisp;
	digDisp.next = graphUp;
	digDisp.units = units;
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













