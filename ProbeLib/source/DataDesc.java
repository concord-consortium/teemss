package extra.util;


public class DataDesc{
public int 	chPerSample;
public int 	chIntPerSample;
public float 	dt;
public int        dataType = DataSequence;
public final static 	int DataSequence = 0;
public final static 	int DataSeries = 1;

public	int 	unit = 0;
public  float tuneValue = 1.0f;

	public DataDesc(){
		this(0.0f,1,1);
	}
	public DataDesc(float dt,int chPerSample){
		this(dt,chPerSample,chPerSample);
	}
	
	public DataDesc(float dt,int chPerSample,int chIntPerSample){
		this.dt = dt;
		this.chPerSample 	= chPerSample;
		this.chIntPerSample = chIntPerSample;
	}
	
	public void 	setDt(float dt){this.dt = dt;}
	public void		setChPerSample(int chPerSample){this.chPerSample = chPerSample;}
	public void		setIntChPerSample(int chIntPerSample){this.chIntPerSample = chIntPerSample;}
	public void		setDataType(int dataType){this.dataType = dataType;}
	public void		setUnit(int unit){this.unit = unit;}
	public void 	setTuneValue(float tuneValue){this.tuneValue = tuneValue;}
	
	public float	getDt(){return dt;}
	public float	getTuneValue(){return tuneValue;}
	public int		getChPerSample(){return chPerSample;}
	public int		getIntChPerSample(){return chIntPerSample;}
	public int		getDataType(){return dataType;}
	public int		getUnit(){return unit;}
}
