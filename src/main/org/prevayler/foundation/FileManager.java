//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.foundation;

import java.io.*;

public class FileManager {

	static public File produceDirectory(String directoryName) throws IOException {
		File directory = new File(directoryName);
		if (!directory.exists() && !directory.mkdirs()) throw new IOException("Directory doesn't exist and could not be created: " + directoryName);
		if (!directory.isDirectory()) throw new IOException("Path exists but is not a directory: " + directoryName);
		return directory;
	}
}
