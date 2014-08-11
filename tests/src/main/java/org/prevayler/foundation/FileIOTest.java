//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.foundation;

import junit.framework.TestCase;

import java.io.*;


public abstract class FileIOTest extends TestCase {

  protected String _testDirectory;
  static private long counter = 0;

  protected void setUp() throws Exception {
    File tempFile = new File("Test" + System.currentTimeMillis() + counter++);
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

  static public void delete(File file) {
    if (file.isDirectory()) deleteDirectoryContents(file);
    assertTrue("File does not exist: " + file, file.exists());
    if (!file.delete()) {
      System.gc();
      assertTrue("Unable to delete " + file, file.delete());
    }
  }

  static private void deleteDirectoryContents(File directory) {
    File[] files = directory.listFiles();
    if (files == null) return;
    for (int i = 0; i < files.length; i++) delete(files[i]);
  }

  protected String journalContents(final String suffix) throws IOException {
    File journal = findJournal(suffix);

    FileInputStream file = new FileInputStream(journal);
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    int n;
    byte[] b = new byte[1024];
    while ((n = file.read(b)) != -1) {
      buffer.write(b, 0, n);
    }

    file.close();

    return buffer.toString("ISO-8859-1");
  }

  protected File findJournal(final String suffix) {
    File[] files = new File(_testDirectory).listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.endsWith("." + suffix);
      }
    });
    assertEquals(1, files.length);
    return files[0];
  }

}
