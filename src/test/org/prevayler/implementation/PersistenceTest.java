// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.implementation;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.prevayler.foundation.FileManager;
import org.prevayler.implementation.SnapshotPrevayler;
import org.prevayler.util.PrevaylerFactory;

import junit.framework.TestCase;

public class PersistenceTest extends TestCase {

	private SnapshotPrevayler prevayler;
	private String prevalenceBase;

	public void testPersistence() throws Exception {

		newPrevalenceBase();

		crashRecover(); //There is nothing to recover at first. A new system will be created.
		crashRecover();
		add(40,40);
		add(30,70);
		verify(70);

		crashRecover();
		verify(70);

		add(20,90);
		add(15,105);
		snapshot();
		snapshot();
		verify(105);

		crashRecover();
		snapshot();
		add(10,115);
		snapshot();
		add(5,120);
		add(4,124);
		verify(124);

		crashRecover();
		add(3,127);
		verify(127);

		snapshot();
		File snapshot =   new File(prevalenceBase, "0000000000000000008.snapshot");
		newPrevalenceBase();
		FileManager.produceDirectory(prevalenceBase);
		snapshot.renameTo(new File(prevalenceBase, "0000000000000000008.snapshot"));
		
		crashRecover();
		add(10,137);
		add(2,139);
		crashRecover();
		add(10,149);
		add(3,152);
		crashRecover();
		add(11,163);
		add(4,167);
		crashRecover();
		verify(167);
	}

    protected void tearDown() throws Exception {
        RollbackTest.delete(prevalenceBase);
    }

	private void crashRecover() throws Exception {
		out("CrashRecovery.");
		prevayler = PrevaylerFactory.createSnapshotPrevayler(new AddingSystem(), prevalenceBase());
	}

	private void snapshot() throws IOException {
		out("Snapshot.");
		prevayler.takeSnapshot();
	}


	private void add(long value, long expectedTotal) throws Exception {
		out("Adding " + value);
		prevayler.execute(new Addition(value));
		verify(expectedTotal);
	}


	private void verify(long expectedTotal) {
		out("Expecting total: " + expectedTotal);
		compare(system().total(), expectedTotal, "Total");
	}


	private AddingSystem system() {
		return (AddingSystem)prevayler.prevalentSystem();
	}


	private String prevalenceBase() {
		return prevalenceBase;
	}


	private void newPrevalenceBase() throws Exception {
		prevalenceBase = "PrevalenceBase" + System.currentTimeMillis();
	}

	private void compare(long observed, long expected, String measurement) {
		verify(observed == expected, measurement + ": " + observed + "   Expected: " + expected);
	}

	private static void verify(boolean condition, String message) {
		if (!condition) {
			throw new RuntimeException(message);
		}
	}

	private static void out(Object obj) {
		if (false) System.out.println(obj);   //Change this line to see what the test is doing.
	}

	public static void deletePrevalenceFiles(String directoryName) {
		File directory = new File(directoryName);
		if(!directory.exists()) return;
	
		File[] files = directory.listFiles(new PersistenceTest.PrevalenceFilter());
	
		for(int i = 0; i < files.length; ++i){
			assertTrue("Unable to delete " + files[i], files[i].delete());
		}
	}

	static private class PrevalenceFilter implements FilenameFilter {
		public boolean accept(File directory, String filename) {
			return filename.endsWith("transactionLog")
				|| filename.endsWith("snapshot");
		}
	}


}
