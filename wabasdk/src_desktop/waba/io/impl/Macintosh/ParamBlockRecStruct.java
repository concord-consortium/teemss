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
