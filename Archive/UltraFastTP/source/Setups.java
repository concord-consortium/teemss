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
public class Setups
{
    static String [] buttonNames = {"Thermocouple", 
				    "Smart Wheel Velo",
				    "Smart Wheel Pos",
				    "24-bit Ch. 0",
				    "24-bit Ch. 1",
				    "10-bit Ch. 0",
				    "10-bit Ch. 1",
				    "10-bit Ch. 0 and 1",
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
	    uf.setup(100f, -30f, 3300f, pc, pc, "mV", 2);
	    break;
	case 6:
	    pc = new PickChannel(1);
	    uf.setup(100f, -30f, 3300f, pc, pc, "mV", 2);
	    break;
	case 7:
	    CombineChannels cc = new CombineChannels(0.0025f);
	    uf.setup(100f, -30f, 3300f, cc, cc, "mV", 2);
	    break;
	case 8:
	    uf.removeAll();
	    About about = new About(uf, 140, 140);
	    about.setRect((uf.getRect().width-140)/2,
			  (uf.getRect().height-140)/2,140,140);
	    uf.add(about);
	    break;
	case 9:
	    uf.exit(1);
	    break;
	}
    }
}
