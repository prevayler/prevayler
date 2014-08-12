package org.prevayler.foundation;

import java.io.SyncFailedException;

/**
 * Used to access and control FileDescriptor sync of journals.
 *
 * @see org.prevayler.PrevaylerFactory#configureJournalDiskSync(JournalDiskSyncStrategy)
 * @since 2.7
 */
public abstract class JournalDiskSyncStrategy {

  /**
   * <br>If return <code>false</code>, transactions may execute without necessarily being written to the
   * physical disk. Transactions are still flushed to the operating system before being
   * executed, but FileDescriptor.sync() is never called. This increases transaction
   * throughput dramatically, but allows transactions to be lost if the system
   * does not shut down cleanly. Calling {@link org.prevayler.Prevayler#close()} will close the
   * underlying journal file and therefore cause all transactions to be written to
   * disk.
   * <br>
   * <br>If return <code>true</code> (default), every transaction is forced to be written to the
   * physical disk before it is executed (using {@link java.io.FileDescriptor#sync()}).
   * (Many transactions may be written at once, but no transaction will be executed
   * before it is written to disk.)
   *
   * @return true if FileDescriptor will be synced after next batch of transactions is written to journal.
   * @see DurableOutputStream#waitUntilSynced(int)
   */
  public abstract boolean syncFileDescriptorAfterNextTransactionBatch();

  /**
   * Syncs FileDescriptor as soon as current sync-lock is released.
   *
   * @throws SyncFailedException
   * @see DurableOutputStream#sync()
   */
  public void sync() throws SyncFailedException {
    durableOutputStream.sync();
  }

  private DurableOutputStream durableOutputStream;

  /**
   * Set by {@link DurableOutputStream} on its creation.
   *
   * @param durableObjectStream
   */
  void setDurableObjectStream(DurableOutputStream durableObjectStream) {
    this.durableOutputStream = durableObjectStream;
  }

  /**
   * Will always sync journal FileDescriptor after next batch of transactions is written to journal.
   */
  public static class Always extends JournalDiskSyncStrategy {
    @Override
    /**
     * @return true
     */
    public boolean syncFileDescriptorAfterNextTransactionBatch() {
      return true;
    }
  }

  /**
   * Will never sync journal FileDescriptor after next batch of transactions is written to journal.
   */
  public static class Never extends JournalDiskSyncStrategy {
    @Override
    /**
     * @return false
     */
    public boolean syncFileDescriptorAfterNextTransactionBatch() {
      return false;
    }
  }

  /**
   * Will sync journal FileDescriptor after next batch of transactions is written to journal
   * once after {@link #setSyncFileDescriptorAfterNextTransactionBatch(boolean)} is called with true,
   * and will reset back to false after syncing.
   * <p/>
   * Calling {@link #sync()} will reset trigger if set to true.
   */
  public static class SometimesSingleTrigger extends JournalDiskSyncStrategy {
    private boolean syncFileDescriptorAfterNextTransactionBatch = false;

    @Override
    public void sync() throws SyncFailedException {
      setSyncFileDescriptorAfterNextTransactionBatch(false);
      super.sync();
    }

    @Override
    public boolean syncFileDescriptorAfterNextTransactionBatch() {
      synchronized (this) {
        try {
          return syncFileDescriptorAfterNextTransactionBatch;
        } finally {
          if (syncFileDescriptorAfterNextTransactionBatch) {
            syncFileDescriptorAfterNextTransactionBatch = false;
          }
        }
      }
    }

    public boolean isSyncFileDescriptorAfterNextTransactionBatch() {
      return syncFileDescriptorAfterNextTransactionBatch;
    }

    public void setSyncFileDescriptorAfterNextTransactionBatch(boolean syncFileDescriptorAfterNextTransactionBatch) {
      synchronized (this) {
        this.syncFileDescriptorAfterNextTransactionBatch = syncFileDescriptorAfterNextTransactionBatch;
      }
    }
  }

}
