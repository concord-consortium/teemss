package waba.io.impl;

public class SerialPortImpl implements ISerialPort
{
int		timeOut 	= 100;
int		baudRate 	= ISerialPort.RATE_9600;
int		bits 		= 8;
boolean parity		= false;
int		stopBits	= 1;

private short 	inpRef = 0;
private short 	outRef = 0;
private int		inpBuffer = 0;

	public SerialPortImpl(int number, int baudRate){
		this(number, baudRate, 8, false, 1);
	}
	public SerialPortImpl(int number, int baudRate, int bits, boolean parity, int stopBits){
		this.baudRate 	= baudRate;
		this.bits 		= bits;
		this.parity 	= parity;
		this.stopBits 	= stopBits;
		System.out.println("SerialPortImpl");
		SerialManager.checkAvailableSerialPorts();
		SerialPortDesc sPort = SerialManager.getAssignedPort();
		if(sPort == null){
			java.awt.Frame f = SerialManager.getMainFrame();
			if(f != null){
				SerialChoiceDialog dialog = new SerialChoiceDialog(f);
				sPort = SerialManager.getAssignedPort();
			}
		}
		String inPort = null,outPort = null;
		if(sPort == null){
			String portPrefix = "."+(char)('A'+number);
			inPort = portPrefix+"In";
			outPort = portPrefix+"Out";
		}else{
			inPort = sPort.inpName;
			outPort = sPort.outName;
		}
		initPort1(inPort,outPort,baudRate,bits,parity,stopBits);
	}
	public int getBaudRate() 	{return baudRate;}
	public int getBits() 		{return bits;}
	public boolean getParity() 	{return parity;}
	public int getStopBits() 	{return stopBits;}
	public int getTimeOut() 	{return timeOut;}
	
	private short 	getInpRef() {return inpRef;}
	private short 	getOutRef() {return outRef;}
	private void 	setInpRef(short v) {inpRef = v;}
	private void 	setOutRef(short v) {outRef = v;}
	private int 	getInpBuffer() {return inpBuffer;}
	private void 	setInpBuffer(int v) {inpBuffer = v;}

	public native void initPort(int number, int baudRate, int bits, boolean parity, int stopBits);
	public native void initPort1(String inPort, String outPort, int baudRate, int bits, boolean parity, int stopBits);

	public synchronized native boolean close();

	public synchronized native boolean isOpen();

	public synchronized native int readBytes(byte buf[], int start, int count);

	public synchronized native int readCheck();

	public synchronized native boolean setFlowControl(boolean on);

	public synchronized boolean setReadTimeout(int millis){
		timeOut = (millis < 0) ? 0 : millis;
		return (millis < 0 || !isOpen());
	}

	public synchronized native int writeBytes(byte buf[], int start, int count);
	public synchronized native void clearBuffer(int v);
	
/*
	protected void finalize() throws Throwable{
		clearBuffer(inpBuffer);
		super.finalize();
	}
*/
}
