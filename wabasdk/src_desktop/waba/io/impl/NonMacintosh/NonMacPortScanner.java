package waba.io.impl;

import java.util.Vector;
import java.util.Properties;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import javax.comm.CommPortIdentifier;

public class NonMacPortScanner{
	public void scanPorts(java.util.Vector v){
		try{
			int index = 0;
			for(java.util.Enumeration en =  CommPortIdentifier.getPortIdentifiers(); en.hasMoreElements();){
				CommPortIdentifier portId = (CommPortIdentifier)en.nextElement();
				if(portId == null) continue;
				if(portId.getPortType() == CommPortIdentifier.PORT_SERIAL){
					String name = portId.getName();
					v.addElement(new SerialPortDesc(index++,name,name,name));
					
				}
			}
		}catch(Throwable t){}
	}
}




