package xml2labbook;

import java.net.*;
import java.io.*;
import java.util.zip.*;
import java.util.Hashtable;
import java.security.Permission;

public class CCJarURLConnection extends URLConnection{
//String		jarName = null;
//String		entryName = null;
InputStream 	jarInputStream = null;
static protected Hashtable zipFiles = null;
	public CCJarURLConnection(URL url){
		super(url);
	}
	public Permission getPermission() throws IOException {
		return null;
	}
	synchronized public void connect() throws IOException{
		if(connected){
			return;
		}
		jarInputStream = null;
		if(url == null) return;
		
		try{
			jarInputStream = getClass().getResourceAsStream(url.getPath());
		}catch(Throwable t){
			System.out.println("Exception connect "+t);
			jarInputStream = null;
		}
		if(jarInputStream == null) return;
		connected = true;
	}
	synchronized public InputStream getInputStream() throws IOException{
		if(!connected)
			connect();
		return jarInputStream;
	}
	public String getContentType(){
		return guessContentTypeFromName(url.getFile());
	}
	public void setDoOutput(boolean dooutput) {
		if (connected)
			throw new IllegalAccessError("Already connected");
		if (dooutput)
			throw new IllegalAccessError("It's impossible to set the field doOutput to true for CCJarURLConnection");
		doOutput = false;
	}
	public void setAllowUserInteraction(boolean allowuserinteraction) {
		if (connected)
			throw new IllegalAccessError("Already connected");
		if (allowuserinteraction)
			throw new IllegalAccessError("It's impossible to set the field allowUserInteraction to true for CCJarURLConnection");
		allowUserInteraction = false;
	}

}
