CCProbe for Windows
Mar 24, 2002

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

For more information go to: http://concord.org/ccprobeware/

----------------------------------------------------------------------------

Installing and running CCProbe on Windows

CCProbe requires Sun's Java 1.1 or higher.  You can get the latest
version of the Java 2 Runtime Environment, Standard Edition for
Windows here:

  http://java.sun.com/j2se/1.3/jre/download-windows.html
	
Even though it is called Java 2, Sun's internal numbering calls this
Java version 1.3.1.

CCProbe no longer needs the Java Communications API.  Instead we are
using the GPL licensed RXTX for serial communication support.  The
java code for RXTX is included in the CCProbe.jar file.  Just make
sure and keep the file Serial.dll in the same directory as the
CCProbe.jar file.

To start: double-click on CCProbe.bat

----------------------------------------------------------------------------

Specifying the location of the Serial Communications Port

The first time CCProbe attempts to connect to the interface through
the serial port it will ask which serial port to use to connect to the
interface.  You can also use the file menu "Serial Port Settings" to
change the serial port used.

----------------------------------------------------------------------------

Changing the CCProbe application window size

Load the file CCProbe.bat into an program which can edit a text file
such as WordPad

Edit the first line in  the file:

  java -cp CCProbe.jar waba.applet.Applet /w 600 /h 400 /color CCProbe

The numbers after the /w and /h set the width and the height of the
application window.  Edit these numbers and save the file.  The next
time the CCProbe application is run it will use the new specification
as the window size.





