package sun.net.www.protocol.ccjar;

import java.io.*;
import java.net.*;
import xml2labbook.*;

public class Handler extends URLStreamHandler{
	protected void parseURL(URL u,String spec, int start, int end)
	{
		if(u.getProtocol().equals("ccjar")){
			super.parseURL(u,spec,start,end);
			return;
		}
		throw new CCJarMalformedURLException("Invalid ccjar resource URL");

	}
	
	protected URLConnection openConnection(URL url)
		throws IOException
	{
		return new CCJarURLConnection(url);
	}
}

class CCJarMalformedURLException extends RuntimeException{
	public CCJarMalformedURLException(String msg){
		super(msg);
	}
}
