package org.prevayler.foundation.serialization;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;

import java.io.Reader;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class XStreamDeserializer implements Deserializer {

	private XStream _xstream;
	private Reader _reader;

	public XStreamDeserializer(XStream xstream, InputStream stream) {
		_xstream = xstream;
		_reader = new BufferedReader(new InputStreamReader(stream));
	}

	public Object readObject() throws IOException {
		try {
			return _xstream.fromXML(_reader);
		} catch (StreamException se) {
			throw new IOException("Unable to deserialize with XStream: " + se.getMessage());
		}
	}

}
