/*
Copyright (c) 2001 Concord Consortium  All rights reserved.
*/

package waba.io.impl;


public class MyHandle extends com.apple.mrj.jdirect.HandleStruct{
	public MyHandle(int h){
		super(h);
	}
	public int getSize(){
		return  JDirectImpl.GetHandleSize(handle);
	}
}

