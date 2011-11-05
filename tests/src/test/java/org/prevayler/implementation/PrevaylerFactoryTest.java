//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import java.io.Serializable;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.*;


public class PrevaylerFactoryTest extends FileIOTest {

	private static final Serializable POJO = new Serializable() {
			private static final long serialVersionUID = -8661727552300291738L;
		};

	public void testTransientPrevaylerCreation() {
		Prevayler<Serializable> prevayler = PrevaylerFactory.createTransientPrevayler(POJO);
		assertEquals(POJO, prevayler.prevalentSystem());
	}

	public void testSnapshotPrevaylerCreation() throws Exception {
		Prevayler<Serializable> prevayler = PrevaylerFactory.createPrevayler(POJO, _testDirectory);
		assertEquals(POJO, prevayler.prevalentSystem());
	}

	public void testCheckpointPrevaylerCreation() {
		Prevayler<Serializable> prevayler = PrevaylerFactory.createCheckpointPrevayler(POJO, _testDirectory);
		assertEquals(POJO, prevayler.prevalentSystem());
	}

}
