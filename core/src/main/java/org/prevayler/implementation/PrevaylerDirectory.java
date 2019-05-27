//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Justin Sampson, Eric Bridgwater

package org.prevayler.implementation;

import org.prevayler.foundation.FileManager;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class PrevaylerDirectory {

  private static final int DIGITS_IN_FILENAME = 19;
  private static final String SNAPSHOT_SUFFIX_PATTERN = "[a-zA-Z0-9]*[Ss]napshot";
  private static final String SNAPSHOT_FILENAME_PATTERN = "\\d{" + DIGITS_IN_FILENAME + "}\\." + SNAPSHOT_SUFFIX_PATTERN;
  private static final String JOURNAL_SUFFIX_PATTERN = "[a-zA-Z0-9]*[Jj]ournal";
  private static final String JOURNAL_FILENAME_PATTERN = "\\d{" + DIGITS_IN_FILENAME + "}\\." + JOURNAL_SUFFIX_PATTERN;

  private File _directory;

  public PrevaylerDirectory(String directory) {
    this(new File(directory));
  }

  public PrevaylerDirectory(File directory) {
    _directory = directory;
  }

  /**
   * Ensure that the directory exists, creating it and parent directories if necessary.
   *
   * @throws IOException if the directory can't be created or isn't a directory.
   */
  public void produceDirectory() throws IOException {
    FileManager.produceDirectory(_directory);
  }

  /**
   * Ensure that the given suffix (which should not include a dot) is valid for snapshots.
   * <p/>
   * Snapshot suffixes must match the pattern /[a-zA-Z0-9]*[Ss]napshot/.
   *
   * @throws IllegalArgumentException otherwise.
   */
  public static void checkValidSnapshotSuffix(String suffix) {
    if (!suffix.matches(SNAPSHOT_SUFFIX_PATTERN)) {
      throw new IllegalArgumentException(
          "Snapshot filename suffix must match /" + SNAPSHOT_SUFFIX_PATTERN + "/, but '" + suffix + "' does not");
    }
  }

  /**
   * Ensure that the given suffix (which should not include a dot) is valid for journals.
   * <p/>
   * Journal suffixes must match the pattern /[a-zA-Z0-9]*[Jj]ournal/.
   *
   * @throws IllegalArgumentException otherwise.
   */
  public static void checkValidJournalSuffix(String suffix) {
    if (!suffix.matches(JOURNAL_SUFFIX_PATTERN)) {
      throw new IllegalArgumentException(
          "Journal filename suffix must match /" + JOURNAL_SUFFIX_PATTERN + "/, but '" + suffix + "' does not");
    }
  }

  /**
   * Generate a valid snapshot filename.
   *
   * @throws IllegalArgumentException if the version is negative or the suffix is invalid.
   */
  public File snapshotFile(long version, String suffix) {
    checkValidSnapshotSuffix(suffix);
    return file(version, suffix);
  }

  /**
   * Generate a valid journal filename.
   *
   * @throws IllegalArgumentException if the version is negative or the suffix is invalid.
   */
  public File journalFile(long transaction, String suffix) {
    checkValidJournalSuffix(suffix);
    return file(transaction, suffix);
  }

  private File file(long version, String suffix) {
    if (version < 0) {
      throw new IllegalArgumentException("Snapshot and journal version numbers must be non-negative: " + version);
    }
    String fileName = "0000000000000000000" + version;
    return new File(_directory, fileName.substring(fileName.length() - DIGITS_IN_FILENAME) + "." + suffix);
  }


  /**
   * Extract the version number from a snapshot filename.
   * <p/>
   * Returns -1 if file does not have a valid snapshot filename.
   */
  public static long snapshotVersion(File file) {
    return version(file, SNAPSHOT_FILENAME_PATTERN);
  }

  /**
   * Extract the version number from a journal filename.
   * <p/>
   * Returns -1 if file does not have a valid journal filename.
   */
  public static long journalVersion(File file) {
    return version(file, JOURNAL_FILENAME_PATTERN);
  }

  private static long version(File file, String filenamePattern) {
    String fileName = file.getName();
    if (!fileName.matches(filenamePattern)) return -1;
    return Long.parseLong(fileName.substring(0, fileName.indexOf(".")));
  }


  /**
   * Find the latest snapshot file.
   * <p/>
   * Returns null if no snapshot file was found.
   */
  public File latestSnapshot() throws IOException {
    File[] files = _directory.listFiles();
    if (files == null) throw new IOException("Error reading file list from directory " + _directory);

    File latestSnapshot = null;
    long latestVersion = -1;
    for (int i = 0; i < files.length; i++) {
      File candidateSnapshot = files[i];
      long candidateVersion = snapshotVersion(candidateSnapshot);
      if (candidateVersion > latestVersion) {
        latestVersion = candidateVersion;
        latestSnapshot = candidateSnapshot;
      }
    }
    return latestSnapshot;
  }

  /**
   * Find the journal file containing the desired transaction.
   * <p/>
   * Returns null if no appropriate journal file was found.
   */
  public File findInitialJournalFile(long initialTransactionWanted) {
    File[] journals = _directory.listFiles(new FileFilter() {
      public boolean accept(File pathname) {
        return pathname.getName().matches(JOURNAL_FILENAME_PATTERN);
      }
    });

    Arrays.sort(journals, new Comparator<File>() {
      public int compare(File f1, File f2) {
        return new Long(journalVersion(f1)).compareTo(new Long(journalVersion(f2)));
      }
    });

    for (int i = journals.length - 1; i >= 0; i--) {
      File journal = journals[i];
      long version = journalVersion(journal);
      if (version <= initialTransactionWanted) {
        return journal;
      }
    }

    return null;
  }

  /**
   * Create a temporary file in the directory.
   */
  public File createTempFile(String prefix, String suffix) throws IOException {
    return File.createTempFile(prefix, suffix, _directory);
  }

  /**
   * Rename a journal file to indicate it was found empty and is being ignored.
   */
  public static void renameUnusedFile(File journalFile) {
    journalFile.renameTo(new File(journalFile.getAbsolutePath() + ".unusedFile" + System.currentTimeMillis()));
  }


  /**
   * Determine which snapshot and journal files are still necessary for recovery.
   * <p/>
   * Necessary files include the latest snapshot file and any journal files
   * potentially containing transactions after that snapshot version.
   */
  public Set<File> necessaryFiles() throws IOException {
    File[] allFiles = _directory.listFiles();
    if (allFiles == null) {
      throw new IOException("Error reading file list from directory " + _directory);
    }
    File latestSnapshot = latestSnapshot();
    long systemVersion = latestSnapshot == null ? 0 : snapshotVersion(latestSnapshot);
    File initialJournal = findInitialJournalFile(systemVersion + 1);
    Set<File> neededFiles = new TreeSet<File>();
    if (latestSnapshot != null) {
      neededFiles.add(latestSnapshot);
    }
    if (initialJournal != null) {
      neededFiles.add(initialJournal);
      long initialJournalVersion = journalVersion(initialJournal);
      for (int i = 0; i < allFiles.length; i++) {
        File file = allFiles[i];
        if (journalVersion(file) > initialJournalVersion) {
          neededFiles.add(file);
        }
      }
    }
    return neededFiles;
  }
}
