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

package waba.applet;

import waba.ui.*;
import java.awt.event.KeyListener;

public class WinCanvas extends java.awt.Canvas implements KeyListener 
{
	Window win;


	public WinCanvas(Window win)
	{
		this.win = win;
	}

	public void keyPressed(java.awt.event.KeyEvent event){
		int id = (event.isActionKey())?java.awt.Event.KEY_ACTION:java.awt.Event.KEY_PRESS;
		int key = (id == java.awt.Event.KEY_ACTION)?convertNewKeyToOld(event.getKeyCode()):event.getKeyChar();
		handleKeyEvent(new java.awt.Event(event.getSource(),event.getWhen(),id,0,0,key,event.getModifiers(),null));
	}

	public void keyReleased(java.awt.event.KeyEvent event){
		int id = (event.isActionKey())?java.awt.Event.KEY_ACTION_RELEASE:java.awt.Event.KEY_RELEASE;
		int key = (id == java.awt.Event.KEY_ACTION_RELEASE)?convertNewKeyToOld(event.getKeyCode()):event.getKeyChar();
		handleKeyEvent(new java.awt.Event(event.getSource(),event.getWhen(),id,0,0,key,event.getModifiers(),null));
	}

	private static int convertNewKeyToOld(int newkey){
		int oldkey = newkey;
			switch(newkey){
				case java.awt.event.KeyEvent.VK_PAGE_UP: 	oldkey = java.awt.Event.PGUP; break;
				case java.awt.event.KeyEvent.VK_PAGE_DOWN: 	oldkey = java.awt.Event.PGDN; break;
				case java.awt.event.KeyEvent.VK_HOME:		oldkey = java.awt.Event.HOME; break;
				case java.awt.event.KeyEvent.VK_END:	 	oldkey = java.awt.Event.END; break;
				case java.awt.event.KeyEvent.VK_UP:			oldkey = java.awt.Event.UP; break;
				case java.awt.event.KeyEvent.VK_DOWN: 		oldkey = java.awt.Event.DOWN; break;
				case java.awt.event.KeyEvent.VK_LEFT: 		oldkey = java.awt.Event.LEFT; break;
				case java.awt.event.KeyEvent.VK_RIGHT: 		oldkey = java.awt.Event.RIGHT; break;
				case java.awt.event.KeyEvent.VK_INSERT: 	oldkey = java.awt.Event.INSERT; break;
				case java.awt.event.KeyEvent.VK_ENTER:		oldkey = java.awt.Event.ENTER; break;
				case java.awt.event.KeyEvent.VK_TAB: 		oldkey = java.awt.Event.TAB; break;
				case java.awt.event.KeyEvent.VK_BACK_SPACE: oldkey = java.awt.Event.BACK_SPACE; break;
				case java.awt.event.KeyEvent.VK_ESCAPE: 	oldkey = java.awt.Event.ESCAPE; break;
				case java.awt.event.KeyEvent.VK_DELETE: 	oldkey = java.awt.Event.DELETE; break;
				case java.awt.event.KeyEvent.VK_F6: 		oldkey = java.awt.Event.F6; break;
				case java.awt.event.KeyEvent.VK_F7: 		oldkey = java.awt.Event.F7; break;
			}
			return oldkey;
	
	
	} 


	public void keyTyped(java.awt.event.KeyEvent event){}


	public boolean handleKeyEvent(java.awt.Event event){
		int type = 0;
		int key = 0;
		int x = 0;
		int y = 0;
		int modifiers = 0;
		if ((event.modifiers & java.awt.Event.SHIFT_MASK) > 0){
			System.out.println("SHIFT_MASK");
			modifiers |= IKeys.SHIFT;
		}
		
		if ((event.modifiers & java.awt.Event.CTRL_MASK) > 0){
			System.out.println("CTRL_MASK");
			modifiers |= IKeys.CONTROL;
		}
		
		if ((event.modifiers & java.awt.Event.ALT_MASK) > 0){
			System.out.println("ALT_MASK");
			modifiers |= IKeys.ALT;
		}
		if ((event.id == java.awt.Event.KEY_PRESS) && ((event.modifiers & java.awt.Event.META_MASK) > 0)){
			System.out.println("META_MASK");
			modifiers |= IKeys.CONTROL;//dima command macintosh
		}

		boolean doPostEvent = false;
		switch (event.id)
			{
			case java.awt.Event.KEY_PRESS:
				type = KeyEvent.KEY_PRESS;
				System.out.println("WC: key_press event.key: " + event.key);
				key = keyValue(event.key, modifiers);
				doPostEvent = true;
				break;
			case java.awt.Event.KEY_ACTION:
				{
					System.out.println("WC: key_action event.key: " + event.key);
					key = actionKeyValue(event.key);
					if (key != 0)
						{
							type = KeyEvent.KEY_PRESS;
							doPostEvent = true;
						}
					break;
				}
			}
		if (doPostEvent)
			{
				int timestamp = (int)event.when;
				synchronized(Applet.uiLock)
					{
						win._postEvent(type, key, x, y, modifiers, timestamp);
					}
			}
		return super.handleEvent(event);
	}


	public boolean handleEvent(java.awt.Event event)
	{
		int type = 0;
		int key = 0;
		int x = 0;
		int y = 0;
		int modifiers = 0;
		if ((event.modifiers & java.awt.Event.SHIFT_MASK) > 0){
			System.out.println("SHIFT_MASK");
			modifiers |= IKeys.SHIFT;
		}
		
		if ((event.modifiers & java.awt.Event.CTRL_MASK) > 0){
			System.out.println("CTRL_MASK");
			modifiers |= IKeys.CONTROL;
		}
		
		if ((event.modifiers & java.awt.Event.ALT_MASK) > 0){
			System.out.println("ALT_MASK");
			modifiers |= IKeys.ALT;
		}
		if ((event.id == java.awt.Event.KEY_PRESS) && ((event.modifiers & java.awt.Event.META_MASK) > 0)){
			System.out.println("META_MASK");
			modifiers |= IKeys.CONTROL;//dima command macintosh
		}

		boolean doPostEvent = false;
		switch (event.id)
			{
			case java.awt.Event.MOUSE_MOVE:
			case java.awt.Event.MOUSE_DRAG:
				type = PenEvent.PEN_MOVE;
				x = event.x;
				y = event.y;
				doPostEvent = true;
				break;
			case java.awt.Event.MOUSE_DOWN:
				System.out.println("WC: MOUSE_DOWN event.key: " + event.key);
				type = PenEvent.PEN_DOWN;
				x = event.x;
				y = event.y;
				doPostEvent = true;
				break;
			case java.awt.Event.MOUSE_UP:
				type = PenEvent.PEN_UP;
				x = event.x;
				y = event.y;
				doPostEvent = true;
				break;
/*
			case java.awt.Event.KEY_PRESS:
				type = KeyEvent.KEY_PRESS;
				System.out.println("WC: key_press event.key: " + event.key);
				key = keyValue(event.key, modifiers);
				doPostEvent = true;
				break;
			case java.awt.Event.KEY_ACTION:
				{
					System.out.println("WC: key_action event.key: " + event.key);
					key = actionKeyValue(event.key);
					if (key != 0)
						{
							type = KeyEvent.KEY_PRESS;
							doPostEvent = true;
						}
					break;
				}
*/
			}
		if (doPostEvent)
			{
				int timestamp = (int)event.when;
				synchronized(Applet.uiLock)
					{
						win._postEvent(type, key, x, y, modifiers, timestamp);
					}
			}
		return super.handleEvent(event);
	}

	public static int keyValue(int key, int mod)
	{
		switch (key)
			{
			case 8:
			case 65288:   // this is hack that works on linux (don't know why)
				key = IKeys.BACKSPACE;
				break;
			case 10:
				key = IKeys.ENTER;
				break;
			case 127:
			case 65535:  // this is hack that works on linux (don't know why)
				key = IKeys.DELETE;
				break;
			}
		return key;
	}

	public static int actionKeyValue(int action)
	{
		int key = 0;
		switch (action)
			{
			case java.awt.Event.PGUP:       key = IKeys.PAGE_UP; break;
			case java.awt.Event.PGDN:       key = IKeys.PAGE_DOWN; break;
			case java.awt.Event.HOME:       key = IKeys.HOME; break;
			case java.awt.Event.END:        key = IKeys.END; break;
			case java.awt.Event.UP:         key = IKeys.UP; break;
			case java.awt.Event.DOWN:       key = IKeys.DOWN; break;
			case java.awt.Event.LEFT:       key = IKeys.LEFT; break;
			case java.awt.Event.RIGHT:      key = IKeys.RIGHT; break;
			case java.awt.Event.INSERT:     key = IKeys.INSERT; break;
			case java.awt.Event.ENTER:      key = IKeys.ENTER; break;
			case java.awt.Event.TAB:        key = IKeys.TAB; break;
			case java.awt.Event.BACK_SPACE: key = IKeys.BACKSPACE; break;
			case java.awt.Event.ESCAPE:     key = IKeys.ESCAPE; break;
			case java.awt.Event.DELETE:     key = IKeys.DELETE; break;
			case java.awt.Event.F6:     	key = IKeys.MENU; break;
			case java.awt.Event.F7:     	key = 76000; break;
			}
		return key;
	}

	public void update(java.awt.Graphics g)
	{
		paint(g);
	}

	public void paint(java.awt.Graphics g)
	{
		java.awt.Rectangle r = null;
		// getClipRect() is missing in the Kaffe distribution for Linux
		try { r = g.getClipBounds(); }
		catch (NoSuchMethodError e) { r = g.getClipRect(); }
		synchronized(Applet.uiLock)
			{
				System.out.println("WinC: paint");
				win._doPaint(r.x, r.y, r.width, r.height);
			}
	}
}

