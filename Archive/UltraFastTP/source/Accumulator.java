public class Accumulator extends Transform
{
    float [] output = new float [2];
    float total;

    public void start()
    {
	total = 0f;
	super.start();
    }

    public boolean transform(int num, int size, float data[])
    {

	for(int i=0; i< num; i++){
	    total += data[i*size + 1];
	
	    output[0] = data[i*size];
	    output[1] = total;

	    next.transform(1,2, output);
	}

	return true;
    }
}
