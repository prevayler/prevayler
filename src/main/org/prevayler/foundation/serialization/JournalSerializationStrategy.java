package org.prevayler.foundation.serialization;

import org.prevayler.implementation.journal.Chunking;
import org.prevayler.implementation.journal.Chunk;
import org.prevayler.foundation.DeepCopier;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.EOFException;

public final class JournalSerializationStrategy {

	private Serializer _serializer;

	public JournalSerializationStrategy(Serializer serializer) {
		_serializer = serializer;
	}

	public JournalSerializer createSerializer(final OutputStream stream) throws IOException {
		return new JournalSerializer() {
			public void writeObject(Object object) throws IOException {
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				_serializer.writeObject(bytes, object);
				Chunking.writeChunk(stream, new Chunk(bytes.toByteArray()));
			}

			public void flush() throws IOException {
				stream.flush();
			}
		};
	}

	public JournalDeserializer createDeserializer(final InputStream stream) throws IOException {
		return new JournalDeserializer() {
			public Object readObject() throws IOException, ClassNotFoundException {
				Chunk chunk = Chunking.readChunk(stream);
				if (chunk == null) {
					throw new EOFException();
				}
				ByteArrayInputStream bytes = new ByteArrayInputStream(chunk.getBytes());
				return _serializer.readObject(bytes);
			}
		};
	}

	public Object deepCopy(Object original) throws IOException, ClassNotFoundException {
		return DeepCopier.deepCopy(original, _serializer);
	}

}
