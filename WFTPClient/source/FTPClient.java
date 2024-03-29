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
 *        Revision 1.1  2001/10/05 14:42:03  bruceb
 *        moved from old project
 *
 *
 */

package org.concord.waba.WFTPClient;

import waba.io.*;
import waba.sys.*;
import waba.util.*;
import org.concord.waba.extra.io.*;

/**
 *  Supports client-side FTP. Most common
 *  FTP operations are present in this class.
 *
 *  @author             Bruce Blackshaw
 *  @version        $Revision$
 *
 */
public class FTPClient {

    /**
     *  Revision control id
     */
    private static String cvsId = "$Id$";

    /**
     *  Socket responsible for controlling
     *  the connection
     */
    private FTPControlSocket control = null;

    /**
     *  Socket responsible for transferring
     *  the data
     */
    private Socket data = null;


    /**
     *  Socket timeout for both data and control. In
     *  milliseconds
     */
    private int timeout = 0;


    /**
     *  Record of the transfer type - make the default ASCII
     */
    private FTPTransferType transferType = FTPTransferType.ASCII;

	// This indicates there is an error somewhere
	// current this means in the FTPCS
	private boolean error;

    /**
     *  Constructor. Creates the control
     *  socket
     *
     *  @param   remoteHost  the remote hostname
     */
    public FTPClient(String remoteHost)
	{
        control = new FTPControlSocket(remoteHost);
		if(control.getError() != FTPControlSocket.ERROR_OK){
			error = true;
		}
    }

    /**
     *  Constructor. Creates the control
     *  socket
     *
     *  @param   remoteHost  the remote hostname
     *  @param   controlPort  port for control stream
     */
    public FTPClient(String remoteHost, int controlPort)
	{
        control = new FTPControlSocket(remoteHost, controlPort);
		if(control.getError() != FTPControlSocket.ERROR_OK){
			error = true;
		}
    }

    /**
     *   Set the TCP timeout on the underlying socket.
     *
     *   If a timeout is set, then any operation which
     *   takes longer than the timeout value will be
     *   killed with a java.io.InterruptedException. We
     *   set both the control and data connections
     *
     *   @param millis The length of the timeout, in milliseconds
     */
    public boolean setTimeout(int millis)
	{
		if(error) return false;

        this.timeout = millis;
        control.setTimeout(millis);
		return true;
    }

    /**
     *  Login into an account on the FTP server. This
     *  call completes the entire login process
     *
     *  @param   user       user name
     *  @param   password   user's password
     */
    public boolean login(String user, String password)
	{
		if(error) return false;

        String response = control.sendCommand("USER " + user);
        if(!control.validateReply(response, "331")){
			error = true;
			return false;
		}

        response = control.sendCommand("PASS " + password);
        if(!control.validateReply(response, "230")){
			error = true;
			return false;
		}
		return true;
    }


    /**
     *  Supply the user name to log into an account
     *  on the FTP server. Must be followed by the
     *  password() method - but we allow for
     *
     *  @param   user       user name
     *  @param   password   user's password
     */
    public boolean user(String user)
	{
		if(error) return false;

        String reply = control.sendCommand("USER " + user);

        // we allow for a site with no password - 230 response
        String[] validCodes = {"230", "331"};
        if(!control.validateReply(reply, validCodes)){
			error = true;
			return false;
		}
		return true;
    }


    /**
     *  Supplies the password for a previously supplied
     *  username to log into the FTP server. Must be
     *  preceeded by the user() method
     *
     *  @param   user       user name
     *  @param   password   user's password
     */
    public boolean password(String password)
	{
		if(error) return false;

        String reply = control.sendCommand("PASS " + password);

        // we allow for a site with no passwords (202)
        String[] validCodes = {"230", "202"};
        if(!control.validateReply(reply, validCodes)){
			error = true;
			return false;
		}
		return true;
    }

    /**
     *  Issue arbitrary ftp commands to the FTP server.
     *
     *  @param command     ftp command to be sent to server
     *  @param validCodes  valid return codes for this command
     */
    public boolean quote(String command, String[] validCodes)
	{
		if(error) return false;

        String reply = control.sendCommand(command);

        // allow for no validation to be supplied
        if (validCodes != null && validCodes.length > 0){
            if(!control.validateReply(reply, validCodes)){
				error = true;
				return false;
			}
		}

		return true;
    }


    /**
     *  Put a local file onto the FTP server. It
     *  is placed in the current directory.
     *
     *  @param  localPath   path of the local file
     *  @param  remoteFile  name of remote file in
     *                      current directory
     */
    public boolean put(String localPath, String remoteFile)
	{
        return put(localPath, remoteFile, false);
    }


    /**
     *  Put a local file onto the FTP server. It
     *  is placed in the current directory. Allows appending
     *  if current file exists
     *
     *  @param  localPath   path of the local file
     *  @param  remoteFile  name of remote file in
     *                      current directory
     *  @param  append      true if appending, false otherwise
     */
    public boolean put(String localPath, String remoteFile, boolean append)
	{
		if(error) return false;

        // get according to set type
        if (getType() == FTPTransferType.ASCII) {
            if(!putASCII(localPath, remoteFile, append)) return false;
        }
        else {
            if(!putBinary(localPath, remoteFile, append)) return false;
        }

        // check the control response
        String[] validCodes2 = {"226", "250"};
        String reply = control.readReply();
        if(!control.validateReply(reply, validCodes2)){
			error = true;
			return false;
		}

		return true;
    }


    /**
     *  Request the server to set up the put
     *
     *  @param  remoteFile  name of remote file in
     *                      current directory
     *  @param  append      true if appending, false otherwise
     */
    private boolean initPut(String remoteFile, boolean append)
	{
        // set up data channel
        data = control.createDataSocket();
		if(data == null){
			error = true;
			return false;
		}
        data.setReadTimeout(timeout);

        // send the command to store
        String cmd = append ? "APPE " : "STOR ";
        String reply = control.sendCommand(cmd + remoteFile);

        // Can get a 125 or a 150
        String[] validCodes1 = {"125", "150"};
        if(!control.validateReply(reply, validCodes1)){
			error = true;
			return false;
		}

		return true;
    }

	
    /**
     *  Put as ASCII, i.e. read a line at a time and write
     *  inserting the correct FTP separator
     *
     *  @param localPath   full path of local file to read from
     *  @param remoteFile  name of remote file we are writing to
     *  @param  append      true if appending, false otherwise
     */
    private boolean putASCII(String localPath, String remoteFile, boolean append)
    {
		return true;

		/*
        // create the buffered stream for reading
        LineNumberReader in
            = new LineNumberReader(
                new FileReader(localPath));

        if(!initPut(remoteFile, append)) return false;

        // get an character output stream to write to ... AFTER we
        // have the ok to go ahead AND AFTER we've successfully opened a
        // stream for the local file
        BufferedWriter out =
            new BufferedWriter(
                new OutputStreamWriter(data.getOutputStream()));

        // write line by line, writing \r\n as required by RFC959 after
        // each line
        String line = null;
        while ((line = in.readLine()) != null) {
            out.write(line, 0, line.length());
            out.write(FTPControlSocket.EOL, 0, FTPControlSocket.EOL.length());
        }
        in.close();
        out.flush();
        out.close();

        // and close the data socket
        try {
            data.close();
        }
        catch (IOException ignore) {}
		*/
    }

    /**
     *  Put as binary, i.e. read and write raw bytes
     *
     *  @param localPath   full path of local file to read from
     *  @param remoteFile  name of remote file we are writing to
     *  @param  append      true if appending, false otherwise
     */
    private boolean putBinary(String localPath, String remoteFile, boolean append)
	{

        // open input stream to read source file ... do this
        // BEFORE opening output stream to server, so if file not
        // found, an exception is thrown
		File in = new File(localPath, File.READ_ONLY);
		if(!in.isOpen()){
			// this is a nasty error situation that we should deal with
			return false;
		}

        if(!initPut(remoteFile, append)) return false;

        byte[] buf = new byte[512];

        // read a chunk at a time and write to the data socket
        int count = 0;
        while ((count = in.readBytes(buf, 0, buf.length)) > 0) {
            data.writeBytes(buf, 0, count);
        }
        in.close();

        // flush and clean up
        data.close();
		data = null;

		return true;
    }


    /**
     *  Put data onto the FTP server. It
     *  is placed in the current directory.
     *
     *  @param  data        array of bytes
     *  @param  remoteFile  name of remote file in
     *                      current directory
     */
    public boolean put(byte[] bytes, String remoteFile)
	{
        return put(bytes, remoteFile, false);
    }

    /**
     *  Put data onto the FTP server. It
     *  is placed in the current directory. Allows
     *  appending if current file exists
     *
     *  @param  data        array of bytes
     *  @param  remoteFile  name of remote file in
     *                      current directory
     *  @param  append      true if appending, false otherwise
     */
    public boolean put(byte[] bytes, String remoteFile, boolean append)
	{

        if(!initPut(remoteFile, append)) return false;

        // write array
        data.writeBytes(bytes, 0, bytes.length);

        // flush and clean up
        data.close();
		data = null;

        // check the control response
        String[] validCodes2 = {"226", "250"};
        String reply = control.readReply();
        if(!control.validateReply(reply, validCodes2)){
			error = true;
			return false;
		}
		return true;
    }


    /**
     *  Get data from the FTP server. Uses the currently
     *  set transfer mode.
     *
     *  @param  localPath   local file to put data in
     *  @param  remoteFile  name of remote file in
     *                      current directory
     */
	/*
    public void get(String localPath, String remoteFile)
        throws IOException, FTPException {

        // get according to set type
        if (getType() == FTPTransferType.ASCII) {
            getASCII(localPath, remoteFile);
        }
        else {
            getBinary(localPath, remoteFile);
        }

        // check the control response
        String[] validCodes2 = {"226", "250"};
        String reply = control.readReply();
        control.validateReply(reply, validCodes2);
    }
	*/

    /**
     *  Request to the server that the get is set up
     *
     *  @param  remoteFile  name of remote file
     */

	/*
    private void initGet(String remoteFile)
        throws IOException, FTPException {

        // set up data channel
        data = control.createDataSocket(connectMode);
        data.setTimeout(timeout);

        // send the retrieve command
        String reply = control.sendCommand("RETR " + remoteFile);

        // Can get a 125 or a 150
        String[] validCodes1 = {"125", "150"};
        control.validateReply(reply, validCodes1);
    }

	*/

    /**
     *  Get as ASCII, i.e. read a line at a time and write
     *  using the correct newline separator for the OS
     *
     *  @param localPath   full path of local file to write to
     *  @param remoteFile  name of remote file
     */
	/*
    private void getASCII(String localPath, String remoteFile)
        throws IOException, FTPException {

        // create the buffered stream for writing
        BufferedWriter out =
            new BufferedWriter(
                new FileWriter(localPath));

        initGet(remoteFile);

        // get an character input stream to read data from ... AFTER we
        // have the ok to go ahead AND AFTER we've successfully opened a
        // stream for the local file
        LineNumberReader in =
            new LineNumberReader(
                new InputStreamReader(data.getInputStream()));

        // read/write a line at a time
        String line = null;
        while ((line = in.readLine()) != null) {
            out.write(line, 0, line.length());
            out.newLine();
        }
        out.close();

        try {
            in.close();
            data.close();
        }
        catch (IOException ignore) {}
    }
	*/

    /**
     *  Get as binary file, i.e. straight transfer of data
     *
     *  @param localPath   full path of local file to write to
     *  @param remoteFile  name of remote file
     */
	/*
    private void getBinary(String localPath, String remoteFile)
        throws IOException, FTPException {

        // create the buffered output stream for writing the file
        BufferedOutputStream out =
            new BufferedOutputStream(
                new FileOutputStream(localPath, false));

        initGet(remoteFile);

        // get an input stream to read data from ... AFTER we have
        // the ok to go ahead AND AFTER we've successfully opened a
        // stream for the local file
        BufferedInputStream in =
            new BufferedInputStream(
                new DataInputStream(data.getInputStream()));

        // do the retrieving
        int chunksize = 4096;
        byte [] chunk = new byte[chunksize];
        int count;

        // read from socket & write to file in chunks
        while ((count = in.read(chunk, 0, chunksize)) >= 0) {

            out.write(chunk, 0, count);
        }
        out.close();

        // close streams
        try {
            in.close();
            data.close();
        }
        catch (IOException ignore) {}
    }
	*/

    /**
     *  Get data from the FTP server. Transfers in
     *  whatever mode we are in. Retrieve as a byte array. Note
     *  that we may experience memory limitations as the
     *  entire file must be held in memory at one time.
     *
     *  @param  remoteFile  name of remote file in
     *                      current directory
     */
	/*

    public byte[] get(String remoteFile)
        throws IOException, FTPException {

        // set up data channel
        data = control.createDataSocket(connectMode);
        data.setTimeout(timeout);

        // send the retrieve command
        String reply = control.sendCommand("RETR " + remoteFile);

        // Can get a 125 or a 150
        String[] validCodes1 = {"125", "150"};
        control.validateReply(reply, validCodes1);

        // get an input stream to read data from
        BufferedInputStream in =
            new BufferedInputStream(
                new DataInputStream(data.getInputStream()));

        // do the retrieving
        int chunksize = 4096;
        byte [] chunk = new byte[chunksize];  // read chunks into
        byte [] resultBuf = new byte[chunksize];  // where we place chunks
        byte [] temp = null;  // temp swap buffer
        int count;  // size of chunk read
        int bufsize = 0;  // size of resultBuf

        // read from socket & write to file
        while ((count = in.read(chunk, 0, chunksize)) >= 0) {

            // new buffer to hold current buf + new chunk
            temp = new byte[bufsize+count];

            // copy current buf to temp
            System.arraycopy(resultBuf, 0, temp, 0, bufsize);

            // copy new chunk onto end of temp
            System.arraycopy(chunk, 0, temp, bufsize, count);

            // re-assign temp buffer to buf
            resultBuf = temp;

            // update size of buffer
            bufsize += count;
        }

        // close streams
        try {
            in.close();
            data.close();
        }
        catch (IOException ignore) {}

        // check the control response
        String[] validCodes2 = {"226", "250"};
        reply = control.readReply();
        control.validateReply(reply, validCodes2);

        return resultBuf;
    }
	*/

    /**
     *  Run a site-specific command on the
     *  server. Support for commands is dependent
     *  on the server
     *
     *  @param  command   the site command to run
     *  @return true if command ok, false if
     *          command not implemented
     */
	/*
    public boolean site(String command)
        throws IOException, FTPException {

        // send the retrieve command
        String reply = control.sendCommand("SITE " + command);

        // Can get a 200 (ok) or 202 (not impl). Some
        // FTP servers return 502 (not impl)
        String[] validCodes = {"200", "202", "502"};
        control.validateReply(reply, validCodes);

        // return true or false? 200 is ok, 202/502 not
        // implemented
        if (reply.substring(0, 3).equals("200"))
            return true;
        else
            return false;
    }
	*/

    /**
     *  List a directory's contents
     *
     *  @param  mask  the file mask to use
     *  @return a string containing the line separated
     *          directory listing
     *  @deprecated  As of FTP 1.1, replaced by {@link #dir(String)}
     */
	/*
    public String list(String mask)
        throws IOException, FTPException {

        return list(mask, false);
    }
	*/

    /**
     *  List a directory's contents as one string. A detailed
     *  listing is available, otherwise just filenames are provided.
     *  The detailed listing varies in details depending on OS and
     *  FTP server.
     *
     *  @param  mask  the file mask to use
     *  @param  full  true if detailed listing required
     *                false otherwise
     *  @return a string containing the line separated
     *          directory listing
     *  @deprecated  As of FTP 1.1, replaced by {@link #dir(String,boolean)}
     */
	/*
    public String list(String mask, boolean full)
        throws IOException, FTPException {

        String[] list = dir(mask, full);

        StringBuffer result = new StringBuffer();
        String sep = System.getProperty("line.separator");

        // loop thru results and make into one string
        for (int i = 0; i < list.length; i++) {
            result.append(list[i]);
            result.append(sep);
        }

        return result.toString();
    }
	*/

    /**
     *  List a directory's contents as an array of strings of filenames.
     *
     *  @param  mask  the file mask to use
     *  @return  an array of directory listing strings
     */
	/*
    public String[] dir(String mask)
        throws IOException, FTPException {

        return dir(mask, false);
    }
	*/

    /**
     *  List a directory's contents as an array of strings. A detailed
     *  listing is available, otherwise just filenames are provided.
     *  The detailed listing varies in details depending on OS and
     *  FTP server.
     *
     *  @param  mask  the file mask to use
     *  @param  full  true if detailed listing required
     *                false otherwise
     *  @return  an array of directory listing strings
     */
	/*
    public String[] dir(String mask, boolean full)
        throws IOException, FTPException {

        // set up data channel
        data = control.createDataSocket(connectMode);
        data.setTimeout(timeout);

        // send the retrieve command
        String command = full ? "LIST ":"NLST ";
        command += mask;

        // some FTP servers bomb out if NLST has whitespace appended
        command = command.trim();
        String reply = control.sendCommand(command);

        // Can get a 125 or a 150
        String[] validCodes1 = {"125", "150"};
        control.validateReply(reply, validCodes1);

        // get an character input stream to read data from ... AFTER we
        // have the ok to go ahead
        LineNumberReader in =
            new LineNumberReader(
                new InputStreamReader(data.getInputStream()));

        // read a line at a time
        Vector lines = new Vector();
        String line = null;
        while ((line = in.readLine()) != null) {
            lines.add(line);
        }

        try {
            in.close();
            data.close();
        }
        catch (IOException ignore) {}

        // check the control response
        String[] validCodes2 = {"226", "250"};
        reply = control.readReply();
        control.validateReply(reply, validCodes2);

        return (String[])lines.toArray(new String[0]);
    }
	*/


    /**
     *  Switch debug of responses on or off
     *
     *  @param  on  true if you wish to have responses to
     *              stdout, false otherwise
     */
    public void debugResponses(boolean on) {

        control.debugResponses(on);
    }


    /**
     *  Get the current transfer type
     *
     *  @return  the current type of the transfer,
     *           i.e. BINARY or ASCII
     */
    public FTPTransferType getType() {

        return transferType;
    }


    /**
     *  Set the transfer type
     *
     *  @param  type  the transfer type to
     *                set the server to
     */
    public boolean setType(FTPTransferType type)
	{

        // determine the character to send
        String typeStr = FTPTransferType.ASCII_CHAR;
        if (type.equals(FTPTransferType.BINARY))
            typeStr = FTPTransferType.BINARY_CHAR;

        // send the command
        String reply = control.sendCommand("TYPE " + typeStr);
        if(!control.validateReply(reply, "200")){
			error = true;
			return false;
		}

        // record the type
        transferType = type;
		return false;
    }


    /**
     *  Delete the specified remote file
     *
     *  @param  remoteFile  name of remote file to
     *                      delete
     */
	/*
    public void delete(String remoteFile)
        throws IOException, FTPException {

        String reply = control.sendCommand("DELE " + remoteFile);
        control.validateReply(reply, "250");
    }
	*/

    /**
     *  Rename a file or directory
     *
     * @param from  name of file or directory to rename
     * @param to    intended name
     */
	/*
    public void rename(String from, String to)
        throws IOException, FTPException {

        String reply = control.sendCommand("RNFR " + from);
        control.validateReply(reply, "350");

        reply = control.sendCommand("RNTO " + to);
        control.validateReply(reply, "250");
    }
	*/

    /**
     *  Delete the specified remote working directory
     *
     *  @param  dir  name of remote directory to
     *               delete
     */
	/*
    public void rmdir(String dir)
        throws IOException, FTPException {

        String reply = control.sendCommand("RMD " + dir);
        control.validateReply(reply, "250");
    }
	*/

    /**
     *  Create the specified remote working directory
     *
     *  @param  dir  name of remote directory to
     *               create
     */
	/*
    public void mkdir(String dir)
        throws IOException, FTPException {

        String reply = control.sendCommand("MKD " + dir);
        control.validateReply(reply, "257");
    }
	*/

    /**
     *  Change the remote working directory to
     *  that supplied
     *
     *  @param  dir  name of remote directory to
     *               change to
     */
    public boolean chdir(String dir)
    {

        String reply = control.sendCommand("CWD " + dir);
        return control.validateReply(reply, "250");
    }

    /**
     *  Get the current remote working directory
     *
     *  @return   the current working directory
     */
    public String pwd()
    {
        String reply = control.sendCommand("PWD");
        if(!control.validateReply(reply, "257")) return null;
        return reply.substring(4);
    }

    /**
     *  Get the type of the OS at the server
     *
     *  @return   the type of server OS
     */
	/*
    public String system()
        throws IOException, FTPException {

        String reply = control.sendCommand("SYST");
        control.validateReply(reply, "215");
        return reply.substring(4);
    }
	*/

    /**
     *  Quit the FTP session
     *
     */
    public boolean quit()
    {
		// Why does this have two end of lines???

        String reply = control.sendCommand("QUIT" + FTPControlSocket.EOL);
        if(!control.validateReply(reply, "221")) return false;

        control.logout();
        control = null;

		return true;
    }
	
}



