//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

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

  protected Class<?> resolveClass(ObjectStreamClass v) throws IOException, ClassNotFoundException {
    if (_loader == null)
      return super.resolveClass(v);
    try {
      return Class.forName(v.getName(), false, _loader);
    } catch (ClassNotFoundException ex) {
      return super.resolveClass(v); // fall back for resolving primitive types
    }
  }
}
