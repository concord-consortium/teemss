------------------------------------------------------------------------------------
CCBeam and CCBeamLauncher
------------------------------------------------------------------------------------
Programmed by Matthew Cruz
		  matthewcruz@mediaone.net

		  01/30/02

Developed for Concord Consortium
------------------------------------------------------------------------------------

Install the following applications on your Palm Pilot

1) ccbeam.prc
		- CCBeam application
			CCBeam.prc is compiled and ready for use with CCProbe.
		- Related files:
			  i) AppMain.c	Source Code	  - C code
			 ii) Res_App.rcp	Resource File - PiLRC
			iii) Makefile	For compiling

2) ccbeamlauncher.prc	
		- CCBeam launching application
			CCBeamLauncher.prc simulates what CCProbe must do to launch CCBeam.
		- Related files:
			  i) AppMain.c	Source Code	  - C code
			 ii) Res_App.rcp	Resource File - PiLRC
			iii) Makefile	For compiling

3) efc.prc
		- Sample database that CCBeamLauncher has CCBeam send to a receiving hand held.

------------------------------------------------------------------------------------
NOTE: CCBeam can send and receive any type of database. See the header descriptions at the top of both CCBeam(AppMain.c) and CCBeamLauncher(AppMain.c) for detailed documentation and use.
------------------------------------------------------------------------------------