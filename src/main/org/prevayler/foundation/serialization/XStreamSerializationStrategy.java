package org.prevayler.foundation.serialization;

import org.prevayler.foundation.serialization.AbstractSerializationStrategy;
import org.prevayler.foundation.serialization.Serializer;
import org.prevayler.foundation.serialization.Deserializer;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;

import java.io.OutputStream;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;

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
