// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package prevayler.test;

import java.io.*;
import java.util.*;

import prevayler.*;

public class PersistenceTest {

    static public void run() throws Exception {

        clearPrevalenceBase();

        crashRecover(); //There is nothing to recover at first. A new system will be created.
        crashRecover();
        add(40);  //1
        add(30);  //2
        total(70);

        crashRecover();
        total(70);

        add(20);  //3
        add(15);  //4
        snapshot();
        snapshot();
        total(105);

        crashRecover();
        snapshot();
        add(10);  //5
        snapshot();
        add(5);   //6
        add(4);   //7
        total(124);

        crashRecover();
        add(3);   //8
        total(127);

        clearPrevalenceBase(); //Check if all files were properly closed and can be deleted.

	}

    static private void crashRecover() throws Exception {
        out("CrashRecovery.");
        prevayler = new PrevaylerFactory().create(new AddingSystemFactory(), prevalenceBase);
        prevaylers.add(prevayler);
    }

    static private void snapshot() throws IOException {
        out("Snapshot.");
        prevayler.takeSnapshot();
    }

    static private void add(long value) throws Exception {
        out("Adding " + value);
        prevayler.executeCommand(new Addition(value));
    }

    static private void total(long value) {
        out("Expecting total: " + value);
        compare(system().total(), value, "Total");
    }

    static private AddingSystem system() {
        return (AddingSystem)prevayler.system();
    }


    static private void clearPrevalenceBase() throws Exception{
        Iterator it = prevaylers.iterator();
        while (it.hasNext()) {
            ((Prevayler)it.next()).takeSnapshot(); //Closes the open log file.
            it.remove();
        }

        File directory = new File(prevalenceBase);
        if(!directory.exists()) return;

        delete(directory.listFiles());
	}

    static private void delete(File[] files) {
        for(int i = 0; i < files.length; ++i){
            assert(files[i].delete(), "Unable to delete " + files[i]);
        }
    }


    static private void compare(long observed, long expected, String measurement) {
        assert(observed == expected, measurement + ": " + observed + "   Expected: " + expected);
	}

    static private void assert(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException(message);
		}
    }

    static private void out(Object obj) {
        //System.out.println(obj);   //Uncomment this line to see what the test is doing.
	}

    static private Prevayler prevayler;
    static private final Set prevaylers = new HashSet();
    static private final String prevalenceBase = System.getProperty("user.dir") + "\\prevalenceBase";

}
