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
 *      $Log$
 *      Revision 1.2  2001/10/09 20:54:36  bruceb
 *      Active mode testing
 *
 */

import waba.ui.*;

import org.concord.waba.WFTPClient.*;

/**
 *  Crude test harness.
 *
 *    TO DO: expand this!
 *
 *  @author             Bruce Blackshaw
 *  @version        $Revision$
 *
 */
public class FTPClientTest extends MainWindow
{

    /**
     *  Revision control id
     */
    private static String cvsId = "$Id$";

    /**
     *   Test harness. We have a long way to
     *   go here! I'll be spending most development
     *   time hence enhancing this!
     *
     *   Planned:
     *         - drive off a config file
     *         - do byte by byte file comparisons of transferred
     *           files
     *         - exercise all functionality
     *         - postive and negative tests
     *
     */
	public void onStart(){

		// assign args to make it clear
		String host = "web.concord.org";
		//args[0];
		String user = "scytacki"; // args[1];
		String password = "tAm8s&d"; // args[2];
		String filename = "test.txt"; // args[3];
		String directory = "."; // args[4];


            // connect and test supplying port no.
		FTPClient ftp = new FTPClient(host, 21);

		// switch on debug of responses
		ftp.debugResponses(true);

		if(!ftp.login(user, password)){
			System.err.println("error");
		}
		if(!ftp.quit()){
			System.err.println("error");
		}

		// connect again
		ftp = new FTPClient(host);

		// switch on debug of responses
		ftp.debugResponses(true);

		ftp.login(user, password);

            // binary transfer

			// do binary by default
		ftp.setType(FTPTransferType.BINARY);
		/*
            if (args[5].equalsIgnoreCase("BINARY")) {
            }
            else if (args[5].equalsIgnoreCase("ASCII")) {
                ftp.setType(FTPTransferType.ASCII);
            }
            else {
                System.out.println("Unknown transfer type: " + args[5]);
                System.exit(-1);
            }
			*/

            // change dir
		ftp.chdir(directory);

		// put a local file to remote host
		ftp.put(filename, filename);

		/*
		  // get bytes
		  byte[] buf = ftp.get(filename);
		  System.out.println("Got " + buf.length + " bytes");

		  // append local file
		  try {
		  ftp.put(filename, filename, true);
		  }
		  catch (FTPException ex) {
		  System.out.println("Append failed: " + ex.getMessage());
		  }

		  // get bytes again - should be 2 x
		  buf = ftp.get(filename);
		  System.out.println("Got " + buf.length + " bytes");

            // rename
            ftp.rename(filename, filename + ".new");

            // get a remote file - the renamed one
            ftp.get(filename + ".tst", filename + ".new");

            // ASCII transfer
            ftp.setType(FTPTransferType.ASCII);

            // test that list() works
            String listing = ftp.list(".");
            System.out.println(listing);

            // test that dir() works in full mode
            String[] listings = ftp.dir(".", true);
            for (int i = 0; i < listings.length; i++)
			System.out.println(listings[i]);

            // try system()
            System.out.println(ftp.system());

            // try pwd()
            System.out.println(ftp.pwd());
		*/

		ftp.quit();
    }

}
