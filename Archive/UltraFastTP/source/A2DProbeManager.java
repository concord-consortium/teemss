/*
Copyright (C) 2001 Concord Consortium

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
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
    byte [] buf = new byte[BUF_SIZE];
    int bufSize = 0;
    int bufOffset = 0;

    int readSize = 512;

    //   Vector inputList = new Vector();

    final static int COMMAND_MODE = 0;
    final static int A2D_24_MODE = 1;
    final static int A2D_10_MODE = 2;
    final static int DIG_COUNT_MODE = 3;
    int mode = A2D_24_MODE;

    float timeStepSize = (float)0.333333;
    float curStepTime = 0f;

    GraphTool gt = null;

    public A2DProbeManager()
    {
    }

    public A2DProbeManager(String name)
    {
	this.name = "LOCAL";
	curData = new float[3];
	value = 0;
	started = false;
	
	probes[0] = new ProbeInfo(ProbeInfo.NAME, name);
	probes[0].id = 1;
	probeId = 1;
    }

    public int Command(byte command, byte response)
    {
	String input = "";

	if(port == null)
	    return 0;

	port.setReadTimeout(0);
	//	System.out.println("Reading bytes");
	while(port.readBytes(buf, 0, BUF_SIZE) > 0);
	// System.out.println("Read bytes");
	
	buf[0] = command;
	tmp = port.writeBytes(buf, 0, 1);
	if(tmp != 1){
	    // Failed write
	    return -1;
	}

	//	System.out.print("Reading bytes: ");
	port.setReadTimeout(1000);
	Vm.sleep(200);
	while(true){
	    tmp = port.readBytes(buf, 0, 1);

	    input += (char)buf[0];
	    if(tmp > 0){	       

		if(buf[0] == response) {
		    port.setReadTimeout(0);
		    return 1;
		} else {
		    continue;
		}
	    } else {
		// if(tmp > 0) System.out.println("" + buf[tmp-1]);
		// gt.line2.setText(input);
		return 0;
	    }
	}

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

	/*
	if(numValues == 0) return false;
	
	value = values[curValIndex];
	numValues--;
	curValIndex++;
	curValIndex%= VAL_BUF_SIZE;
	*/

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

    boolean gotChannel0 = false;
    int curTimeOffset = 0;

    public final static int TRANS_BUF_SIZE = 300;
    public final static int MIN_TRANS_SIZE = 150;
    float [] transData = new float [TRANS_BUF_SIZE];

    boolean convertValA2D(int value)
    {
	int curChannel = 0;
	boolean syncChannels = false;

	if(mode == A2D_24_MODE){
	    // Ignore the change bit
	    curChannel = ((value & 0x8000000) >> 27);
	    value &= 0x7FFFFFF;

	    // Offset the value to zero
	    value = value - (int)0x4000000;
	
	    // Return ar reasonable resolution
	    curData[curChannel+1] = (float)value * (float)0.000075;
	    syncChannels = true;

	} else if(mode == A2D_10_MODE){
	    // Ignore the change bit
	    // The channel bit is reversed on the 10bit converter hence
	    // the 2 -
	    curChannel = 1 - ((value & 0x02000) >> 13);
	    value = (value & 0x03F) | ((value >> 1) & 0x03C0);
	    
	    // Return a reasonable resolution
	    curData[curChannel+1] = (float)value * (float)3.22;

	    syncChannels = true;
	} else if(mode == DIG_COUNT_MODE){
	    curData[1] = (float)value;
	    curData[0] = curStepTime;
	    curStepTime += timeStepSize;
	    trans.transform(1, 2, curData);
	}

	if(syncChannels){
	    if(gotChannel0 && curChannel == 1){
		curData[0] = curStepTime;
		curStepTime += timeStepSize;
		trans.transform(1, 3, curData);
		gotChannel0 = false;
	    } else {
		gotChannel0 = curChannel == 0;
	    }
	}



	return true;
    }

    int curDataPos = 0;

    boolean step10bit()
    {
	int ret;
	byte tmp;
	byte pos;
	int i,j;
	int value;
	int curPos;
	int curChannel = 0;

	response = OK;

	while(true){
	    curChannel = 0;
	    if(port == null){
		break;
	    }

	    ret = port.readBytes(buf, bufOffset, readSize);

	    //	System.out.println("Read " + ret + " bytes");

	    if(ret <= 0){
		// there are no bytes available
		break;
	    } 

	    ret += bufOffset;	    
	    if(ret < 8){
		bufOffset = ret;
		break;
	    }
	    
	    curPos = 0;
	    int endPos = ret - 1;

	    while(curPos < endPos){
		// Check if the buf has enough space
		// if not this means a partial package was read
		    
		value = 0;
		tmp = buf[curPos++];
		pos = (byte)(tmp & MASK);
		if(pos != (byte)0x00){
		    // We found a bogus char 
		    continue;
		}
		
		value |= (tmp & (byte)0x07F) << 6;
		
		tmp = buf[curPos++];
		pos = (byte)(tmp & MASK);
		if(pos != (byte)0x80){
		    // We found a bogus char 
		    continue;
		}
		
		value |= (tmp & (byte)0x03F);
		
		// Ignore the change bit
		// The channel bit is reversed on the 10bit converter hence
		// the 2 -
		curChannel = 1 - ((value & 0x01000) >> 12);
		value &= 0x03FF;
		
		// Return a reasonable resolution
		curData[curChannel+1] = (float)value * (float)3.22;

		if(gotChannel0 && curChannel == 1){
		    transData[curDataPos++] = curStepTime;
		    transData[curDataPos++] = curData[1];
		    transData[curDataPos++] = curData[2];
		    curStepTime += timeStepSize;
		    if(curDataPos >= TRANS_BUF_SIZE){			
			trans.transform(TRANS_BUF_SIZE/3, 3, transData);
			curDataPos = 0;
		    }
		    gotChannel0 = false;
		} else {
		    gotChannel0 = curChannel == 0;
		}
		
	    }

	    if((ret - curPos) > 0){
		for(j=0; j<(ret-curPos); j++){
		    buf[j] = buf[curPos + j];
		}
		bufOffset = j;
	    }

	    if(curDataPos >= MIN_TRANS_SIZE){			
		trans.transform(curDataPos/3, 3, transData);
		curDataPos = 0;
	    }

	}

	return false;

    }

    public boolean stepGeneric()
    {
	int ret;
	int offset;
	byte tmp;
	byte pos;
	int i,j;
	int value;
	int curPos;
	int curChannel = 0;

	response = OK;

	ret = port.readBytes(buf, bufOffset, readSize);

	//	System.out.println("Read " + ret + " bytes");

	if(ret <= 0){
	    // there are no bytes available
	    return false;
	} 

	ret += bufOffset;	    
	curPos = 0;
	int endPos = ret;
	int packEnd = 0;

	while(curPos < endPos){
	    // Check if the buf has enough space
	    // if not this means a partial package was read
	    if((ret - curPos) < numBytes){
		for(j=0; j<(ret-curPos); j++){
		    buf[j] = buf[curPos + j];
		}
		bufOffset = j;
		return true;
	    }
		    
	    value = 0;
    	    for(i=0; i < numBytes; i++){
		tmp = buf[curPos++];
		pos = (byte)(tmp & MASK);
		if(pos != position[i]){
		    // We found a bogus char 
		    bufOffset = 0;
		    response = ERROR;
		    msg = "Error in serial stream:" + i + ":" + pos;

		    // set the buf to the next byte
		    for(j=0; j<(ret-curPos); j++){
			buf[j] = buf[curPos + j];
		    }
		    bufOffset = j;
		    return false;
		}
			
		value |= (tmp & (byte)~MASK) << (((numBytes-1)-i)*bitsPerByte);
	    }
		
	    convertValA2D(value);
	}
	
	bufOffset = 0;
	return true;

    }


    public int numBytes = 4;
    public int bitsPerByte = 7;
    public byte MASK = (byte)(0x0FF << bitsPerByte);

    byte position[] = {
	(byte)0x00,
	(byte)0x80,
	(byte)0x80,
	(byte)0x80, };

    int readTime = 0;

    boolean step()
    {
	probeId = 0;
	response = OK;

	if(started){
	    if(mode == A2D_10_MODE){
		return step10bit();
	    } else {
		return stepGeneric();
	    }
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
		port = new SerialPort(1,9600);
	    }
	    
	    if(!port.isOpen()){
		return false;
	    }

	    Vm.sleep(300);	    
	    // need to set the timeout	    
	    
	    if(Command((byte)'?', (byte)'?') != 1){
		return false;
	    }

	    Vm.sleep(300);
	    port.setReadTimeout(0);
	    tmp = port.readBytes(buf, 0, BUF_SIZE);

	    if(Command((byte)'1', (byte)'1') != 1){
		return false;
	    }

	    buf[0] = (byte)'!';
	    tmp = port.writeBytes(buf, 0, 1);
	    bufOffset = 0;
	    

	    port.setReadTimeout(0);
	}

	return true;
    }

    int curProbe = -1;
    Transform trans = null;

    boolean start(int id, Transform t)
    {
	if(port == null){
	    port = new SerialPort(1,9600);
	    //		System.out.println("Opened serial port");
	}
	
	if(!port.isOpen()){
	    return false;
	}

	trans = t;
	port.setFlowControl(false);

	switch(id){
	case 1:
	    if(!startA2D('d')) return false;
	    numBytes = 4;
	    bitsPerByte = 7;
	    MASK = (byte)(0x0FF << bitsPerByte);

	    position[0] = (byte)0x00;
	    position[1] = position[2] = position[3] = (byte)0x80;

	    timeStepSize = (float)0.333333;
	    mode = A2D_24_MODE;
	    break;
	case 2:
	    if(!startA2D('a')) return false;

	    numBytes = 2;
	    bitsPerByte = 7;
	    MASK = (byte)(0x0FF << bitsPerByte);

	    position[0] = (byte)0x00;
	    position[1] = (byte)0x80;

	    timeStepSize = (float)0.005;
	    mode = A2D_10_MODE;
	    curDataPos = 0;
	    break;
	case 3:
	    if(!startA2D('e')) return false;

	    numBytes = 1;
	    bitsPerByte = 8;
	    MASK = (byte)(0x00);

	    position[0] = (byte)0x00;

	    timeStepSize = (float)0.01;
	    mode = DIG_COUNT_MODE;
	    readSize = 100;
	    break;
	}
	
	started = true;
	curProbe = id;
	curStepTime = 0f;

	return true;

    }

    boolean startA2D(char startChar)
    {	
	// Let the device wake up a bit
	// But try to stop it as soon as we can
	buf[0] = (byte)'c';
	for(int i=0; i<5; i++){
	    tmp = port.writeBytes(buf, 0, 1);
	    Vm.sleep(150);
	}

	// incase the the port is left open
	// stop it
	
	if(!port.setReadTimeout(100)){
	    //	System.out.println("Failed to set read timeout");
	}

	// need to set the timeout
	int ret;
	if((ret = Command((byte)'c', (byte)67)) != 1){
	    //	    System.out.println("Failed Command: " + ret);
	    gt.bytesRead.setText("Cm:" + ret);
	    port.close();
	    port = null;
	    return false;
	}
	//	    System.out.println("Sent c command");

	port.setReadTimeout(0);
	
	tmp = port.readBytes(buf, 0, BUF_SIZE);
	//	    System.out.println("Read extrainous bytes");

	buf[0] = (byte)startChar;
	tmp = port.writeBytes(buf, 0, 1);
	bufOffset = 0;
	//	    System.out.println("Started data");
	
	port.setReadTimeout(0);

	return true;
    }

    boolean stop(int id)
    {
	//	System.out.println("Stopping");

	if(id == curProbe){
	    started = false;
	    if(port != null){
		port.close();
		port = null;
	    }

	}

	if(id == 2){
	    if(curDataPos > 0){
		trans.transform(curDataPos/3, 3, transData);
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










