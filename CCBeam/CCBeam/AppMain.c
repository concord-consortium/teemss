/************************************************************************/
/************************************************************************/
/* CCBeam																*/
/*																		*/
/*	01/31/02															*/
/*																		*/
/*	Programmed by Matthew Cruz											*/
/*				  matthewcruz@mediaone.net								*/
/*																		*/
/*	Developed for Concord Consortium									*/
/*																		*/
/************************************************************************/
/*																		*/
/*	The purpose of this application is to perform as a beaming program	*/
/*	for any other palm applications that do not have internal beaming	*/
/*	support. This specifically applies to Palm Applications created		*/
/*  using the WABA software development kit.							*/
/*																		*/
/*  To use this program, the main application must launch CCBeam and	*/
/*  pass it a parameter block that contains the Database Card Number,	*/
/*  the Database LocalID, the Database Filename, and a Description.		*/
/*  An example of this is included with this package. The example is	*/
/*  Called "ccbeam_launcher". It demonstrates the appropriate way to	*/
/*  create a parameter block and to launch CCBeam.						*/
/*																		*/
/*																		*/
/*	CCBeam registers with the PalmOS to accept the following filetypes:	*/
/*																		*/
/*	"ccb"																*/
/*																		*/
/*	Despite this fact, CCBeam is capable of sending and receiving any   */
/*  database types specified in the paramter block because it simply	*/
/*	uses "dummy.ccb" as the the outgoing database filename so			*/
/*	that the receiving Palm's CCBeam application can accept it.			*/
/*	This does not affect the database being installed on the receiving	*/
/*	Palm so this makes CCBeam an effective database beamer.				*/
/*																		*/
/************************************************************************/

/***********************************************************************/
/***********************************************************************/
#include <PalmOS.h>				// all the system toolbox headers
#include "ExgMgr.h"				// object exchange functions

#define CCBCreator			'cCCB'			// The creator code
#define	CCBeamDBFilename	"dummy.ccb"		// Dummy filename used for transport of any database
											// Hard coded so that CCBeam can send and receive any type of database

typedef struct {
	UInt16 cardNo; // card number of the database 
	char inName[40];
	char outName[32];
	char appName[32];
	char description[256];
} BeamParamsType;

typedef BeamParamsType *BeamParamsPtr;


/***********************************************************************
 * Prototypes for internal functions
 **********************************************************************/
static Err SendDatabase (BeamParamsPtr cmdPBP);
static Err WriteDBData(const void* dataP, UInt32* sizeP, void* userDataP);


static Err ReceiveDatabase (ExgSocketPtr exgSocketP);
static Err ReadDBData(void* dataP, UInt32* sizeP, void* userDataP);
static Boolean DeleteExistingDB(const char* nameP, UInt16 version, UInt16 cardNo, LocalID dbID, void* userDataP);


void RegisterExtensions(void);


/***********************************************************************
 *
 * FUNCTION:     RegisterExtensions
 *
 * DESCRIPTION:  This routine registers the database types that can be received.
 *
 * PARAMETERS:   None.
 *
 * RETURNED:     Nothing.
 *
 ***********************************************************************/
void RegisterExtensions(void)
{
	ExgRegisterData(CCBCreator, exgRegExtensionID, "ccb");
}


/***********************************************************************
 *
 * FUNCTION:	WriteDBData
 *
 * DESCRIPTION: Callback for ExgDBWrite to send data with exchange manager
 *
 * PARAMETERS:  dataP : buffer containing data to send
 *				sizeP : number of bytes to send
 *				userDataP: app defined buffer for context
 (					(holds exgSocket when using ExgManager)
 *
 * RETURNED:    error if non-zero
 * 
 ***********************************************************************/
static Err WriteDBData(const void* dataP, UInt32* sizeP, void* userDataP)
{
	Err			err;

	// Try to send as many bytes as were requested by the caller
	*sizeP = ExgSend((ExgSocketPtr)userDataP, (void*)dataP, *sizeP, &err);

	return err;
}


/***********************************************************************
 *
 * FUNCTION:	ReadDBData
 *
 * DESCRIPTION: Callback for ExgDBRead to receive data with exchange manager
 *
 * PARAMETERS:  dataP : buffer containing data to store received data in
 *				sizeP : number of bytes to receive
 *				userDataP: app defined buffer for context
 (					(holds exgSocket when using ExgManager)
 *
 * RETURNED:    error if non-zero
 * 
 ***********************************************************************/
static Err ReadDBData(void* dataP, UInt32* sizeP, void* userDataP)
{
	Err			err;

	// Try to send as many bytes as were requested by the caller
	*sizeP = ExgReceive((ExgSocketPtr)userDataP, (void*)dataP, *sizeP, &err);

	return err;
}

static Boolean DeleteExistingDB(const char* nameP, UInt16 version, UInt16 cardNo, LocalID dbID, void* userDataP)
{

	Err err;

	err = DmDeleteDatabase(cardNo, dbID);

	if (!err)
		return(true);
	else
		return(false);
	
}

/***********************************************************************
 *
 * FUNCTION:		SendDatabase
 *
 * DESCRIPTION:	Sends data in the input field using the Exg API
 *
 * RETURNED:		error code or zero for no error.
 *
 ***************************************************************************/
static Err SendDatabase (BeamParamsPtr cmdPBP)
{
	Err					err;
	ExgSocketType		exgSocket;
	int bytesSent = 0;
	LocalID inputDbID = -1;

	inputDbID = DmFindDatabase(0, cmdPBP->inName);
	if(inputDbID == 0){
		return(err);
	}

	// Create exgSocket structure
	MemSet(&exgSocket, sizeof(ExgSocketType), 0);

	exgSocket.description = cmdPBP -> description;

	// If the there isn't an appName and outName 
	// then send the database directly.
	if(StrLen(cmdPBP->appName) == 0 &&
	   StrLen(cmdPBP->outName) == 0){
		exgSocket.name = "dummy.pdb";

		// Start and exchange put operation
		err = ExgPut(&exgSocket);
		if (!err){
			// This function converts a palm database into its external (public)
			// format. The first parameter is a callback that will be passed parts of 
			// the database to send or write.
			err = ExgDBWrite(WriteDBData, &exgSocket, NULL, inputDbID, cmdPBP -> cardNo);
			// Disconnect Exg and pass error
			err = ExgDisconnect(&exgSocket, err);
		} else {
			FrmAlert (2000);
		}
		return err;
	} 

	// Otherwise use our wrapper file name 
	// This ensures that CCBeam will be launched again
	// by the recieving palm
	exgSocket.name = CCBeamDBFilename;

	// Start and exchange put operation
	err = ExgPut(&exgSocket);
	if (!err){
		// Send the out name we fix it at 32 bytes
		bytesSent = ExgSend(&exgSocket, cmdPBP->outName, 32, &err);
		if(bytesSent < 32){
			return err;
		}

		// Send the application name we fix it at 32 bytes
		bytesSent = ExgSend(&exgSocket, cmdPBP->appName, 32, &err);
		if(bytesSent < 32){
			return err;
		}

		// This function converts a palm database into its external (public)
		// format. The first parameter is a callback that will be passed parts of 
		// the database to send or write.
		err = ExgDBWrite(WriteDBData, &exgSocket, NULL, inputDbID, cmdPBP -> cardNo);
		// Disconnect Exg and pass error
		err = ExgDisconnect(&exgSocket, err);
	} else {
		FrmAlert (2000);
	}
	
	return err;

}

/***********************************************************************
 *
 * FUNCTION:		ReceiveDatabase
 *
 * DESCRIPTION:	Receives data in the input field using the Exg API
 *
 * RETURNED:		error code or zero for no error.
 *
 ***************************************************************************/
static Err ReceiveDatabase (ExgSocketPtr exgSocketP)
{
	Err		err;
	LocalID dbID;
	UInt16	cardNo = 0;
	Boolean *needResetP = NULL;
	Boolean keepDates = false;
	char outName [32];
	char appName [32];
	int bytesSent = -1;

	LocalID app_dbID;

	// Create exgSocket structure
	MemSet(exgSocketP, sizeof(exgSocketP->length), 0);

	// Start and exchange put operation
	err = ExgAccept(exgSocketP);
	if (!err){
		// Receive the DB name we fix it at 32 bytes
		bytesSent = ExgReceive(exgSocketP, outName, 32, &err);
		if(bytesSent != 32){
			return err;
		}

		// Receive the DB name we fix it at 32 bytes
		bytesSent = ExgReceive(exgSocketP, appName, 32, &err);
		if(bytesSent != 32){
			return err;
		}

		// Receive the database
		err = ExgDBRead(ReadDBData, DeleteExistingDB, exgSocketP, &dbID, cardNo, needResetP, keepDates);

		/*
		exgSocketP->goToCreator = 'CCPr';
		exgSocketP->noGoTo = 0;
		*/

		// Disconnect Exg and pass error
		err = ExgDisconnect(exgSocketP, err);

		// Try to rename the database to the outName
		if(StrLen(outName) > 0){
			/*
			  UInt cardNo, 
			  LocalID dbID, CharPtr nameP, 
			  UIntPtr attributesP, UIntPtr versionP, 
			  ULongPtr crDateP, ULongPtr modDateP, 
			  ULongPtr bckUpDateP, ULongPtr modNumP, 
			  LocalID* appInfoIDP, LocalID* sortInfoIDP, 
			  ULongPtr typeP, ULongPtr creatorP); 
			  
			*/
			DmSetDatabaseInfo(cardNo, dbID, outName, 
							  NULL, NULL, NULL, NULL, 
							  NULL, NULL, NULL, NULL, NULL, NULL);
		}
	
		if (!err && StrLen(appName) > 0)
		{
			// Find the database
			app_dbID = DmFindDatabase(0, appName);
			if(app_dbID != 0)
			{
				SysUIAppSwitch(0, app_dbID, sysAppLaunchCmdNormalLaunch, NULL);
			}

		}
	}
	/****************************************************************************/

	return err;
}


void parseArgs(char *dstStr, char *srcStr, int startChar, int len, 
			   int maxLen)
{
	if(len <= 0){
		dstStr[0] = 0;
		return;
	} else if(len > maxLen - 1){
		len = maxLen - 1;
	}

	StrNCopy(dstStr, &srcStr[startChar], len);
	dstStr[len] = 0;
}

/***********************************************************************
 *
 * FUNCTION:		PilotMain
 *
 * DESCRIPTION:	This function is the equivalent of a main() function
 *						in standard ÒCÓ.  It is called by the Emulator to begin
 *						execution of this application.
 *
 * PARAMETERS:		cmd - command specifying how to launch the application.
 *						cmdPBP - parameter block for the command.
 *						launchFlags - flags used to configure the launch.			
 *
 * RETURNED:		Any applicable error code.
 *
 ***********************************************************************/
UInt32 PilotMain(UInt16 cmd, MemPtr cmdPBP, UInt16 launchFlags)
{
	UInt32 err = 0;
	

	// Check for a normal launch.
	if (cmd == sysAppLaunchCmdNormalLaunch){
		// process the passed in parameters
		char * args = cmdPBP;

		int startChar = 0;
		int argc = 0;
		int i=0;

		BeamParamsPtr pBP;
		MemHandle memHandle;
		
		// If we were not launched as a subroutine, i.e. we were launched from the application launcher
		// then we must return (exit - do not run)
		if (cmdPBP == NULL){
			FrmAlert (2000);
			return(err);
		}

		memHandle = MemHandleNew(sizeof(BeamParamsType));		
	
		// check for out of memory problem
		if(memHandle == 0) return (err);

		pBP = (BeamParamsPtr)MemHandleLock(memHandle);
		
		// Create exgSocket structure
		MemSet(pBP, sizeof(BeamParamsType), 0);

		pBP->cardNo = 0;

		while(true){
			if(args[i] == ',' || args[i] == 0){
				int len = i-startChar;

				switch(argc){
				case 0: 
					parseArgs(pBP->inName, args, startChar, len, 32);
					break;					
				case 1:
					parseArgs(pBP->outName, args, startChar, len, 32);
					break;
				case 2:
					parseArgs(pBP->appName, args, startChar, len, 32);
					break;					
				case 3:
					parseArgs(pBP->description, args, startChar, len, 256);
					break;
				}
				startChar = i+1;
				argc++;
			}
			if(args[i] == 0){
				break;
			}
			i++;
		} 

		SendDatabase(pBP);
	}

	else if (cmd == sysAppLaunchCmdSyncNotify)
		{
		// register our extension on syncNotify so we do not need to
		// be run before we can receive data.
		RegisterExtensions();
		}

	else if (cmd == sysAppLaunchCmdExgReceiveData)
		{
		err = ReceiveDatabase((ExgSocketPtr)cmdPBP);
		}
	
	return(err);
}

