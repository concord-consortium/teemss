package waba.io.impl;

public class PortScanner{
	public void scanPorts(java.util.Vector v){
		boolean macOS = System.getProperty("os.name").startsWith("Mac OS");
		if(macOS){
			String osVersion = System.getProperty("os.version");
			if(osVersion.startsWith("10")) 	(new RXTXPortScanner()).scanPorts(v);
			else 							(new MacPortScanner()).scanPorts(v);
		}
//		else		(new NonMacPortScanner()).scanPorts(v);
		else		(new RXTXPortScanner()).scanPorts(v);
	}
}
