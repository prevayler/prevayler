//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the license distributed along with this file for more details.
//Contributions: Rodrigo B de Oliveira.

package org.prevayler.foundation;


public class Daemon extends Thread {

	public Daemon(Runnable runnable) {
		super(runnable);
		setDaemon(true);
		start();
	}

}
