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

import waba.applet.Applet;

public class Frame extends java.awt.Frame
{
waba.ui.Window wabaWindow = null;
	void setWabaWindow(waba.ui.Window wabaWindow){
		this.wabaWindow = wabaWindow;
		System.out.println("Fr: setWabaWindow: " + wabaWindow);
		//		addKeyListener(wabaWindow);
	}

	public void addNotify(){
		super.addNotify();
	    java.awt.Insets insets 	= getInsets();
	    java.awt.Dimension df 	= getSize();
		setSize(df.width + insets.left + insets.right,df.height + insets.top + insets.bottom);
		java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		df = getSize();
		
		setLocation(d.width/2 - df.width/2,d.height/2 - df.height/2);
		
		if(wabaWindow != null) wabaWindow.readyForRegistering();

	}
    
    int visWidth = -1;
    int visHeight = -1;
    public void setVisibleSize(int w, int h)
    {
	visWidth = w;
	visHeight = h;
    }
    public void paint(java.awt.Graphics g)
    {
		if(visWidth != -1){
		    java.awt.Insets insets = getInsets();
		    setSize(visWidth+insets.right+insets.left,visHeight+insets.top+insets.bottom);
		    visWidth = -1;
		}
    }
}
