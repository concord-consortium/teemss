package extra.util;


public class DataDesc{
public int 	chPerSample;
public float 	dt;
public int        dataType = DataSequence;
public final static 	int DataSequence = 0;
public final static 	int DataSeries = 1;

public	int 	unit = 0;

	public DataDesc(){
		this(0.0f,1);
	}
	public DataDesc(float dt,int chPerSample){
		this.dt = dt;
		this.chPerSample = chPerSample;
	}
	
	public void 	setDt(float dt){this.dt = dt;}
	public void	setChPerSample(int chPerSample){this.chPerSample = chPerSample;}
	public void	setDataType(int dataType){this.dataType = dataType;}
	public void	setUnit(int unit){this.unit = unit;}
	
	public float	getDt(){return dt;}
	public int		getChPerSample(){return chPerSample;}
	public int		getDataType(){return dataType;}
	public int		getUnit(){return unit;}
}
