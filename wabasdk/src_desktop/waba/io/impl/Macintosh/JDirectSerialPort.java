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
		System.out.println("JDirectSerialPort");
		initPort(number,baudRate,bits,parity,stopBits);
	}
	public void initPort(int number, int baudRate, int bits, boolean parity, int stopBits){
		jdirect = JDirectImpl.getJDirectImpl();
		if(jdirect == null) return;
		short 		inpRef[] = new short[1];
		short 		outRef[] = new short[1];


		short	errInp,errOut;
		short macRate,macBits,macParity,macStopBits;
		
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
			System.out.println(sPort.inpName+" "+sPort.outName+" "+sPort.name);
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

	public boolean close(){
		return closePort();
	}

	public boolean isOpen(){
		return ((jdirect != null) && (inputRefNumber != 0) && (outputRefNumber != 0));
	}

	public int readBytes(byte buf[], int start, int count){
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
					if(errInp != JDirectImpl.noErr) break;
					currBuffer 	+= needBytes;//??
					readData 	+= needBytes;
				}
			}
	    }while((readData < actNeedData) && !doExit);
	    
	    byte []srcBytes = myBuffer.getBytes();
	    int srcLength 	= srcBytes.length;
	    int needLength	= buf.length;
	    if(srcLength < needLength) needLength = srcLength;
	    System.arraycopy(srcBytes,0,buf,0,needLength);
	    
		myBuffer.freePointer();
		jdirect.DisposePtr(pointer);
		return readData;
	}

	public int readCheck(){
		if(!isOpen()) return -1;
		int	numBytes[] = new int[1];
		short errInp = jdirect.SerGetBuf(inputRefNumber,numBytes);
		if(errInp != JDirectImpl.noErr) return -1;
		return numBytes[0];
	}

	public boolean setFlowControl(boolean on){
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

	public boolean setReadTimeout(int millis){
		timeOut = (millis < 0) ? 0 : millis;
		return (millis < 0 || !isOpen());
	}

	public int writeBytes(byte buf[], int start, int count){
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
	public void clearBuffer(int v){}
	
	private boolean	closePort()
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
public class JDirectImpl implements InterfaceLib {
public static final short noErr = 0;
	protected JDirectImpl() { //use factory
	}
	static JDirectImpl theJDirectImpl = null;
	
	public static JDirectImpl getJDirectImpl() {
		if (theJDirectImpl == null) {
			theJDirectImpl = new JDirectImpl();
		}
		return theJDirectImpl;
	}
	/** converts a Java string to a Pascal-style string. */
	public static byte[] toStr255(String str) {
		int length = str.length();
		if (length > 255) length = 255;
		byte result[] = new byte[length + 1];
		result[0] = (byte)length;
		str.getBytes(0, length, result, 1);
		return result;
	}
	public static short	getMacBaudRate(int rate)
	{
		short retValue = SerialConstants.baud9600;
		switch(rate){
			case 150: 	retValue = SerialConstants.baud150; 	break;
			case 300: 	retValue = SerialConstants.baud300; 	break;
			case 600: 	retValue = SerialConstants.baud600; 	break;
			case 1200: 	retValue = SerialConstants.baud1200; 	break;
			case 1800: 	retValue = SerialConstants.baud1800; 	break;
			case 2400: 	retValue = SerialConstants.baud2400; 	break;
			case 3600: 	retValue = SerialConstants.baud3600; 	break;
			case 4800: 	retValue = SerialConstants.baud4800; 	break;
			case 7200: 	retValue = SerialConstants.baud7200; 	break;
			case 9600: 	retValue = SerialConstants.baud9600; 	break;
			case 14400:	retValue = SerialConstants.baud14400; 	break;
			case 19200: retValue = SerialConstants.baud19200; 	break;
			case 28800: retValue = SerialConstants.baud28800; 	break;
			case 38400: retValue = SerialConstants.baud38400; 	break;
			case 57600: retValue = SerialConstants.baud57600; 	break;
			default: 	retValue = SerialConstants.baud9600;	break;
		}
		return retValue;
 	}
	public static short	getMacStopBits(int stopbits)
	{
		short retValue = SerialConstants.stop10;
		switch(stopbits){
			case 1: 	retValue = SerialConstants.stop10; 	break;
			case 2: 	retValue = SerialConstants.stop20; 	break;
			default: 	retValue = SerialConstants.stop10;	break;
		}
		return retValue;
	}

	public static short	getMacBits(int bits)
	{
		short retValue = SerialConstants.data8;
		switch(bits){
			case 5: 	retValue = SerialConstants.data5; 	break;
			case 6: 	retValue = SerialConstants.data6; 	break;
			case 7: 	retValue = SerialConstants.data7; 	break;
			case 8: 	retValue = SerialConstants.data8; 	break;
			default: 	retValue = SerialConstants.data8;	break;
		}
		return retValue;
	}
	public static String getStringFromStringHandle(int h){
		String retValue = "";
		try{
			MyHandle hTemp = new MyHandle(h);
			byte b[] = hTemp.getBytes();
			int lnb = (b == null)?0:b.length;
			if(lnb > 0){
				int strLength = b[0];
				if(strLength > 0) retValue = new String(b,1,strLength);//pascal str
			}
		}catch(Exception e){
			retValue = null;
		}
		return retValue;
	}
//look Devices.java example	
	/**
	 * @param name			in C: <CODE>ConstStr255Param name</CODE>
	 * @param drvrRefNum	in C: <CODE>short *drvrRefNum</CODE>
	 * @return				in C: <CODE>OSErr </CODE>
	 */
	public native static short OpenDriver(byte [] name, short [] drvrRefNum);


	/**
	 * @param refNum		in C: <CODE>short refNum</CODE>
	 * @return				in C: <CODE>OSErr </CODE>
	 */
	public native static short CloseDriver(short refNum);
	/**
	 * @param refNum		in C: <CODE>short refNum</CODE>
	 * @return				in C: <CODE>OSErr </CODE>
	 */
	public native static short KillIO(short refNum);

	/**
	 * @param refNum		in C: <CODE>short refNum</CODE>
	 * @param csCode		in C: <CODE>short csCode</CODE>
	 * @param csParamPtr	in C: <CODE>const void *csParamPtr</CODE>
	 * @return				in C: <CODE>OSErr </CODE>
	 */
	public native static short Control(short refNum, short csCode, byte [] csParamPtr);


	/**
	 * @param paramBlock	in C: <CODE>ParmBlkPtr paramBlock</CODE>
	 * @return				in C: <CODE>OSErr </CODE>
	 */
	public static short PBWriteSync(ParamBlockRecStruct paramBlock) {
		 return PBWriteSync(paramBlock.getPointer());
	}
	public native static short PBWriteSync(int paramBlock);

	/**
	 * @param paramBlock	in C: <CODE>ParmBlkPtr paramBlock</CODE>
	 * @return				in C: <CODE>OSErr </CODE>
	 */
	public static short PBReadSync(ParamBlockRecStruct paramBlock) {
		 return PBReadSync(paramBlock.getPointer());
	}
	public native static short PBReadSync(int paramBlock);
//Serial

	/**
	 * @param refNum		in C: <CODE>short refNum</CODE>
	 * @param serConfig		in C: <CODE>short serConfig</CODE>
	 * @return				in C: <CODE>OSErr </CODE>
	 */
	 
	public native static short SerReset(short refNum, short serConfig);
	/**
	 * @param refNum		in C: <CODE>short refNum</CODE>
	 * @param count			in C: <CODE>long *count</CODE>
	 * @return				in C: <CODE>OSErr </CODE>
	 */
	public native static short SerGetBuf(short refNum, int [] count);
	
	
//MacMemory
	/**
	 * @param byteCount		in C: <CODE>Size byteCount</CODE>
	 * @return				in C: <CODE>Ptr </CODE>
	 */
	public native static int NewPtr(int byteCount);
	/**
	 * @param byteCount		in C: <CODE>Size byteCount</CODE>
	 * @return				in C: <CODE>Ptr </CODE>
	 */
	public native static int NewPtrClear(int byteCount);
	
	/**
	 * @param p				in C: <CODE>Ptr p</CODE>
	 */
	public native static void DisposePtr(int p);
	public native static int GetPtrSize(int p);
	public native static int GetHandleSize(int h);
	
//CRM routines
	public native static short InitCRM();
	public static int CRMSearch(CRMRecStruct crmReqPtr) {
		 return CRMSearch(crmReqPtr.getByteArray());
	}
	public native static int CRMSearch(byte[] crmReqPtr);

}	
public class MyPtr1 extends com.apple.mrj.jdirect.PointerStruct{
	public MyPtr1(int p){
		super(p);
	}
	public int getSize(){
		return  JDirectImpl.GetPtrSize(pointer);
	}
}

public class MyPtr extends com.apple.mrj.macos.toolbox.Ptr{
    public MyPtr(byte ab[])
    {
    	super(ab);
    }
    public void freePointer(){
    	super.freePointer();
    }
}
public class MyHandle extends com.apple.mrj.jdirect.HandleStruct{
	public MyHandle(int h){
		super(h);
	}
	public int getSize(){
		return  JDirectImpl.GetHandleSize(handle);
	}
}

/**
 * <CODE>SerShkStruct</CODE> is a class which mimics the C struct <CODE>SerShk</CODE> defined in <CODE>Serial.h</CODE><BR><BR>
 * This class contains a get and set method for each field in <CODE>SerShk</CODE>.<BR><BR>
 * Using JDirect 2.0, a <CODE>SerShkStruct</CODE> can be passed to a native toolbox function
 * that expects a pointer to a <CODE>SerShk</CODE> by using the <CODE>getByteArray()</CODE> method. 
 */
public class SerShkStruct extends ByteArrayStruct {

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

/**
 * The interface <CODE>SerialConstants</CODE> contains java equivalents for all constants<BR>
 * defined in the C header <CODE>Serial.h</CODE><BR>
 * <BR>
 * When writing a java class which needs these constants, you can either use the <BR>
 * fully qualified name of the constant (e.g. <CODE>option = SerialConstants.kWilyBit</CODE>)<BR>
 * or you can declare that your class <CODE>implements SerialConstants<CODE> and then use the<BR>
 * simple name (e.g. <CODE>option = kWilyBit</CODE>)<BR>
 */
public interface SerialConstants {

	/**
		in C: <CODE>763</CODE>
	*/
	public final int 	baud150							= 763;

	/**
		in C: <CODE>380</CODE>
	*/
	public final int 	baud300							= 380;

	/**
		in C: <CODE>189</CODE>
	*/
	public final int 	baud600							= 189;

	/**
		in C: <CODE>94</CODE>
	*/
	public final int 	baud1200						= 94;

	/**
		in C: <CODE>62</CODE>
	*/
	public final int 	baud1800						= 62;

	/**
		in C: <CODE>46</CODE>
	*/
	public final int 	baud2400						= 46;

	/**
		in C: <CODE>30</CODE>
	*/
	public final int 	baud3600						= 30;

	/**
		in C: <CODE>22</CODE>
	*/
	public final int 	baud4800						= 22;

	/**
		in C: <CODE>14</CODE>
	*/
	public final int 	baud7200						= 14;

	/**
		in C: <CODE>10</CODE>
	*/
	public final int 	baud9600						= 10;

	/**
		in C: <CODE>6</CODE>
	*/
	public final int 	baud14400						= 6;

	/**
		in C: <CODE>4</CODE>
	*/
	public final int 	baud19200						= 4;

	/**
		in C: <CODE>2</CODE>
	*/
	public final int 	baud28800						= 2;

	/**
		in C: <CODE>1</CODE>
	*/
	public final int 	baud38400						= 1;

	/**
		in C: <CODE>0</CODE>
	*/
	public final int 	baud57600						= 0;


	/**
		in C: <CODE>16384</CODE>
	*/
	public final int 	stop10							= 16384;

	/**
		in C: <CODE>-32768L</CODE>
	*/
	public final int 	stop15							= -32768;

	/**
		in C: <CODE>-16384</CODE>
	*/
	public final int 	stop20							= -16384;


	/**
		in C: <CODE>0</CODE>
	*/
	public final int 	noParity						= 0;

	/**
		in C: <CODE>4096</CODE>
	*/
	public final int 	oddParity						= 4096;

	/**
		in C: <CODE>12288</CODE>
	*/
	public final int 	evenParity						= 12288;


	/**
		in C: <CODE>0</CODE>
	*/
	public final int 	data5							= 0;

	/**
		in C: <CODE>2048</CODE>
	*/
	public final int 	data6							= 2048;

	/**
		in C: <CODE>1024</CODE>
	*/
	public final int 	data7							= 1024;

	/**
		in C: <CODE>3072</CODE>
	*/
	public final int 	data8							= 3072;


	/**
		in C: <CODE>6</CODE><BR>
		 channel A data in or out (historical) 
	*/
	public final int 	aData							= 6;

	/**
		in C: <CODE>2</CODE><BR>
		 channel A control (historical) 
	*/
	public final int 	aCtl							= 2;

	/**
		in C: <CODE>4</CODE><BR>
		 channel B data in or out (historical) 
	*/
	public final int 	bData							= 4;

	/**
		in C: <CODE>0</CODE><BR>
		 channel B control (historical) 
	*/
	public final int 	bCtl							= 0;


	/**
		in C: <CODE>2</CODE><BR>
		 flag for SerShk.evts 
	*/
	public final byte 	dsrEvent						= (byte)2;

	/**
		in C: <CODE>4</CODE><BR>
		 flag for SerShk.evts 
	*/
	public final byte 	riEvent							= (byte)4;

	/**
		in C: <CODE>8</CODE><BR>
		 flag for SerShk.evts 
	*/
	public final byte 	dcdEvent						= (byte)8;

	/**
		in C: <CODE>32</CODE><BR>
		 flag for SerShk.evts 
	*/
	public final byte 	ctsEvent						= (byte)32;

	/**
		in C: <CODE>-128</CODE><BR>
		 flag for SerShk.evts 
	*/
	public final byte 	breakEvent						= (byte)-128;


	/**
		in C: <CODE>-128</CODE><BR>
		 flag for SerStaRec.xOffSent 
	*/
	public final byte 	xOffWasSent						= (byte)-128;

	/**
		in C: <CODE>64</CODE><BR>
		 flag for SerStaRec.xOffSent 
	*/
	public final byte 	dtrNegated						= (byte)64;

	/**
		in C: <CODE>32</CODE><BR>
		 flag for SerStaRec.xOffSent 
	*/
	public final byte 	rtsNegated						= (byte)32;


	/**
		in C: <CODE>-6</CODE><BR>
		 serial port A input 
	*/
	public final short 	ainRefNum						= (short)-6;

	/**
		in C: <CODE>-7</CODE><BR>
		 serial port A output 
	*/
	public final short 	aoutRefNum						= (short)-7;

	/**
		in C: <CODE>-8</CODE><BR>
		 serial port B input 
	*/
	public final short 	binRefNum						= (short)-8;

	/**
		in C: <CODE>-9</CODE><BR>
		 serial port B output 
	*/
	public final short 	boutRefNum						= (short)-9;


	/**
		in C: <CODE>1</CODE><BR>
		 serial driver error masks 
	*/
	public final byte 	swOverrunErr					= (byte)1;

	/**
		in C: <CODE>8</CODE><BR>
		 serial driver error masks 
	*/
	public final byte 	breakErr						= (byte)8;

	/**
		in C: <CODE>16</CODE><BR>
		 serial driver error masks 
	*/
	public final byte 	parityErr						= (byte)16;

	/**
		in C: <CODE>32</CODE><BR>
		 serial driver error masks 
	*/
	public final byte 	hwOverrunErr					= (byte)32;

	/**
		in C: <CODE>64</CODE><BR>
		 serial driver error masks 
	*/
	public final byte 	framingErr						= (byte)64;


	/**
		in C: <CODE>128</CODE><BR>
		 option bit used with Control code 16 
	*/
	public final int 	kOptionPreserveDTR				= 128;

	/**
		in C: <CODE>64</CODE><BR>
		 option bit used with Control code 16 
	*/
	public final int 	kOptionClockX1CTS				= 64;


	/**
		in C: <CODE>-128</CODE><BR>
		 flag for SerShk.fCTS 
	*/
	public final byte 	kUseCTSOutputFlowControl		= (byte)-128;

	/**
		in C: <CODE>64</CODE><BR>
		 flag for SerShk.fCTS 
	*/
	public final byte 	kUseDSROutputFlowControl		= (byte)64;

	/**
		in C: <CODE>-128</CODE><BR>
		 flag for SerShk.fDTR 
	*/
	public final byte 	kUseRTSInputFlowControl			= (byte)-128;

	/**
		in C: <CODE>64</CODE><BR>
		 flag for SerShk.fDTR 
	*/
	public final byte 	kUseDTRInputFlowControl			= (byte)64;


	/**
		in C: <CODE>0</CODE><BR>
		 Macintosh modem port 
	*/
	public final byte 	sPortA							= (byte)0;

	/**
		in C: <CODE>1</CODE><BR>
		 Macintosh printer port 
	*/
	public final byte 	sPortB							= (byte)1;

	/**
		in C: <CODE>2</CODE><BR>
		 RS-232 port COM1 
	*/
	public final byte 	sCOM1							= (byte)2;

	/**
		in C: <CODE>3</CODE><BR>
		 RS-232 port COM2 
	*/
	public final byte 	sCOM2							= (byte)3;


	/**
		in C: <CODE>8</CODE><BR>
		 program port speed, bits/char, parity, and stop bits 
	*/
	public final short 	kSERDConfiguration				= (short)8;

	/**
		in C: <CODE>9</CODE><BR>
		 set buffer for chars received with no read pending 
	*/
	public final short 	kSERDInputBuffer				= (short)9;

	/**
		in C: <CODE>10</CODE><BR>
		 equivalent to SerHShake, largely obsolete 
	*/
	public final short 	kSERDSerHShake					= (short)10;

	/**
		in C: <CODE>11</CODE><BR>
		 assert break signal on output 
	*/
	public final short 	kSERDClearBreak					= (short)11;

	/**
		in C: <CODE>12</CODE><BR>
		 negate break state on output 
	*/
	public final short 	kSERDSetBreak					= (short)12;

	/**
		in C: <CODE>13</CODE><BR>
		 set explicit baud rate, other settings unchanged 
	*/
	public final short 	kSERDBaudRate					= (short)13;

	/**
		in C: <CODE>14</CODE><BR>
		 superset of 10, honors setting of fDTR 
	*/
	public final short 	kSERDHandshake					= (short)14;

	/**
		in C: <CODE>15</CODE><BR>
		 clock externally on CTS with specified multiplier 
	*/
	public final short 	kSERDClockMIDI					= (short)15;

	/**
		in C: <CODE>16</CODE><BR>
		 select clock source and DTR behavior on close 
	*/
	public final short 	kSERDMiscOptions				= (short)16;

	/**
		in C: <CODE>17</CODE><BR>
		 assert DTR output 
	*/
	public final short 	kSERDAssertDTR					= (short)17;

	/**
		in C: <CODE>18</CODE><BR>
		 negate DTR output 
	*/
	public final short 	kSERDNegateDTR					= (short)18;

	/**
		in C: <CODE>19</CODE><BR>
		 select char to replace chars with invalid parity 
	*/
	public final short 	kSERDSetPEChar					= (short)19;

	/**
		in C: <CODE>20</CODE><BR>
		 select char to replace char that replaces chars with invalid parity 
	*/
	public final short 	kSERDSetPEAltChar				= (short)20;

	/**
		in C: <CODE>21</CODE><BR>
		 set XOff output flow control (same as receiving XOff) 
	*/
	public final short 	kSERDSetXOffFlag				= (short)21;

	/**
		in C: <CODE>22</CODE><BR>
		 clear XOff output flow control (same as receiving XOn) 
	*/
	public final short 	kSERDClearXOffFlag				= (short)22;

	/**
		in C: <CODE>23</CODE><BR>
		 send XOn if input flow control state is XOff 
	*/
	public final short 	kSERDSendXOn					= (short)23;

	/**
		in C: <CODE>24</CODE><BR>
		 send XOn regardless of input flow control state 
	*/
	public final short 	kSERDSendXOnOut					= (short)24;

	/**
		in C: <CODE>25</CODE><BR>
		 send XOff if input flow control state is XOn 
	*/
	public final short 	kSERDSendXOff					= (short)25;

	/**
		in C: <CODE>26</CODE><BR>
		 send XOff regardless of input flow control state 
	*/
	public final short 	kSERDSendXOffOut				= (short)26;

	/**
		in C: <CODE>27</CODE><BR>
		 reset serial I/O channel hardware 
	*/
	public final short 	kSERDResetChannel				= (short)27;

	/**
		in C: <CODE>28</CODE><BR>
		 extension of 14, allows full RS-232 hardware handshaking 
	*/
	public final short 	kSERDHandshakeRS232				= (short)28;

	/**
		in C: <CODE>29</CODE><BR>
		 use mark/space parity 
	*/
	public final short 	kSERDStickParity				= (short)29;

	/**
		in C: <CODE>30</CODE><BR>
		 assert RTS output 
	*/
	public final short 	kSERDAssertRTS					= (short)30;

	/**
		in C: <CODE>31</CODE><BR>
		 negate RTS output 
	*/
	public final short 	kSERDNegateRTS					= (short)31;

	/**
		in C: <CODE>115</CODE><BR>
		 set 115.2K baud data rate 
	*/
	public final short 	kSERD115KBaud					= (short)115;

	/**
		in C: <CODE>230</CODE><BR>
		 set 230.4K baud data rate 
	*/
	public final short 	kSERD230KBaud					= (short)230;


	/**
		in C: <CODE>2</CODE><BR>
		 return characters available (SerGetBuf) 
	*/
	public final short 	kSERDInputCount					= (short)2;

	/**
		in C: <CODE>8</CODE><BR>
		 return characters available (SerStatus) 
	*/
	public final short 	kSERDStatus						= (short)8;

	/**
		in C: <CODE>9</CODE><BR>
		 return version number in first byte of csParam 
	*/
	public final short 	kSERDVersion					= (short)9;

	/**
		in C: <CODE>256</CODE><BR>
		 get instantaneous state of DCD (GPi) 
	*/
	public final short 	kSERDGetDCD						= (short)256;


	/**
		in C: <CODE>kOptionClockX1CTS</CODE><BR>
		 option bit used with Control code 16 
	*/
	public final int 	serdOptionClockExternal			= 64;

	/**
		in C: <CODE>kOptionPreserveDTR</CODE><BR>
		 option bit used with Control code 16 
	*/
	public final int 	serdOptionPreserveDTR			= 128;


	/**
		in C: <CODE>kSERDConfiguration</CODE>
	*/
	public final int 	serdReset						= 8;

	/**
		in C: <CODE>kSERDInputBuffer</CODE>
	*/
	public final int 	serdSetBuf						= 9;

	/**
		in C: <CODE>kSERDSerHShake</CODE>
	*/
	public final int 	serdHShake						= 10;

	/**
		in C: <CODE>kSERDClearBreak</CODE>
	*/
	public final int 	serdClrBrk						= 11;

	/**
		in C: <CODE>kSERDSetBreak</CODE>
	*/
	public final int 	serdSetBrk						= 12;

	/**
		in C: <CODE>kSERDBaudRate</CODE>
	*/
	public final int 	serdSetBaud						= 13;

	/**
		in C: <CODE>kSERDHandshake</CODE>
	*/
	public final int 	serdHShakeDTR					= 14;

	/**
		in C: <CODE>kSERDClockMIDI</CODE>
	*/
	public final int 	serdSetMIDI						= 15;

	/**
		in C: <CODE>kSERDMiscOptions</CODE>
	*/
	public final int 	serdSetMisc						= 16;

	/**
		in C: <CODE>kSERDAssertDTR</CODE>
	*/
	public final int 	serdSetDTR						= 17;

	/**
		in C: <CODE>kSERDNegateDTR</CODE>
	*/
	public final int 	serdClrDTR						= 18;

	/**
		in C: <CODE>kSERDSetPEChar</CODE>
	*/
	public final int 	serdSetPEChar					= 19;

	/**
		in C: <CODE>kSERDSetPEAltChar</CODE>
	*/
	public final int 	serdSetPECharAlternate			= 20;

	/**
		in C: <CODE>kSERDSetXOffFlag</CODE>
	*/
	public final int 	serdSetXOff						= 21;

	/**
		in C: <CODE>kSERDClearXOffFlag</CODE>
	*/
	public final int 	serdClrXOff						= 22;

	/**
		in C: <CODE>kSERDSendXOn</CODE>
	*/
	public final int 	serdSendXOnConditional			= 23;

	/**
		in C: <CODE>kSERDSendXOnOut</CODE>
	*/
	public final int 	serdSendXOn						= 24;

	/**
		in C: <CODE>kSERDSendXOff</CODE>
	*/
	public final int 	serdSendXOffConditional			= 25;

	/**
		in C: <CODE>kSERDSendXOffOut</CODE>
	*/
	public final int 	serdSendXOff					= 26;

	/**
		in C: <CODE>kSERDResetChannel</CODE>
	*/
	public final int 	serdChannelReset				= 27;

	/**
		in C: <CODE>kSERD230KBaud</CODE><BR>
		 set 230K baud data rate 
	*/
	public final int 	serdSet230KBaud					= 230;


	/**
		in C: <CODE>kSERDInputCount</CODE>
	*/
	public final int 	serdGetBuf						= 2;

	/**
		in C: <CODE>kSERDStatus</CODE>
	*/
	public final int 	serdStatus						= 8;

	/**
		in C: <CODE>kSERDVersion</CODE>
	*/
	public final int 	serdGetVers						= 9;


}
/**
 * <CODE>ParamBlockRecStruct</CODE> is a class which holds a reference to the C struct <CODE>ParamBlockRec</CODE> defined in <CODE>Files.h</CODE><BR><BR>
 * This class contains a get and set method for each field in <CODE>ParamBlockRec</CODE>.<BR><BR>
 * Using JDirect 2.0, a <CODE>ParamBlockRecStruct</CODE> can be passed to a native toolbox function
 * that expects a pointer to a <CODE>ParamBlockRec</CODE> by using the <CODE>getPointer()</CODE> method. 
 */
public class ParamBlockRecStruct extends PointerStruct {

	/**
	 * Constructs a <CODE>ParamBlockRecStruct</CODE> given a pointer to a MacOS toolbox <CODE>ParamBlockRec</CODE>
	 */
	public ParamBlockRecStruct(int pointer) {
		super(pointer);
	}

	/**
	 * in C: <CODE>QElemPtr qLink</CODE>
	 * @return field <CODE>qLink</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getQLink() {
		return getIntAt(0);
	}

	/**
	 * in C: <CODE>QElemPtr qLink</CODE>
	 * @param qLink sets field <CODE>qLink</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setQLink(int qLink) {
		setIntAt(0, qLink);
	}

	/**
	 * in C: <CODE>short qType</CODE>
	 * @return field <CODE>qType</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getQType() {
		return getShortAt(4);
	}

	/**
	 * in C: <CODE>short qType</CODE>
	 * @param qType sets field <CODE>qType</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setQType(short qType) {
		setShortAt(4, qType);
	}

	/**
	 * in C: <CODE>short ioTrap</CODE>
	 * @return field <CODE>ioTrap</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getIoTrap() {
		return getShortAt(6);
	}

	/**
	 * in C: <CODE>short ioTrap</CODE>
	 * @param ioTrap sets field <CODE>ioTrap</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoTrap(short ioTrap) {
		setShortAt(6, ioTrap);
	}

	/**
	 * in C: <CODE>Ptr ioCmdAddr</CODE>
	 * @return field <CODE>ioCmdAddr</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getIoCmdAddr() {
		return getIntAt(8);
	}

	/**
	 * in C: <CODE>Ptr ioCmdAddr</CODE>
	 * @param ioCmdAddr sets field <CODE>ioCmdAddr</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoCmdAddr(int ioCmdAddr) {
		setIntAt(8, ioCmdAddr);
	}

	/**
	 * in C: <CODE>IOCompletionUPP ioCompletion</CODE>
	 * @return field <CODE>ioCompletion</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getIoCompletion() {
		return getIntAt(12);
	}

	/**
	 * in C: <CODE>IOCompletionUPP ioCompletion</CODE>
	 * @param ioCompletion sets field <CODE>ioCompletion</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoCompletion(int ioCompletion) {
		setIntAt(12, ioCompletion);
	}

	/**
	 * in C: <CODE>OSErr ioResult</CODE>
	 * @return field <CODE>ioResult</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getIoResult() {
		return getShortAt(16);
	}

	/**
	 * in C: <CODE>OSErr ioResult</CODE>
	 * @param ioResult sets field <CODE>ioResult</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoResult(short ioResult) {
		setShortAt(16, ioResult);
	}

	/**
	 * in C: <CODE>StringPtr ioNamePtr</CODE>
	 * @return field <CODE>ioNamePtr</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getIoNamePtr() {
		return getIntAt(18);
	}

	/**
	 * in C: <CODE>StringPtr ioNamePtr</CODE>
	 * @param ioNamePtr sets field <CODE>ioNamePtr</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoNamePtr(int ioNamePtr) {
		setIntAt(18, ioNamePtr);
	}

	/**
	 * in C: <CODE>short ioVRefNum</CODE>
	 * @return field <CODE>ioVRefNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getIoVRefNum() {
		return getShortAt(22);
	}

	/**
	 * in C: <CODE>short ioVRefNum</CODE>
	 * @param ioVRefNum sets field <CODE>ioVRefNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoVRefNum(short ioVRefNum) {
		setShortAt(22, ioVRefNum);
	}

	/**
	 * in C: <CODE>short ioRefNum</CODE>
	 * @return field <CODE>ioRefNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getIoRefNum() {
		return getShortAt(24);
	}

	/**
	 * in C: <CODE>short ioRefNum</CODE>
	 * @param ioRefNum sets field <CODE>ioRefNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoRefNum(short ioRefNum) {
		setShortAt(24, ioRefNum);
	}

	/**
	 * in C: <CODE>SInt8 ioVersNum</CODE>
	 * @return field <CODE>ioVersNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final byte getIoVersNum() {
		return getByteAt(26);
	}

	/**
	 * in C: <CODE>SInt8 ioVersNum</CODE>
	 * @param ioVersNum sets field <CODE>ioVersNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoVersNum(byte ioVersNum) {
		setByteAt(26, ioVersNum);
	}

	/**
	 * in C: <CODE>SInt8 ioPermssn</CODE>
	 * @return field <CODE>ioPermssn</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final byte getIoPermssn() {
		return getByteAt(27);
	}

	/**
	 * in C: <CODE>SInt8 ioPermssn</CODE>
	 * @param ioPermssn sets field <CODE>ioPermssn</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoPermssn(byte ioPermssn) {
		setByteAt(27, ioPermssn);
	}

	/**
	 * in C: <CODE>Ptr ioMisc</CODE>
	 * @return field <CODE>ioMisc</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getIoMisc() {
		return getIntAt(28);
	}

	/**
	 * in C: <CODE>Ptr ioMisc</CODE>
	 * @param ioMisc sets field <CODE>ioMisc</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoMisc(int ioMisc) {
		setIntAt(28, ioMisc);
	}

	/**
	 * in C: <CODE>Ptr ioBuffer</CODE>
	 * @return field <CODE>ioBuffer</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getIoBuffer() {
		return getIntAt(32);
	}

	/**
	 * in C: <CODE>Ptr ioBuffer</CODE>
	 * @param ioBuffer sets field <CODE>ioBuffer</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoBuffer(int ioBuffer) {
		setIntAt(32, ioBuffer);
	}

	/**
	 * in C: <CODE>long ioReqCount</CODE>
	 * @return field <CODE>ioReqCount</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getIoReqCount() {
		return getIntAt(36);
	}

	/**
	 * in C: <CODE>long ioReqCount</CODE>
	 * @param ioReqCount sets field <CODE>ioReqCount</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoReqCount(int ioReqCount) {
		setIntAt(36, ioReqCount);
	}

	/**
	 * in C: <CODE>long ioActCount</CODE>
	 * @return field <CODE>ioActCount</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getIoActCount() {
		return getIntAt(40);
	}

	/**
	 * in C: <CODE>long ioActCount</CODE>
	 * @param ioActCount sets field <CODE>ioActCount</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoActCount(int ioActCount) {
		setIntAt(40, ioActCount);
	}

	/**
	 * in C: <CODE>short ioPosMode</CODE>
	 * @return field <CODE>ioPosMode</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getIoPosMode() {
		return getShortAt(44);
	}

	/**
	 * in C: <CODE>short ioPosMode</CODE>
	 * @param ioPosMode sets field <CODE>ioPosMode</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoPosMode(short ioPosMode) {
		setShortAt(44, ioPosMode);
	}

	/**
	 * in C: <CODE>long ioPosOffset</CODE>
	 * @return field <CODE>ioPosOffset</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getIoPosOffset() {
		return getIntAt(46);
	}

	/**
	 * in C: <CODE>long ioPosOffset</CODE>
	 * @param ioPosOffset sets field <CODE>ioPosOffset</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoPosOffset(int ioPosOffset) {
		setIntAt(46, ioPosOffset);
	}

	/**
	 * in C: <CODE>short ioFRefNum</CODE>
	 * @return field <CODE>ioFRefNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getIoFRefNum() {
		return getShortAt(24);
	}

	/**
	 * in C: <CODE>short ioFRefNum</CODE>
	 * @param ioFRefNum sets field <CODE>ioFRefNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoFRefNum(short ioFRefNum) {
		setShortAt(24, ioFRefNum);
	}

	/**
	 * in C: <CODE>SInt8 ioFVersNum</CODE>
	 * @return field <CODE>ioFVersNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final byte getIoFVersNum() {
		return getByteAt(26);
	}

	/**
	 * in C: <CODE>SInt8 ioFVersNum</CODE>
	 * @param ioFVersNum sets field <CODE>ioFVersNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoFVersNum(byte ioFVersNum) {
		setByteAt(26, ioFVersNum);
	}

	/**
	 * in C: <CODE>short ioFDirIndex</CODE>
	 * @return field <CODE>ioFDirIndex</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getIoFDirIndex() {
		return getShortAt(28);
	}

	/**
	 * in C: <CODE>short ioFDirIndex</CODE>
	 * @param ioFDirIndex sets field <CODE>ioFDirIndex</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoFDirIndex(short ioFDirIndex) {
		setShortAt(28, ioFDirIndex);
	}

	/**
	 * in C: <CODE>SInt8 ioFlAttrib</CODE>
	 * @return field <CODE>ioFlAttrib</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final byte getIoFlAttrib() {
		return getByteAt(30);
	}

	/**
	 * in C: <CODE>SInt8 ioFlAttrib</CODE>
	 * @param ioFlAttrib sets field <CODE>ioFlAttrib</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoFlAttrib(byte ioFlAttrib) {
		setByteAt(30, ioFlAttrib);
	}

	/**
	 * in C: <CODE>SInt8 ioFlVersNum</CODE>
	 * @return field <CODE>ioFlVersNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final byte getIoFlVersNum() {
		return getByteAt(31);
	}

	/**
	 * in C: <CODE>SInt8 ioFlVersNum</CODE>
	 * @param ioFlVersNum sets field <CODE>ioFlVersNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoFlVersNum(byte ioFlVersNum) {
		setByteAt(31, ioFlVersNum);
	}



	/**
	 * in C: <CODE>unsigned long ioFlNum</CODE>
	 * @return field <CODE>ioFlNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getIoFlNum() {
		return getIntAt(48);
	}

	/**
	 * in C: <CODE>unsigned long ioFlNum</CODE>
	 * @param ioFlNum sets field <CODE>ioFlNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoFlNum(int ioFlNum) {
		setIntAt(48, ioFlNum);
	}

	/**
	 * in C: <CODE>unsigned short ioFlStBlk</CODE>
	 * @return field <CODE>ioFlStBlk</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getIoFlStBlk() {
		return getShortAt(52);
	}

	/**
	 * in C: <CODE>unsigned short ioFlStBlk</CODE>
	 * @param ioFlStBlk sets field <CODE>ioFlStBlk</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoFlStBlk(short ioFlStBlk) {
		setShortAt(52, ioFlStBlk);
	}

	/**
	 * in C: <CODE>long ioFlLgLen</CODE>
	 * @return field <CODE>ioFlLgLen</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getIoFlLgLen() {
		return getIntAt(54);
	}

	/**
	 * in C: <CODE>long ioFlLgLen</CODE>
	 * @param ioFlLgLen sets field <CODE>ioFlLgLen</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoFlLgLen(int ioFlLgLen) {
		setIntAt(54, ioFlLgLen);
	}

	/**
	 * in C: <CODE>long ioFlPyLen</CODE>
	 * @return field <CODE>ioFlPyLen</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getIoFlPyLen() {
		return getIntAt(58);
	}

	/**
	 * in C: <CODE>long ioFlPyLen</CODE>
	 * @param ioFlPyLen sets field <CODE>ioFlPyLen</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoFlPyLen(int ioFlPyLen) {
		setIntAt(58, ioFlPyLen);
	}

	/**
	 * in C: <CODE>unsigned short ioFlRStBlk</CODE>
	 * @return field <CODE>ioFlRStBlk</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getIoFlRStBlk() {
		return getShortAt(62);
	}

	/**
	 * in C: <CODE>unsigned short ioFlRStBlk</CODE>
	 * @param ioFlRStBlk sets field <CODE>ioFlRStBlk</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoFlRStBlk(short ioFlRStBlk) {
		setShortAt(62, ioFlRStBlk);
	}

	/**
	 * in C: <CODE>long ioFlRLgLen</CODE>
	 * @return field <CODE>ioFlRLgLen</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getIoFlRLgLen() {
		return getIntAt(64);
	}

	/**
	 * in C: <CODE>long ioFlRLgLen</CODE>
	 * @param ioFlRLgLen sets field <CODE>ioFlRLgLen</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoFlRLgLen(int ioFlRLgLen) {
		setIntAt(64, ioFlRLgLen);
	}

	/**
	 * in C: <CODE>long ioFlRPyLen</CODE>
	 * @return field <CODE>ioFlRPyLen</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getIoFlRPyLen() {
		return getIntAt(68);
	}

	/**
	 * in C: <CODE>long ioFlRPyLen</CODE>
	 * @param ioFlRPyLen sets field <CODE>ioFlRPyLen</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoFlRPyLen(int ioFlRPyLen) {
		setIntAt(68, ioFlRPyLen);
	}

	/**
	 * in C: <CODE>unsigned long ioFlCrDat</CODE>
	 * @return field <CODE>ioFlCrDat</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getIoFlCrDat() {
		return getIntAt(72);
	}

	/**
	 * in C: <CODE>unsigned long ioFlCrDat</CODE>
	 * @param ioFlCrDat sets field <CODE>ioFlCrDat</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoFlCrDat(int ioFlCrDat) {
		setIntAt(72, ioFlCrDat);
	}

	/**
	 * in C: <CODE>unsigned long ioFlMdDat</CODE>
	 * @return field <CODE>ioFlMdDat</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getIoFlMdDat() {
		return getIntAt(76);
	}

	/**
	 * in C: <CODE>unsigned long ioFlMdDat</CODE>
	 * @param ioFlMdDat sets field <CODE>ioFlMdDat</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoFlMdDat(int ioFlMdDat) {
		setIntAt(76, ioFlMdDat);
	}

	/**
	 * in C: <CODE>short ioVolIndex</CODE>
	 * @return field <CODE>ioVolIndex</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getIoVolIndex() {
		return getShortAt(28);
	}

	/**
	 * in C: <CODE>short ioVolIndex</CODE>
	 * @param ioVolIndex sets field <CODE>ioVolIndex</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoVolIndex(short ioVolIndex) {
		setShortAt(28, ioVolIndex);
	}

	/**
	 * in C: <CODE>unsigned long ioVCrDate</CODE>
	 * @return field <CODE>ioVCrDate</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getIoVCrDate() {
		return getIntAt(30);
	}

	/**
	 * in C: <CODE>unsigned long ioVCrDate</CODE>
	 * @param ioVCrDate sets field <CODE>ioVCrDate</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoVCrDate(int ioVCrDate) {
		setIntAt(30, ioVCrDate);
	}

	/**
	 * in C: <CODE>unsigned long ioVLsBkUp</CODE>
	 * @return field <CODE>ioVLsBkUp</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getIoVLsBkUp() {
		return getIntAt(34);
	}

	/**
	 * in C: <CODE>unsigned long ioVLsBkUp</CODE>
	 * @param ioVLsBkUp sets field <CODE>ioVLsBkUp</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoVLsBkUp(int ioVLsBkUp) {
		setIntAt(34, ioVLsBkUp);
	}

	/**
	 * in C: <CODE>unsigned short ioVAtrb</CODE>
	 * @return field <CODE>ioVAtrb</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getIoVAtrb() {
		return getShortAt(38);
	}

	/**
	 * in C: <CODE>unsigned short ioVAtrb</CODE>
	 * @param ioVAtrb sets field <CODE>ioVAtrb</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoVAtrb(short ioVAtrb) {
		setShortAt(38, ioVAtrb);
	}

	/**
	 * in C: <CODE>unsigned short ioVNmFls</CODE>
	 * @return field <CODE>ioVNmFls</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getIoVNmFls() {
		return getShortAt(40);
	}

	/**
	 * in C: <CODE>unsigned short ioVNmFls</CODE>
	 * @param ioVNmFls sets field <CODE>ioVNmFls</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoVNmFls(short ioVNmFls) {
		setShortAt(40, ioVNmFls);
	}

	/**
	 * in C: <CODE>unsigned short ioVDirSt</CODE>
	 * @return field <CODE>ioVDirSt</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getIoVDirSt() {
		return getShortAt(42);
	}

	/**
	 * in C: <CODE>unsigned short ioVDirSt</CODE>
	 * @param ioVDirSt sets field <CODE>ioVDirSt</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoVDirSt(short ioVDirSt) {
		setShortAt(42, ioVDirSt);
	}

	/**
	 * in C: <CODE>short ioVBlLn</CODE>
	 * @return field <CODE>ioVBlLn</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getIoVBlLn() {
		return getShortAt(44);
	}

	/**
	 * in C: <CODE>short ioVBlLn</CODE>
	 * @param ioVBlLn sets field <CODE>ioVBlLn</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoVBlLn(short ioVBlLn) {
		setShortAt(44, ioVBlLn);
	}

	/**
	 * in C: <CODE>unsigned short ioVNmAlBlks</CODE>
	 * @return field <CODE>ioVNmAlBlks</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getIoVNmAlBlks() {
		return getShortAt(46);
	}

	/**
	 * in C: <CODE>unsigned short ioVNmAlBlks</CODE>
	 * @param ioVNmAlBlks sets field <CODE>ioVNmAlBlks</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoVNmAlBlks(short ioVNmAlBlks) {
		setShortAt(46, ioVNmAlBlks);
	}

	/**
	 * in C: <CODE>unsigned long ioVAlBlkSiz</CODE>
	 * @return field <CODE>ioVAlBlkSiz</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getIoVAlBlkSiz() {
		return getIntAt(48);
	}

	/**
	 * in C: <CODE>unsigned long ioVAlBlkSiz</CODE>
	 * @param ioVAlBlkSiz sets field <CODE>ioVAlBlkSiz</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoVAlBlkSiz(int ioVAlBlkSiz) {
		setIntAt(48, ioVAlBlkSiz);
	}

	/**
	 * in C: <CODE>unsigned long ioVClpSiz</CODE>
	 * @return field <CODE>ioVClpSiz</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getIoVClpSiz() {
		return getIntAt(52);
	}

	/**
	 * in C: <CODE>unsigned long ioVClpSiz</CODE>
	 * @param ioVClpSiz sets field <CODE>ioVClpSiz</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoVClpSiz(int ioVClpSiz) {
		setIntAt(52, ioVClpSiz);
	}

	/**
	 * in C: <CODE>unsigned short ioAlBlSt</CODE>
	 * @return field <CODE>ioAlBlSt</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getIoAlBlSt() {
		return getShortAt(56);
	}

	/**
	 * in C: <CODE>unsigned short ioAlBlSt</CODE>
	 * @param ioAlBlSt sets field <CODE>ioAlBlSt</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoAlBlSt(short ioAlBlSt) {
		setShortAt(56, ioAlBlSt);
	}

	/**
	 * in C: <CODE>unsigned long ioVNxtFNum</CODE>
	 * @return field <CODE>ioVNxtFNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getIoVNxtFNum() {
		return getIntAt(58);
	}

	/**
	 * in C: <CODE>unsigned long ioVNxtFNum</CODE>
	 * @param ioVNxtFNum sets field <CODE>ioVNxtFNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoVNxtFNum(int ioVNxtFNum) {
		setIntAt(58, ioVNxtFNum);
	}

	/**
	 * in C: <CODE>unsigned short ioVFrBlk</CODE>
	 * @return field <CODE>ioVFrBlk</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getIoVFrBlk() {
		return getShortAt(62);
	}

	/**
	 * in C: <CODE>unsigned short ioVFrBlk</CODE>
	 * @param ioVFrBlk sets field <CODE>ioVFrBlk</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoVFrBlk(short ioVFrBlk) {
		setShortAt(62, ioVFrBlk);
	}

	/**
	 * in C: <CODE>short ioCRefNum</CODE>
	 * @return field <CODE>ioCRefNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getIoCRefNum() {
		return getShortAt(24);
	}

	/**
	 * in C: <CODE>short ioCRefNum</CODE>
	 * @param ioCRefNum sets field <CODE>ioCRefNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoCRefNum(short ioCRefNum) {
		setShortAt(24, ioCRefNum);
	}

	/**
	 * in C: <CODE>short csCode</CODE>
	 * @return field <CODE>csCode</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getCsCode() {
		return getShortAt(26);
	}

	/**
	 * in C: <CODE>short csCode</CODE>
	 * @param csCode sets field <CODE>csCode</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setCsCode(short csCode) {
		setShortAt(26, csCode);
	}

	/**
	 * in C: <CODE>short csParam[11]</CODE>
	 * @param arrayindex zero based index
	 * @return arrayindex element of field <CODE>csParam</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getCsParam(int arrayindex) {
		return getShortAt(28 + (arrayindex*2));
	}

	/**
	 * in C: <CODE>short csParam[11]</CODE>
	 * @param arrayindex zero based index
	 * @param csParam sets arrayindex element of field <CODE>csParam</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setCsParam(int arrayindex, short csParam) {
		setShortAt(28 + (arrayindex*2), csParam);
	}

	/**
	 * in C: <CODE>short ioSRefNum</CODE>
	 * @return field <CODE>ioSRefNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getIoSRefNum() {
		return getShortAt(24);
	}

	/**
	 * in C: <CODE>short ioSRefNum</CODE>
	 * @param ioSRefNum sets field <CODE>ioSRefNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoSRefNum(short ioSRefNum) {
		setShortAt(24, ioSRefNum);
	}

	/**
	 * in C: <CODE>SInt8 ioSVersNum</CODE>
	 * @return field <CODE>ioSVersNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final byte getIoSVersNum() {
		return getByteAt(26);
	}

	/**
	 * in C: <CODE>SInt8 ioSVersNum</CODE>
	 * @param ioSVersNum sets field <CODE>ioSVersNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoSVersNum(byte ioSVersNum) {
		setByteAt(26, ioSVersNum);
	}

	/**
	 * in C: <CODE>SInt8 ioSPermssn</CODE>
	 * @return field <CODE>ioSPermssn</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final byte getIoSPermssn() {
		return getByteAt(27);
	}

	/**
	 * in C: <CODE>SInt8 ioSPermssn</CODE>
	 * @param ioSPermssn sets field <CODE>ioSPermssn</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoSPermssn(byte ioSPermssn) {
		setByteAt(27, ioSPermssn);
	}

	/**
	 * in C: <CODE>Ptr ioSMix</CODE>
	 * @return field <CODE>ioSMix</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getIoSMix() {
		return getIntAt(28);
	}

	/**
	 * in C: <CODE>Ptr ioSMix</CODE>
	 * @param ioSMix sets field <CODE>ioSMix</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoSMix(int ioSMix) {
		setIntAt(28, ioSMix);
	}

	/**
	 * in C: <CODE>short ioSFlags</CODE>
	 * @return field <CODE>ioSFlags</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getIoSFlags() {
		return getShortAt(32);
	}

	/**
	 * in C: <CODE>short ioSFlags</CODE>
	 * @param ioSFlags sets field <CODE>ioSFlags</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoSFlags(short ioSFlags) {
		setShortAt(32, ioSFlags);
	}

	/**
	 * in C: <CODE>SInt8 ioSlot</CODE>
	 * @return field <CODE>ioSlot</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final byte getIoSlot() {
		return getByteAt(34);
	}

	/**
	 * in C: <CODE>SInt8 ioSlot</CODE>
	 * @param ioSlot sets field <CODE>ioSlot</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoSlot(byte ioSlot) {
		setByteAt(34, ioSlot);
	}

	/**
	 * in C: <CODE>SInt8 ioID</CODE>
	 * @return field <CODE>ioID</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final byte getIoID() {
		return getByteAt(35);
	}

	/**
	 * in C: <CODE>SInt8 ioID</CODE>
	 * @param ioID sets field <CODE>ioID</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoID(byte ioID) {
		setByteAt(35, ioID);
	}

	/**
	 * in C: <CODE>short ioMRefNum</CODE>
	 * @return field <CODE>ioMRefNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getIoMRefNum() {
		return getShortAt(24);
	}

	/**
	 * in C: <CODE>short ioMRefNum</CODE>
	 * @param ioMRefNum sets field <CODE>ioMRefNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoMRefNum(short ioMRefNum) {
		setShortAt(24, ioMRefNum);
	}

	/**
	 * in C: <CODE>SInt8 ioMVersNum</CODE>
	 * @return field <CODE>ioMVersNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final byte getIoMVersNum() {
		return getByteAt(26);
	}

	/**
	 * in C: <CODE>SInt8 ioMVersNum</CODE>
	 * @param ioMVersNum sets field <CODE>ioMVersNum</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoMVersNum(byte ioMVersNum) {
		setByteAt(26, ioMVersNum);
	}

	/**
	 * in C: <CODE>SInt8 ioMPermssn</CODE>
	 * @return field <CODE>ioMPermssn</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final byte getIoMPermssn() {
		return getByteAt(27);
	}

	/**
	 * in C: <CODE>SInt8 ioMPermssn</CODE>
	 * @param ioMPermssn sets field <CODE>ioMPermssn</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoMPermssn(byte ioMPermssn) {
		setByteAt(27, ioMPermssn);
	}

	/**
	 * in C: <CODE>Ptr ioMMix</CODE>
	 * @return field <CODE>ioMMix</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getIoMMix() {
		return getIntAt(28);
	}

	/**
	 * in C: <CODE>Ptr ioMMix</CODE>
	 * @param ioMMix sets field <CODE>ioMMix</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoMMix(int ioMMix) {
		setIntAt(28, ioMMix);
	}

	/**
	 * in C: <CODE>short ioMFlags</CODE>
	 * @return field <CODE>ioMFlags</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final short getIoMFlags() {
		return getShortAt(32);
	}

	/**
	 * in C: <CODE>short ioMFlags</CODE>
	 * @param ioMFlags sets field <CODE>ioMFlags</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoMFlags(short ioMFlags) {
		setShortAt(32, ioMFlags);
	}

	/**
	 * in C: <CODE>Ptr ioSEBlkPtr</CODE>
	 * @return field <CODE>ioSEBlkPtr</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final int getIoSEBlkPtr() {
		return getIntAt(34);
	}

	/**
	 * in C: <CODE>Ptr ioSEBlkPtr</CODE>
	 * @param ioSEBlkPtr sets field <CODE>ioSEBlkPtr</CODE> of <CODE>struct ParamBlockRec</CODE>
	 */
	public final void setIoSEBlkPtr(int ioSEBlkPtr) {
		setIntAt(34, ioSEBlkPtr);
	}

	/**
	 * Size of <CODE>struct ParamBlockRec</CODE> in bytes
	 */
	public final static int sizeOfParamBlockRec = 80;

	public int getSize() {
		return sizeOfParamBlockRec;
	}
}
