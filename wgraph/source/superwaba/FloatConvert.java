package graph;

import extra.util.*;

public class FloatConvert
{
    public int minDigits = 1;
    public int maxDigits = 4;

   /**
   * Convert float with correct digits
   *
   * This is very ugly code I'll fix it someday :)
   */
  public String toString(float val)
  {
    // We don't want any exponents printed so we need to do this ourselves.
        int j;
	char intChars[];
	char fltChars[];
	int len, nLen;
	int start=0, end;
	int exp=0;
	int multExp;
	int count;
	String absLabel;

	for(j=0; j<maxDigits; j++) val *= (float)10;
	if(val < 0) val -= (float)0.5;
	else val += (float)0.5;

	if(((int)val) == 0){
	    if(minDigits != 0)
		return new String("0.0");
	    else
		return new String("0");
	}

	intChars = String.valueOf((int)Maths.abs(val)).toCharArray();
	len = intChars.length;

	if(len <= maxDigits){
	    fltChars = new char[maxDigits + 2];
	    fltChars[0] = '0';
	    fltChars[1] = '.';
	    for(j=0; j < maxDigits - len; j++){
		fltChars[2+j] = '0';
	    }
	    start = 2+j;
	    for(j=0; j < len; j++)
		fltChars[start + j] = intChars[j];
	} else {
	    fltChars = new char [len + 1];
	    for(j=0; j < len - maxDigits; j++){
		fltChars[j] = intChars[j];
	    }
	    fltChars[j] = '.';
	    for(; j < len; j++)
		fltChars[j + 1] = intChars[j];
	}

	end = fltChars.length - 1;
	for(j=0; j < maxDigits - minDigits; j++){
	    if(fltChars[end - j] != '0') break;
	}
	

	absLabel = new String(fltChars, 0, fltChars.length - j);

	if(val < 0)
	    return new String("-" + absLabel);
	else 
	    return absLabel;

  }    

    float toFloat(String s)
    {

	// Just returning junk for now
	return waba.sys.Convert.toFloat(s);
    }



}
