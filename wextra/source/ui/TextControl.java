package org.concord.waba.extra.ui;

public interface TextControl
{
	public void saveLineIndex();
	public String getLineAtSavedIndex();
	public void setLineAtSavedIndex(String line);
}
