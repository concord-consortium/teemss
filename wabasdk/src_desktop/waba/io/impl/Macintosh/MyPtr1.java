/*
Copyright (c) 2001 Concord Consortium  All rights reserved.
*/

package waba.io.impl;


public class MyPtr1 extends com.apple.mrj.jdirect.PointerStruct{
	public MyPtr1(int p){
		super(p);
	}
	public int getSize(){
		return  JDirectImpl.GetPtrSize(pointer);
	}
}

