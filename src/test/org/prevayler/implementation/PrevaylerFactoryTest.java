//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import java.io.IOException;
import java.io.Serializable;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.*;


public class PrevaylerFactoryTest extends FileIOTest {

	private static final Serializable POJO = new Serializable() {}; 

	public void testTransientPrevaylerCreation() {
		Prevayler prevayler = PrevaylerFactory.createTransientPrevayler(POJO);
		assertEquals(POJO, prevayler.prevalentSystem());
	}

	public void testSnapshotPrevaylerCreation() throws IOException, ClassNotFoundException {
		Prevayler prevayler = PrevaylerFactory.createPrevayler(POJO, _testDirectory);
		assertEquals(POJO, prevayler.prevalentSystem());
	}

	public void testCheckpointPrevaylerCreation() throws IOException, ClassNotFoundException {
		Prevayler prevayler = PrevaylerFactory.createCheckpointPrevayler(POJO, _testDirectory);
		assertEquals(POJO, prevayler.prevalentSystem());
	}

}
