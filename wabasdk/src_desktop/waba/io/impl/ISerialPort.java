package waba.io.impl;

public interface ISerialPort
{
public final int RATE_300 	=  300;
public final int RATE_600 	=  600;
public final int RATE_1800 	=  1800;
public final int RATE_2400 	=  2400;
public final int RATE_3600 	=  3600;
public final int RATE_4800 	=  4800;
public final int RATE_7200 	=  7200;
public final int RATE_9600 	=  9600;
public final int RATE_19200 =  19200;
public final int RATE_38400 =  38400;
public final int RATE_57600 =  57600;

	public void initPort(int number, int baudRate, int bits, boolean parity, int stopBits);

	public boolean close();

	public boolean isOpen();

	public int readBytes(byte buf[], int start, int count);//readBytes

	public int readCheck();

	public boolean setFlowControl(boolean on);

	public boolean setReadTimeout(int millis);

	public int writeBytes(byte buf[], int start, int count);
	public void clearBuffer(int v);
}
