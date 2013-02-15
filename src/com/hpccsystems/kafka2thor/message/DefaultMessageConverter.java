package com.hpccsystems.kafka2thor.message;

import java.nio.ByteBuffer;

import kafka.message.Message;

public class DefaultMessageConverter implements MessageConverter {

	public byte[] convertToBytes(Message message)  {
	    ByteBuffer buffer = message.payload();
	    byte [] bytes = new byte[buffer.remaining()];
	    buffer.get(bytes);
	    return bytes; 
	  }
}
