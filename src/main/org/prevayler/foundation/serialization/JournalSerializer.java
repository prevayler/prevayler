package org.prevayler.foundation.serialization;

import java.io.IOException;

public interface JournalSerializer {

	public void writeObject(Object object) throws IOException;

	public void flush() throws IOException;

}
