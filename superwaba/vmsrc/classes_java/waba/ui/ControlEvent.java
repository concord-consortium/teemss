package waba.ui;

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

import waba.sys.*;

/**
 * ControlEvent is an event posted by a control.
 */

public class ControlEvent extends Event
{
/** The event type for a pressed event. */
public static final int PRESSED = 300;
/** The event type for a focus in event. */
public static final int FOCUS_IN = 301;
/** The event type for a focus out event. */
public static final int FOCUS_OUT = 302;
/** The event type for a timer event. */
public static final int TIMER = 303;
/** The event type for a closing window. */
public static final int WINDOW_CLOSED = 499;

public ControlEvent()
	{
	}
/**
 * Constructs a control event of the given type.
 * @param type the type of event
 * @param c the target control
 */
public ControlEvent(int type, Control c)
	{
	this.type = type;
	target = c;
	timeStamp = Vm.getTimeStamp();
	}
}