package waba.io.impl;

public class PortScanner{
	public void scanPorts(java.util.Vector v){
		boolean macOS = System.getProperty("os.name").startsWith("Mac OS");
		if(macOS) (new MacPortScanner()).scanPorts(v);
		else		(new NonMacPortScanner()).scanPorts(v);
	}
}
