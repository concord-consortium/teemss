/************************************************************************/
/************************************************************************/
/* CCBeam_Launcher														*/
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
/*	The purpose of this application is use CCBeam as a beaming program	*/
/*	because this application does not have internal beaming	function	*/
/*	support. Although, not the case here, this need applies readily to	*/
/*	Palm Applications created using the WABA software development kit.	*/
/*																		*/
/*  This application launches CCBeam and passes it a parameter block	*/
/*	that contains the Database Card Number,	the Database LocalID, the	*/
/*	Database Filename, and a Description of a database that it desires  */
/*	to have beamed to another palm so that the database can be used by  */
/*  CCProbe.  CCProbe is just an example. The application launched by   */
/*	CCBeam is specified in the parameter block.							*/
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


#include <PalmOS.h>	// MANDATORY - include all the system toolbox headers


/************************************************************************/
/* MANDATORY - MUST BE USED												*/
/************************************************************************/
/* This structure must be used to send a parameter block to CCBEAM		*/
/************************************************************************/
typedef struct {
	UInt16 cardNo; // card number of the database 
	LocalID dbID; // LocalID of the database
	char name[64];
	char description[256];
	UInt32 goToCreator;
	ExgGoToType goToParams;
} BeamParamsType;

typedef BeamParamsType *BeamParamsPtr;
/************************************************************************/




/***********************************************************************
 *
 * FUNCTION:		PilotMain
 *
 * DESCRIPTION:		Execution entry point of this application.
 *
 * PARAMETERS:				cmd	- command specifying how to launch the application.
 *						 cmdPBP	- parameter block for the command.
 *					launchFlags	- flags used to configure the launch.			
 *
 * RETURNED:		Any appliccable error code.
 *
 ***********************************************************************/
UInt32 PilotMain(UInt16 cmd, MemPtr cmdPBP, UInt16 launchFlags)
{
	UInt32 err = 0;			// RECOMMENED USE	-	Used for error checking
	UInt32 resultP;			// MANDATORY USE	-	Parameter for SysAppLaunch
	LocalID ccbeam_dbID;	// MANDATORY USE	-	Used to identifity CCBEAM database


/************************************************************************/
/* MANDATORY - MUST BE USED												*/
/* Declare and define the parameter block for CCBEAM					*/
/************************************************************************/

	BeamParamsPtr cmdPBPlauncher;						// DECLARE the parameter block

	
	cmdPBPlauncher = MemPtrNew(sizeof(BeamParamsType)); // ALLOCATE MEMORY for parameter block
	if (!cmdPBPlauncher) return(1);						// Error checking


	/*------------------------------------------------------------------*/
	/* FILL THE PARAMETER BLOCK											*/

	cmdPBPlauncher -> cardNo = 0;						// Card number of database to beam - DEFAULT IS 0
	

	// DATABASE LOCAL ID
	// parameterblock -> local database ID = DmFindDatabase(cardnumber, database name)
	cmdPBPlauncher -> dbID = DmFindDatabase(0, "LabBook");	// Local ID of datatabase to beam
			// NOTE: The name of the database can be the name of any database regardless of it's type.
			//		 CCBEAM can beam any database type.

	StrCopy(cmdPBPlauncher -> name, "");

	// DATABASE NAME
	// StrCopy(cmdPBPlauncher -> name, "LabBook.pdb");	// Displayed description of database to beam

	// DATABASE DESCRIPTION
	// StrCopy(parameter block -> description, "Text to be displayed in the 'Beaming' dialog box")
	StrCopy(cmdPBPlauncher -> description, "Ecological Footprint Calculator");	// Displayed description of database to beam
			// NOTE: The recommended description is the long name of the database or the contents of it.

	// APPLICATION TO LAUNCH AFTER RECEIVING BEAM
	// might need to reverse this order
	// cmdPBPlauncher -> goToCreator = 'CCPr';			// CURRENTLY NOT USED
			// NOTE: Future enhancement to CCBEAM will allow this parameter to be set.
			//		 CCBEAM will lauch the application defined by this parameter on the receiving palm.
			//		 Currently CCBEAM is hard coded to launch CCPROBE.


	/* END OF 'FILL THE PARAMETER BLOCK									*/
	/*------------------------------------------------------------------*/


/************************************************************************/



/************************************************************************/
/* LAUNCH APPLICATION WITH PARAMETER BLOCK								*/
/*		This example launches CCBEAM with the parameter block			*/
/************************************************************************/

	if (cmd == sysAppLaunchCmdNormalLaunch) // Check for a normal launch
	{
	
		/************************************************************************/
		/* MANDATORY - MUST BE USED	- The following code block should be put	*/
		/*							  inside the normal launch command handler.	*/
		/************************************************************************/
		ccbeam_dbID = DmFindDatabase(0, "CCBEAM"); // Locate the CCBEAM application database

		if (ccbeam_dbID != 0)	// Error Check: Make sure the application is installed.
		{
			// Launch CCBEAM as a SUBROUTINE

			// SysAppLaunch(UInt16 cardNo, LocalID dbID, UInt16 launchFlags, UInt16 cmd, MemPtr cmdPBP, UInt32 *resultP)
			err = SysAppLaunch(0, ccbeam_dbID, 0, sysAppLaunchCmdNormalLaunch, cmdPBPlauncher, &resultP);
		}
		/* END MANDATORY CODE BLOCK												*/
		/************************************************************************/

	}

	return(err);
}

