public class PickChannel extends Transform
{
    final static int TRANS_SIZE = 420;
    float [] output = new float [TRANS_SIZE];
    int channel = 1;

    public PickChannel(int chan)
    {
	channel = 1+chan;
    }

    public boolean transform(int num, int size, float data[])
    {
	int endPos = num*size;
	int outPos = 0;

	for(int i=0; i<endPos; i+= size){
	    output[outPos++] = data[i];
	    output[outPos++] = data[i+channel];
	    if(outPos >= TRANS_SIZE){
		next.transform(TRANS_SIZE/2, 2, output);
		outPos = 0;
	    }
	}

	if(outPos > 0)
	    return next.transform(outPos/2, 2, output);
	
	return true;
    }
}
