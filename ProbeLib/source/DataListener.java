package org.concord.waba.extra.event;
public interface DataListener{
	public void dataReceived(DataEvent dataEvent);

	public void dataStreamEvent(DataEvent dataEvent);
}

