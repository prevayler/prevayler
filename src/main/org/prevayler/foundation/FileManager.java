package org.prevayler.foundation;

import java.io.File;
import java.io.IOException;

public class FileManager {

	public static File produceDirectory(String directoryPath) throws IOException {
		File directory = new File(directoryPath);
		produceDirectory(directory);
		return directory;
	}

	public static void produceDirectory(File directory) throws IOException {
		if (!directory.exists() && !directory.mkdirs()) throw new IOException("Directory doesn't exist and could not be created: " + directory);
		if (!directory.isDirectory()) throw new IOException("Path exists but is not a directory: " + directory);
	}

}
