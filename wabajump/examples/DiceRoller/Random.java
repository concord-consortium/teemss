import waba.sys.Time;

//Kindly received this class from Clay Helberg (clavius@mail.com)

class Random {
/*
 Class implementing pseudorandom numbers, based loosely on "Numerical
Methods" by
 Dahlquist, Bjork and Anderson
*/

	static final int A = 0x40000000;
	static final int B = 5;
	static final int lambda = A + B;
	static final int P = 0x80000000;
	static final int mu = 1;
	int lastRandom;
	
public Random () { // Default constructor uses time as seed number
Time t = new Time();
lastRandom = 	(((((((((t.year * 12)
				 	+ t.month) * 30) 
				    + t.day) * 24)
				    + t.hour) * 60) 
				    + t.minute) * 60)
				    + t.second;
}

public Random (int seed) { // Use fixed seed for repeating series
	lastRandom = seed;
}

public void setSeed (int seed) { // Use fixed seed to start again
	lastRandom = seed;
}

public int getRandom() {
      lastRandom = ((lambda * lastRandom) + B) % P;
      return (lastRandom < 0) ? -lastRandom : lastRandom;
}

public int getRandom(int range) {
       int r = getRandom();
       return r % range;
}
}
