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

public class SerialManager
{
private 	static Vector 	availablePorts = new Vector();
	public static void checkAvailableSerialPorts(){
		boolean macOS = System.getProperty("os.name").startsWith("Mac OS");
		availablePorts.removeAllElements();
		if(macOS) createMACPorts();
	}
	
	private static void createMACPorts(){
		CRMRecStruct 			c = new CRMRecStruct();
		int					cPtrInt = -1;
		int crmErr = JDirectImpl.InitCRM();
		if(crmErr == JDirectImpl.noErr){
			c.setCrmDeviceType(CRMSerialDeviceConstants.crmSerialDevice);
			c.setCrmDeviceID(0);
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
					availablePorts.addElement(new SerialPortDesc(in,out,nm));
				}
			}
		}
	}
	
	public static int getNumbSerialPorts(){
		if(availablePorts == null) return 0;
		return availablePorts.size();
	}
	public static java.util.Enumeration getSerialPorts() {return availablePorts.elements();}
	
	public static void assignZeroPort(int index){
		if(index < 0 || index >= getNumbSerialPorts()) return;
		String userHomeName = System.getProperty("user.home");
		File serialProp = new File(userHomeName,"concordSerial.properties");
		if(serialProp.exists()) serialProp.delete();
		Properties p = new Properties();
		SerialPortDesc pDesc = (SerialPortDesc)availablePorts.elementAt(index);
		p.put("org.concord.zeroport",pDesc.inpName+";"+pDesc.outName+";"+pDesc.name);
		try{
			FileOutputStream fout = new FileOutputStream(serialProp);
			p.save(fout,"Serial port assigned to communication with probs");
			fout.close();
		}catch(Exception e){}
	}
	
	public static SerialPortDesc getAssignedPort(){
		SerialPortDesc retValue = null;
		String userHomeName = System.getProperty("user.home");
		File serialProp = new File(userHomeName,"concordSerial.properties");
		if(!serialProp.exists()) return retValue;
		String portProp = null;
		try{
			FileInputStream fin = new FileInputStream(serialProp);
			Properties p = new Properties();
			p.load(fin);
			portProp = p.getProperty("org.concord.zeroport");
			fin.close();
			java.util.StringTokenizer parser = new java.util.StringTokenizer(portProp,";");
			String inpName = parser.nextToken();
			String outName = parser.nextToken();
			String name = parser.nextToken();
			retValue = new SerialPortDesc(inpName,outName,name);
		}catch(Exception e){
			retValue = null;
		}
		return retValue;

	}
	
}

public interface CRMSerialDeviceConstants {
	public final int 	crmSerialDevice					= 1;
	public final int 	curCRMSerRecVers				= 1;


}
public class CRMRecStruct extends ByteArrayStruct {

	/**
	 * Constructs an uninitialized <CODE>CRMRecStruct</CODE>
	 */
	public CRMRecStruct() {
		super(sizeOfCRMRec);
	}

	/**
	 * Constructs a <CODE>CRMRecStruct</CODE> and initializes it with the data in another <CODE>Struct</CODE>. 
	 * Useful when a C struct <CODE>CRMRec</CODE> is embedded in another C struct and you want to
	 * copy it out. 
	 * @param src the <CODE>Struct</CODE> containing the data to be copied
	 * @param offsetInSrc the offest in of the data in <CODE>src</CODE>
	 */
	public CRMRecStruct(Struct src, int offsetInSrc) {
		super(sizeOfCRMRec);
		byte[] bytes = src.getBytesAt(offsetInSrc, sizeOfCRMRec);
		this.setBytesAt(0, bytes);
	}

	/**
	 * Used only by subclasses of <CODE>CRMRecStruct</CODE>
	 */
	protected CRMRecStruct(int size) {
		super(size);
	}

	/**
	 * in C: <CODE>QElemPtr qLink</CODE>
	 * @return field <CODE>qLink</CODE> of <CODE>struct CRMRec</CODE>
	 */
	public final int getQLink() {
		return getIntAt(0);
	}

	/**
	 * in C: <CODE>QElemPtr qLink</CODE>
	 * @param qLink sets field <CODE>qLink</CODE> of <CODE>struct CRMRec</CODE>
	 */
	public final void setQLink(int qLink) {
		setIntAt(0, qLink);
	}

	/**
	 * in C: <CODE>short qType</CODE>
	 * @return field <CODE>qType</CODE> of <CODE>struct CRMRec</CODE>
	 */
	public final short getQType() {
		return getShortAt(4);
	}

	/**
	 * in C: <CODE>short qType</CODE>
	 * @param qType sets field <CODE>qType</CODE> of <CODE>struct CRMRec</CODE>
	 */
	public final void setQType(short qType) {
		setShortAt(4, qType);
	}

	/**
	 * in C: <CODE>short crmVersion</CODE>
	 * @return field <CODE>crmVersion</CODE> of <CODE>struct CRMRec</CODE>
	 */
	public final short getCrmVersion() {
		return getShortAt(6);
	}

	/**
	 * in C: <CODE>short crmVersion</CODE>
	 * @param crmVersion sets field <CODE>crmVersion</CODE> of <CODE>struct CRMRec</CODE>
	 */
	public final void setCrmVersion(short crmVersion) {
		setShortAt(6, crmVersion);
	}

	/**
	 * in C: <CODE>long crmPrivate</CODE>
	 * @return field <CODE>crmPrivate</CODE> of <CODE>struct CRMRec</CODE>
	 */
	public final int getCrmPrivate() {
		return getIntAt(8);
	}

	/**
	 * in C: <CODE>long crmPrivate</CODE>
	 * @param crmPrivate sets field <CODE>crmPrivate</CODE> of <CODE>struct CRMRec</CODE>
	 */
	public final void setCrmPrivate(int crmPrivate) {
		setIntAt(8, crmPrivate);
	}

	/**
	 * in C: <CODE>short crmReserved</CODE>
	 * @return field <CODE>crmReserved</CODE> of <CODE>struct CRMRec</CODE>
	 */
	public final short getCrmReserved() {
		return getShortAt(12);
	}

	/**
	 * in C: <CODE>short crmReserved</CODE>
	 * @param crmReserved sets field <CODE>crmReserved</CODE> of <CODE>struct CRMRec</CODE>
	 */
	public final void setCrmReserved(short crmReserved) {
		setShortAt(12, crmReserved);
	}

	/**
	 * in C: <CODE>long crmDeviceType</CODE>
	 * @return field <CODE>crmDeviceType</CODE> of <CODE>struct CRMRec</CODE>
	 */
	public final int getCrmDeviceType() {
		return getIntAt(14);
	}

	/**
	 * in C: <CODE>long crmDeviceType</CODE>
	 * @param crmDeviceType sets field <CODE>crmDeviceType</CODE> of <CODE>struct CRMRec</CODE>
	 */
	public final void setCrmDeviceType(int crmDeviceType) {
		setIntAt(14, crmDeviceType);
	}

	/**
	 * in C: <CODE>long crmDeviceID</CODE>
	 * @return field <CODE>crmDeviceID</CODE> of <CODE>struct CRMRec</CODE>
	 */
	public final int getCrmDeviceID() {
		return getIntAt(18);
	}

	/**
	 * in C: <CODE>long crmDeviceID</CODE>
	 * @param crmDeviceID sets field <CODE>crmDeviceID</CODE> of <CODE>struct CRMRec</CODE>
	 */
	public final void setCrmDeviceID(int crmDeviceID) {
		setIntAt(18, crmDeviceID);
	}

	/**
	 * in C: <CODE>long crmAttributes</CODE>
	 * @return field <CODE>crmAttributes</CODE> of <CODE>struct CRMRec</CODE>
	 */
	public final int getCrmAttributes() {
		return getIntAt(22);
	}

	/**
	 * in C: <CODE>long crmAttributes</CODE>
	 * @param crmAttributes sets field <CODE>crmAttributes</CODE> of <CODE>struct CRMRec</CODE>
	 */
	public final void setCrmAttributes(int crmAttributes) {
		setIntAt(22, crmAttributes);
	}

	/**
	 * in C: <CODE>long crmStatus</CODE>
	 * @return field <CODE>crmStatus</CODE> of <CODE>struct CRMRec</CODE>
	 */
	public final int getCrmStatus() {
		return getIntAt(26);
	}

	/**
	 * in C: <CODE>long crmStatus</CODE>
	 * @param crmStatus sets field <CODE>crmStatus</CODE> of <CODE>struct CRMRec</CODE>
	 */
	public final void setCrmStatus(int crmStatus) {
		setIntAt(26, crmStatus);
	}

	/**
	 * in C: <CODE>long crmRefCon</CODE>
	 * @return field <CODE>crmRefCon</CODE> of <CODE>struct CRMRec</CODE>
	 */
	public final int getCrmRefCon() {
		return getIntAt(30);
	}

	/**
	 * in C: <CODE>long crmRefCon</CODE>
	 * @param crmRefCon sets field <CODE>crmRefCon</CODE> of <CODE>struct CRMRec</CODE>
	 */
	public final void setCrmRefCon(int crmRefCon) {
		setIntAt(30, crmRefCon);
	}

	/**
	 * Size of <CODE>struct CRMRec</CODE> in bytes
	 */
	public final static int sizeOfCRMRec = 34;
}
public class SerialPortDesc{
public String	inpName;
public String	outName;
public String	name;
	public SerialPortDesc(String inpName,String outName,String name){
		this.inpName = inpName;
		this.outName = outName;
		this.name = name;
	}
}


public class CRMSerialRecordStruct extends ByteArrayStruct {

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
