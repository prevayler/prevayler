// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

package org.prevayler.foundation;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class ObjectInputStreamWithClassLoader extends ObjectInputStream {
    ClassLoader _loader;

    public ObjectInputStreamWithClassLoader(InputStream stream, ClassLoader loader) throws IOException {
        super(stream);

        _loader = loader;
    }

    @Override protected Class<?> resolveClass(ObjectStreamClass v) throws IOException, ClassNotFoundException {
        return (_loader != null ? _loader.loadClass(v.getName()) : super.resolveClass(v));
    }
}
