package org.prevayler.foundation.serialization;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;

import java.io.Writer;
import java.io.OutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;

public class XStreamSerializer implements Serializer {

	private XStream _xstream;
	private Writer _writer;

	public XStreamSerializer(XStream xstream, OutputStream stream) {
		_xstream = xstream;
		_writer = new BufferedWriter(new OutputStreamWriter(stream));
	}

	public void writeObject(Object object) throws IOException {
		try {
			_xstream.toXML(object, _writer);
		} catch (StreamException se) {
			throw new IOException("Unable to serialize with XStream: " + se.getMessage());
		}
	}

	public void flush() throws IOException {
		_writer.flush();
	}

}
