/*
Copyright (c) 2001 Concord Consortium  All rights reserved.
*/

package waba.io.impl;


public class MyPtr extends com.apple.mrj.macos.toolbox.Ptr{
    public MyPtr(byte ab[])
    {
    	super(ab);
    }
    public void freePointer(){
    	super.freePointer();
    }
}

