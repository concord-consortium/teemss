import waba.ui.*;
import waba.fx.*;
import waba.io.*;
import waba.sys.*;
import graph.*;

public class PSConnection extends Control
{
    final static int MAX_COUNT = 500;
    final static int DEF_PORT = 1234;

    ProbeManager pm = null;
    String addr;
    int port;
    int curStatusLine;
    MainWindow mw;
    GraphViewLine graph = null;
    Label curVal = null;
    int count;
    UltraFastTP uf;

    public PSConnection(UltraFastTP uf)
    {
	this.uf = uf;
	addr = null;
	mw = MainWindow.getMainWindow();
	
	port = DEF_PORT;
	mw = MainWindow.getMainWindow();

	curStatusLine = 0;
	pm = null;
    }

    public void writeExt(DataStream ds)
    {
	ds.writeShort(x);
	ds.writeShort(y);
	ds.writeShort(width);
	ds.writeShort(height);
	ds.writeString(addr);
    }

    public void readExt(DataStream ds)
    {
	int x,y, w, h;
	x = ds.readShort();
	y = ds.readShort();
	w = ds.readShort();
	h = ds.readShort();

	setRect(x,y,w,h);

	addr = ds.readString();
    }

    void printStatus(String s)
    {
	/*
	statusLines[curStatusLine].setText(s);
	curStatusLine++;
	curStatusLine = curStatusLine % statusLines.length;    
	*/
    }

    public void open(String a, boolean local)
    {
	if(pm == null){
	    addr = a;

	    // Do some error checking

	    pm = (ProbeManager) new JavaProbeManager(addr, port, 1, "wPC");
	    // should check status of create
	    if(pm.response == pm.ERROR){
		printStatus("Error opening");
		return;
	    }

	    int numProbes = discover();
	    if(numProbes == -1){
		// error in discover
		return;
	    }

	    printStatus(numProbes + " blocks");

	    count = 0;

	    if(local){
		buf[0] = (byte)'I';
		((JavaProbeManager)pm).dc.writeBytes(buf, 1);
	    }
	}		
    }

    public void close()
    {
	if(pm != null){
	    // disconnect
	    pm.close();
	    pm = null;
	}

    }
    
    byte buf [] = new byte [9];

    public void addPoint(float [] data)
    {
	if(pm  != null){
	    JavaProbeManager jpm = (JavaProbeManager)pm;
	    buf[0] = (byte)'D';
	    jpm.dc.writeInt(1, buf, 1);
	    jpm.dc.writeFloat(data[0], buf, 5);
	    jpm.dc.writeBytes(buf, 9);
	}
    }

    // return how many blocks are attached
    // -1 for an error
    public int discover()
    {
	ProbeInfo [] probes;
	int i,j;

	if(pm != null){
	    // We are currently streaming 
	    // restart
	    pm.close();

	    pm = (ProbeManager) new JavaProbeManager(addr, port, 0, "wPC");
	    if(pm.response == pm.ERROR){
		printStatus("Error Opening");
		return -1;
	    }
	}

	probes = pm.getProbes();
	if(probes == null){

	    if(pm.response == pm.ERROR){
		printStatus("getProbes failed");
		// disconnect
		pm = null;
		return -1;
	    } else {
		// there are no probes currently
		return 0;
	    }
	}
	
	// make a new array of blocks
	String name;
	for(i=0; i < probes.length; i++){
	    name = probes[i].strVal;
	    pm.readInfo(probes[i].id);

	    pm.start(probes[i].id);
	}
	 
	if(probes.length > 0){
	    count = 0;	    
	} 
		
	return probes.length;
    }

    boolean step(){
	int i;

	if(pm == null){
	    printStatus("step error");
	    return false;
	}

	pm.requestAck();

	// There is a bit of a bug here because
	// if an  ack is requested an the while exits
	// before getting the ack we will fall behind.
	while(true){
	    if(!pm.getPackage()){
		// we haven't recieved the ack back
		// but we exited?
		// this should be an error
		return false;
	    }

	    if(pm.response == pm.ACK){
		if(count > MAX_COUNT){
		    // we haven't received data in count steps
		    return false;
		} 
		count++;
		break;
	    }

	    if(pm.response == ProbeManager.NEW_PROBE){
		// make a new probe

	    } else {
		// find probe
		// act appropriatly function in probeState
		// info, data, stop
		switch(pm.response){
		case ProbeManager.PROBE_INFO :
		    // Find the probe
		    // This should tell us when a probe is detached
		    

		    break;
		case ProbeManager.PROBE_DATA :
		    int length = pm.curData.length;

		    // update the graph
		    if(uf.updateGraph){
			uf.lg.addPoint(0, 0, pm.curData[0], true);
		    }
		    curVal.setText("" + pm.curData[0]);

		    count = -1;
		    break;
		}
	    }
	}
  
	return true;
    }
       
}











