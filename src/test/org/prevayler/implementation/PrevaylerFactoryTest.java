package org.prevayler.implementation;

import java.io.IOException;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;


public class PrevaylerFactoryTest extends PrevalenceTest {

	private static final Object POJO = new Object(); 

	public void testTransientPrevaylerCreation() {
		Prevayler prevayler = PrevaylerFactory.createTransientPrevayler(POJO,_testDirectory);
		assertEquals(POJO, prevayler.prevalentSystem());
	}

	public void testSnapshotPrevaylerCreation() throws IOException, ClassNotFoundException {
		Prevayler prevayler = PrevaylerFactory.createPrevayler(POJO, _testDirectory);
		assertEquals(POJO, prevayler.prevalentSystem());
	}

}
