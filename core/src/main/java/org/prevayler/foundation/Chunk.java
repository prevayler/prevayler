package org.prevayler.foundation;

import java.util.LinkedHashMap;
import java.util.Map;

public class Chunk {

  private byte[] _bytes;
  private Map<String, String> _parameters;

  public Chunk(byte[] bytes) {
    this(bytes, new LinkedHashMap<String, String>());
  }

  public Chunk(byte[] bytes, Map<String, String> parameters) {
    _bytes = bytes;
    _parameters = parameters;
  }

  public byte[] getBytes() {
    return _bytes;
  }

  public void setParameter(String name, String value) {
    _parameters.put(name, value);
  }

  public String getParameter(String name) {
    return _parameters.get(name);
  }

  public Map<String, String> getParameters() {
    return _parameters;
  }

}
