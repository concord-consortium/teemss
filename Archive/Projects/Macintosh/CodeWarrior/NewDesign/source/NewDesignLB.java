import waba.io.SerialPort;
import waba.sys.Vm;
import waba.ui.*;
import org.concord.waba.extra.event.*;
import org.concord.waba.extra.probware.ProbManager;
import org.concord.waba.extra.probware.CCInterfaceManager;
import org.concord.waba.extra.probware.probs.*;
import extra.util.*;
import org.concord.waba.extra.ui.*;
import org.concord.LabBook.*;

public class NewDesignLB extends ExtraMainWindow implements DialogListener, DataListener, ProbManagerListener{
Button startButton, stopButton,exitButton;
Button calButton;

LObjProbeDataSource dataSource = null;

	public static void main(String []args){
		new NewDesignLB();
	}
	public NewDesignLB(){
		


		
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
		
		
		dataSource = LObjProbeDataSource.getProbeDataSource(ProbFactory.Prob_ThermalCouple,CCInterfaceManager.INTERFACE_2,CCProb.INTERFACE_PORT_A);
		((LObjProbeDataSource)dataSource).addProbManagerListener(this);
		dataSource.addDataListener(this);	
/*
		pb = ProbManager.getProbManager(CCInterfaceManager.INTERFACE_2);
//		pb = ProbManager.getProbManager(CCInterfaceManager.INTERFACE_0);
		pb.addProbManagerListener(this);
		probe = ProbFactory.createProb(ProbFactory.Prob_ThermalCouple,CCProb.INTERFACE_PORT_A);
		probe.setPropertyValue(1,CCThermalCouple.tempModes[CCThermalCouple.FAHRENHEIT_TEMP_OUT]);
//		probe = ProbFactory.createProb(ProbFactory.Prob_SmartWheel,CCProb.INTERFACE_PORT_A);
//		probe.setPropertyValue(1,CCSmartWheel.wheelModes[CCSmartWheel.ANG_MODE_OUT]);
//		probe = ProbFactory.createProb(ProbFactory.Prob_Force,CCProb.INTERFACE_PORT_A);

//		probe = ProbFactory.createProb(ProbFactory.Prob_RawData,CCProb.INTERFACE_PORT_A);
		
		pb.registerProb(probe);
		pb.addDataListenerToProb(probe.getName(),this);
		
		System.out.println("number of properties "+probe.countProperties());
		for(int i = 0; i < probe.countProperties();i++){
			PropObject po = probe.getProperty(i);
			System.out.println("property[ "+i+"]="+po.getName());
		}
//		pb.registerProb(ProbFactory.createProb(ProbFactory.Prob_Light));
//		pb.addDataListenerToProb("light",this);

*/		

	}
   	public void actionPerformed(org.concord.waba.extra.event.ActionEvent e){
    	}
	public void onEvent(Event event){
		if (event.type == ControlEvent.PRESSED){
			if (event.target == exitButton){
				closeEverything();
				exit(0);
			}else if (event.target == startButton){
				if(dataSource != null) dataSource.startDataDelivery();
			}else if (event.target == stopButton){
				if(dataSource != null) dataSource.stopDataDelivery();
			}else if(event.target == calButton){
				if(dataSource != null) dataSource.calibrateMe(this,this);
			}
		}
	}
	
	public void dataReceived(DataEvent dataEvent){
		float[] data = dataEvent.getData();
		float t0 = dataEvent.getTime();
		float dt = dataEvent.getDataDesc().getDt();
		int    chPerSample = dataEvent.getDataDesc().getChPerSample();

		int ndata = dataEvent.getNumbSamples()*chPerSample;
		int nOffset = dataEvent.getDataOffset();
		float  dtChannel = dt / (float)chPerSample;
		float tuneValue = dataEvent.getDataDesc().getTuneValue();
		System.out.println("ndata "+ndata);
		for(int i = 0; i < ndata; i+=chPerSample){
			float t = t0 + dtChannel*(float)i;
			System.out.println("time "+t+"; temp "+data[nOffset+i]);
		}
	}

	public void closeEverything(){
		if(dataSource != null){
			dataSource.closeEverything();
			dataSource = null;
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
	
    	public void pmRegistration(ProbManagerEvent e){
    		System.out.println(e);
    	}
    	public void pmActionPerformed(ProbManagerEvent e){
     		System.out.println(e);
   	}
	
	
	
}








