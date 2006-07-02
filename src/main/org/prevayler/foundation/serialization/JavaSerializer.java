// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.foundation.serialization;

import org.prevayler.foundation.ObjectInputStreamWithClassLoader;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Writes and reads objects using Java serialization. This serializer can be
 * used for snapshots, journals or both.
 */
public class JavaSerializer<T> implements Serializer<T> {

    private ClassLoader _loader;

    public JavaSerializer() {
        _loader = null;
    }

    public JavaSerializer(ClassLoader loader) {
        _loader = loader;
    }

    public void writeObject(OutputStream stream, T object) throws Exception {
        ObjectOutputStream objects = new ObjectOutputStream(stream);
        objects.writeObject(object);
        objects.close();
    }

    @SuppressWarnings("unchecked") public T readObject(InputStream stream) throws Exception {
        ObjectInputStream objects = new ObjectInputStreamWithClassLoader(stream, _loader);
        return (T) objects.readObject();
    }

}
