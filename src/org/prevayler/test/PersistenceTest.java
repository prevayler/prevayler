// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.test;

import java.io.*;
import java.util.*;

import org.prevayler.*;
import org.prevayler.implementation.*;

public class PersistenceTest {

	static private SnapshotPrevayler prevayler;
	static private final Set prevaylers = new HashSet();

	static public void run() throws Exception {

		clearPrevalenceBase();

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

		clearPrevalenceBase();
		snapshot();
		
		crashRecover();
		add(10,137);
		add(2,139);
		crashRecover();
		verify(139);
	}

	static private void crashRecover() throws Exception {
		out("CrashRecovery.");
		prevayler = new SnapshotPrevayler(new AddingSystem(), prevalenceBase());
		prevaylers.add(prevayler);
	}

	static private void snapshot() throws IOException {
		out("Snapshot.");
		prevayler.takeSnapshot();
	}


	static private void add(long value, long expectedTotal) throws Exception {
		out("Adding " + value);
		Long total = (Long)prevayler.executeCommand(new Addition(value));
		compare(total.longValue(), expectedTotal, "Total");
	}


	static private void verify(long expectedTotal) {
		out("Expecting total: " + expectedTotal);
		compare(system().total(), expectedTotal, "Total");
	}


	static private AddingSystem system() {
		return (AddingSystem)prevayler.system();
	}


	static private String prevalenceBase() {
		return "PrevalenceBase";
	}


	static private void clearPrevalenceBase() throws Exception{
		Iterator it = prevaylers.iterator();
		while (it.hasNext()) {
			((SnapshotPrevayler)it.next()).takeSnapshot(); //Closes the open log file.
		}
		prevaylers.clear();

		deletePrevalenceFiles(prevalenceBase());
	}

	static public void deletePrevalenceFiles(String directoryName) {
		File directory = new File(directoryName);
		if(!directory.exists()) return;

		File[] files = directory.listFiles(new PrevalenceFilter());

		for(int i = 0; i < files.length; ++i){
			out("Deleting: " + files[i]);
			verify(files[i].delete(), "Unable to delete " + files[i]);
		}
	}


	static private void compare(long observed, long expected, String measurement) {
		verify(observed == expected, measurement + ": " + observed + "   Expected: " + expected);
	}

	static private void verify(boolean condition, String message) {
		if (!condition) {
			throw new RuntimeException(message);
		}
	}

	static private void out(Object obj) {
		//System.out.println(obj);   //Uncomment this line to see what the test is doing.
	}


	static private class PrevalenceFilter implements FilenameFilter {
		public boolean accept(File directory, String filename) {
			return filename.endsWith("commandLog")
				|| filename.endsWith("snapshot");
		}
	}
}
