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
		boolean macOS = System.getProperty("os.name").startsWith("Mac OS");
		if(macOS){
			String osVersion = System.getProperty("os.version");
			if(osVersion == null) return null;
			if(osVersion.startsWith("10")){
				return new RXTXSerialPortImpl(number,baudRate,bits,parity,stopBits);
			}
			if(!loadLibrarySuccess){
				return new JDirectSerialPort(number,baudRate,bits,parity,stopBits);
			}
			return new SerialPortImpl(number,baudRate,bits,parity,stopBits);//native lib requires in extensions
		}
		return new WinSerialPortImpl(number,baudRate,bits,parity,stopBits);
	}
}
