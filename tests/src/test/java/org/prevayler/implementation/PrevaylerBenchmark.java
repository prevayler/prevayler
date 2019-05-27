package org.prevayler.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.Query;
import org.prevayler.Transaction;

/**
 * Defines a benchmark for Prevayler.
 * Also works as a test.<p/>
 *
 * The benchmark executes a specified number of operations on the prevayler
 * using specified number of threads.
 * Each thread performs almost the same number of operations as the others.
 */
public class PrevaylerBenchmark {
  static final Logger LOG = Logger.getLogger(PrevaylerBenchmark.class.getName());
  final static String TEST_DATA_DIRECTORY = "target/data/prevayler/";
  final static int NUM_TRANSACTIONS = 100;

  static Prevayler<Map<String, Serializable>> prevayler;

  public static class AddTx implements Transaction<Map<String, Serializable>> {
    private static final long serialVersionUID = 1L;

    private String key;
    private Serializable value;

    public AddTx(final String key, final Serializable data) {
      this.key = key;
      this.value = data;
    }

    @Override
    public void executeOn(Map<String, Serializable> prevalentSystem, Date executionTime) {
      Map<String, Serializable> map = prevalentSystem;
      map.put(key, value);
    }

    @Override
    public String toString() {
      return "(" + key + ", " + value + ")";
    }
  }

  public static class GetQuery implements Query<Map<String, Serializable>, Serializable> {
    private static final long serialVersionUID = 1L;

    private String key;

    public GetQuery(String key) {
      this.key = key;
    }

    @Override
    public Serializable query(Map<String, Serializable> prevalentSystem, Date executionTime)
        throws Exception {
      Map<String, Serializable> map = prevalentSystem;
      return map.get(key);
    }
  }

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    deletePrevaylerDirectory();

    prevayler = createPrevayler(true);
    Map<String, Serializable> map = prevayler.prevalentSystem();
    assertEquals("Prevayler size", 0, map.size());
    LOG.info("Created empty prevayler");
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    if(prevayler != null) prevayler.close();
    // deletePrevaylerDirectory();
    LOG.info("Closed prevayler");
  }

  static Prevayler<Map<String, Serializable>>
  createPrevayler(boolean journalDiskSync) throws Exception {
    PrevaylerFactory<Map<String, Serializable>> factory =
        new PrevaylerFactory<Map<String, Serializable>>();
    factory.configurePrevalentSystem(new ConcurrentHashMap<String, Serializable>());
    factory.configurePrevalenceDirectory(TEST_DATA_DIRECTORY);
    factory.configureJournalDiskSync(journalDiskSync);
    factory.configureTransactionDeepCopy(false);
    return factory.create();
  }

  @Test
  public void testSingleThreaded() throws Exception {
    AddOp bench = new AddOp(Arrays.asList(new String[]
        {"-op", "add", "-threads", "1", "-numTx", "100"}));
    bench.benchmark();
    bench.printResults();

    // verify
    for(int i = 0; i < NUM_TRANSACTIONS; i++) {
      String key = "key_" + 0 + "_" + i;
      String data = "data_" + 0 + "_" + i;
      Serializable val = prevayler.execute(new GetQuery(key));
      assertEquals("Prevayler's value is different.", data, val);
    }
  }

  @Test
  public void testMultithreaded() throws Exception {
    AddOp bench = new AddOp(Arrays.asList(new String[]
        {"-op", "add", "-threads", "128", "-numTx", "5000"}));
    bench.benchmark();
    bench.printResults();
  }

  private static void deletePrevaylerDirectory() {
    File prevaylerDir = new File(TEST_DATA_DIRECTORY);
    if(prevaylerDir.exists())
      assertTrue("Cannot delete prevayler directory",
          delete(prevaylerDir, true));
  }

  /**
   * Delete file or directory
   * @param file path to delete
   * @param recursive delete recursively if true
   * @return true if successfully deleted
   */
  public static boolean delete(File file, boolean recursive) {
    if (!file.exists()) {
        return true;
    }

    if (!recursive || !file.isDirectory())
        return file.delete();

    String[] list = file.list();
    for (int i = 0; i < list.length; i++) {
        if (!delete(new File(file, list[i]), true))
            return false;
    }

    return file.delete();
  }

  public static long now() {
    // return System.nanoTime();
    return System.currentTimeMillis();
  }

  /**
   * One of the threads that perform stats operations.
   */
  private static class StatsDaemon extends Thread {
    private final int daemonId;
    private int opsPerThread;
    private String arg1;      // argument passed to executeOp()
    private volatile int  localNumOpsExecuted = 0;
    private volatile long localCumulativeTime = 0;
    private final OperationStatsBase statsOp;

    StatsDaemon(int daemonId, int nrOps, OperationStatsBase op) {
      this.daemonId = daemonId;
      this.opsPerThread = nrOps;
      this.statsOp = op;
      setName(toString());
    }

    @Override
    public void run() {
      localNumOpsExecuted = 0;
      localCumulativeTime = 0;
      arg1 = statsOp.getExecutionArgument(daemonId);
      try {
        benchmarkOne();
      } catch(IOException ex) {
        LOG.log(Level.SEVERE, "StatsDaemon " + daemonId + " failed: \n", ex);
      }
    }

    @Override
    public String toString() {
      return "StatsDaemon-" + daemonId;
    }

    void benchmarkOne() throws IOException {
      for(int idx = 0; idx < opsPerThread; idx++) {
        long stat = statsOp.executeOp(daemonId, idx, arg1);
        localNumOpsExecuted++;
        localCumulativeTime += stat;
      }
    }

    boolean isInProgress() {
      return localNumOpsExecuted < opsPerThread;
    }
  }

  /**
   * Base class for collecting operation statistics.
   * 
   * Overload this class in order to run statistics for a 
   * specific name-node operation.
   */
  abstract static class OperationStatsBase {
    protected static final String OP_ALL_NAME = "all";
    protected static final String OP_ALL_USAGE = "-op all <other ops options>";

    protected int  numThreads = 0;        // number of threads
    protected int  numOpsRequired = 0;    // number of operations requested
    protected int  numOpsExecuted = 0;    // number of operations executed
    protected long cumulativeTime = 0;    // sum of times for each op
    protected long elapsedTime = 0;       // time from start to finish
    protected boolean keepResults = false;// don't clean base directory on exit
    protected Level logLevel;             // logging level, OFF by default

    protected List<StatsDaemon> daemons;

    /**
     * Operation name.
     */
    abstract String getOpName();

    /**
     * Parse command line arguments.
     * 
     * @param args arguments
     * @throws IOException
     */
    abstract void parseArguments(List<String> args) throws IOException;

    /**
     * Generate inputs for each daemon thread.
     * 
     * @param opsPerThread number of inputs for each thread.
     * @throws IOException
     */
    abstract void generateInputs(int[] opsPerThread) throws IOException;

    /**
     * This corresponds to the arg1 argument of 
     * {@link #executeOp(int, int, String)}, which can have different meanings
     * depending on the operation performed.
     * 
     * @param daemonId id of the daemon calling this method
     * @return the argument
     */
    abstract String getExecutionArgument(int daemonId);

    /**
     * Execute name-node operation.
     * 
     * @param daemonId id of the daemon calling this method.
     * @param inputIdx serial index of the operation called by the deamon.
     * @param arg1 operation specific argument.
     * @return time of the individual name-node call.
     * @throws IOException
     */
    abstract long executeOp(int daemonId, int inputIdx, String arg1) throws IOException;

    /**
     * Print the results of the benchmarking.
     */
    abstract void printResults();

    OperationStatsBase() {
      numOpsRequired = 10;
      numThreads = 3;
      logLevel = Level.OFF;
    }

    void benchmark() throws IOException {
      daemons = new ArrayList<StatsDaemon>();
      long start = 0;
      try {
        numOpsExecuted = 0;
        cumulativeTime = 0;
        if(numThreads < 1)
          return;
        int tIdx = 0; // thread index < nrThreads
        int opsPerThread[] = new int[numThreads];
        for(int opsScheduled = 0; opsScheduled < numOpsRequired; 
                                  opsScheduled += opsPerThread[tIdx++]) {
          // execute  in a separate thread
          opsPerThread[tIdx] = (numOpsRequired-opsScheduled)/(numThreads-tIdx);
          if(opsPerThread[tIdx] == 0)
            opsPerThread[tIdx] = 1;
        }
        // if numThreads > numOpsRequired then the remaining threads will do nothing
        for(; tIdx < numThreads; tIdx++)
          opsPerThread[tIdx] = 0;
        generateInputs(opsPerThread);
        for(tIdx=0; tIdx < numThreads; tIdx++)
          daemons.add(new StatsDaemon(tIdx, opsPerThread[tIdx], this));
        start = now();
        LOG.info("Starting " + numOpsRequired + " " + getOpName() + "(s)"
            + " using " + numThreads + " threads.");
        for(StatsDaemon d : daemons)
          d.start();
      } finally {
        while(isInProgress()) {
          // wait until all threads complete
        }
        elapsedTime = now() - start;
        for(StatsDaemon d : daemons) {
          incrementStats(d.localNumOpsExecuted, d.localCumulativeTime);
        }
      }
    }

    private boolean isInProgress() {
      for(StatsDaemon d : daemons)
        if(d.isInProgress())
          return true;
      return false;
    }

    int getNumOpsExecuted() {
      return numOpsExecuted;
    }

    long getCumulativeTime() {
      return cumulativeTime;
    }

    long getElapsedTime() {
      return elapsedTime;
    }

    long getAverageTime() {
      return numOpsExecuted == 0 ? 0 : cumulativeTime / numOpsExecuted;
    }

    double getOpsPerSecond() {
      return elapsedTime == 0 ? 0 : 1000*(double)numOpsExecuted / elapsedTime;
    }

    /* String getBaseDir() {
      return baseDir;
    } */

    String getClientName(int idx) {
      return getOpName() + "-client-" + idx;
    }

    void incrementStats(int ops, long time) {
      numOpsExecuted += ops;
      cumulativeTime += time;
    }

    /**
     * Parse first 2 arguments, corresponding to the "-op" option.
     * 
     * @param args argument list
     * @return true if operation is all, which means that options not related
     * to this operation should be ignored, or false otherwise, meaning
     * that usage should be printed when an unrelated option is encountered.
     */
    protected boolean verifyOpArgument(List<String> args) {
      if(args.size() < 2 || ! args.get(0).startsWith("-op"))
        printUsage();

      // process common options
      int llIndex = args.indexOf("-logLevel");
      if(llIndex >= 0) {
        if(args.size() <= llIndex + 1)
          printUsage();
        logLevel = Level.parse(args.get(llIndex+1));
        args.remove(llIndex+1);
        args.remove(llIndex);
      }

      String type = args.get(1);
      if(OP_ALL_NAME.equals(type)) {
        type = getOpName();
        return true;
      }
      if(!getOpName().equals(type))
        printUsage();
      return false;
    }

    void printStats() {
      LOG.info("--- " + getOpName() + " stats  ---");
      LOG.info("# operations: " + getNumOpsExecuted());
      LOG.info("Elapsed Time: " + getElapsedTime());
      LOG.info(" Ops per sec: " + getOpsPerSecond());
      LOG.info("Average Time: " + getAverageTime());
    }
  }

  /**
   * Add operation.
   */
  public static class AddOp extends OperationStatsBase {
    static final String OP_ADD_NAME = "add";
    static final String OP_ADD_USAGE = "-op add [-threads T] [-numTx N] ";

    protected AddTx[][] transactions;

    AddOp(List<String> args) {
      super();
      parseArguments(args);
    }

    @Override
    String getOpName() {
      return OP_ADD_NAME;
    }

    @Override
    void parseArguments(List<String> args) {
      boolean ignoreUnrelatedOptions = verifyOpArgument(args);
      for (int i = 2; i < args.size(); i++) {       // parse command line
        if(args.get(i).equals("-numTx")) {
          if(i+1 == args.size())  printUsage();
          numOpsRequired = Integer.parseInt(args.get(++i));
        } else if(args.get(i).equals("-threads")) {
          if(i+1 == args.size())  printUsage();
          numThreads = Integer.parseInt(args.get(++i));
        } else if(!ignoreUnrelatedOptions)
          printUsage();
      }
    }

    @Override
    void generateInputs(int[] opsPerThread) throws IOException {
      assert opsPerThread.length == numThreads : "Error opsPerThread.length";
      LOG.info("Generate " + numOpsRequired + " inputs for " + getOpName());
      transactions = new AddTx[numThreads][];
      for(int idx=0; idx < numThreads; idx++) {
        int threadOps = opsPerThread[idx];
        transactions[idx] = new AddTx[threadOps];
        for(int jdx=0; jdx < threadOps; jdx++) {
          transactions[idx][jdx] =
              new AddTx("key_" + idx + "_" + jdx, "data_" + idx + "_" + jdx);
        }
      }
    }

    /**
     * returns client name
     */
    @Override
    String getExecutionArgument(int daemonId) {
      return getClientName(daemonId);
    }

    /**
     * Do add operation.
     */
    @Override
    long executeOp(int daemonId, int inputIdx, String clientName)
        throws IOException {
      long start = now();
      prevayler.execute(transactions[daemonId][inputIdx]);
      long end = now();
      return end-start;
    }

    @Override
    void printResults() {
      LOG.info("--- " + getOpName() + " inputs ---");
      LOG.info("numTx = " + numOpsRequired);
      LOG.info("nrThreads = " + numThreads);
      printStats();
    }
  }

  static void printUsage() {
    System.err.println("Usage: " + PrevaylerBenchmark.class.getSimpleName()
        + "\n\t"    + OperationStatsBase.OP_ALL_USAGE
        + " | \n\t" + AddOp.OP_ADD_USAGE
    );
    System.exit(-1);
  }

  public static void main(String[] args) throws Exception {
    setUpBeforeClass();
    AddOp bench = null;
    try {
      bench = new AddOp(Arrays.asList(args));
      bench.benchmark();
      bench.printResults();
    } finally {
      tearDownAfterClass();
    }
  }
}
