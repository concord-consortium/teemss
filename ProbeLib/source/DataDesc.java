package extra.util;


public class DataDesc{
	public int 	chPerSample;
	public float 	dt;
	
	public DataDesc(){
		this(0.0f,1);
	}
	public DataDesc(float dt,int chPerSample){
		this.dt = dt;
		this.chPerSample = chPerSample;
	}
	
	public void 	setDt(float dt){this.dt = dt;}
	public void	setChPerSample(int chPerSample){this.chPerSample = chPerSample;}
	public float	getDt(){return dt;}
	public int		getChPerSample(){return chPerSample;}
}
