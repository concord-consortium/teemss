public class AvgWindow extends Transform
{
    float curTemp;
    float [] output = new float [2];
    int winSize;

    float [] queue = null;
    int curIndex = 0;

    public AvgWindow()
    {
	this(10);
    }
    
    public AvgWindow(int s)
    {
	winSize = s;
	queue = new float [s];
    }

    public boolean transform(int num, int size, float data[])
    {
	for(int i=0; i< num*size; i+=size){
	    queue[curIndex++] = data[i+1];
	    if(curIndex == winSize){
		float total = 0f;
		for(int j=0; j<10; j++){
		    total += queue[j];
		}
		
		output[0] = data[i];
		output[1] = total / (float)winSize;
		
		curIndex = 0;
		next.transform(1, 2, output);
	    }
	}
	return true;
    }
}
