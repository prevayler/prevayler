package org.prevayler.implementation;

import java.io.IOException;

import junit.framework.TestCase;

import org.prevayler.Prevayler;
import org.prevayler.implementation.PrevaylerFactory;


public class PrevaylerFactoryTest extends TestCase {

	private static final Object POJO = new Object(); 

	public void testTransientPrevaylerCreation() {
		Prevayler prevayler = PrevaylerFactory.createTransientPrevayler(POJO);
		assertEquals(POJO, prevayler.prevalentSystem());
	}

	public void testSnapshotPrevaylerCreation() throws IOException, ClassNotFoundException {
		Prevayler prevayler = PrevaylerFactory.createPrevayler(POJO);
		assertEquals(POJO, prevayler.prevalentSystem());

		prevayler = PrevaylerFactory.createPrevayler(POJO, "anything");
		assertEquals(POJO, prevayler.prevalentSystem());
	}

}
