public class Setups
{
    static String [] buttonNames = {"Thermocouple", 
				    "Smart Wheel Velo",
				    "Smart Wheel Pos",
				    "24-bit Ch. 0",
				    "24-bit Ch. 1",
				    "10-bit Ch. 0",
				    "10-bit Ch. 1",
				    "About",
				    "Exit"};
    static String logName = "data.txt";

    static void setup(int buttonId, UltraFastTP uf)
    {
	PickChannel pc;

	switch (buttonId){
	case 0: 
	    ThermalCouple tCouple = new ThermalCouple();
	    uf.setup(100f, 10f, 40f, tCouple, tCouple, "°C", 1);
	    break;
	case 1:
	    AvgWindow avgW = new AvgWindow(10);
	    uf.setup(30f, -20f, 50f, avgW, avgW, "m/10s", 3);
	    break;
	case 2:
	    Accumulator acc = new Accumulator();
	    uf.setup(30f, -400f, 400f, acc, acc, "mm", 3);
	    break;
	case 3:
	    pc = new PickChannel(0);
	    uf.setup(100f, -30f, 100f, pc, pc, "mV", 1);
	    break;
	case 4:
	    pc = new PickChannel(1);
	    uf.setup(100f, -30f, 100f, pc, pc, "mV", 1);
	    break;
	case 5:
	    pc = new PickChannel(0);
	    uf.setup(100f, -30f, 100f, pc, pc, "mV", 2);
	    break;
	case 6:
	    pc = new PickChannel(1);
	    uf.setup(100f, -30f, 100f, pc, pc, "mV", 2);
	    break;
	case 7:
	    uf.removeAll();
	    About about = new About(uf, 140, 140);
	    about.setRect((uf.getRect().width-140)/2,
			  (uf.getRect().height-140)/2,140,140);
	    uf.add(about);
	    break;
	case 8:
	    uf.exit(1);
	    break;
	}
    }
}
