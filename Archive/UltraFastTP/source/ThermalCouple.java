public class ThermalCouple extends Transform
{
    float curTemp;
    float [] output = new float [2];

    public final static float temperature (float mV)
    {

	float mV2 = mV * mV;
	float mV3 = mV2 * mV;
	return mV * (float)17.084 + mV2 * (float)-0.25863 + mV3 * (float)0.011012;
    }

    public boolean transform(int num, int size, float data[])
    {
	float lastColdJunct;
	int endPos = num*size;

	for(int i=0; i<endPos; i+= size){
	    lastColdJunct = (data[i+2] / 10) - (float)50;
	    output[1] = curTemp = temperature(data[i+1]) + lastColdJunct;
	    output[0] = data[i];
	    next.transform(1, 2, output);
	}

	return true;
    }
}
