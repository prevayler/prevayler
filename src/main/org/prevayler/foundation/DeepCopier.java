package org.prevayler.foundation;

import org.prevayler.foundation.serialization.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class DeepCopier {

	/**
	 * Produce a deep copy of the given object. Serializes the entire object to a byte array in memory. Recommended for
	 * relatively small objects, such as individual transactions.
	 */
	public static Object deepCopy(Object original, Serializer serializer) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		serializer.writeObject(byteOut, original);
		ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
		return serializer.readObject(byteIn);
	}

	/**
	 * Produce a deep copy of the given object. Serializes the object through a pipe between two threads. Recommended for
	 * very large objects, such as an entire prevalent system. The current thread is used for serializing the original
	 * object in order to respect any synchronization the caller may have around it, and a new thread is used for
	 * deserializing the copy.
	 */
	public static Object deepCopyParallel(Object original, Serializer serializer) throws IOException, ClassNotFoundException {
		PipedOutputStream outputStream = new PipedOutputStream();
		PipedInputStream inputStream = new PipedInputStream(outputStream);

		Receiver receiver = new Receiver(inputStream, serializer);
		receiver.start();

		try {
			serializer.writeObject(outputStream, original);
		} finally {
			outputStream.close();
		}

		try {
			receiver.join();
		} catch (InterruptedException e) {
			throw new RuntimeException("Unexpected InterruptedException", e);
		}

		if (receiver._error != null) throw new RuntimeException("Error during deserialization", receiver._error);
		if (receiver._runtimeException != null) throw receiver._runtimeException;
		if (receiver._classNotFoundException != null) throw receiver._classNotFoundException;
		if (receiver._ioException != null) throw receiver._ioException;
		if (receiver._result != null) return receiver._result;
		throw new RuntimeException("Deep copy failed in an unknown way");
	}

	private static class Receiver extends Thread {

		private InputStream _inputStream;
		private Serializer _serializer;
		public Object _result;
		public IOException _ioException;
		public ClassNotFoundException _classNotFoundException;
		public RuntimeException _runtimeException;
		public Error _error;

		public Receiver(InputStream inputStream, Serializer serializer) {
			_inputStream = inputStream;
			_serializer = serializer;
		}

		public void run() {
			try {
				_result = _serializer.readObject(_inputStream);
			} catch (IOException e) {
				_ioException = e;
			} catch (ClassNotFoundException e) {
				_classNotFoundException = e;
			} catch (RuntimeException e) {
				_runtimeException = e;
			} catch (Error e) {
				_error = e;
				throw e;
			}
		}

	}

}
