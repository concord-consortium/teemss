import graph.*;

public class GraphUpdater extends Transform
{
    int bin = 0;
    boolean update = false;
    AnnotView lg;
    GraphTool gt;

    public GraphUpdater(AnnotView av, GraphTool gt)
    {
	lg = av;
	this.gt = gt;
    }
    
    public boolean transform(int num, int size, float data []){
	if(lg.active){

	    if(!lg.addPoints(0, num, data)){
		gt.stop();
		lg.curView.draw();
		return false;		
	    }
	}
	return true;
    }

}
