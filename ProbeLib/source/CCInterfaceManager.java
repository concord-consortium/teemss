package org.concord.waba.extra.probware;
import waba.ui.Control;
import waba.sys.Vm;
import waba.io.SerialPort;
import waba.ui.Event;
import waba.ui.ControlEvent;
import waba.ui.Timer;
import org.concord.waba.extra.event.DataListener;
import org.concord.waba.extra.event.DataEvent;
import extra.util.DataDesc;

public class CCInterfaceManager extends Control{
static protected CCInterfaceManager im = null;
protected  SerialPort port;
static int refCount = 0;
public int		startTimer =  0;

protected Timer	timer = null;

public 	waba.util.Vector 	dataListeners = new waba.util.Vector();
public final static int COMMAND_MODE = 0;
public final static int A2D_24_MODE = 1;
public final static int A2D_10_MODE = 2;
public final static int DIG_COUNT_MODE = 3;


protected ProbManager	pb = null;
	protected CCInterfaceManager(){
	}
	
	public void setProbManager(ProbManager pb){
		this.pb = pb;
	}
	
	public void start(){
		port = new SerialPort(1,9600);
		timer = addTimer(getRightMilliseconds());
		startTimer = Vm.getTimeStamp();
		if((port == null) || !port.isOpen()) return;
		port.setFlowControl(false);
	    	setByteStreamProperties();
	    	if(!startA2D(getStartChar())) return;
		setCurTime(0.0f);
		valueData[0] = curStepTime;
		gotChannel0 = false;
		dDesc.setDt(timeStepSize);
		dDesc.setChPerSample(2);
		dEvent.setDataOffset(1);
		dEvent.setDataDesc(dDesc);
	}
	public void stop(){
		if(timer != null){
			removeTimer(timer);
			timer = null;
		}
		if(port != null){
			port.close();
			port = null;
		}
	}
	
	public static CCInterfaceManager getInterfaceManager(){
		if(im == null) im = new CCInterfaceManager();
		refCount++;
		return im;
	}
	//we need optimization probably: dynamically calculate getRightMilliseconds
	public int getRightMilliseconds(){return 50;}
	
	public void onEvent(Event event){
    		if (event.type==ControlEvent.TIMER){
    			doRightThings();
		}
	}
	
	protected void doRightThings(){
		if((port == null) || !port.isOpen()) return;
		if(mode == A2D_10_MODE){
			step10bit();
		}else{
			stepGeneric();
		}

	}

	boolean step10bit(){
		if((port == null) || !port.isOpen()) return false;
		int ret;
		byte tmp;
		byte pos;
		int i,j;
		int value;
		int curPos;
		int curChannel = 0;

//		response = OK;

		
		while(true){
			curChannel = 0;
			ret = port.readBytes(buf, bufOffset, readSize);
			if(ret <= 0) break; // there are no bytes available
			ret += bufOffset;	    
			if(ret < 8){
				bufOffset = ret;//too few?
				break;
			}
			curPos = 0;
			int endPos = ret - 1;

			curDataPos = 0;
			valueData[0] = curStepTime;
			while(curPos < endPos){
						// Check if the buf has enough space
						// if not this means a partial package was read

				value = 0;
				tmp = buf[curPos++];
				pos = (byte)(tmp & MASK);
				if(pos != (byte)0x00) continue; // We found a bogus char 

				value |= (tmp & (byte)0x07F) << 6;
				tmp = buf[curPos++];
				pos = (byte)(tmp & MASK);
				if(pos != (byte)0x80) continue; // We found a bogus char 

				value |= (tmp & (byte)0x03F);
				// Ignore the change bit
				// The channel bit is reversed on the 10bit converter hence
				// the 2 -
				curChannel = 1 - ((value & 0x01000) >> 12);
				value &= 0x03FF;

				// Return a reasonable resolution
				float rValue = (float)value * tuneValue;

				if(gotChannel0 && curChannel == 1){
					curStepTime += timeStepSize;
					gotChannel0 = false;
					curData[curChannel + 1] = rValue;
					curData[0] = curStepTime;
					valueData[1+(curDataPos++)] = curData[1];
					valueData[1+(curDataPos++)] = curData[2];
				} else {
					curData[1] = rValue;
					gotChannel0 = (curChannel == 0);
				}
			}
			dEvent.setNumbData(curDataPos);
			dEvent.setData(valueData);
			dEvent.setTime(valueData[0]);
			notifyProbManager(dEvent);
			if((ret - curPos) > 0){
				for(j=0; j<(ret-curPos); j++) buf[j] = buf[curPos + j];
				bufOffset = j;
			}
		}
		return false;
    }
	
	protected void notifyProbManager(DataEvent e){
			if(pb != null) pb.transform(e);
/*
		if(dataListeners != null){
			for(int cl = 0; cl < dataListeners.getCount(); cl++){
				DataListener l = (DataListener)dataListeners.get(cl);
				l.dataReceived(e);
			}
		}
*/
	}
	
	boolean stepGeneric(){
		if((port == null) || !port.isOpen()) return false;
		int ret;
		int offset;
		byte tmp;
		byte pos;
		int i,j;
		int value;
		int curPos;

//		response = OK;
		ret = port.readBytes(buf, bufOffset, readSize);
		if(ret <= 0)	return false;

		ret += bufOffset;	    
		curPos = 0;
		int endPos = ret;
		int packEnd = 0;

		curDataPos = 0;
		valueData[0] = curStepTime;
		boolean clearBufferOffset = true;
		while(curPos < endPos){
		    // Check if the buf has enough space
		    // if not this means a partial package was read
			if((ret - curPos) < numBytes){
				for(j=0; j<(ret-curPos); j++) buf[j] = buf[curPos + j];
				bufOffset = j;
				clearBufferOffset = false;
				break;
			}
		  	value = 0;
			for(i=0; i < numBytes; i++){
				tmp = buf[curPos++];
				pos = (byte)(tmp & MASK);
				if(pos != position[i]){
					// We found a bogus char 
					bufOffset = 0;
					//response = ERROR;
					//msg = "Error in serial stream:" + i + ":" + pos;

					// set the buf to the next byte
					for(j=0; j<(ret-curPos); j++) buf[j] = buf[curPos + j];
					bufOffset = j;
					return false;
				}
				value |= (tmp & (byte)~MASK) << (((numBytes-1)-i)*bitsPerByte);
			}
			int curChannel 			= 0;
			boolean syncChannels 	= false;
			if(mode == A2D_24_MODE){
				// Ignore the change bit
				curChannel = ((value & 0x8000000) >> 27);
				value &= 0x7FFFFFF;
				// Offset the value to zero
				value = value - (int)0x4000000;
				// Return ar reasonable resolution
				curData[curChannel+1] = (float)value * tuneValue;
				syncChannels = true;
			}else if(mode == A2D_10_MODE){
				// Ignore the change bit
				// The channel bit is reversed on the 10bit converter hence
				// the 2 -
				curChannel = 1 - ((value & 0x02000) >> 13);
				value = (value & 0x03F) | ((value >> 1) & 0x03C0);
				// Return a reasonable resolution
				syncChannels = true;
			}else if(mode == DIG_COUNT_MODE){
	    			curData[0] = curStepTime;
	   			curStepTime += timeStepSize;
			}
			curData[curChannel+1] = (float)value * tuneValue;
			if(syncChannels){
				if(gotChannel0 && curChannel == 1){
					curData[0] = curStepTime;
					curStepTime += timeStepSize;
					gotChannel0 = false;
					valueData[1+(curDataPos++)] = curData[1];
					valueData[1+(curDataPos++)] = curData[2];
				} else {
					gotChannel0 = (curChannel == 0);
				}
			}
//		    	convertValA2D(value);
		}
		if(curDataPos > 0){
			dEvent.setNumbData(curDataPos);
			dEvent.setData(valueData);
			dEvent.setTime(valueData[0]);
			notifyProbManager(dEvent);
		}
		if(clearBufferOffset) bufOffset = 0;
		return true;
    }

	public void addDataListener(DataListener l){
		dataListeners.add(l);
	}
	
	public int Command(byte command, byte response){
		if(port == null) return 0;
		port.setReadTimeout(0);

		while(port.readBytes(buf, 0, BUF_SIZE) > 0);


		buf[0] = command;
		int tmp = port.writeBytes(buf, 0, 1);
		
		if(tmp != 1) return -1; //error

		port.setReadTimeout(1000);
		Vm.sleep(200);
		while(true){
			tmp = port.readBytes(buf, 0, 1);
			if(tmp > 0){	       
				if(buf[0] == response) {
				    port.setReadTimeout(0);
				    return 1;
				}else{
					continue;
				}
			}else{
				return 0;
			}
		}
	}

	boolean stopSampling(){
		// Let the device wake up a bit
		// But try to stop it as soon as we can
		int tmp = 0 ;
		buf[0] = (byte)'c';
		for(int i=0; i<5; i++){
			tmp = port.writeBytes(buf, 0, 1);
			Vm.sleep(150);
		}
		// in case the the port is left open stop it

		int ret;
		if((ret = Command((byte)'c', (byte)67)) != 1){
			port.close();
			port = null;
			return false;
		}
		port.setReadTimeout(0);
		tmp = port.readBytes(buf, 0, BUF_SIZE);//workaround 
		return true;
	}

	boolean startA2D(char startChar){	
		if(!stopSampling()) return false;
		buf[0] = (byte)startChar;
		port.writeBytes(buf, 0, 1);
		bufOffset = 0;
		return true;
	}


	
	public void dispose(){
		stop();
		refCount--;
		if(refCount <= 0) im = null;
	}
	
    	protected void finalize() throws Throwable {
    		dispose();
	}
	
	
	int bufOffset = 0;
	final static int BUF_SIZE = 1000;
	byte position[] = {(byte)0x00,(byte)0x80,(byte)0x80,(byte)0x80,};
	byte [] buf = new byte[BUF_SIZE];
	float []valueData = new float[1 + BUF_SIZE / 2]; //0 init time, 1 - deltat, 2 - numb data(total)
    	int mode = A2D_10_MODE;

	public int getMode(){return mode;}
	public void setMode(int mode){
		this.mode = mode;
		if(port != null){
			stop();
			start();
		}
	}

	char getStartChar(){
		if(mode == COMMAND_MODE) return 0;
		if(mode == A2D_24_MODE) return 'd';
		if(mode == A2D_10_MODE) return 'a';
		if(mode == DIG_COUNT_MODE) return 'e';
		return 0;
	}
	
	public void setByteStreamProperties(){
		numBytes = 4;
		bitsPerByte = 7;
		position[0] = (byte)0x00;
		position[1] = position[2] = position[3] = (byte)0x80;
		timeStepSize = (float)0.333333;
		curDataPos = 0;
		tuneValue = 1.0f;
		readSize = 512;
		switch(mode){
			case A2D_24_MODE:
				MASK = (byte)(0x0FF << bitsPerByte);
	    			tuneValue = 0.000075f;
				break;
			case A2D_10_MODE:
	   			numBytes = 2;
	   			bitsPerByte = 7;
				MASK = (byte)(0x0FF << bitsPerByte);
	    			timeStepSize = (float)0.005;
	    			tuneValue = 3.22f;
				break;
			case DIG_COUNT_MODE:
	    			numBytes = 1;
	    			bitsPerByte = 8;
	    			MASK = (byte)(0x00);
	    			timeStepSize = (float)0.01;
	    			readSize = 100;
				break;
		}
	}
	
	public int 		numBytes = 4;
	public int 		bitsPerByte = 7;
	public byte 		MASK = (byte)(0x0FF << bitsPerByte);
    	float 			timeStepSize = (float)0.333333;
	int 				curDataPos = 0;
    	float 			curStepTime = 0f;
    	int 				readSize = 512;
    	public float		getCurTime(){return curStepTime;}
    	public void		setCurTime(float val){curStepTime = val;}

    	public final static int ERROR = -1;
    	public final static int OK = 0;

    	public final static int NUMB_CHANNELS = 2;
	float				[]curData = new float[1+NUMB_CHANNELS];
	
	public float		tuneValue = 1.0f;
    	boolean 			gotChannel0 = false;

	public DataDesc	dDesc = new DataDesc();
	public DataEvent	dEvent = new DataEvent();

}