// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.test;

import java.io.*;
import java.util.*;

import org.prevayler.implementation.SnapshotPrevayler;

public class PersistenceTest {

    static public void run() throws Exception {

	clearPrevalenceBase();

	crashRecover(); //There is nothing to recover at first. A new system will be created.
	crashRecover();
	add(40,40);     //1
	add(30,70);     //2
	verify(70);

	crashRecover();
	verify(70);

	add(20,90);     //3
	add(15,105);    //4
	snapshot();
	snapshot();
	verify(105);

	crashRecover();
	snapshot();
	add(10,115);    //5
	snapshot();
	add(5,120);     //6
	add(4,124);     //7
	verify(124);

	crashRecover();
	add(3,127);     //8
	verify(127);

	snapshot();
	clearPrevalenceBase();
	snapshot();
	crashRecover();
	verify(127);

	add(2,129);     //9
	crashRecover();
	verify(129);

	clearPrevalenceBase(); //Check if all files were properly closed and can be deleted.

	}

    static private void crashRecover() throws Exception {
	out("CrashRecovery.");
	prevayler = new SnapshotPrevayler(new AddingSystem(), prevalenceBase);
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


    static private void clearPrevalenceBase() throws Exception{
	Iterator it = prevaylers.iterator();
	while (it.hasNext()) {
	    ((SnapshotPrevayler)it.next()).takeSnapshot(); //Closes the open log file.
	    it.remove();
	}

	File directory = new File(prevalenceBase);
	if(!directory.exists()) return;

	delete(directory.listFiles());
	}

    static private void delete(File[] files) {
	for(int i = 0; i < files.length; ++i){
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

    static private SnapshotPrevayler prevayler;
    static private final Set prevaylers = new HashSet();
    static private final String prevalenceBase = System.getProperty("user.dir") + "\\prevalenceBase";

}
