package waba.io.impl;

import  com.apple.mrj.macos.libraries.InterfaceLib;
import  com.apple.jdirect.SharedLibrary;
import  com.apple.mrj.jdirect.*;
import  com.apple.mrj.macos.toolbox.Handle;
import java.util.Vector;
import java.util.Properties;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;

public class MacPortScanner{
	public void scanPorts(java.util.Vector v){
		try{
			CRMRecStruct 			c = new CRMRecStruct();
			int					cPtrInt = -1;
			int crmErr = JDirectImpl.InitCRM();
			if(crmErr == JDirectImpl.noErr){
				c.setCrmDeviceType(CRMSerialDeviceConstants.crmSerialDevice);
				c.setCrmDeviceID(0);
				int index = 0;
				while(cPtrInt != 0){
					cPtrInt = JDirectImpl.CRMSearch(c);
					if(cPtrInt != 0){
						MyPtr1 tempPtr = new MyPtr1(cPtrInt);
						CRMRecStruct ct = new CRMRecStruct(tempPtr,0);
						int crmAttrInt = ct.getCrmAttributes();
						tempPtr = new MyPtr1(crmAttrInt);
						CRMSerialRecordStruct serialStruct = new CRMSerialRecordStruct(tempPtr,0);
						String in = JDirectImpl.getStringFromStringHandle(serialStruct.getInputDriverName());
						String out = JDirectImpl.getStringFromStringHandle(serialStruct.getOutputDriverName());
						String nm = JDirectImpl.getStringFromStringHandle(serialStruct.getName());
						c.setCrmDeviceID(ct.getCrmDeviceID());
						if(!nm.startsWith("Infrared") && !nm.startsWith("Internal")){
							v.addElement(new SerialPortDesc(index++,in,out,nm));
						}
					}
				}
			}
		}catch(Throwable t){}
	}
}
class CRMSerialRecordStruct extends ByteArrayStruct {

	/**
	 * Constructs an uninitialized <CODE>CRMSerialRecordStruct</CODE>
	 */
	public CRMSerialRecordStruct() {
		super(sizeOfCRMSerialRecord);
	}

	/**
	 * Constructs a <CODE>CRMSerialRecordStruct</CODE> and initializes it with the data in another <CODE>Struct</CODE>. 
	 * Useful when a C struct <CODE>CRMSerialRecord</CODE> is embedded in another C struct and you want to
	 * copy it out. 
	 * @param src the <CODE>Struct</CODE> containing the data to be copied
	 * @param offsetInSrc the offest in of the data in <CODE>src</CODE>
	 */
	public CRMSerialRecordStruct(Struct src, int offsetInSrc) {
		super(sizeOfCRMSerialRecord);
		byte[] bytes = src.getBytesAt(offsetInSrc, sizeOfCRMSerialRecord);
		this.setBytesAt(0, bytes);
	}

	/**
	 * Used only by subclasses of <CODE>CRMSerialRecordStruct</CODE>
	 */
	protected CRMSerialRecordStruct(int size) {
		super(size);
	}

	/**
	 * in C: <CODE>short version</CODE>
	 * @return field <CODE>version</CODE> of <CODE>struct CRMSerialRecord</CODE>
	 */
	public final short getVersion() {
		return getShortAt(0);
	}

	/**
	 * in C: <CODE>short version</CODE>
	 * @param version sets field <CODE>version</CODE> of <CODE>struct CRMSerialRecord</CODE>
	 */
	public final void setVersion(short version) {
		setShortAt(0, version);
	}

	/**
	 * in C: <CODE>StringHandle inputDriverName</CODE>
	 * @return field <CODE>inputDriverName</CODE> of <CODE>struct CRMSerialRecord</CODE>
	 */
	public final int getInputDriverName() {
		return getIntAt(2);
	}

	/**
	 * in C: <CODE>StringHandle inputDriverName</CODE>
	 * @param inputDriverName sets field <CODE>inputDriverName</CODE> of <CODE>struct CRMSerialRecord</CODE>
	 */
	public final void setInputDriverName(int inputDriverName) {
		setIntAt(2, inputDriverName);
	}

	/**
	 * in C: <CODE>StringHandle outputDriverName</CODE>
	 * @return field <CODE>outputDriverName</CODE> of <CODE>struct CRMSerialRecord</CODE>
	 */
	public final int getOutputDriverName() {
		return getIntAt(6);
	}

	/**
	 * in C: <CODE>StringHandle outputDriverName</CODE>
	 * @param outputDriverName sets field <CODE>outputDriverName</CODE> of <CODE>struct CRMSerialRecord</CODE>
	 */
	public final void setOutputDriverName(int outputDriverName) {
		setIntAt(6, outputDriverName);
	}

	/**
	 * in C: <CODE>StringHandle name</CODE>
	 * @return field <CODE>name</CODE> of <CODE>struct CRMSerialRecord</CODE>
	 */
	public final int getName() {
		return getIntAt(10);
	}

	/**
	 * in C: <CODE>StringHandle name</CODE>
	 * @param name sets field <CODE>name</CODE> of <CODE>struct CRMSerialRecord</CODE>
	 */
	public final void setName(int name) {
		setIntAt(10, name);
	}

	/**
	 * in C: <CODE>CRMIconHandle deviceIcon</CODE>
	 * @return field <CODE>deviceIcon</CODE> of <CODE>struct CRMSerialRecord</CODE>
	 */
	public final int getDeviceIcon() {
		return getIntAt(14);
	}

	/**
	 * in C: <CODE>CRMIconHandle deviceIcon</CODE>
	 * @param deviceIcon sets field <CODE>deviceIcon</CODE> of <CODE>struct CRMSerialRecord</CODE>
	 */
	public final void setDeviceIcon(int deviceIcon) {
		setIntAt(14, deviceIcon);
	}

	/**
	 * in C: <CODE>long ratedSpeed</CODE>
	 * @return field <CODE>ratedSpeed</CODE> of <CODE>struct CRMSerialRecord</CODE>
	 */
	public final int getRatedSpeed() {
		return getIntAt(18);
	}

	/**
	 * in C: <CODE>long ratedSpeed</CODE>
	 * @param ratedSpeed sets field <CODE>ratedSpeed</CODE> of <CODE>struct CRMSerialRecord</CODE>
	 */
	public final void setRatedSpeed(int ratedSpeed) {
		setIntAt(18, ratedSpeed);
	}

	/**
	 * in C: <CODE>long maxSpeed</CODE>
	 * @return field <CODE>maxSpeed</CODE> of <CODE>struct CRMSerialRecord</CODE>
	 */
	public final int getMaxSpeed() {
		return getIntAt(22);
	}

	/**
	 * in C: <CODE>long maxSpeed</CODE>
	 * @param maxSpeed sets field <CODE>maxSpeed</CODE> of <CODE>struct CRMSerialRecord</CODE>
	 */
	public final void setMaxSpeed(int maxSpeed) {
		setIntAt(22, maxSpeed);
	}

	/**
	 * in C: <CODE>long reserved</CODE>
	 * @return field <CODE>reserved</CODE> of <CODE>struct CRMSerialRecord</CODE>
	 */
	public final int getReserved() {
		return getIntAt(26);
	}

	/**
	 * in C: <CODE>long reserved</CODE>
	 * @param reserved sets field <CODE>reserved</CODE> of <CODE>struct CRMSerialRecord</CODE>
	 */
	public final void setReserved(int reserved) {
		setIntAt(26, reserved);
	}

	/**
	 * Size of <CODE>struct CRMSerialRecord</CODE> in bytes
	 */
	public final static int sizeOfCRMSerialRecord = 30;
}
