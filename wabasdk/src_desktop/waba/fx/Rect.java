/*

Copyright (c) 1998, 1999, 2000 Wabasoft  All rights reserved.



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



package waba.fx;



/**

 * Rect is a rectangle.

 */



public class Rect

{

/** x position */

public int x;

/** y position */

public int y;

/** rectangle width */

public int width;

/** rectangle height */

public int height;



/** Constructs a rectangle with the given x, y, width and height. */

public Rect(int x, int y, int width, int height)

	{

	this.x = x;

	this.y = y;

	this.width = width;

	this.height = height;

	}

}