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
