/*
Copyright (C) 2001 Concord Consortium

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
import waba.ui.*;
import waba.fx.*;
import waba.io.*;
import waba.sys.*;
import waba.util.*;
import graph.*;
import extra.ui.*;
import org.concord.waba.extra.probware.*;
import org.concord.waba.extra.probware.probs.*;
import org.concord.waba.extra.event.*;

public class GraphTool extends Container
    implements DataListener
{
    ProbManager pm = ProbManager.getProbManager();
    AnnotView lg;
    Pushbutton [] modControl;

    int i;
    Label tOffVal, tVal;
    LabelBuf curVal, curTime;
    Label localLabel;
    Button clearB, exitB;
    TextLine convertor = new TextLine("0");

    //    Transform startingTrans;
 
    //    int curProbe = 3;

    Label bytesRead = new Label("-2");
    Label line2 = new Label("-1");

    MainWindow mw = null;
    String units = "";

    CCProb curProbe = null;

    Bin curBin = null;

    public GraphTool(AnnotView av, int probeId, 
		     String units, int w, int h) 
    {
	int xPos = 0;
	int buttonWidth;
       

	this.units = units;
	lg = av;
	
	//	curProbe = probeId;

	convertor.maxDigits = 2;
	setRect(0,0,w, h);

	String [] names = new String [2];
	names [0] = "Start";
	names [1] = "Stop";
	modControl = Pushbutton.createGroup(names, 1);

	buttonWidth = h-2;
	if(h < 30) buttonWidth = 30;

	modControl[0].setRect(xPos,0,buttonWidth,h-2);
	xPos += buttonWidth+2;
	modControl[1].setRect(xPos,0,buttonWidth,h-2);
	xPos += buttonWidth;
	add(modControl[0]);
	add(modControl[1]);

	curVal = new LabelBuf("");
	curVal.setRect(xPos,0,w*50/160,h/2);	
	curVal.setFont(new Font("Helvetica", 
				Font.BOLD, h*12/20 - (h-20)*8/20));
	add(curVal);

	curTime = new LabelBuf("0.0s");
	curTime.setRect(xPos,h/2,w*50/160,h/2);
	curTime.setFont(new Font("Helvetica", 
				 Font.BOLD, h*12/20 - (h-20)*8/20));
	add(curTime);

	xPos += w*50/160;

	if(w < 170){
	    clearB = new Button("Clear");
	    clearB.setRect(w-26, 0, 25, 17);
	    add(clearB);	    
	} else {
	    exitB = new Button("Exit");
	    exitB.setRect(w-32, 0, 32, h/2);
	    add(exitB);

	    clearB = new Button("Clear");
	    clearB.setRect(w-32, h/2, 32, h/2);
	    add(clearB);	    
	}

	curBin = lg.getBin();

	curProbe = ProbFactory.createProb(probeId);

	pm.registerProb(curProbe);
	pm.addDataListenerToProb(curProbe.getName(),this);

	bytesRead.setRect(105, 0, 30, 10);
	//add(bytesRead);
	
	line2.setRect(0, 10, 160, 10);
	//add(line2);

	mw = MainWindow.getMainWindow();
    }
    
    public void dataReceived(DataEvent dataEvent)
    {
	int dOff = dataEvent.getDataOffset();
	float data [] = dataEvent.getData();

	if(!curBin.dataReceived(dataEvent)){
	    stop();
	    lg.curView.draw();
	    return;		
	}
	lg.update();
    }


    public void setPos(int x, int y)
    {
	setRect(x,y,width,height);
    }

    float oldVal;

    public void onExit()
    {
	pm.stop();
	
	if(curProbe != null){
	    pm.unRegisterProb(curProbe);
	}
	
	lg.free();
    }

    void stop()
    {

	if(lg.active){
	    lg.active = false;
	    curBin = lg.pause();
	}
	
	pm.stop();

	modControl[1].setSelected(true);    
	repaint();
    }

    public void onEvent(Event e)
    {
	float newVal;

	if(e.type == ControlEvent.PRESSED){
	    Control target = (Control)e.target;
	    int index;
	    if(target == modControl[0] && modControl[0].isSelected()){
		// start
		lg.active = true;
		
		bytesRead.setText((byte)'C' + "");
		pm.start();

	    } else if(target == modControl[1] && modControl[1].isSelected()){
		stop();
	    } else if(target == clearB){
		// ask for confirmation
		    lg.active = false;
		    stop();
		    lg.reset();

		    curVal.setText("");
		    curTime.setText("0.0s");

	    } else if(target == exitB){
		    // doit
		    mw.exit(0);
	    }
	} else if(e.type == 1003){
	    //	    System.out.println("Got 1003");
	    if(lg.lgView.selAnnot != null){
		curVal.setText(convertor.fToString(lg.lgView.selAnnot.value) + units);
		curTime.setText(convertor.fToString(lg.lgView.selAnnot.time) + "s");
	    } else {
		// hack
		curVal.setText("");
		curTime.setText("");
	    }		
	}  
    }
    
}












