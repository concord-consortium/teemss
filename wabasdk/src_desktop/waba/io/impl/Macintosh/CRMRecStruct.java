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
