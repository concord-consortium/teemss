import waba.ui.*;
import waba.fx.*;
import waba.io.*;
import waba.sys.*;

public class SerialTest extends MainWindow
{
    final static int BUF_SIZE = 500;
    Timer tick = null;
    int value = 0;
    int line = 10;
    Graphics mwG;
    SerialPort port;
    byte buf[] = new byte[BUF_SIZE];
    boolean readingRotary = false;

    Button c = new Button("c");
    Button v = new Button("v");
    Button n9 = new Button("9");
    Button r = new Button("r");
    Button e = new Button("e");
    Button toggle = new Button("open");

    Label bytesRead = new Label("0");
    Label errorNum = new Label("0");

    int testArray[][] = new int [10] [];

    int testSize = 15500;

    public SerialTest()
    {
	c.setRect(0,143,17,17);
	v.setRect(20,143,17,17);
	n9.setRect(40,143,17,17);
	r.setRect(60,143,17,17);
	e.setRect(80,143,17,17);
	toggle.setRect(120,143,40,17);
	
	add(c);
	add(v);
	add(n9);
	add(r);
	add(e);
	add(toggle);

	bytesRead.setRect(60,0,50,10);
	add(bytesRead);
	
	errorNum.setRect(110,0,50,10);
	add(errorNum);

	/*
	for(int i=0; i<3; i++){
	    testArray[i] = new int [testSize];
	}
	*/
    }

    boolean firstTime = true;

    public void onPaint(Graphics g)
    {
	g.setColor(0,0,0);
	g.drawText(Vm.getPlatform(), 0, 0);

	if(firstTime){
	    firstTime = false;


	    /*
	    g.drawText("test 1:", 50, 0);
	    int i;
	    for(i=0; i<testSize; i++){
		testArray[0][i] = 0;
		g.setColor(255,255,255);
		g.fillRect(100, 0, 60, 10);
		g.setColor(0,0,0);
		g.drawText("" + i, 100, 0);
	    }
	    
	    int tmp;

	    g.setColor(255,255,255);
	    g.fillRect(50, 0, 50, 10);
	    g.setColor(0,0,0);
	    g.drawText("test 2:", 50, 0);
	    for(i=0; i<testSize; i++){
		tmp = testArray[0][i];
		g.setColor(255,255,255);
		g.fillRect(100, 0, 60, 10);
		g.setColor(0,0,0);
		g.drawText("" + i + ":" + tmp, 100, 0);
	    }
	    */
	}
       

	g.drawText("read:", 30, 0);
	mwG = g;
    }   

    public boolean start()
    {
	port = new SerialPort(0,9600);
	port.setReadTimeout(0);
	port.setFlowControl(false);
	
	
	int tmp = 0 ;
	buf[0] = (byte)'c';
	for(int i=0; i<10; i++){
	    tmp = port.writeBytes(buf, 0, 1);
	    Vm.sleep(100);
	}
	// in case the the port is left open stop it
	//	waba.sys.Vm.sleep(500);
	
	port.setReadTimeout(0);
	tmp = port.readBytes(buf, 0, BUF_SIZE);//workaround 
	if(tmp < 0){
	    bytesRead.setText("Cl 2");
	    if(port.readBytes(buf, 0, BUF_SIZE) < 0){
		bytesRead.setText("Cl err");
		stop();
		return false;
	    }
	}
	tick = addTimer(100);
	return true;
    }

    public boolean stop()
    {
	readingRotary = false;
	if(tick != null){
	    removeTimer(tick);
	    tick = null;
	}

	if(port != null && port.isOpen()) port.close();
	port = null;
	return true;
    }

  public void println(String s, int pos)
  {
    mwG.setColor(0,0,0);
    mwG.drawText(s,pos,line);
    line += 10;
  }

    int row = 0;
    public void print(String s)
    {
	mwG.setColor(0,0,0);
	mwG.drawText(s,row,line);
	row += 8;
	if(row >= 155){	    
	    line += 10;
	    row = 0;
	    mwG.setColor(255,255,255);
	    mwG.fillRect(0,line,160,10);
	    mwG.setColor(0,0,0);
	}

	if(line >= 140){
	    line = 10;
	    row = 0;
	}
    }

    public void onExit()
    {
	if(port != null && port.isOpen())
	    port.close();
    }


    int count = 0;
    int bCount = 0;

  public void onEvent(Event e)
  {	
      int ret;

    if(e.type == ControlEvent.TIMER){	
	bCount = 0;
	
	if(readingRotary){
	    while((ret = port.readBytes(buf, 0, 100)) > 0){
		//	    print("" + (char)buf[8]);
		bCount += ret;
		count+= bCount;
		bytesRead.setText("" + count);
		if(ret > 0){
		    for(int i=0; i < ret; i++){
			//			System.out.println("" + (int)buf[i]);
		    }
		}
	    }
	    return;
	} else {    
	    while((ret = port.readBytes(buf, 0, 1)) > 0){
		print("" + (char)buf[0]);
		bCount += ret;
		count+= bCount;
		bytesRead.setText("" + count);
	    }
	}

	
	if(ret == -1){
	    // errorNum.setText("" + port.errRet + ":" + port.errNum);
	    // stop(); 
	    //toggle.setText("open");
	   
	    bytesRead.setText("Error at: " + count);
	}

	if(line > 200){
	    stop();
	    toggle.setText("open");
	    bytesRead.setText("Stopped at: " + count);
	}
    } else if(e.type == ControlEvent.PRESSED){
	if(e.target == toggle){
	    if(tick != null){
		if(stop()){
		    toggle.setText("Open");
		}
	    } else {
		if(start()){
		    toggle.setText("Close");
		}
	    }
	    return;
	}
	
	if(port == null)
	    return;
	if(e.target == c){
	    readingRotary = false;
	    buf[0] = (byte)'c';
	    port.writeBytes(buf, 0, 1);
	} else if(e.target == v){
	    buf[0] = (byte)'v';
	    port.writeBytes(buf, 0, 1);
	} else if(e.target == n9){
	    buf[0] = (byte)'9';
	    port.writeBytes(buf, 0, 1);
	} else if(e.target == r){
	    readingRotary = false;
	    buf[0] = (byte)'r';
	    port.writeBytes(buf, 0, 1);
	} else if(e.target == this.e){
	    buf[0] = (byte)'e';
	    port.writeBytes(buf, 0, 1);
	}
    }
    
  }
}


















