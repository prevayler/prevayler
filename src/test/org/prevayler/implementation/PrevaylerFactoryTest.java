package org.prevayler.implementation;

import java.io.IOException;
import java.io.Serializable;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;


public class PrevaylerFactoryTest extends PrevalenceTest {

	private static final Serializable POJO = new Serializable() {}; 

	public void testTransientPrevaylerCreation() {
		Prevayler prevayler = PrevaylerFactory.createTransientPrevayler(POJO);
		assertEquals(POJO, prevayler.prevalentSystem());
	}

	public void testSnapshotPrevaylerCreation() throws IOException, ClassNotFoundException {
		Prevayler prevayler = PrevaylerFactory.createPrevayler(POJO, _testDirectory);
		assertEquals(POJO, prevayler.prevalentSystem());
	}

}
