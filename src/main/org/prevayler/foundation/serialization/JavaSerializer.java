package org.prevayler.foundation.serialization;

import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

public class JavaSerializer implements Serializer {

	private final ObjectOutputStream _stream;

	public JavaSerializer(OutputStream stream) throws IOException {
		_stream = new ObjectOutputStream(stream);
	}

	public void writeObject(Object object) throws IOException {
		_stream.writeObject(object);
	}

	public void flush() throws IOException {
		// Resetting an ObjectOutputStream allows it to forget all of
		// the objects it has seen, which could improve memory usage
		// during long periods of uptime. Otherwise, the
		// ObjectOutputStream would have to remember the identity of
		// every single object it had ever written out, just in case it
		// were to be written again, so that it could write a
		// back-reference instead of writing the object itself. Of
		// course, that's not what we would want anyway, since
		// transactions are supposed to be completely independent.

		_stream.reset();
		_stream.flush();
	}

}
