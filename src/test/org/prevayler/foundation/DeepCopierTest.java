package org.prevayler.foundation;

import junit.framework.TestCase;
import org.prevayler.foundation.serialization.JavaSerializer;

import java.io.IOException;

public class DeepCopierTest extends TestCase {

	public void testNormal() throws IOException, ClassNotFoundException {
		Object original = "foo";
		Object copy = DeepCopier.deepCopy(original, new JavaSerializer());

		assertEquals(original, copy);
		assertNotSame(original, copy);
	}

	public void testParallel() throws IOException, ClassNotFoundException {
		Object original = "foo";
		Object copy = DeepCopier.deepCopyParallel(original, new JavaSerializer());

		assertEquals(original, copy);
		assertNotSame(original, copy);
	}

}
