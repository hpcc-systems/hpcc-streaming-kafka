package com.hpccsystems.kafka2thor.message;

import kafka.message.Message;

public interface MessageConverter {
	
	public abstract byte[] convertToBytes(Message message);
	
}