CCProbe for Unix/Linux
March 24, 2002

CCProbe supports sensor and model-based visualization and analysis
along with a Lab Notebook for saving and communicating probe data and
views. Written in Waba and licensed under the GPL it runs on PalmOS,
WinCE, PocketPC, Windows, MacOS, and Linux.

CCProbe Copyright (c) 2001 by Concord Consortium, All Rights Reserved

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA

For more information go to: http://concord.org/ccprobeware/ccprobe

----------------------------------------------------------------------------

Installing and running CCProbe on Unix

CCProbe requires Sun's Java 1.1 or higher.  You can get the latest version
of the Java 2 Runtime Environment, Standard Edition for Unix here:

  Solaris SPARC/x86	

    http://java.sun.com/j2se/1.3/jre/download-solaris.html
    
  Linux x86

    http://java.sun.com/j2se/1.3/jre/download-linux.html

Even though it is called Java 2, Sun's internal numbering calls this Java 
version 1.3.1.

CCProbe no longer needs the Java Communications API.  Instead we are
using the GPL licensed RXTX for serial communication support.

1. Download and build RXTX version 1.5-8 or later.

	http://www.rxtx.org
	
	Add the RXTX directory to your CLASSPATH - as instructed in the RXTX 
	documentation.  Create a text file in your JDK lib directory.  This file 
	must be named "javax.comm.properties".  The contents of this file is a 
	single line which should read:

		Driver=gnu.io.RXTXCommDriver

	(the rxtx installer might do this for you automatically)
	
If you are running on a RedHat system make sure and checkout the
Readme file which explains how premissions need to be changed to
support UUCP locking.

2. Make the shell file CCProbe executable

  The shell file CCProbe consists of the following:
	
    #! /bin/sh
    java -cp CCProbe.jar waba.applet.Applet /w 300 /h 300 /color CCProbe

    The numbers after the /w and /h set the width and the height of 
    the application window.  

    Make the file CCProbe executable with the following cammand:
	
	chmod +x CCProbe

to start: execute ./CCProbe

----------------------------------------------------------------------------

Specifying the location of the Serial Communications Port

The first time CCProbe attempts to connect to the interface through
the serial port it will ask which serial port to use to connect to the
interface.  You can also use the file menu "Serial Port Settings" to
change the serial port used.











