package waba.io.impl;

public class SerialPortFactory
{
	static	boolean loadLibrarySuccess = false;
	static{
		try{
			System.loadLibrary("NativeWabaSerial");
			loadLibrarySuccess = true;	
		}catch(Throwable t){
			loadLibrarySuccess = false;
		}
	}
	
	public static ISerialPort getISerialPort(int number, int baudRate){
		return getISerialPort(number, baudRate, 8, false, 1);
	}
	public static ISerialPort getISerialPort(int number, int baudRate, int bits, boolean parity, int stopBits){
		String osVersion = System.getProperty("os.version");
		if(osVersion == null) return null;
		if(!loadLibrarySuccess){
			return new JDirectSerialPort(number,baudRate,bits,parity,stopBits);
		}
		if(osVersion.startsWith("10")) return null;//X
		return new SerialPortImpl(number,baudRate,bits,parity,stopBits);//native lib requires in extensions
	}
}
