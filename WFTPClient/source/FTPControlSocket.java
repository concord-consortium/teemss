/**
 *
 *  Java FTP client library.
 *
 *  Copyright (C) 2000  Enterprise Distributed Technologies Ltd
 *
 *  www.enterprisedt.com
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Bug fixes, suggestions and comments should be sent to:
 *
 *  bruceb@cryptsoft.com
 *
 *  or by snail mail to:
 *
 *  Bruce P. Blackshaw
 *  53 Wakehurst Road
 *  London SW11 6DB
 *  United Kingdom
 *
 *  Change Log:
 *
 *        $Log$
 *        Revision 1.3  2001/10/09 20:53:46  bruceb
 *        Active mode changes
 *
 *        Revision 1.1  2001/10/05 14:42:04  bruceb
 *        moved from old project
 *
 *
 */

package org.concord.waba.WFTPClient;

import waba.io.*;
import waba.util.*;
import waba.sys.*;
import org.concord.waba.extra.io.*;

/**
 *  Supports client-side FTP operations
 *
 *  @author             Bruce Blackshaw
 *      @version        $Revision$
 *
 */
 public class FTPControlSocket {

     /**
      *  Revision control id
      */
     private static String cvsId = "$Id$";

     /**
      *   Standard FTP end of line sequence
      */
     static final String EOL = "\r\n";
	 static final int EOLLength = 2;

     /**
      *   The control port number for FTP
      */
     private static final int CONTROL_PORT = 21;

     /**
      *   Controls if responses sent back by the
      *   server are sent to stdout or not
      */
     private boolean debugResponses = false;

     /**
      *  The underlying socket.
      */
     private Socket controlSock = null;

	 private BufferStream bufferedStream = null;

	 private int readTimeOut = 1500;

     /**
      *  The current error code
      */
     private int errorCode = 0;
	 private String errorStr = null;

	 public final static int ERROR_OK = 0;
	 public final static int ERROR_OPENING = 1;
	 public final static int ERROR_ILLEGAL_STATE = 2;
	 public final static int ERROR_INVALID_227 = 3;
	 public final static int ERROR_READ_LINE = 4;
	 public final static int ERROR_INVALID_REPLY = 5;

     /**
      *  The data stream for the control socket
      */
	 private DataStream ds = null;

     /**
      *   Constructor. Performs TCP connection and
      *   sets up reader/writer
      *
      *   @param   remoteHost   Remote hostname
      */
     public FTPControlSocket(String remoteHost)
	 {
         this(remoteHost, CONTROL_PORT);
     }


     /**
      *   Constructor. Performs TCP connection and
      *   sets up reader/writer. Allows different control
      *   port to be used
      *
      *   @param   remoteHost   Remote hostname
      *   @param   controlPort  port for control stream
      */
     public FTPControlSocket(String remoteHost, int controlPort)
	 {
         controlSock = new Socket(remoteHost, controlPort);
		 if(!controlSock.isOpen()){
			 errorCode = ERROR_OPENING;
			 return;
		 }
		 bufferedStream = new BufferStream(controlSock);

		 // This is dangerous but we are only going to use ds for
		 // writing
		 ds = new DataStream(controlSock);
         validateConnection();
     }

	 public int getError()
	 {
		 return errorCode;
	 }

     /**
      *   Checks that the standard 220 reply is returned
      *   following the initiated connection
      */
     private boolean validateConnection()
	 {
         String reply = readReply();
         return validateReply(reply, "220");
     }

    /**
     *   Set the TCP timeout on the underlying control socket.
     *
     *   If a timeout is set, then any operation which
     *   takes longer than the timeout value will be
     *   killed with a java.io.InterruptedException.
     *
     *   @param millis The length of the timeout, in milliseconds
     */
	 void setTimeout(int millis)
	 {
		 if(errorCode != ERROR_OK) return;

		 if (controlSock == null) {
			 errorCode = ERROR_ILLEGAL_STATE;
			 return;
		 }

		 readTimeOut = millis;
		 controlSock.setReadTimeout(millis);
	 }


     /**
      *  Quit this FTP session and clean up.
      */
     public void logout()
	 {
		 ds.close();
     }


     /**
      *  Request a data socket be created on the
      *  server, connect to it and return our
      *  connected socket.
      *
      *  @param  active   if true, create in active mode, else
      *                   in passive mode
      *  @return  connected data socket
      */
     Socket createDataSocket()
	 {
		 return createDataSocketPASV();
	 }

    /**
     *  Helper method to convert a byte into an unsigned short value
     *
     *  @param  value   value to convert
     *  @return  the byte value as an unsigned short
     */
    private short toUnsignedShort(byte value) {
        return ( value < 0 )
            ? (short) (value + 256)
            : (short) value;
     }

    /**
     *  Convert a short into a byte array
     *
     *  @param  value   value to convert
     *  @return  a byte array
     */
    protected byte[] toByteArray (short value) {

        byte[] bytes = new byte[2];
        bytes[0] = (byte) (value >> 8);     // bits 1- 8
        bytes[1] = (byte) (value & 0x00FF); // bits 9-16
        return bytes;
    }

     /**
      *  Request a data socket be created on the
      *  server, connect to it and return our
      *  connected socket.
      *
      *  @return  connected data socket
      */
     Socket createDataSocketPASV()
	 {
         // PASSIVE command - tells the server to listen for
         // a connection attempt rather than initiating it
         String reply = sendCommand("PASV");
         if(!validateReply(reply, "227")) return null;

         // The reply to PASV is in the form:
         // 227 Entering Passive Mode (h1,h2,h3,h4,p1,p2).
         // where h1..h4 are the IP address to connect and
         // p1,p2 the port number
         // Example:
         // 227 Entering Passive Mode (128,3,122,1,15,87).

         // extract the IP data string from between the brackets
         int bracket1 = indexOf(reply, '(');
         int bracket2 = indexOf(reply, ')');
		 if(bracket1 < 0 || bracket2 < 0){
			 errorCode = ERROR_INVALID_227;
			 errorStr = "Malformed PASV reply: " + reply;
			 return null;
		 }
         String ipData = reply.substring(bracket1+1,bracket2);
         int parts[] = new int[6];

         int len = ipData.length();
         int partCount = 0;
         StringBuffer buf = new StringBuffer();

         // loop thru and examine each char
         for (int i = 0; i < len && partCount <= 6; i++) {

             char ch = ipData.charAt(i);
             if (isDigit(ch))
                 buf.append(ch);
             else if (ch != ',') {
				 errorCode = ERROR_INVALID_227;
				 errorStr = "Malformed PASV reply: " + reply;
				 return null;
             }

             // get the part
             if (ch == ',' || i+1 == len) { // at end or at separator
				 // shouldn't be malformed int because we checked each digit

				 parts[partCount++] = Convert.toInt(buf.toString());
				 buf.setLength(0);
             }
         }

         // assemble the IP address
         // we try connecting, so we don't bother checking digits etc
         String ipAddress = parts[0] + "."+ parts[1]+ "." +
             parts[2] + "." + parts[3];

         // assemble the port number
         int port = (parts[4] << 8) + parts[5];

         // create the socket
         return new Socket(ipAddress, port);
     }

	 public int indexOf(String s, char c)
	 {
		 if(s == null ||
			s.length() < 1) return -1;

		 char [] array = s.toCharArray();
		 for(int i=0; i<array.length; i++){
			 if(array[i] == c) return i;
		 }
		 
		 return -1;
	 }

	 public boolean isDigit(char c)
	 {
		 int charCode = (int)c;
		 return (c >= (int)'0') && (c <= (int)'9');
	 }


     /**
      *  Send a command to the FTP server and
      *  return the server's reply
      *
      *  @return  reply to the supplied command
      */
     String sendCommand(String command)
	 {
         if (debugResponses)
             System.out.println("---> " + command);

         // send it
         ds.writeFixedString(command + EOL, command.length() + EOLLength);

		 // we can't flush I hope this isn't a problem
         // writer.flush();

         // and read the result
         return readReply();
     }

     /**
      *  Read the FTP server's reply to a previously
      *  issued command. RFC 959 states that a reply
      *  consists of the 3 digit code followed by text.
      *  The 3 digit code is followed by a hyphen if it
      *  is a muliline response, and the last line starts
      *  with the same 3 digit code.
      *
      *  @return  reply string
      */
     String readReply()
	 {
		 
		 String replyStr = bufferedStream.readLine();
		 if(replyStr == null) return null;

         StringBuffer reply = new StringBuffer(replyStr);
		 if(reply == null || reply.length() < 4){
			 // error in the reply
			 return null;
		 }

         if (debugResponses)
             System.out.println(reply.toString());

         String replyCode = reply.toString().substring(0, 3);

         // check for multiline response and build up
         // the reply
         if (reply.charAt(3) == '-') {

             boolean complete = false;
             while (!complete) {

                 String line = bufferedStream.readLine();
				 if(line == null || line.length() < 4){
					 // error in line
					 return null;
				 }

                 if (debugResponses)
                     System.out.println(line);

                 if (line.length() > 3 &&
                     line.substring(0, 3).equals(replyCode) &&
                     line.charAt(3) == ' ') {
                     reply.append(line.substring(3));
                     complete = true;
                 }
                 else { // not the last line
                     reply.append(" ");
                     reply.append(line);
                 }
             } // end while
         } // end if
         return reply.toString();
     }


     /**
      *  Validate the response the host has supplied against the
      *  expected reply. If we get an unexpected reply we throw an
      *  exception, setting the message to that returned by the
      *  FTP server
      *
      *  @param   reply              the entire reply string we received
      *  @param   expectedReplyCode  the reply we expected to receive
      *
      */
     boolean validateReply(String reply, String expectedReplyCode)
	 {
         // all reply codes are 3 chars long
		 if(reply == null) return false;
         String replyCode = reply.substring(0, 3);

         // if unexpected reply, throw an exception
         if (!replyCode.equals(expectedReplyCode)) {
			 errorCode = ERROR_INVALID_REPLY;
			 errorStr = "reply: " + reply;
			 return false;
         }

		 return true;
     }

     /**
      *  Validate the response the host has supplied against the
      *  expected reply. If we get an unexpected reply we throw an
      *  exception, setting the message to that returned by the
      *  FTP server
      *
      *  @param   reply               the entire reply string we received
      *  @param   expectedReplyCodes  array of expected replies
      *
      */
     boolean validateReply(String reply, String[] expectedReplyCodes)
	 {
         // all reply codes are 3 chars long
         String replyCode = reply.substring(0, 3);

         for (int i = 0; i < expectedReplyCodes.length; i++)
             if (replyCode.equals(expectedReplyCodes[i]))
                 return true;

         // got this far, not recognised
		 errorCode = ERROR_INVALID_REPLY;
		 errorStr = "reply: " + reply;
		 return false;
     }


     /**
      *  Switch debug of responses on or off
      *
      *  @param  on  true if you wish to have responses to
      *              stdout, false otherwise
      */
     void debugResponses(boolean on) {

         debugResponses = on;
     }

 }


