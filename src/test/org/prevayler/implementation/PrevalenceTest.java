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

	static public void delete(String dir) {
	    delete(new File(dir));
	}

	static private void delete(File file) {
	    if (file.isDirectory()) deleteDirectoryContents(file);
		if (!file.delete()) {
			System.gc(); 
			assertTrue("Unable to delete " + file, file.delete());
		}
	}

	static private void deleteDirectoryContents(File directory) {
		File[] files = directory.listFiles(new PrevalenceFilter());
		if (files == null) return;
	    for (int i = 0; i < files.length; i++) delete(files[i]);
	}

	static private class PrevalenceFilter implements FileFilter {
		public boolean accept(File file) {
			return file.getName().endsWith("transactionLog")
				|| file.getName().endsWith("snapshot")
				|| file.isDirectory();
		}
	}

}
