package org.prevayler.foundation;

import org.prevayler.foundation.serialization.JavaSerializationStrategy;

import java.io.File;
import java.io.IOException;


/**
 * @deprecated Use DurableOutputStream instead. Deprecated since Prevayler 2.00.002.
 */
public class SimpleOutputStream extends DurableOutputStream {

	public SimpleOutputStream(File file) throws IOException {
		super(file, new JavaSerializationStrategy());
	}

}
