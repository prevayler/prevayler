package org.prevayler.foundation.serialization;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class XStreamDeserializer implements Deserializer {

	static final String BOUNDARY = "\n<!--PREVAYLER RULES!-->\n";

	private XStream _xstream;
	private BufferedReader _reader;

	public XStreamDeserializer(XStream xstream, InputStream stream) {
		_xstream = xstream;
		_reader = new BufferedReader(new InputStreamReader(stream));
	}

	public Object readObject() throws IOException {
		try {
			_reader.mark(1);
			if (_reader.read() == -1) {
				throw new EOFException();
			}
			_reader.reset();

			return _xstream.fromXML(new BoundaryReader());
		} catch (StreamException se) {
			throw new IOException("Unable to deserialize with XStream: " + se.getMessage());
		}
	}

	private class BoundaryReader extends Reader {

		public int read(char cbuf[], int off, int len) throws IOException {
			int copied = 0;

			while (copied < len) {
				int c = _reader.read();

				if (c == -1) {
					break;
				}

				if (c == BOUNDARY.charAt(0)) {
					if (foundBoundary()) {
						break;
					}
				}

				cbuf[off + copied] = (char) c;
				copied++;
			}

			if (copied == 0) {
				return -1;
			} else {
				return copied;
			}
		}

		private boolean foundBoundary() throws IOException {
			_reader.mark(BOUNDARY.length() - 1);

			for (int i = 1; i < BOUNDARY.length(); i++) {
				int c = _reader.read();
				if (c != BOUNDARY.charAt(i)) {
					_reader.reset();
					return false;
				}
			}

			return true;
		}

		public void close() throws IOException {
		}

	}

}
