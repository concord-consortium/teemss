import waba.io.*;
import waba.sys.*;

public class A2DProbeManager extends ProbeManager
{
    int count = 0;
    int bCount = 0;
    int tmp;
    
    int packNum= 0;
    final static int BUF_SIZE = 20;
    final static byte MASK = (byte)0x80;
    byte position[] = {
	(byte)0x00,
	(byte)0x80,
	(byte)0x80,
	(byte)0x80, };
    SerialPort port = null;
    byte buf[] = new byte[BUF_SIZE];

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
	while(port.readBytes(buf, 0, BUF_SIZE) != 0);
	
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

    boolean getPackage()
    {
	int ret;
	probeId = 1;
	int offset;
	boolean failed = false;
	byte tmp;
	byte pos;
	int i;
	int value;

	if(started){
	    if(port == null){
		return false;
	    }

	    ret = port.readBytes(buf, 0, 8);	    
	    if(ret == -1){
		return false;
	    }

	    if(ret < 4){
		//println("Hit midd of package", 0);
		return false;
	    }

	    if(ret > 4){
		//println("Behind", 0);
	    }

	    offset = -1;
	    for(i=ret-1;i>=0; i--){
		if((buf[i] & MASK) == position[0]){
		    offset = i;
		}
	    }

	    if(offset == -1){
		// We didn't ge a package
		//println("Didn't get a full one", 0);
		return false;
	    }

	    failed = false;
	    value = 0;
	    for(i=0; i<4; i++){
		tmp = buf[i+offset];
		pos = (byte)(tmp & MASK);
		if(pos != position[i]){
		    failed = true;
		    break;
		}
		
		value |= (tmp & ~MASK) << ((3-i)*7);
	    }

	    if(failed){
		//println("Out of order recept", 0);
		return false;
	    }
    
	    // Ignore the change bit
	    probeId = ((value & 0x8000000) >> 27)+1;
	    value &= 0x7FFFFFF;

	    // Offset the value to zero
	    value = value - (int)0x4000000;

	    // Return ar reasonable resolution

	    response = PROBE_DATA;
	    curData[0] = (float)value * (float)0.075;
	    return true;
	}
	
	probeId = 0;
	response = PROBE_INFO;
	curInfo = new ProbeInfo(ProbeInfo.STOPPED, 
				"You already stopped the probe");
	curInfo.id = 1;
	return true;
    }

    boolean start(int id)
    {
	if(id == 1){
	    started = true;
	    if(port == null){
		port = new SerialPort(0,9600);
	    }
	    
	    if(!port.isOpen()){
		return false;
	    }

	    // need to set the timeout	    
	    if(Command('c', 'C') != 1){
		return false;
	    }

	    buf[0] = (byte)'d';
	    tmp = port.writeBytes(buf, 0, 1);
	    
	    port.setReadTimeout(200);
	}

	return true;
    }

    boolean stop(int id)
    {
	if(id == 1){
	    started = false;
	    if(port != null){
		port.close();
		port = null;
	    }
	}

	return true;
    }
    
    boolean readInfo(int id)
    {
	return true;
    }

}










