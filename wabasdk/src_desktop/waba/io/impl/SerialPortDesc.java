package waba.io.impl;

public class SerialPortDesc{
public int 	index = -1;
public String	inpName;
public String	outName;
public String	name;
	public SerialPortDesc(int index,String inpName,String outName,String name){
		this.index = index;
		this.inpName = inpName;
		this.outName = outName;
		this.name = name;
	}
	
   	 public boolean equals(Object obj) {
		if(super.equals(obj)) return true;
		if(!(obj instanceof SerialPortDesc)) return false;
		SerialPortDesc p = (SerialPortDesc)obj;
		return (p.name.equals(name) && p.inpName.equals(inpName) && p.outName.equals(outName));
	}
}



