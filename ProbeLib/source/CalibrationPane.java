package org.concord.waba.extra.ui;
import extra.util.*;
import org.concord.waba.extra.event.*;
import waba.ui.*;
import org.concord.waba.extra.probware.probs.CCProb;
import org.concord.waba.extra.probware.*;

public class CalibrationPane extends PropertyPane
	implements DataListener{
	CCButton bStart = null;
	CCButton bStop = null;

	int 	bHeight = 16;
	WTable 		calTable = null;
	CCProb	probe = null;
	int nChannels = 1;
	int   []calibratedRows = null;

	public static ProbManager pb = null;

	int 		dataDim = 128;
	//float 	[]dataFFT = new float[dataDim*2];
	//int		dataPointer = 0;

	float       	deviation = 100.0f;
	float		totalSumm = 0.0f;
	float		totalSamples = 0f;
	DeviationControl	devControl;

	public	int drawDevControlCounter = 0;
	public	int drawTableCounter = 0;
	ActionListener listener = null;
	int widthBorder = 1;
	PropertyView pView = null;

	public CalibrationPane(CCProb probe, ActionListener al, PropertyView pv){
		super(pv);
		this.probe = probe;

		int interfaceId = probe.getInterfaceType();
		// Watch out for this
		// nContainers = (probe != null && probe.needCalibration())?2:1;

		listener = al;
		pb = ProbManager.getProbManager(interfaceId);
		//		pb.setMode(CCInterfaceManager.A2D_24_MODE);
		//		pb.setMode(CCInterfaceManager.A2D_10_MODE);
		pb.registerProb(probe);
		probe.addDataListener(this);
		devControl = new DeviationControl(20.0f);		
		nChannels = probe.getActiveCalibrationChannels();
	}

	public String getName(){return "Calibration";}

	public void setupPane()
	{
		while(children != null){
			remove(children);
		}

		calTable = new WTable(getWabaWindow());
		CalibrationDesc caldesc = probe.getCalibrationDesc();
		if(caldesc == null) return;
		int nRows = caldesc.countAvailableParams();
		if(calibratedRows == null){
			calibratedRows = new int[nRows];
		}else if(calibratedRows.length != nRows){
			calibratedRows = new int[nRows];
		}
		for(int i = 0; i < nRows; i++) calibratedRows[i] = -1;
		if(nChannels == 1){
			calTable.addColumn("Raw");
		}else{
			calTable.addColumn("Raw1");
			calTable.addColumn("Raw2");
		}
		calTable.addColumn("Calibrated",true);
		String []s =new String[nChannels + 1];
		for(int i = 0; i < s.length; i++) s[i] = "";
		for(int i = 0; i < nRows;i++) calTable.addRow(s);
		calTable.setRect(0, bHeight + 2, width, height - 25);
		calTable.pack();
		waba.fx.FontMetrics fm = getFontMetrics(MainWindow.defaultFont);
		int bWidthStart 	= fm.getTextWidth("Start") + 5;
		int bWidthStop 	= fm.getTextWidth("Stop") + 5;
		if(bStart == null){
			bStart = new CCButton("Start");
			bStart.setRect(width- bWidthStart,1,bWidthStart,bHeight);
			add(bStart);
		}
		if(bStop == null){
			bStop = new CCButton("Stop");
			bStop.setRect(width - 2 - (bWidthStop +bWidthStart) ,1,bWidthStop,bHeight);
			add(bStop);
		}
		add(calTable);
		if(devControl == null) devControl = new DeviationControl(4f);
		devControl.setRect(width/2 - 50,height - 25,100,23);
		add(devControl);

		setup = true;
	}

	boolean visible = false;
	public void setVisible(boolean vis)
	{
		visible = vis;
		if(vis){
			doStop();

			// What it should do here is the channel property
			// and get its visible value
			// because the values won't have been applyed yet
			if(nChannels != probe.getActiveCalibrationChannels()){
				nChannels = probe.getActiveCalibrationChannels();
				setupPane();
			}
			if(bStart != null) bStart.setEnabled(false);
			if(calTable != null) calTable.selectRow(1);
		} else {
			doStop();
		}
	}

	public void close()
	{
		doStop();
		probe.removeDataListener(this);
		if(calTable != null) calTable.free();
	}

	public void apply()
	{
		doStop();
		if(probe != null && calTable != null){
			CalibrationDesc caldesc = probe.getCalibrationDesc();
			if(caldesc == null) return;
			int nRows = caldesc.countAvailableParams();
			//nChannels
			float []row1 = new float[nRows];
			float []calibrated = new float[nRows];
			float []row2 = null;
			if(nChannels > 1){
				row2 = new float[nRows];
			}
			for(int i = 0; i < nRows; i++){
				Object cell = calTable.getCell(0,i+1);
				if((cell != null) && (cell instanceof CCLabel)){
					row1[i] = ConvertExtra.toFloat(((CCLabel)cell).getText());
				}
				cell = calTable.getCell(nChannels,i+1);
				if((cell != null) && (cell instanceof CCLabel)){
					calibrated[i] = ConvertExtra.toFloat(((CCLabel)cell).getText());
				}
				if(row2 != null){
					cell = calTable.getCell(1,i+1);
					if((cell != null) && (cell instanceof CCLabel)){
						row2[i] = ConvertExtra.toFloat(((CCLabel)cell).getText());
					}
				}
			}
			probe.calibrationDone(row1,row2,calibrated);
		}
	}

	// FIXME
	public Window getWabaWindow()
	{
		Control curControl = this;
		while(curControl != null && !(curControl instanceof Window)){
			curControl = curControl.getParent();
		}		
		if(curControl == null) return null;
		else return (Window)curControl;
	}

	private void checkApplyEnabled(){
		boolean applyEnabled = false;
		if(!visible){
			applyEnabled = true;
		}else{
			if((calTable != null) && (calibratedRows != null)){
				applyEnabled = (calibratedRows.length > 0);
				for(int i = 0; i < calibratedRows.length; i++){
					if(calibratedRows[i] != 1){
						applyEnabled = false;
						break;
					}
				}
			}
		}
		// need to get the property view and set it's enable
		// bApply.setEnabled(applyEnabled);
		if(pView != null) pView.setApplyEnabled(applyEnabled);
	}
	
	public void doStop(){
		if(probe != null) probe.clearCalibrationListener();
		if(pb != null) pb.stop();
		if(calTable != null) calTable.setClickable(true);
		deviation = 0.0f;
		devControl.setValue(deviation);
		drawDeviation();
		if((calTable != null) && (calibratedRows != null)){
			calTable.setClickable(true);
			int selIndex = calTable.getSelectIndex() - 1;
			if(selIndex >= 0 && selIndex < calibratedRows.length){
				if(calibratedRows[selIndex] == 0) calibratedRows[selIndex] = 1;
			}
		}
		checkApplyEnabled();
	}
	public void doStart(){
		//		dataPointer = 0;//FFT
		deviation = 100.0f;
		totalSumm = 0.0f;
		totalSamples = 0;
		if(probe != null) probe.setCalibrationListener(this);
		if(pb != null) pb.start();
		deviation = 100.0f;
		devControl.setValue(deviation);
		drawDeviation();
		if((calTable != null) && (calibratedRows != null)){
			calTable.setClickable(false);
			int selIndex = calTable.getSelectIndex() - 1;
			if(selIndex >= 0 && selIndex < calibratedRows.length){
				calibratedRows[selIndex] = 0;
			}
		}
	}
 	public void onEvent(waba.ui.Event event){
		if (event.type == PenEvent.PEN_UP && (calTable != null)){
			bStart.setEnabled(calTable.getSelectIndex() > 0);
		}
		if (event.type == waba.ui.ControlEvent.PRESSED){
			if(event.target instanceof CCButton){
				if (event.target == bStart){
					doStart();
				}else if (event.target == bStop){
					doStop();
				}

				if(listener != null){
					String message = ((CCButton)event.target).getText();
					ActionEvent ae = new ActionEvent(this,null,message);
					listener.actionPerformed(ae);
				}

				return;
			}
		}
  	}

  	public void drawDeviation(){
  		drawDevControlCounter++;
  		if(drawDevControlCounter != 1){
  			if(drawDevControlCounter == 10) drawDevControlCounter = 0;
  			return;
  		}
  		if(devControl == null) return;
		waba.fx.Graphics g = devControl.createGraphics();
  		if(g != null){
  			devControl.onPaint(g);
  			g.free();
  		}
  	}
  	
	public void dataStreamEvent(DataEvent dataEvent)
	{		
	}

	public void dataReceived(DataEvent dataEvent){
		float[] data = dataEvent.getData();
		float t0 = dataEvent.getTime();
		float dt = dataEvent.getDataDesc().getDt();
		int    chPerSample = dataEvent.getDataDesc().getChPerSample();
		Object cell = null;

		if(data != null && calTable != null){
			int selIndex = calTable.getSelectIndex();
			
			cell = calTable.getCell(nChannels,selIndex);
			if((cell != null) && (cell instanceof CCLabel)){
				((CCLabel)cell).setText(""+data[0],false);
			}
			cell = calTable.getCell(0,selIndex);
			if((cell != null) && (cell instanceof CCLabel)){
				((CCLabel)cell).setText(""+data[1],false);
			}
			if(nChannels == 2 && data.length > 1){
				cell = calTable.getCell(1,selIndex);
				if((cell != null) && (cell instanceof CCLabel)){
					((CCLabel)cell).setText(""+data[2],false);
				}
			}

  			drawTableCounter++;
  			if(drawTableCounter != 1){
  				if(drawTableCounter == 4) drawTableCounter = 0;
  			}else{
				waba.fx.Graphics g = calTable.createGraphics();
				if(g != null){
					calTable.onPaint(g);
					g.free();
				}
			}
		}

		int ndata = dataEvent.getNumbSamples()*dataEvent.getDataDesc().getChPerSample();
		int nOffset = dataEvent.getDataOffset();
		float  dtChannel = dt / (float)chPerSample;
		if(dataEvent.getNumbSamples() > 0){
		    totalSumm += data[nOffset];
		    totalSamples++;
		    if(totalSamples > 1){
				float av = (totalSumm/totalSamples);
				//				System.out.println("data[nOffset+i] "+data[nOffset+i]+" totalSumm "+ totalSumm + " av "+av+" totalSamples "+totalSamples);
				deviation = 100.0f*(data[nOffset] - av)/av;
				//				System.out.println("data["+i+"]="+data[nOffset+i]);
				if(deviation > 100.0f) deviation = 100.0f;
				if(totalSamples > 16){
					totalSamples = 1;
					totalSumm = av;
				}
		    }
		}
		//		System.out.println("deviation "+deviation);
		//		System.out.println("totalSamples "+totalSamples);
		//		System.out.println("av "+(totalSumm/totalSamples));

		devControl.setValue(deviation);
		drawDeviation();


	}
}


class DeviationControl extends Container{
	float value = 0.0f;
	float maxValue = 100.0f;
	ImagePane ip = null;
	waba.fx.Image bufIm = null;
	public DeviationControl(float maxValue){
		this.maxValue = Maths.abs(maxValue);
		ip = (ImagePane) new ImagePane("deviation.bmp");
		ip.setRect(0,0,23,23);
		add(ip);
	}
	public void setValue(float value){
		this.value = value;
		if(value > maxValue) this.value = maxValue;
		if(value < - maxValue) this.value = -maxValue;
		//		System.out.println("deviation "+value);
	}
	
	public void onPaint(waba.fx.Graphics g){
		if(g == null) return;
		if(bufIm == null) bufIm=new waba.fx.Image(width,height);
		waba.fx.Graphics ig = new waba.fx.Graphics(bufIm);
		ig.setColor(0xDE,0xDE,0xDE);
		ig.fillRect(0,0,width,height);
		ig.setColor(255,0,0);
		ig.drawLine(13 + width/2-1,0,13 + width/2-1,height - 1);
		ig.drawLine(13 + width/2,0,13 + width/2,height - 1);
		ig.drawLine(13 + width/2+1,0,13 + width/2+1,height - 1);
		
		float fw = (float)(width/2 - 13 - 10)*value/maxValue;
		int w = Maths.round(fw);
		ig.setColor(0,0,255);
		if(w > 0){
			ig.fillRect(13 + width/2 + 2,2,w,height - 4);
		}else{
			ig.fillRect(13 + width/2 - 1 + w,2,-w,height - 4);
		}		
		ig.setColor(0,0,0);
		ip.onPaint(ig);
		g.copyRect(bufIm,0,0,width,height,0,0);
		ig.free();
	}
	
}
