package org.concord.CCProbe;



public class CCApplHandlerFactory
{
	public static CCApplHandler getCCApplHandler(){
		String plat = waba.sys.Vm.getPlatform();
		if(!plat.equals("Java")) return null;
		boolean macOS = System.getProperty("os.name").startsWith("Mac OS");
		if(!macOS) return null;
		return new CCMacApplHandler();
	}
}
