//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import java.io.File;
import java.io.FileFilter;

import junit.framework.TestCase;


public abstract class PrevalenceTest extends TestCase {

	protected String _testDirectory;

	protected void setUp() throws Exception {
		File tempFile = File.createTempFile("Prevalence", "Base");
		tempFile.delete();  //I don't want a file. I want a directory.
		assertTrue("Unable to create directory " + tempFile, tempFile.mkdirs());
		_testDirectory = tempFile.getAbsolutePath();
	}

	protected void tearDown() throws Exception {
	    delete(_testDirectory);
	}

	protected void deleteFromTestDirectory(String fileName) {
	    delete(new File(_testDirectory + File.separator + fileName));
	}

	static public void delete(String fileName) {
	    delete(new File(fileName));
	}

	static private void delete(File file) {
	    if (file.isDirectory()) deleteDirectoryContents(file);
		assertTrue("Unable to delete " + file, file.delete());
	}

	static private void deleteDirectoryContents(File directory) {
		File[] files = directory.listFiles(new PrevalenceFileFilter());
		if (files == null) return;
	    for (int i = 0; i < files.length; i++) delete(files[i]);
	}

	static private class PrevalenceFileFilter implements FileFilter {
		public boolean accept(File file) {
			return file.getName().endsWith("transactionLog")
				|| file.getName().endsWith("snapshot")
				|| file.isDirectory();
		}
	}

}
