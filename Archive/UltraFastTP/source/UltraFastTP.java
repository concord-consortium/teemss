import waba.ui.*;
import waba.fx.*;
import waba.io.*;
import waba.sys.*;
import waba.util.*;
import graph.*;

public class UltraFastTP extends MainWindow
{
    AnnotView lg;
    PushButtonGroup modControl, conControl;
    int i;
    A2DProbeManager pm;
    PSConnection connection;
    boolean local = true;
    Label curVal, tOffVal, tVal;
    Label localLabel;
    Edit addrEdit;
    Button discover, clearB, exitB;
    boolean updateGraph;
    String logName = "data.txt";
    File logFile = null;
    boolean wrotePID1 = false;
    TextLine convertor = new TextLine("0");

    public void onStart()
    {
	convertor.maxDigits = 2;

	setRect(0,0,240, 320);

	lg = new AnnotView(236,200);
	lg.setPos(1,40);

	lg.xStepSize = (float)0.333;
	lg.setRange((float)0, (float)120, (float)0, (float)50);

	add(lg);


	String [] names = new String [2];
	names [0] = "Start";
	names [1] = "Stop";
	modControl = new PushButtonGroup(names, true, 1, 4, 4, 2, true, PushButtonGroup.NORMAL); 
	modControl.setRect(1,1,28,30);
	add(modControl);

	clearB = new Button("Clear");
	clearB.setRect(175, 10, 30, 17);
	add(clearB);

	exitB = new Button("Exit");
	exitB.setRect(206, 10, 30, 17);
	add(exitB);

	addrEdit = new Edit();
	addrEdit.setRect(170, 1, 30, 17);
	addrEdit.setText("");

	names = new String  [2];
	names [0] = "Open";
	names [1] = "Close";
	conControl = new PushButtonGroup(names, true, 1, 4, 4, 2, true, PushButtonGroup.NORMAL); 
	conControl.setRect(200,1,31,30);
	//	add(conControl);


	curVal = new Label("0.00");
	curVal.setRect(1,240,60,20);
	add(curVal);

	tOffVal = new Label("0.00");
	tOffVal.setRect(100, 240, 60, 20);
	add(tOffVal);

	tVal = new Label("0");
	tVal.setRect(190, 240, 50, 20);
	add(tVal);

	pm = new A2DProbeManager("Ultra");
	connection = new PSConnection(this);
	connection.graph = lg.lgView;
	connection.curVal = curVal;
    }

    Timer timer = null;
    float oldVal;

    public void onExit()
    {
	if(timer != null){
	    pm.stop(1);
	    removeTimer(timer);
	    timer = null;
	}

	// Close the log file
	if(logFile != null){
	    logFile.close();
	    logFile = null;
	}
    }

    public void discover()
    {
	if(local){
	    if(timer != null){
		pm.stop(1);
		removeTimer(timer);
		timer = null;
	    }
	    local = false;
	    localLabel.setText("remote");
	}
	
	if(pm.start(1)){
	    // we found a local probe on the serial port
	    local = true;
	    localLabel.setText("local");
	}
	pm.stop(1);
	modControl.setSelected(1);	    		
    }

    public void writeString(String str)
    {
	int i;
	byte [] outChar = new byte [str.length()];

	if(logFile != null){
	    for(i=0; i<str.length(); i++){
		outChar[i] = (byte)str.charAt(i);
	    }
	    
	    logFile.writeBytes(outChar, 0, str.length());
	}

    }

    public final static float temperature (float mV)
    {

	float mV2 = mV * mV;
	float mV3 = mV2 * mV;
	return mV * (float)17.084 + mV2 * (float)-0.25863 + mV3 * (float)0.011012;
    }

    float lastColdJunct = 0;
    MessageBox confirmClear = null;
    MessageBox confirmExit = null;
    String [] confButtons = {"Yes", "No"};

    public void onEvent(Event e)
    {
	float newVal;
	MessageBox mb;
	float curTemp = 0f;
	boolean doPlot = true;

	if(e.type == ControlEvent.WINDOW_CLOSED){
	    if(e.target == confirmClear){
		if(confirmClear.getPressedButtonIndex() == 0){
		    // doit
		    updateGraph = false;
		    modControl.setSelected(1);
		    if(timer != null){
			if(local){
			    pm.stop(1);
			} 
			if(connection.pm != null){
			    connection.close();
			}
			removeTimer(timer);
			timer = null;
		    
		    }		    		
		    i = 0;
		    lg.reset();
		    // Close the log file
		    if(logFile != null){
			logFile.close();
			logFile = null;
		    }

		} 
	    } else if(e.target == confirmExit){
		if(confirmExit.getPressedButtonIndex() == 0){
		    // doit
		    exit(0);
		} 
	    }	    	    
	} else if(e.type == ControlEvent.PRESSED){
	    Control target = (Control)e.target;
	    int index;
	    if(target == modControl){
		index = modControl.getSelected();
		if(index == 0){
		    // start
		    updateGraph = true;
		    if(timer == null){
			if(local){
			    pm.start(1);
			    //		    System.out.println("Started Serial");
			} 
			if(!addrEdit.getText().equals("")){
			    if(addrEdit.getText().equals("0")){
				connection.open("127.0.0.1", local);
			    } else {
				connection.open("4.19.234." + addrEdit.getText(), local);
			    }
			}

			// Open the log file
			if(false && logFile == null){
			    logFile = new File(logName, File.DONT_OPEN);
			    if(logFile.exists()){
				logFile.delete();
			    }
			    logFile.close();
			    logFile = new File(logName, File.CREATE);
			    if(!logFile.isOpen()){
				// put up message box
				mb = new MessageBox("Error", "Can't open file: " + logName);
				popupModal(mb);
				logFile = null;
			    } else {
				wrotePID1 = false;
			    }
			}
			if(timer == null)
			    timer = addTimer(10);

		    }
		} else {
		    // stop
		    if(updateGraph){
			updateGraph = false;
			lg.pause();
		    }

		    if(timer != null){
			if(local){
			    pm.stop(1);
			} 
			if(connection.pm != null){
			    connection.close();
			}
			removeTimer(timer);
			timer = null;
			
		    }		    
		}
	    } else if(target == clearB){
		// ask for confirmation
		confirmClear = new MessageBox("Confirm Clear",
						"Are you sure you want to|erase everything?",
						confButtons);
		popupModal(confirmClear);
	    } else if(target == exitB){
		// ask for confirmation
		// ask for confirmation
		confirmExit = new MessageBox("Confirm Exit",
						"Are you sure you want to exit|and lose your work?",
						confButtons);
		popupModal(confirmExit);
	    } else if(target == discover){
		discover();
	    }
	} else if(e.type == ControlEvent.TIMER){
	    if(local){
		int count = 0;
		boolean addedPoint = false;
		while(pm.getPackage()){
		    if(pm.probeId == 1){
			i++;
			if(logFile != null){
			    if(wrotePID1){
				// we need to finish the line
				writeString("null\r\n");
			    }
			    writeString(Convert.toString(pm.curData[0]) + "\t");
			    wrotePID1 = true;
			}
			if(i > 1){
			    curTemp = temperature(pm.curData[0]) + lastColdJunct;
			    //  doPlot = (Vm.getTimeStamp() - pm.curTime) < 10;
			    if(updateGraph){
				addedPoint = true;
				if(!lg.addPoint(0, i-2, curTemp, doPlot)){
				    updateGraph = false;
				    lg.length = (float)0.0;
				    lg.curView.draw();
				    modControl.setSelected(1);
				    return;
				    
				}

			    }
			    if(doPlot){
				curVal.setText(convertor.fToString(curTemp));
			    }
			}
		    } else if(pm.probeId == 2){
			if(logFile != null){
			    if(!wrotePID1){
				writeString("null\t");
			    }
			    writeString(Convert.toString(pm.curData[0]) + "\r\n");
			    wrotePID1 = false;
			}
			lastColdJunct = (pm.curData[0] / 10) - (float)50;
			//doPlot = (Vm.getTimeStamp() - pm.curTime) < 10; 
			doPlot = false;
			if(doPlot){
			    //  tOffVal.setText("" + lastColdJunct);
			}
			
		    } else {
			// something funny going on
			if(logFile != null){
			    if(wrotePID1){
				writeString("null\r\n");
			    }
			    writeString("GotPackage for: " + pm.probeId + "\r\n");
			}
		    }

		    count++;
		} 
		if(addedPoint){
		    lg.update();
		    curVal.setText(convertor.fToString(curTemp));
		}

		if(pm.response == pm.ERROR){
		    // put up message box
		    // These happen rarely
		    // mb = new MessageBox("Error", pm.msg);
		    // popupModal(mb);			    		
		}
	    } else {
		if(!connection.step()){
		    conControl.setSelected(1);
		    connection.close();
		    if(timer != null){
			removeTimer(timer);
			timer = null;
		    }
		} else {
		    i++;
		}

	    }
	    

	}     
    }

}













