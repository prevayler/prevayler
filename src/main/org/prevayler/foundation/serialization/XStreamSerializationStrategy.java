package org.prevayler.foundation.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.thoughtworks.xstream.XStream;


public class XStreamSerializationStrategy extends AbstractSerializationStrategy {

	private XStream _xstream;

	public XStreamSerializationStrategy() {
		this(new XStream());
	}

	public XStreamSerializationStrategy(XStream xstream) {
		_xstream = xstream;
	}

	public Serializer createSerializer(OutputStream stream) throws IOException {
		return new XStreamSerializer(_xstream, stream);
	}

	public Deserializer createDeserializer(InputStream stream) throws IOException {
		return new XStreamDeserializer(_xstream, stream);
	}

}
