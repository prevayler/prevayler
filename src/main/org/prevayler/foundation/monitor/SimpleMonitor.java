//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2004 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Carlos Villela.

package org.prevayler.foundation.monitor;

import java.io.File;
import java.io.PrintStream;


/**
 * A Monitor that logs output to a PrintStream (System.out by default).
 */
public class SimpleMonitor implements Monitor {

    private final PrintStream _stream;

	public SimpleMonitor() {
		this(System.out);
	}

	/**
	 * @param stream The stream to be used for logging.
	 */
	public SimpleMonitor(PrintStream stream) {
		_stream = stream;
	}

	public void notify(String message) {
		_stream.println("\n" + message);
	}

	public void notify(String message, Exception ex) {
		notify(message);
        notify(ex);
	}

	public void notify(String message, File file) {
		notify(message + "/nFile: " + file);
	}

	public void notify(String message, File file, Exception ex) {
		notify(message, file);
        notify(ex);
    }

	private void notify(Exception ex) {
		ex.printStackTrace(_stream);
	}
}