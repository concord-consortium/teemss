import waba.io.*;
import waba.sys.*;
import waba.util.*;

public class A2DProbeManager extends ProbeManager
{
    int count = 0;
    int bCount = 0;
    int tmp;

    int curTime = 0;

    int packNum= 0;
    final static int BUF_SIZE = 1000;
    SerialPort port = null;
    byte buf[] = new byte[BUF_SIZE];
    int bufSize = 0;
    int bufOffset = 0;

    Vector inputList = new Vector();

    public A2DProbeManager()
    {
    }

    public A2DProbeManager(String name)
    {
	this.name = "LOCAL";
	posInfo = INFO_SIZE;
	curData = new float[1];
	value = 0;
	started = false;
	
	probes[0] = new ProbeInfo(ProbeInfo.NAME, name);
	probes[0].id = 1;
	probeId = 1;
    }

    public int Command(char command, char response)
    {
	if(port == null)
	    return 0;

	port.setReadTimeout(0);
	//	System.out.println("Reading bytes");
	while(port.readBytes(buf, 0, BUF_SIZE) > 0);
	// System.out.println("Read bytes");    
	
	port.setReadTimeout(500);
	
	buf[0] = (byte)command;
	tmp = port.writeBytes(buf, 0, 1);
	if(tmp != 1){
	    // Failed write
	    return -1;
	}

	int time = Vm.getTimeStamp();
	while((Vm.getTimeStamp() - time) < 500){
	    tmp = port.readBytes(buf, 0, 1);
	    if(tmp == 1){
		if(buf[0] == (byte)response){
		    // success
		    return 1;
		} else {
		    // We got a char just not the right one
		}
	    } else {
		// Failed Read
		return -2;
	    }
	}

	return 0;
    }

    void close(){}

    ProbeInfo [] getProbes()
    {
	return probes;
    }

    void requestAck()
    {
    }

    // Sonar Range
    boolean convertValSR()
    {
	/*
	 *  add the following defines
	 *     public final static int numBytes = 3;
	 *     public final static int bitsPerByte = 6;
	 *
	 *  byte position[] = {
	 *   (byte)0x40,
	 *   (byte)0x80,
	 *   (byte)0xC0, };
	 */
	
	int [] valPtr;

	if(inputList.getCount() == 0) return false;
	
	// We should give the next point in the input queue
	valPtr = (int [])(inputList.get(0));
	curTime = valPtr[0];
	value = valPtr[1];
	inputList.del(0);
	
	// Ignore the change bit
	//	probeId = ((value & 0x8000000) >> 27)+1;
	value &= 0x03FFF;

	// Offset the value to zero
	//	value = value - (int)0x4000000;
	
	// Return ar reasonable resolution
	curData[0] = (float)value;

	response = PROBE_DATA;
	return true;
    }

    boolean convertVal24()
    {
	// you must set the number of bytes to 4

	int [] valPtr;

	if(inputList.getCount() == 0) return false;
	
	// We should give the next point in the input queue
	valPtr = (int [])(inputList.get(0));
	curTime = valPtr[0];
	value = valPtr[1];
	inputList.del(0);
	
	// Ignore the change bit
	probeId = ((value & 0x8000000) >> 27)+1;
	value &= 0x7FFFFFF;

	// Offset the value to zero
	value = value - (int)0x4000000;
	
	// Return ar reasonable resolution
	curData[0] = (float)value * (float)0.000075;

	response = PROBE_DATA;
	return true;
    }

    boolean convertVal10()
    {
	// note you must set the number of bytes to 2

	int [] valPtr;

	if(inputList.getCount() == 0) return false;
	
	// We should give the next point in the input queue
	valPtr = (int [])(inputList.get(0));
	curTime = valPtr[0];
	value = valPtr[1];
	inputList.del(0);
	
	// Ignore the change bit
	// The channel bit is reversed on the 10bit converter hence
	// the 2 -
	probeId = 2 - ((value & 0x02000) >> 13);
	value = (value & 0x03F) | ((value >> 1) & 0x03C0);

	// Return ar reasonable resolution
	curData[0] = (float)value * (float)3.22;

	response = PROBE_DATA;
	return true;
    }



    public final static int numBytes = 4;
    public final static int bitsPerByte = 7;
    public final static byte MASK = (byte)(0x0FF << bitsPerByte);

    byte position[] = {
	(byte)0x00,
	(byte)0x80,
	(byte)0x80,
	(byte)0x80, };

    boolean getPackage()
    {
	int ret;
	probeId = 1;
	int offset;
	boolean failed = false;
	byte tmp;
	byte pos;
	int i,j;
	int value;
	int [] valPtr;
	int curPos;

	response = OK;

	if(started){
	    if(port == null){
		return false;
	    }
	    if(convertVal24())
		return true;

	    ret = port.readBytes(buf, bufOffset, BUF_SIZE - bufOffset);
	    if(ret == -1){
		// there are no bytes available
		return false;
	    }
	    
	    //	    System.out.println("Read " + ret + "bytes");

	    ret += bufOffset;	    
	    if(ret < numBytes){
		// There aren't enough bytes in the stream
		bufOffset = ret;
		return false;
	    }

	    curPos = 0;
	    while(curPos < ret){
		// Find the first byte
		offset = -1;
		for(i=curPos;i<ret; i++){
		    if((byte)(buf[i] & MASK) == position[0]){
			offset = i;
			break;
		    }
		}

		if(offset == -1){
		    // We didn't find a package
		    // This is bad this means there is bogus chars in 
		    // the stream 
		    bufOffset = 0;
		    response = ERROR;
		    msg = "Error serial stream: 1";
		    return false;
		}

		// Check if the buf has enough space
		// if not this means a partial package was read
		if((ret - offset) < numBytes){
		    for(j=0; j<(ret-offset); j++){
			buf[j] = buf[offset + j];
		    }
		    bufOffset = j;
		    break;
		}

		failed = false;
		value = 0;
		for(i=0; i < numBytes; i++){
		    tmp = buf[i+offset];
		    pos = (byte)(tmp & MASK);
		    if(pos != position[i]){
			failed = true;
			break;
		    }
		
		    value |= (tmp & (byte)~MASK) << (((numBytes-1)-i)*bitsPerByte);
		}

		if(failed){
		    // We found a bogus char 
		    bufOffset = 0;
		    response = ERROR;
		    msg = "Error in serial stream: 2";
		    return false;
		}
		
		valPtr = new int[2];
		valPtr[0] = Vm.getTimeStamp();
		valPtr[1] = value;
		inputList.add(valPtr);
		curPos = offset + numBytes;
	    }    
	    //	    System.out.println("Parsed " + curPos + " bytes");

	    return convertVal24();
	}
	
	probeId = 0;
	response = PROBE_INFO;
	curInfo = new ProbeInfo(ProbeInfo.STOPPED, 
				"You already stopped the probe");
	curInfo.id = 1;
	return true;
    }

    boolean startSR(int id)
    {
	if(id == 1){
	    started = true;

	    if(port == null){
		//		port = new SerialPort(0,9600, 8, false, 2);
		port = new SerialPort(0,9600);
	    }
	    
	    if(!port.isOpen()){
		return false;
	    }

	    Vm.sleep(300);	    
	    // need to set the timeout	    
	    
	    if(Command('?', '?') != 1){
		return false;
	    }

	    Vm.sleep(300);
	    port.setReadTimeout(0);
	    tmp = port.readBytes(buf, 0, BUF_SIZE);

	    if(Command('1', '1') != 1){
		return false;
	    }

	    buf[0] = (byte)'!';
	    tmp = port.writeBytes(buf, 0, 1);
	    bufOffset = 0;
	    

	    port.setReadTimeout(0);
	}

	return true;
    }



    boolean start(int id)
    {
	if(id == 1){
	    started = true;

	    if(port == null){
		port = new SerialPort(0,9600);
		//		System.out.println("Opened serial port");
	    }
	    
	    if(!port.isOpen()){
		return false;
	    }

	    // incase the the port is left open
	    // stop it
	    buf[0] = (byte)'c';
	    tmp = port.writeBytes(buf, 0, 1);
	    //	    System.out.println("Stopped port");
	    
	    if(!port.setReadTimeout(100)){
		//	System.out.println("Failed to set read timeout");
	    }

	    // need to set the timeout	    
	    if(Command('c', 'C') != 1){
		return false;
	    }
	    //	    System.out.println("Sent c command");

	    Vm.sleep(300);
	    port.setReadTimeout(0);
	    tmp = port.readBytes(buf, 0, BUF_SIZE);
	    //	    System.out.println("Read extrainous bytes");

	    buf[0] = (byte)'d';
	    tmp = port.writeBytes(buf, 0, 1);
	    bufOffset = 0;
	    //	    System.out.println("Started data");

	    port.setReadTimeout(0);
	}

	return true;
    }

    boolean stop(int id)
    {
	//	System.out.println("Stopping");

	if(id == 1){
	    started = false;
	    if(port != null){
		port.close();
		port = null;
	    }

	}

	//	System.out.println("Stopped");

	return true;
    }
    
    boolean readInfo(int id)
    {
	return true;
    }

}










