/*
Copyright (c) 2001 Concord Consortium  All rights reserved.
*/

package waba.io.impl;

import  com.apple.mrj.macos.libraries.InterfaceLib;
import  com.apple.jdirect.SharedLibrary;
import  com.apple.mrj.jdirect.JDirectLinker;
import  com.apple.mrj.jdirect.ByteArrayStruct;
import  com.apple.mrj.jdirect.Struct;
import  com.apple.mrj.jdirect.PointerStruct;
import  com.apple.mrj.macos.toolbox.Handle;
import java.util.Vector;
import java.util.Enumeration;
/**
*JDirectSerialPort is Waba's SerialPort implementation for Mac OS less than X
*JDirectSerialPort uses JDirect2 for calling MAC OS Toolbox's routines

*/


public class JDirectSerialPort implements waba.io.impl.ISerialPort{

int			timeOut 	= 100;
int			baudRate 	= waba.io.impl.ISerialPort.RATE_9600;
int			bits 		= 8;
boolean 	parity		= false;
int			stopBits	= 1;
JDirectImpl jdirect 	= null;
short		inputRefNumber = 0;
short		outputRefNumber = 0;
	public JDirectSerialPort(int number, int baudRate){
		this(number, baudRate, 8, false, 1);
	}
	public JDirectSerialPort(int number, int baudRate, int bits, boolean parity, int stopBits){
		this.baudRate 	= baudRate;
		this.bits 		= bits;
		this.parity 	= parity;
		this.stopBits 	= stopBits;
//		System.out.println("JDirectSerialPort");
		initPort(number,baudRate,bits,parity,stopBits);
	}
	public synchronized void initPort(int number, int baudRate, int bits, boolean parity, int stopBits){
		jdirect = JDirectImpl.getJDirectImpl();
		if(jdirect == null) return;
		short 		inpRef[] = new short[1];
		short 		outRef[] = new short[1];


		short	errInp,errOut;
		short macRate,macBits,macParity,macStopBits;
		
		SerialManager.checkAvailableSerialPorts();
		SerialPortDesc sPort = SerialManager.getAssignedPort();
		if(sPort == null){
			java.awt.Frame f = SerialManager.getMainFrame();
			if(f != null){
				SerialChoiceDialog dialog = new SerialChoiceDialog(f);
				sPort = SerialManager.getAssignedPort();
			}
		}
		if(sPort == null){
			String portPrefix = "."+(char)('A'+number);
			errOut 	= jdirect.OpenDriver(JDirectImpl.toStr255(portPrefix+"Out"),outRef);
			errInp 	= jdirect.OpenDriver(JDirectImpl.toStr255(portPrefix+"in"),inpRef);
		}else{
//			System.out.println(sPort.inpName+" "+sPort.outName+" "+sPort.name);
			errOut 	= jdirect.OpenDriver(JDirectImpl.toStr255(sPort.outName),outRef);
			errInp 	= jdirect.OpenDriver(JDirectImpl.toStr255(sPort.inpName),inpRef);
		}


		if(errOut != JDirectImpl.noErr || errInp != JDirectImpl.noErr){
			outputRefNumber = 0;
			inputRefNumber = 0;
		}else{
			inputRefNumber = inpRef[0];
			outputRefNumber = outRef[0];
			macRate = JDirectImpl.getMacBaudRate(baudRate);
			macBits = JDirectImpl.getMacBits(bits);
	//param parity true for even parity, false for no parity
			macParity = (parity) ? (short)SerialConstants.evenParity : (short)SerialConstants.noParity;
			macStopBits = JDirectImpl.getMacStopBits(stopBits);
			errOut = jdirect.SerReset(outputRefNumber,(short)(macRate+macStopBits+macBits+macParity));
			errInp = jdirect.SerReset(inputRefNumber,(short)(macRate+macStopBits+macBits+macParity));
			if(errOut != JDirectImpl.noErr || errInp != JDirectImpl.noErr){
				closePort();
			}
		}
		if(isOpen()){
			SerShkStruct hs = new SerShkStruct();
			hs.setFXOn((byte)0);
			hs.setFCTS((byte)0);
			hs.setErrs((byte)0);
			hs.setEvts((byte)0);
			hs.setFInX((byte)0);
			hs.setFDTR((byte)0);
			errOut = jdirect.Control(outputRefNumber, (short)14, hs.getBytes()); //csCode = 14
			if(errOut != JDirectImpl.noErr){
				closePort();
			}
		}
	}

	public synchronized boolean close(){
		return closePort();
	}

	public synchronized boolean isOpen(){
		return ((jdirect != null) && (inputRefNumber != 0) && (outputRefNumber != 0));
	}

	public synchronized int readBytes(byte buf[], int start, int count){
//		System.out.println("timeOut "+timeOut+" count "+count+" buf "+buf.length+" start "+start);
		if(!isOpen()) return -1;
		if(buf == null) return -1;
    	int bufSize = buf.length;
    	if(bufSize <= 0) return 0;						//buffer is too small
    	int  actNeedData = count;
		int  actStart = start;
    	if(actStart < 0) actStart = 0;
    	int  actCount = count;
    	if(actCount < 0) actCount = 0;
    	if(actCount == 0) return 0;
    	if(actStart + actCount > bufSize){//wants more data than I can deliver
    		actNeedData = bufSize - start;
		}
    	if(actNeedData <= 0) return 0;// user actually does't need data
		int pointer = jdirect.NewPtr(80);
    	if(pointer == 0) return -1;
		MyPtr myBuffer = new MyPtr(buf);
		int	  	numBytes = 0;
		short 	errInp = JDirectImpl.noErr;
		long 	startTime = java.lang.System.currentTimeMillis();
		int 	readData = 0;
		boolean doExit = false;
		
		int currBuffer = myBuffer.getPointer();
	
		boolean checkTimeOut = false;
	    do{
			numBytes = readCheck();
			if(numBytes < 0) break;
			if(numBytes == 0){
				if(!checkTimeOut){
					checkTimeOut = true;
					startTime = java.lang.System.currentTimeMillis();
				}
				doExit = (java.lang.System.currentTimeMillis() - startTime > timeOut);
			}else{
				if(checkTimeOut) checkTimeOut = false;
				int needBytes = actNeedData - readData;
				if(numBytes < needBytes) needBytes = numBytes;
				if(needBytes > 0){
					ParamBlockRecStruct pInBlock = new ParamBlockRecStruct(pointer);
					pInBlock.setIoRefNum(inputRefNumber);  //write to the output driver
					pInBlock.setIoBuffer(currBuffer);  //pointer to my data buffer
					pInBlock.setIoReqCount(needBytes);  //number of bytes to read
					pInBlock.setIoCompletion(0);  //no completion routine specified
					pInBlock.setIoVRefNum((short)0);  //not used by the Serial Driver
					pInBlock.setIoPosMode((short)0);  //not used by the Serial Driver
					errInp = jdirect.PBReadSync(pInBlock);
					int actReadBytes = pInBlock.getIoActCount();
					if(errInp != JDirectImpl.noErr){
						break;
					}
					currBuffer 	+= actReadBytes;
					readData 		+= actReadBytes;
				}
			}
	    }while((readData < actNeedData) && !doExit);
	    
	    byte []srcBytes = myBuffer.getBytes();
	    int srcLength 	= srcBytes.length;
	    int needLength	= readData;
	    if(needLength > buf.length - start) needLength = buf.length - start;
	    if(srcLength < needLength) needLength = srcLength;
	    System.arraycopy(srcBytes,0,buf,start,needLength);
	    
		myBuffer.freePointer();
		jdirect.DisposePtr(pointer);
//		System.out.println("readData "+readData+" actNeedData "+actNeedData);
		return readData;
	}

	public int readCheck(){
		if(!isOpen()) return -1;
		int	numBytes[] = new int[1];
		short errInp = jdirect.SerGetBuf(inputRefNumber,numBytes);
		if(errInp != JDirectImpl.noErr) return -1;
		return numBytes[0];
	}

	public synchronized boolean setFlowControl(boolean on){
		if(!isOpen()) return false;
		SerShkStruct hs = new SerShkStruct();
		hs.setFXOn((byte)0);
		hs.setFCTS((!on)?(byte)0:(byte)1);
		hs.setErrs((byte)0);
		hs.setEvts((byte)0);
		hs.setFInX((byte)0);// in MBL was 1
		hs.setFDTR((byte)0);

		short errOut = jdirect.Control(outputRefNumber, (short)14, hs.getBytes()); //csCode = 14
		return (errOut == JDirectImpl.noErr);
	}

	public synchronized boolean setReadTimeout(int millis){
		timeOut = (millis < 0) ? 0 : millis;
		return (millis < 0 || !isOpen());
	}

	public synchronized int writeBytes(byte buf[], int start, int count){
		if(!isOpen()) return -1;
		if(buf == null) return -1;
		int bufSize = buf.length;
    	if(bufSize <= 0) return 0;						//buffer is too small
	    int  actNeedData = count;
	    int  actStart = start;
	    if(actStart < 0) actStart = 0;
	    int  actCount = count;
	    if(actCount < 0) actCount = 0;
	    if(actCount == 0) return 0;
	    if(actStart + actCount > bufSize){//wants more data than I can deliver
	    	actNeedData = bufSize - start;
	    }
	    if(actNeedData <= 0) return 0;// user actually does't need data

		int pointer = jdirect.NewPtr(80);
		if(pointer > 0){
			int myBuffer = jdirect.NewPtr(actNeedData);
			if(myBuffer > 0){
				MyPtr ptr = new MyPtr(buf);
				
				ParamBlockRecStruct pOutBlock = new ParamBlockRecStruct(pointer);
				pOutBlock.setIoRefNum(outputRefNumber);  //write to the output driver
				pOutBlock.setIoBuffer(ptr.getPointer());  //pointer to my data buffer
				pOutBlock.setIoReqCount(actNeedData);  //number of bytes to read
				pOutBlock.setIoCompletion(0);  //no completion routine specified
				pOutBlock.setIoVRefNum((short)0);  //not used by the Serial Driver
				pOutBlock.setIoPosMode((short)0);  //not used by the Serial Driver
				short errOut = jdirect.PBWriteSync(pOutBlock);  //synchronous Device Manager request
				if(errOut != JDirectImpl.noErr) actNeedData = -1;
				ptr.freePointer();
				ptr = null;
			}
			jdirect.DisposePtr(pointer);
		}
		return actNeedData;
	}
	public synchronized void clearBuffer(int v){}
	
	private synchronized boolean	closePort()
	{
		boolean retValue = true;
		short	errInp;
		short	errOut;
		if(jdirect == null) return false;
		if(inputRefNumber == 0){
			retValue = false;
		}else{
			errInp = jdirect.KillIO(inputRefNumber);
			if(retValue && (errInp != JDirectImpl.noErr)) retValue = false;
			errInp = jdirect.CloseDriver(inputRefNumber);
			if(retValue && (errInp != JDirectImpl.noErr)) retValue = false;
		}
		if(outputRefNumber == 0){
			retValue = false;
		}else{
			errOut = jdirect.KillIO(outputRefNumber);
			if(retValue && (errOut != JDirectImpl.noErr)) retValue = false;
			errOut = jdirect.CloseDriver(outputRefNumber);
			if(retValue && (errOut != JDirectImpl.noErr)) retValue = false;
		}
		inputRefNumber = 0;
		outputRefNumber = 0;
		return retValue;
	}
}


/**
 * <CODE>SerShkStruct</CODE> is a class which mimics the C struct <CODE>SerShk</CODE> defined in <CODE>Serial.h</CODE><BR><BR>
 * This class contains a get and set method for each field in <CODE>SerShk</CODE>.<BR><BR>
 * Using JDirect 2.0, a <CODE>SerShkStruct</CODE> can be passed to a native toolbox function
 * that expects a pointer to a <CODE>SerShk</CODE> by using the <CODE>getByteArray()</CODE> method. 
 */
class SerShkStruct extends ByteArrayStruct {

	/**
	 * Constructs an uninitialized <CODE>SerShkStruct</CODE>
	 */
	public SerShkStruct() {
		super(sizeOfSerShk);
	}

	/**
	 * Constructs a <CODE>SerShkStruct</CODE> and initializes it with the data in another <CODE>Struct</CODE>. 
	 * Useful when a C struct <CODE>SerShk</CODE> is embedded in another C struct and you want to
	 * copy it out. 
	 * @param src the <CODE>Struct</CODE> containing the data to be copied
	 * @param offsetInSrc the offest in of the data in <CODE>src</CODE>
	 */
	public SerShkStruct(Struct src, int offsetInSrc) {
		super(sizeOfSerShk);
		byte[] bytes = src.getBytesAt(offsetInSrc, sizeOfSerShk);
		this.setBytesAt(0, bytes);
	}

	/**
	 * Used only by subclasses of <CODE>SerShkStruct</CODE>
	 */
	protected SerShkStruct(int size) {
		super(size);
	}

	/**
	 * in C: <CODE>Byte fXOn</CODE>
	 * @return field <CODE>fXOn</CODE> of <CODE>struct SerShk</CODE>
	 */
	public final byte getFXOn() {
		return getByteAt(0);
	}

	/**
	 * in C: <CODE>Byte fXOn</CODE>
	 * @param fXOn sets field <CODE>fXOn</CODE> of <CODE>struct SerShk</CODE>
	 */
	public final void setFXOn(byte fXOn) {
		setByteAt(0, fXOn);
	}

	/**
	 * in C: <CODE>Byte fCTS</CODE>
	 * @return field <CODE>fCTS</CODE> of <CODE>struct SerShk</CODE>
	 */
	public final byte getFCTS() {
		return getByteAt(1);
	}

	/**
	 * in C: <CODE>Byte fCTS</CODE>
	 * @param fCTS sets field <CODE>fCTS</CODE> of <CODE>struct SerShk</CODE>
	 */
	public final void setFCTS(byte fCTS) {
		setByteAt(1, fCTS);
	}

	/**
	 * in C: <CODE>unsigned char xOn</CODE>
	 * @return field <CODE>xOn</CODE> of <CODE>struct SerShk</CODE>
	 */
	public final byte getXOn() {
		return getByteAt(2);
	}

	/**
	 * in C: <CODE>unsigned char xOn</CODE>
	 * @param xOn sets field <CODE>xOn</CODE> of <CODE>struct SerShk</CODE>
	 */
	public final void setXOn(byte xOn) {
		setByteAt(2, xOn);
	}

	/**
	 * in C: <CODE>unsigned char xOff</CODE>
	 * @return field <CODE>xOff</CODE> of <CODE>struct SerShk</CODE>
	 */
	public final byte getXOff() {
		return getByteAt(3);
	}

	/**
	 * in C: <CODE>unsigned char xOff</CODE>
	 * @param xOff sets field <CODE>xOff</CODE> of <CODE>struct SerShk</CODE>
	 */
	public final void setXOff(byte xOff) {
		setByteAt(3, xOff);
	}

	/**
	 * in C: <CODE>Byte errs</CODE>
	 * @return field <CODE>errs</CODE> of <CODE>struct SerShk</CODE>
	 */
	public final byte getErrs() {
		return getByteAt(4);
	}

	/**
	 * in C: <CODE>Byte errs</CODE>
	 * @param errs sets field <CODE>errs</CODE> of <CODE>struct SerShk</CODE>
	 */
	public final void setErrs(byte errs) {
		setByteAt(4, errs);
	}

	/**
	 * in C: <CODE>Byte evts</CODE>
	 * @return field <CODE>evts</CODE> of <CODE>struct SerShk</CODE>
	 */
	public final byte getEvts() {
		return getByteAt(5);
	}

	/**
	 * in C: <CODE>Byte evts</CODE>
	 * @param evts sets field <CODE>evts</CODE> of <CODE>struct SerShk</CODE>
	 */
	public final void setEvts(byte evts) {
		setByteAt(5, evts);
	}

	/**
	 * in C: <CODE>Byte fInX</CODE>
	 * @return field <CODE>fInX</CODE> of <CODE>struct SerShk</CODE>
	 */
	public final byte getFInX() {
		return getByteAt(6);
	}

	/**
	 * in C: <CODE>Byte fInX</CODE>
	 * @param fInX sets field <CODE>fInX</CODE> of <CODE>struct SerShk</CODE>
	 */
	public final void setFInX(byte fInX) {
		setByteAt(6, fInX);
	}

	/**
	 * in C: <CODE>Byte fDTR</CODE>
	 * @return field <CODE>fDTR</CODE> of <CODE>struct SerShk</CODE>
	 */
	public final byte getFDTR() {
		return getByteAt(7);
	}

	/**
	 * in C: <CODE>Byte fDTR</CODE>
	 * @param fDTR sets field <CODE>fDTR</CODE> of <CODE>struct SerShk</CODE>
	 */
	public final void setFDTR(byte fDTR) {
		setByteAt(7, fDTR);
	}

	/**
	 * Size of <CODE>struct SerShk</CODE> in bytes
	 */
	public final static int sizeOfSerShk = 8;

	public long getValue() {
		return getLongAt(0);
	}
}

