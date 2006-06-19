// Prevayler, The Free-Software Prevalence Layer
// Copyright 2001-2006 by Klaus Wuestefeld
//
// This library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE.
//
// Prevayler is a trademark of Klaus Wuestefeld.
// See the LICENSE file for license details.

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
        if (!directory.exists() && !directory.mkdirs())
            throw new InvalidDirectoryException("Directory doesn't exist and could not be created: " + directory);
        if (!directory.isDirectory())
            throw new InvalidDirectoryException("Path exists but is not a directory: " + directory);
    }

}
