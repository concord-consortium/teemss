package waba.io.impl;

import java.util.Vector;
import java.util.Properties;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;

public class SerialManager
{
private 	static Vector 	availablePorts = new Vector();
private     static java.awt.Frame  mainFrame = null;

	public static java.awt.Frame getMainFrame(){return mainFrame;}
	
	public static void checkAvailableSerialPorts(){
		if(mainFrame == null){
			mainFrame = new java.awt.Frame("Java Serial Test");
			mainFrame.reshape(0,0,200,200);
		}
		availablePorts.removeAllElements();
		(new PortScanner()).scanPorts(availablePorts);
		SerialPortDesc sPort = getAssignedPort();
		if(sPort == null) return;
//check does sPort exist in port's list
		boolean wasPort = false;
		for(java.util.Enumeration pe = getSerialPorts();pe.hasMoreElements();){
			SerialPortDesc portDesc = (SerialPortDesc)pe.nextElement();
			if(sPort.equals(portDesc)){
				wasPort = true;
				break;
			}
		}
		if(wasPort) return;
		String userHomeName = System.getProperty("user.home");
		File serialProp = new File(userHomeName,"concordSerial.properties");
		if(serialProp.exists()) serialProp.delete();
	}
	
	public static void showSetupDialog(){
		java.awt.Frame f = getMainFrame();
		if(f != null){
			SerialChoiceDialog dialog = new SerialChoiceDialog(f);
		}
	}
	
	public static int getNumbSerialPorts(){
		if(availablePorts == null) return 0;
		return availablePorts.size();
	}
	public static java.util.Enumeration getSerialPorts() {return availablePorts.elements();}
	
	public static void assignZeroPort(int index){
		if(index < 0 || index >= getNumbSerialPorts()) return;
		SerialPortDesc pDesc = (SerialPortDesc)availablePorts.elementAt(index);
		assignZeroPort(pDesc);
	}
	public static void assignZeroPort(SerialPortDesc pDesc){
		if(pDesc == null) return;
		String userHomeName = System.getProperty("user.home");
		File serialProp = new File(userHomeName,"concordSerial.properties");
		if(serialProp.exists()) serialProp.delete();
		Properties p = new Properties();
		p.put("org.concord.zeroport",""+pDesc.index+";"+pDesc.inpName+";"+pDesc.outName+";"+pDesc.name);
		try{
			FileOutputStream fout = new FileOutputStream(serialProp);
			p.save(fout,"Serial port assigned to communication with probs");
			fout.close();
		}catch(Exception e){}
	}
	
	public static SerialPortDesc getAssignedPort(){
		SerialPortDesc retValue = null;
		String userHomeName = System.getProperty("user.home");
		File serialProp = new File(userHomeName,"concordSerial.properties");
		if(!serialProp.exists()) return retValue;
		String portProp = null;
		try{
			FileInputStream fin = new FileInputStream(serialProp);
			Properties p = new Properties();
			p.load(fin);
			portProp = p.getProperty("org.concord.zeroport");
			fin.close();
			java.util.StringTokenizer parser = new java.util.StringTokenizer(portProp,";");
			int index = Integer.valueOf(parser.nextToken()).intValue();
			String inpName = parser.nextToken();
			String outName = parser.nextToken();
			String name = parser.nextToken();
			retValue = new SerialPortDesc(index,inpName,outName,name);
	}catch(Exception e){
			retValue = null;
		}
		return retValue;

	}
	
}




