import waba.ui.*;
import waba.fx.*;
import waba.io.*;
import waba.sys.*;
import waba.util.*;

public class UltraFastTP extends MainWindow
{
    GraphView lg;
    PushButtonGroup modControl, conControl;
    float data [] = new float [1];
    int i;
    A2DProbeManager pm;
    PSConnection connection;
    boolean local = true;
    Label curVal;
    Label localLabel;
    Edit addrEdit;
    Button discover, reset;
    boolean updateGraph;
    String logName = "data.txt";
    File logFile = null;
    DataStream logData = null;
    boolean wrotePID1 = false;

    public void onStart()
    {
	setRect(0,0,240, 320);

	lg = new GraphView(236,200);
	lg.setRect(1,40,236,200);
	add(lg);

	String [] names = new String [2];
	names [0] = "Start";
	names [1] = "Stop";
	modControl = new PushButtonGroup(names, true, 1, 4, 4, 2, true, PushButtonGroup.NORMAL); 
	modControl.setRect(1,1,28,30);
	add(modControl);

	reset = new Button("Reset");
	reset.setRect(30, 10, 40, 17);
	add(reset);

	addrEdit = new Edit();
	addrEdit.setRect(170, 1, 30, 17);
	addrEdit.setText("");

	names = new String  [2];
	names [0] = "Open";
	names [1] = "Close";
	conControl = new PushButtonGroup(names, true, 1, 4, 4, 2, true, PushButtonGroup.NORMAL); 
	conControl.setRect(200,1,31,30);
	add(conControl);

	curVal = new Label("0.00");
	curVal.setRect(1,240,50,20);
	add(curVal);

	pm = new A2DProbeManager("Ultra");
	connection = new PSConnection(this);
	connection.graph = lg;
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

    public void onEvent(Event e)
    {
	float newVal;
	MessageBox mb;

	if(e.type == ControlEvent.PRESSED){
	    Control target = (Control)e.target;
	    int index;
	    if(target == modControl){
		index = modControl.getSelected();
		if(index == 0){
		    // start
		    updateGraph = true;
		    if(i>= 100){
			i = 0;
			lg.reset();
		    }
		} else {
		    // stop
		    updateGraph = false;
		}
	    } else if(target == conControl){
		index = conControl.getSelected();
		if(index == 0){
		    if(timer == null){
			i = 0;
			lg.reset();
			if(local){
			    pm.start(1);			    
			} 
			if(!addrEdit.getText().equals("")){
			    if(addrEdit.getText().equals("0")){
				connection.open("127.0.0.1", local);
			    } else {
				connection.open("4.19.234." + addrEdit.getText(), local);
			    }
			}

			// Open the log file
			// put up message box
			mb = new MessageBox("Testing", "Testing MessageBox:  " + logName);
			popupModal(mb);
			
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
			    logData = null;
			} else {
			    logData = new DataStream(logFile);
			    wrotePID1 = false;
			}

			timer = addTimer(100);

		    }
		} else {
		    if(timer != null){
			if(local){
			    pm.stop(1);
			} 
			if(connection.pm != null){
			    connection.close();
			}
			removeTimer(timer);
			timer = null;

			// Close the log file
			if(logData != null){
			    logData.close();
			    logFile = null;
			    logData = null;
			}
		    }		    
		}
	    } else if(target == reset){
		i = 0;
		lg.reset();
	    } else if(target == discover){
		discover();
	    }
	} else if(e.type == ControlEvent.TIMER){
	    if(local){
		if(pm.getPackage()){
		    curVal.setText("" + pm.curData[0]);
		    if(pm.probeId == 1){
			data[0] = pm.curData[0];
			if(updateGraph){
			    lg.addPoint(i,data);
			} 
			i++;
			if(logData != null){
			    if(wrotePID1){
				// we need to finish the line
				logData.writeString("\r\n");
			    }
			    logData.writeString(Convert.toString(pm.curData[0]) + "\t");
			    wrotePID1 = true;
			}
		    } else if(pm.probeId == 2){
			if(logData != null){
			    if(!wrotePID1){
				logData.writeString("\t");
			    }
			    logData.writeString(Convert.toString(pm.curData[0]) + "\r\n");
			    wrotePID1 = false;
			}
		    }
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













