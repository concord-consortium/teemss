package org.concord.waba.extra.ui;

public class CCApplHandlerFactory
{
	public static CCApplHandler getCCApplHandler(){
		boolean macOS = System.getProperty("os.name").startsWith("Mac OS");
		if(!macOS) return null;
		return new CCMacApplHandler();
	}
}
