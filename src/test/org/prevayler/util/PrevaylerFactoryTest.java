package org.prevayler.util;

import java.io.IOException;

import junit.framework.TestCase;

import org.prevayler.Prevayler;
import org.prevayler.implementation.SnapshotPrevayler;


public class PrevaylerFactoryTest extends TestCase {

	private static final Object POJO = new Object(); 

	public void testTransientPrevaylerCreation() {
		Prevayler prevayler = PrevaylerFactory.createTransientPrevayler(POJO);
		assertEquals(POJO, prevayler.prevalentSystem());
	}

	public void testSnapshotPrevaylerCreation() throws IOException, ClassNotFoundException {
		SnapshotPrevayler prevayler = PrevaylerFactory.createSnapshotPrevayler(POJO);
		assertEquals(POJO, prevayler.prevalentSystem());

		prevayler = PrevaylerFactory.createSnapshotPrevayler(POJO, "anything");
		assertEquals(POJO, prevayler.prevalentSystem());
	}

}
