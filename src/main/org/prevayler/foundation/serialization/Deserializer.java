package org.prevayler.foundation.serialization;

import java.io.IOException;

public interface Deserializer {

	public Object readObject() throws IOException, ClassNotFoundException;

}
