package org.concord.CCProbe;

public interface CCApplHandler
{
	public void registerHandlers(CCApplHandlerListener	l);
	public void addCCApplHandlerListener(CCApplHandlerListener	l);
	public void removeCCApplHandlerListener(CCApplHandlerListener	l);
}
