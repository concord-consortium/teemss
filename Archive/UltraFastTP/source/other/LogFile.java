import waba.io.*;
import graph.*;
import waba.ui.*;

public class LogFile extends Transform
{
    File logFile = null;
    String logName = null;
    TextLine convertor = new TextLine("0");


    public LogFile(String name)
    {
	logName = name;
	convertor.maxDigits = 5;
    }

    public void start()
    {
	logFile = new File(logName, File.DONT_OPEN);
	if(logFile.exists()){
	    logFile.delete();
	}
	logFile.close();
	logFile = new File(logName, File.CREATE);
	if(!logFile.isOpen()){
	    // put up message box
	    // mb = new MessageBox("Error", "Can't open file: " + logName);
	    // popupModal(mb);
	    logFile = null;
	} 
	super.start();
    }

    public void writeString(String str)
    {
	int i;
	byte [] outChar = new byte [str.length()];

	if(logFile != null){
	    for(i=0; i<str.length(); i++){
		outChar[i] = (byte)str.charAt(i);
	    }
	    
	    logFile.writeBytes(outChar, 0, str.length());
	}

    }

    public void stop()
    {
	// Close the log file
	if(logFile != null){
	    logFile.close();
	    logFile = null;
	}
	super.stop();
    }

    public boolean transform(int num, int size, float [] data)
    {
	int i,j;

	if(logFile != null){
	    int endPos = num*size;

	    for(j=0; j<endPos; j+= size){
		i=0;
		while(true){
		    writeString(convertor.fToString(data[j+i]));
		    i++;
		    if(i >= size) break;
		    writeString("\t");
		}

		writeString("\r\n");
	    }
	}

	next.transform(num, size, data);
	return true;
    }

}
