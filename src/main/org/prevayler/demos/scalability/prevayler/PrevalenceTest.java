// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.demos.scalability.prevayler;

import java.io.File;
import java.io.FileFilter;


public class PrevalenceTest {

	static public void delete(String dir) {
	    delete(new File(dir));
	}

	static private void delete(File file) {
	    if (file.isDirectory()) deleteDirectoryContents(file);
		if (!file.delete()) {
			System.gc();
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
