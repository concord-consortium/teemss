CCProbe for PocketPC
March 24, 2002

CCProbe supports sensor and model-based visualization and analysis
along with a Lab Notebook for saving and communicating probe data and
views. Written in Waba and licensed under the GPL it runs on PalmOS,
WinCE, PocketPC, Windows, MacOS, and Linux.

CCProbe Copyright (c) 2002 by Concord Consortium, All Rights Reserved

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License in 
    the file named COPYING along with this program; if not, write to the 
    Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, 
    MA  02111-1307  USA

For more information on CCProbe go to: http://concord.org/ccprobeware/ccprobe

------------------------------------------------------------------------

Installing and running CCProbe on PocketPC

CCProbe requires that you have the Waba virtual machine installed.
The Waba vm consists of two files: waba.exe and waba.wrp.  The
waba.exe file is specific to the processor used in the PocketPC.

On either system create a new folder called waba here:

  My Device:Program Files:waba

Place the appropriate waba.exe and waba.wrp file in the waba folder.

  iPaq       waba-strongarm/waba.exe
  Casiopeia  waba-mips/waba.exe
	
The waba.wrp is identical on both systems.

Now copy the CCProbe link and waba executable files to the same directory:

  CCProbe.lnk
  CCProbe.wrp

Now finally copy the LabBook file named "LabBook" to the My Devices
directory on your PocketPC system.  

There will now be two files named CCProbe in the waba folder on your
PocketPC.  Start CCProbe by tapping on the CCProbe link file.  This
file is under 1k in size, it is much smaller than the waba executable.

Copy CCProbe.lnk to the Windows/Start Menu directory if you would like
it to appear in the Start menu.




