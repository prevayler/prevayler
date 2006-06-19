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

import java.util.LinkedHashMap;
import java.util.Map;

public class Chunk {

    private byte[] _bytes;

    private Map _parameters;

    public Chunk(byte[] bytes) {
        this(bytes, new LinkedHashMap());
    }

    public Chunk(byte[] bytes, Map parameters) {
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
        return (String) _parameters.get(name);
    }

    public Map getParameters() {
        return _parameters;
    }

}
