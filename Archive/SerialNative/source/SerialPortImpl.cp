#include "SerialPortImpl.h"
#include "JNISig.h"
#include <string.h>
#include <Sound.h>
#include <Serial.h>
#include <Devices.h>
#define OUTDRIVERB "\p.BOut"
#define INDRIVERB "\p.BIn"

jint getTimeOut(JNIEnv *env, jobject object);
short getInpPortRef(JNIEnv *env, jobject object);
short getOutPortRef(JNIEnv *env, jobject object);
void setInpPortRef(JNIEnv *env, jobject object,short val);
void setOutPortRef(JNIEnv *env, jobject object,short val);
jlong getCurrentSysTimeMillis(JNIEnv *env);

short		getMacStopBits(jint stopbits);
short		getMacBaudRate(jint rate);
short		getMacBits(jint bits);
jboolean	closePort(short inpref,short outref);

/*
 * Class:     waba_io_impl_SerialPortImpl
 * Method:    initPort
 * Signature: (IIIZI)V
 */
JNIEXPORT void JNICALL Java_waba_io_impl_SerialPortImpl_initPort
	(JNIEnv *env, jobject obj, jint number, jint baudRate, jint bits, jboolean parity, jint stopBits)
{
	short	inputRefNumber,outputRefNumber;
	OSErr	errInp;
	OSErr	errOut;
	short macRate,macBits,macParity,macStopBits;

/*
	{
		short int t1 = sizeof(ParamBlockRec);//80
		short int t2 = sizeof(IOParam);//50
		short int t3 = sizeof(FileParam);//80
		short int t4 = sizeof(VolumeParam);//64
		short int t5 = sizeof(CntrlParam);//50
		short int t6 = sizeof(SlotDevParam);//36
		short int t7 = sizeof(MultiDevParam);//38
		if(t1 > 0) SysBeep(0);
		if(t2 > 0) SysBeep(0);
		if(t3 > 0) SysBeep(0);
		if(t4 > 0) SysBeep(0);
		if(t5 > 0) SysBeep(0);
		if(t6 > 0) SysBeep(0);
		if(t7 > 0) SysBeep(0);
		SysBeep(0);
	}
*/

	char	portInpName[256];
	char	portOutName[256];

	portInpName[0] = 4;
	portOutName[0] = 5;
	portInpName[1] = portOutName[1] = '.';
	portInpName[2] = portOutName[2] = 'A'+number;
	memcpy(portInpName+3,"In",2);
	memcpy(portOutName+3,"Out",3);
	
	
	errOut 	= OpenDriver((const unsigned char *)portOutName,&outputRefNumber);
	errInp 	= OpenDriver((const unsigned char *)portInpName,&inputRefNumber);
	if(errOut != noErr || errInp != noErr){
		outputRefNumber = 0;
		inputRefNumber = 0;
	}else{
		macRate = getMacBaudRate(baudRate);
		macBits = getMacBits(bits);
//param parity true for even parity, false for no parity
		macParity = (parity == JNI_TRUE) ? evenParity : noParity;
		macStopBits = getMacStopBits(stopBits);
		errOut = SerReset(outputRefNumber,macRate+macStopBits+macBits+macParity);
		errInp = SerReset(inputRefNumber,macRate+macStopBits+macBits+macParity);
		if(errOut != noErr || errInp != noErr){
			closePort(inputRefNumber,outputRefNumber);
		}
	}
	SerShk hs;
	hs.fXOn 	= 0;
 	hs.fCTS 	= 0;
 	hs.errs 	= 0;
 	hs.evts 	= 0;
  	hs.fInX 	= 0; // in MBL was 1
	hs.fDTR 	= 0;
	errOut = Control(outputRefNumber, 14, &hs); //csCode = 14
	if(errOut != noErr){
		closePort(inputRefNumber,outputRefNumber);
	}else{
		setInpPortRef(env,obj,inputRefNumber);
		setOutPortRef(env,obj,outputRefNumber);
	}
}


/*
 * Class:     waba_io_impl_SerialPortImpl
 * Method:    initPort1
 * Signature: (Ljava/lang/String;Ljava/lang/String;IIZI)V
*/
JNIEXPORT void JNICALL Java_waba_io_impl_SerialPortImpl_initPort1
	(JNIEnv *env, jobject obj, jstring inPort, jstring outPort, jint baudRate, jint bits, jboolean parity, jint stopBits)
{

	short	inputRefNumber,outputRefNumber;
	OSErr	errInp;
	OSErr	errOut;
	short macRate,macBits,macParity,macStopBits;
	char	portInpName[256];
	char	portOutName[256];
	jboolean isCopy;
	const char* inStrPort = env->GetStringUTFChars(inPort,&isCopy);
	jint len = strlen(inStrPort);
	memcpy(portInpName + 1,inStrPort,len);
	portInpName[0] = len;
	if(isCopy == JNI_TRUE){
		env->ReleaseStringUTFChars(inPort,inStrPort);
	}
	const char* outStrPort = env->GetStringUTFChars(outPort,&isCopy);
	len = strlen(outStrPort);
	memcpy(portOutName + 1,outStrPort,len);
	portOutName[0] = len;
	if(isCopy == JNI_TRUE){
		env->ReleaseStringUTFChars(outPort,outStrPort);
	}
	errOut 	= OpenDriver((const unsigned char *)portOutName,&outputRefNumber);
	errInp 	= OpenDriver((const unsigned char *)portInpName,&inputRefNumber);
	if(errOut != noErr || errInp != noErr){
		outputRefNumber = 0;
		inputRefNumber = 0;
	}else{
		macRate = getMacBaudRate(baudRate);
		macBits = getMacBits(bits);
//param parity true for even parity, false for no parity
		macParity = (parity == JNI_TRUE) ? evenParity : noParity;
		macStopBits = getMacStopBits(stopBits);
		errOut = SerReset(outputRefNumber,macRate+macStopBits+macBits+macParity);
		errInp = SerReset(inputRefNumber,macRate+macStopBits+macBits+macParity);
		if(errOut != noErr || errInp != noErr){
			closePort(inputRefNumber,outputRefNumber);
		}
	}
	SerShk hs;
	hs.fXOn 	= 0;
 	hs.fCTS 	= 0;
 	hs.errs 	= 0;
 	hs.evts 	= 0;
  	hs.fInX 	= 0; // in MBL was 1
	hs.fDTR 	= 0;
	errOut = Control(outputRefNumber, 14, &hs); //csCode = 14
	if(errOut != noErr){
		closePort(inputRefNumber,outputRefNumber);
	}else{
		setInpPortRef(env,obj,inputRefNumber);
		setOutPortRef(env,obj,outputRefNumber);
	}

}


/*
 * Class:     waba_io_impl_SerialPortImpl
 * Method:    close
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_waba_io_impl_SerialPortImpl_close
	(JNIEnv *env, jobject object)
{
	short	inputRefNumber = getInpPortRef(env,object);
	short	outputRefNumber = getOutPortRef(env,object);
	return closePort(inputRefNumber,outputRefNumber);
}

/*
 * Class:     waba_io_impl_SerialPortImpl
 * Method:    isOpen
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_waba_io_impl_SerialPortImpl_isOpen
	(JNIEnv *env, jobject object)
{
jboolean retValue = JNI_FALSE;
	short	inputRefNumber = getInpPortRef(env,object);
	short	outputRefNumber = getOutPortRef(env,object);
	retValue = ((inputRefNumber == 0) || (outputRefNumber == 0)) ? JNI_FALSE : JNI_TRUE;
	return retValue;
}

/*
 * Class:     waba_io_impl_SerialPortImpl
 * Method:    readBytes
 * Signature: ([BII)I
 */
JNIEXPORT jint JNICALL Java_waba_io_impl_SerialPortImpl_readBytes
	(JNIEnv *env, jobject object, jbyteArray buffer, jint start, jint count)
{
	short	inputRefNumber = getInpPortRef(env,object);
	if(inputRefNumber == NULL) return -1;
	if(buffer == NULL) return -1;
    jsize bufSize = env->GetArrayLength(buffer);
    if(bufSize <= 0) return 0;						//buffer is too small
    jint  actNeedData = count;
    jint  actStart = start;
    if(actStart < 0) actStart = 0;
    jint  actCount = count;
    if(actCount < 0) actCount = 0;
    if(actCount == 0) return 0;
    if(actStart + actCount > bufSize){//wants more data than I can deliver
    	actNeedData = bufSize - start;
    }
    if(actNeedData <= 0) return 0;// user actually does't need data
    
	jlong timeOut = (jlong)getTimeOut(env,object);  
 	if(timeOut < 0) timeOut = 0;
 	
    char *myBuffer = NewPtrClear(actNeedData);
    if(myBuffer == NULL) return -1;
	long	numBytes = 0;
	OSErr errInp = noErr;
	jlong startTime = getCurrentSysTimeMillis(env);
	long readData = 0;
	bool doExit = false;
	bool checkTimeOut = false;
	char *currBuffer = myBuffer;
    do{
		errInp = SerGetBuf(inputRefNumber,&numBytes);
		if(errInp != noErr) break;
		if(numBytes == 0){
			if(!checkTimeOut){
				checkTimeOut = true;
				startTime = getCurrentSysTimeMillis(env);
			}
			doExit = (getCurrentSysTimeMillis(env) - startTime > timeOut);
		}else{
			if(checkTimeOut) checkTimeOut = false;
			jint needBytes = actNeedData - readData;
			if(numBytes < needBytes) needBytes = numBytes;
			if(needBytes > 0){
				IOParam pInBlock;
				pInBlock.ioRefNum 		= inputRefNumber;  				//read from the input driver
				pInBlock.ioBuffer 		= currBuffer;  					//pointer to my data buffer
				pInBlock.ioReqCount 	= needBytes; 					//number of bytes to read
				pInBlock.ioCompletion 	= NULL;       					//no completion routine specified
				pInBlock.ioVRefNum 		= 0;            				//not used by the Serial Driver
				pInBlock.ioPosMode 		= 0;            				//not used by the Serial Driver
				errInp = PBRead((ParmBlkPtr)&pInBlock,false);
				if(errInp != noErr) break;
				currBuffer 	+= pInBlock.ioActCount;
				readData 	+= pInBlock.ioActCount;
			}
		}
    }while((readData < actNeedData) && !doExit);
	env->SetByteArrayRegion(buffer,actStart,readData,myBuffer);	
    DisposePtr(myBuffer);
	return readData;
}

/*
 * Class:     waba_io_impl_SerialPortImpl
 * Method:    readCheck
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_waba_io_impl_SerialPortImpl_readCheck
	(JNIEnv *env, jobject obj)
{
long	numBytes = 0;
	if((env == NULL) || (obj == NULL)) return -1;
	short	inputRefNumber = getInpPortRef(env,obj);
	if(inputRefNumber == 0) return -1;
	OSErr errInp = SerGetBuf(inputRefNumber,&numBytes);
	if(errInp != noErr) return -1;
	return (jint)numBytes;
}

/*
 * Class:     waba_io_impl_SerialPortImpl
 * Method:    setFlowControl
 * Signature: (Z)Z
 */
JNIEXPORT jboolean JNICALL Java_waba_io_impl_SerialPortImpl_setFlowControl
	(JNIEnv *env, jobject obj, jboolean cond)
{
	short	outputRefNumber = getOutPortRef(env,obj);
	if(outputRefNumber == 0) return JNI_FALSE;
	SerShk hs;
	hs.fXOn 	= 0;
 	hs.fCTS 	= (cond == JNI_FALSE) ? 0 : 1;
 	hs.errs 	= 0;
 	hs.evts 	= 0;
  	hs.fInX 	= 0; // in MBL was 1
	hs.fDTR 	= 0;
	OSErr errOut = Control(outputRefNumber, 14, &hs); //csCode = 14
	if(errOut == noErr) return JNI_TRUE;
	return JNI_FALSE;
}

/*
 * Class:     waba_io_impl_SerialPortImpl
 * Method:    writeBytes
 * Signature: ([BII)I
 */
JNIEXPORT jint JNICALL Java_waba_io_impl_SerialPortImpl_writeBytes
	(JNIEnv *env, jobject object, jbyteArray buffer, jint start, jint count)
{
	short	outputRefNumber = getOutPortRef(env,object);
	if(outputRefNumber == NULL) return -1;
	if(buffer == NULL) return -1;
    jsize bufSize = env->GetArrayLength(buffer);
    if(bufSize <= 0) return 0;						//buffer is too small
    jint  actNeedData = count;
    jint  actStart = start;
    if(actStart < 0) actStart = 0;
    jint  actCount = count;
    if(actCount < 0) actCount = 0;
    if(actCount == 0) return 0;
    if(actStart + actCount > bufSize){//wants more data than I can deliver
    	actNeedData = bufSize - start;
    }
    if(actNeedData <= 0) return 0;// user actually does't need data
    
	char *myBuffer = NewPtr(actNeedData);
	if(myBuffer == NULL) return -1;
   	env->GetByteArrayRegion(buffer, actStart,actNeedData, myBuffer);
	IOParam pOutBlock;
	pOutBlock.ioRefNum 		= outputRefNumber;  //write to the output driver
	pOutBlock.ioBuffer 		= myBuffer;  		//pointer to my data buffer
	pOutBlock.ioReqCount 	= actNeedData; 		//number of bytes to read
	pOutBlock.ioCompletion 	= NULL;       		//no completion routine specified
	pOutBlock.ioVRefNum 	= 0;            	//not used by the Serial Driver
	pOutBlock.ioPosMode 	= 0;            	//not used by the Serial Driver
	OSErr errOut = PBWrite((ParmBlkPtr)&pOutBlock, false);  //synchronous Device Manager request
	if(errOut != noErr) actNeedData = -1;
	DisposePtr(myBuffer);
	return actNeedData;
}

/*
 * Class:     waba_io_impl_SerialPortImpl
 * Method:    clearBuffer
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_waba_io_impl_SerialPortImpl_clearBuffer
	(JNIEnv *, jobject, jint)
{
	SysBeep(0);
}

short	getMacStopBits(jint stopbits)
{
short retValue = stop10;
	switch(stopbits){
		case 1: 	retValue = stop10; 	break;
		case 2: 	retValue = stop20; 	break;
		default: 	retValue = stop10;	break;
	}
	return retValue;
}

short	getMacBits(jint bits)
{
short retValue = data8;
	switch(bits){
		case 5: 	retValue = data5; 	break;
		case 6: 	retValue = data6; 	break;
		case 7: 	retValue = data7; 	break;
		case 8: 	retValue = data8; 	break;
		default: 	retValue = data8;	break;
	}
	return retValue;
}
short	getMacBaudRate(jint rate)
{
short retValue = baud9600;
	switch(rate){
		case 150: 	retValue = baud150; 	break;
		case 300: 	retValue = baud300; 	break;
		case 600: 	retValue = baud600; 	break;
		case 1200: 	retValue = baud1200; 	break;
		case 1800: 	retValue = baud1800; 	break;
		case 2400: 	retValue = baud2400; 	break;
		case 3600: 	retValue = baud3600; 	break;
		case 4800: 	retValue = baud4800; 	break;
		case 7200: 	retValue = baud7200; 	break;
		case 9600: 	retValue = baud9600; 	break;
		case 14400:	retValue = baud14400; 	break;
		case 19200: retValue = baud19200; 	break;
		case 28800: retValue = baud28800; 	break;
		case 38400: retValue = baud38400; 	break;
		case 57600: retValue = baud57600; 	break;
		default: 	retValue = baud9600;	break;
	}
	return retValue;
 
/*
    baud150                     = 763, //384
    baud300                     = 380, //192
    baud600                     = 189, //96
    baud1200                    = 94,  //48
    baud1800                    = 62,  //32
    baud2400                    = 46,	//24
    baud3600                    = 30,  //16
    baud4800                    = 22,  //12
    baud7200                    = 14,  //8
    baud9600                    = 10,	//6
    baud14400                   = 6,   //4
    baud19200                   = 4,   //3
    baud28800                   = 2,   //2
    baud38400                   = 1,	//1.5
    baud57600                   = 0     0
*/
}
jboolean	closePort(short inpref,short outref)
{
jboolean retValue = JNI_TRUE;
	OSErr	errInp;
	OSErr	errOut;
	if(inpref == 0){
		retValue = JNI_FALSE;
	}else{
		errInp = KillIO(inpref);
		if((retValue == JNI_TRUE) && (errInp != noErr)) retValue = JNI_FALSE;
		errInp = CloseDriver(inpref);
		if((retValue == JNI_TRUE) && (errInp != noErr)) retValue = JNI_FALSE;
	}
	if(outref == 0){
		retValue = JNI_FALSE;
	}else{
		errOut = KillIO(outref);
		if((retValue == JNI_TRUE) && (errOut != noErr)) retValue = JNI_FALSE;
		errOut = CloseDriver(outref);
		if((retValue == JNI_TRUE) && (errOut != noErr)) retValue = JNI_FALSE;
	}
	return retValue;
}

jint getTimeOut(JNIEnv *env, jobject object)
{
jint retValue = 0;
	jclass   objClass = NULL;
	jfieldID timeOutID = NULL;
	if(env && object){
		objClass = env->GetObjectClass(object);
	}
	if(objClass){
		timeOutID = env->GetFieldID(objClass,"timeOut",JRISigInt);
		if(timeOutID) retValue = env->GetShortField(object,timeOutID);
	}
	return retValue;
}
short getInpPortRef(JNIEnv *env, jobject object)
{
short retValue = 0;
	jclass   objClass = NULL;
	jfieldID inpRefID = NULL;
	if(env && object){
		objClass = env->GetObjectClass(object);
	}
	if(objClass){
		inpRefID = env->GetFieldID(objClass,"inpRef",JRISigShort);
		if(inpRefID) retValue = env->GetShortField(object,inpRefID);
	}
	return retValue;
}
short getOutPortRef(JNIEnv *env, jobject object)
{
short retValue = 0;
	jclass   objClass = NULL;
	jfieldID outRefID = NULL;
	if(env && object){
		objClass = env->GetObjectClass(object);
	}
	if(objClass){
		outRefID = env->GetFieldID(objClass,"outRef",JRISigShort);
		if(outRefID) retValue = env->GetShortField(object,outRefID);
	}
	return retValue;
}

void setInpPortRef(JNIEnv *env, jobject object,short val)
{
	if(env && object){
		jclass objClass = env->GetObjectClass(object);
		if(objClass){
			jfieldID outRefID = env->GetFieldID(objClass,"inpRef",JRISigShort);
			if(outRefID) env->SetShortField(object,outRefID,val);
		}
	}
}
void setOutPortRef(JNIEnv *env, jobject object,short val)
{
	if(env && object){
		jclass objClass = env->GetObjectClass(object);
		if(objClass){
			jfieldID outRefID = env->GetFieldID(objClass,"outRef",JRISigShort);
			if(outRefID) env->SetShortField(object,outRefID,val);
		}
	}
}

jlong getCurrentSysTimeMillis(JNIEnv *env)
{
	jlong retValue = -1;
	if(env == NULL) return retValue;
	jclass   	sysClass = env->FindClass("java/lang/System");
	if(sysClass == NULL) return retValue;
	jmethodID currTimeID = env->GetStaticMethodID(sysClass,"currentTimeMillis",JRISigMethod(JRISigNoArgs) JRISigLong);
	if(currTimeID == NULL) return retValue;
	retValue = env->CallStaticLongMethodA(sysClass,currTimeID,NULL);
	return retValue;
}
