package com.hpccsystems.etl.event;

import org.springframework.context.ApplicationEvent;

public class LoadableDataReceivedEvent extends ApplicationEvent  {

	private static final long serialVersionUID = 1L;

	private byte[] data;

	public LoadableDataReceivedEvent(Object source) {
		super(source);

	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}


}
