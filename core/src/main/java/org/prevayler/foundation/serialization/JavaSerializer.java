package org.prevayler.foundation.serialization;

import org.prevayler.foundation.ObjectInputStreamWithClassLoader;

import java.io.*;

/**
 * Writes and reads objects using Java serialization. This serializer can be used for snapshots, journals or both.
 */
public class JavaSerializer implements Serializer {

  private ClassLoader _loader;

  public JavaSerializer() {
    _loader = null;
  }

  public JavaSerializer(ClassLoader loader) {
    _loader = loader;
  }

  public void writeObject(OutputStream stream, Object object) throws IOException {
    ObjectOutputStream objects = new ObjectOutputStream(stream);
    objects.writeObject(object);
    objects.close();
  }

  public Object readObject(InputStream stream) throws IOException, ClassNotFoundException {
    ObjectInputStream objects = new ObjectInputStreamWithClassLoader(stream, _loader);
    Object object = objects.readObject();
    objects.close();
    return object;
  }

}
