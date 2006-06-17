package org.prevayler.foundation;

import org.prevayler.foundation.serialization.JavaSerializer;
import org.prevayler.foundation.serialization.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class DeepCopier {

    /**
     * Same as deepCopy(original, new JavaSerializer()).
     * 
     * @throws ClassNotFoundException
     * @throws IOException
     * 
     */
    public static Object deepCopy(Object original) {
        return deepCopy(original, new JavaSerializer());
    }

    /**
     * Produce a deep copy of the given object. Serializes the entire object to
     * a byte array in memory. Recommended for relatively small objects, such as
     * individual transactions.
     */
    public static Object deepCopy(Object original, Serializer serializer) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            serializer.writeObject(byteOut, original);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            return serializer.readObject(byteIn);
        } catch (Exception e) {
            throw new DeepCopyError(e);
        }
    }

    /**
     * Produce a deep copy of the given object. Serializes the object through a
     * pipe between two threads. Recommended for very large objects, such as an
     * entire prevalent system. The current thread is used for serializing the
     * original object in order to respect any synchronization the caller may
     * have around it, and a new thread is used for deserializing the copy.
     */
    public static Object deepCopyParallel(Object original, Serializer serializer) {
        try {
            PipedOutputStream outputStream = new PipedOutputStream();
            PipedInputStream inputStream = new PipedInputStream(outputStream);

            Receiver receiver = new Receiver(inputStream, serializer);

            try {
                serializer.writeObject(outputStream, original);
            } finally {
                outputStream.close();
            }

            return receiver.getResult();
        } catch (Exception e) {
            throw new DeepCopyError(e);
        }
    }

    private static class Receiver extends Thread {

        private InputStream _inputStream;

        private Serializer _serializer;

        private Object _result;

        private Throwable _thrown;

        public Receiver(InputStream inputStream, Serializer serializer) {
            _inputStream = inputStream;
            _serializer = serializer;
            start();
        }

        public void run() {
            try {
                _result = _serializer.readObject(_inputStream);
            } catch (Error e) {
                _thrown = e;
                throw e;
            } catch (Exception e) {
                _thrown = e;
            }

            try {
                // Some serializers may write more than they actually need to
                // deserialize the object, but if
                // we don't read it all the PipedOutputStream will choke.
                while (_inputStream.read() != -1) {
                }
            } catch (IOException e) {
                // The object has been successfully deserialized, so ignore
                // problems at this point (for example,
                // the serializer may have explicitly closed the _inputStream
                // itself, causing this read to fail).
            }
        }

        public Object getResult() {
            Cool.join(this);

            // join() guarantees that all shared memory is synchronized between
            // the two threads

            if (_thrown != null) {
                throw new DeepCopyError(_thrown);
            } else if (_result == null) {
                throw new DeepCopyError();
            } else {
                return _result;
            }
        }

    }

}
