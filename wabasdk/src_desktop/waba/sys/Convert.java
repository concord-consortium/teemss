/*

Copyright (c) 1998, 1999 Wabasoft  All rights reserved.



This software is furnished under a license and may be used only in accordance

with the terms of that license. This software and documentation, and its

copyrights are owned by Wabasoft and are protected by copyright law.



THIS SOFTWARE AND REFERENCE MATERIALS ARE PROVIDED "AS IS" WITHOUT WARRANTY

AS TO THEIR PERFORMANCE, MERCHANTABILITY, FITNESS FOR ANY PARTICULAR PURPOSE,

OR AGAINST INFRINGEMENT. WABASOFT ASSUMES NO RESPONSIBILITY FOR THE USE OR

INABILITY TO USE THIS SOFTWARE. WABASOFT SHALL NOT BE LIABLE FOR INDIRECT,

SPECIAL OR CONSEQUENTIAL DAMAGES RESULTING FROM THE USE OF THIS PRODUCT.



WABASOFT SHALL HAVE NO LIABILITY OR RESPONSIBILITY FOR SOFTWARE ALTERED,

MODIFIED, OR CONVERTED BY YOU OR A THIRD PARTY, DAMAGES RESULTING FROM

ACCIDENT, ABUSE OR MISAPPLICATION, OR FOR PROBLEMS DUE TO THE MALFUNCTION OF

YOUR EQUIPMENT OR SOFTWARE NOT SUPPLIED BY WABASOFT.

*/



package waba.sys;



/**

 * Convert is used to convert between objects and basic types.

 */



public class Convert

{

private Convert()

	{

	}



/**

 * Converts the given String to an int. If the string passed is not a valid

 * integer, 0 is returned.

 */



public static int toInt(String s)

	{

	int i = 0;

	try { i = java.lang.Integer.parseInt(s); }

		catch (Exception e) {}

	return i;

	}



/** Converts the given boolean to a String. */



public static String toString(boolean b)

	{

	return "" + b;

	}



/** Converts the given char to a String. */



public static String toString(char c)

	{

	return "" + c;

	}



/** Converts the given float to its bit representation in IEEE 754 format. */



public static int toIntBitwise(float f)

	{

	return Float.floatToIntBits(f);

	}



/** Converts the given IEEE 754 bit representation of a float to a float. */



public static float toFloatBitwise(int i)

	{

	return Float.intBitsToFloat(i);

	}





/** Converts the given float to a String. */



public static String toString(float f)

	{

	return java.lang.Float.toString(f);

	}



/** Converts the given int to a String. */



public static String toString(int i)

	{

	return java.lang.Integer.toString(i);

	}

}

