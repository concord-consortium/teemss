import waba.io.SerialPort;
import waba.sys.Vm;
import waba.ui.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.probware.ProbManager;
import org.concord.waba.extra.probware.CCInterfaceManager;
import org.concord.waba.extra.probware.probs.*;
import extra.util.*;
import org.concord.waba.extra.ui.*;

public class NewDesign extends ExtraMainWindow implements DialogListener, DataListener{
public static ProbManager pb = null;
Button startButton, stopButton,exitButton;
Button calButton;
int 		dataDim = 128;
float []dataFFT = new float[dataDim*2];
int	dataPointer = 0;
CCProb probe = null;
	public static void main(String []args){
		new NewDesign();
	}
	public NewDesign(){
		startButton = new Button("START");
		startButton.setRect(width/2 - 20 - 40, height/2 - 10, 40, 20);
		add(startButton);
		stopButton = new Button("STOP");
		stopButton.setRect(width/2 +20, height/2 - 10, 40, 20);
		add(stopButton);
		exitButton = new Button("EXIT");
		exitButton.setRect(width/2 -20, height/2 +40, 40, 20);
		add(exitButton);
		calButton = new Button("Calibration");
		calButton.setRect(width/2 -40, height/2 -40, 80, 20);
		add(calButton);
		
		pb = ProbManager.getProbManager();
		probe = CCProb.getCCThermalCoupleProb("thermocouple");
		probe.setPropertyValue(1,CCThermalCouple.tempModes[CCThermalCouple.FAHRENHEIT_TEMP_OUT]);
		pb.registerProb(probe);
		pb.addDataListenerToProb("thermocouple",this);
		
		
		System.out.println("number of properties "+probe.countProperties());
		for(int i = 0; i < probe.countProperties();i++){
			PropObject po = probe.getProperty(i);
			System.out.println("property[ "+i+"]="+po.getName());
		}
//		pb.registerProb(CCProb.getCCLightIntensityProb("light"));
//		pb.addDataListenerToProb("light",this);


	}
   	public void actionPerformed(org.concord.waba.extra.event.ActionEvent e){
    	}
	public void onEvent(Event event){
		if (event.type == ControlEvent.PRESSED){
			if (event.target == exitButton){
				closeEverything();
				exit(0);
			}else if (event.target == startButton){
				dataPointer = 0;
				if(pb != null) pb.start();
			}else if (event.target == stopButton){
				if(pb != null) pb.stop();
			}else if(event.target == calButton){
				CalibrationDialog cDialog = new CalibrationDialog(this,this,"Calibration",probe);
				cDialog.setRect(100,100,160,160);
				cDialog.setContent();
				cDialog.show();
			}
		}
	}
	
	public void dataReceived(DataEvent dataEvent){
		float[] data = dataEvent.getData();
		float t0 = dataEvent.getTime();
		float dt = dataEvent.getDataDesc().getDt();
		int    chPerSample = dataEvent.getDataDesc().getChPerSample();

		int ndata = dataEvent.getNumbData();
		int nOffset = dataEvent.getDataOffset();
		float  dtChannel = dt / (float)chPerSample;
		boolean doFFT = false;
		for(int i = 0; i < ndata; i+=chPerSample){
			float t = t0 + dtChannel*(float)i;
			System.out.println("time "+t+"; temp "+data[nOffset+i]);
			if(!doFFT) dataFFT[dataPointer++] = data[nOffset+i];
			if(dataPointer >= dataDim){
				doFFT = true;
			}
		}
		if(doFFT){
			dataPointer = 0;
			
			float maxData = 0.0f;
			float summ = 0.0f;
			float summ2 = 0.0f;
			for(int k = 0; k < dataDim; k++){
				float d = dataFFT[k];
				summ += d;
				summ2 += d*d;
				if(maxData < d) maxData = d;
			}
			float ave = summ/dataDim;
			for(int k = 0; k < dataDim; k++){
				dataFFT[k] = dataFFT[k] - ave;
			}
			float disp = extra.util.Maths.sqrt(summ2/(float)dataDim - ave*ave)/ave;
			System.out.println("FFT Ave: "+ave+" disp "+disp);
			org.concord.waba.extra.util.FFT.realft(dataFFT,dataDim,1);
			float []normKoeff = new float[dataDim/2];
			
			
			float maxKoeff = 0.0f;
			for(int k = 1; k <= dataDim;k+=2){
				float nk = extra.util.Maths.sqrt(dataFFT[k]*dataFFT[k]+dataFFT[k+1]*dataFFT[k+1]);
				if(k == 1) nk /= 2.0;
				normKoeff[(k - 1)/2] = nk;
				if(nk > maxKoeff) maxKoeff = nk;
			}
			for(int k = 0; k < normKoeff.length ;k++){
				System.out.println("index "+k+"; freq: "+(int)(100.0f*normKoeff[k]/maxKoeff+0.5));
			}
		}
	}

	public void closeEverything(){
		if(pb != null){
			pb.dispose();
			pb = null;
		}
	}
	public void dialogClosed(org.concord.waba.extra.event.DialogEvent e){
		System.out.println("Command "+e.getActionCommand()+" InfoType "+e.getInfoType()+" Info "+e.getInfo());
		if((e.getInfoType() == org.concord.waba.extra.event.DialogEvent.PROPERTIES) && (e.getInfo() instanceof PropContainer)){
			System.out.println("PROPERTIES");
			PropContainer pc = (PropContainer)e.getInfo();
			int nContainers = pc.getNumbPropContainers();
			for(int i = 0; i < nContainers; i++){
				System.out.println("Name "+pc.getPropertiesContainerName(i));
				waba.util.Vector prop = pc.getProperties(i);
				if(prop == null) continue;
				int nProperties = prop.getCount();
				for(int j = 0; j < nProperties; j++){
					PropObject po = (PropObject)prop.get(j);
					System.out.println(po.getName()+" = "+po.getValue());
				}
				System.out.println();
			}
		}
	}
}










