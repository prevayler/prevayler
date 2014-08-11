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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIPSerializer implements Serializer {

  private final Serializer _delegate;

  private final int _bufferSize;

  public GZIPSerializer(Serializer delegate) {
    this(delegate, 512);
  }

  public GZIPSerializer(Serializer delegate, int gzipBufferSize) {
    _delegate = delegate;
    _bufferSize = gzipBufferSize;
  }

  public void writeObject(OutputStream stream, Object object) throws Exception {
    GZIPOutputStream gzip = new GZIPOutputStream(stream, _bufferSize);
    _delegate.writeObject(gzip, object);
    gzip.close();
  }

  public Object readObject(InputStream stream) throws Exception {
    GZIPInputStream gunzip = new GZIPInputStream(stream, _bufferSize);
    return _delegate.readObject(gunzip);
  }

}
