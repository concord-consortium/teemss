package org.concord.waba.extra.event;
import extra.util.DataDesc;

public class DataEvent extends waba.ui.Event{
public static final int DATA_RECEIVED 		= 1000;
public static final int DATA_COLLECTING 	= 1001;
public static final int DATA_READY_TO_START = 1002;
public float 	[]data = null;
public DataDesc	dataDesc = null;
public int		numbSamples = 1;
public int		dataOffset = 0;
public float		time;

    public int [] pTimes = new int [10];
    public int numPTimes = 0;

	public DataEvent(){
		this(0.0f);
	}
	public DataEvent(float time){
		this(DATA_RECEIVED,time,null,null);
	}
	public DataEvent(int type,float time){
		this(type,0.0f,null,null);
	}
	public DataEvent(int type,float time,float[] data,DataDesc dataDesc){
		this.time = time;
		this.type = type;
		this.data = data;
		this.dataDesc = dataDesc;
	}
	
	public void setData(float[] data){this.data = data;}
	public void setDataDesc(DataDesc dataDesc){ this.dataDesc = dataDesc;}
	public void setType(int type){this.type = type;}
	public void setTime(float time){this.time = time;}
	public void setNumbSamples(int numbSamples){this.numbSamples = numbSamples;}
	public void setDataOffset(int dataOffset){this.dataOffset = dataOffset;}
	
	public float[] getData(){return data;}
	public DataDesc getDataDesc(){return dataDesc;}
	public int getType(){return type;}
	public float getTime(){return time;}
	public int getNumbSamples(){return numbSamples;}
	public int getDataOffset(){return dataOffset;}
}
