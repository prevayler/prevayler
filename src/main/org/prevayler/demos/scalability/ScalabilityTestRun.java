package org.prevayler.demos.scalability;

import org.prevayler.foundation.Cool;
import org.prevayler.foundation.StopWatch;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

/** Represents a single run of a scalability test. To understand the implementation of this class, you must be familiar with Prevayler's Scalability Test (run org.prevayler.test.scalability.ScalabilityTest).
*/
abstract class ScalabilityTestRun {

	static private final long ROUND_DURATION_MILLIS = 1000 * 20;

	private final ScalabilityTestSubject subject;
	protected final int numberOfObjects;

	private double bestRoundOperationsPerSecond;
	private int bestRoundThreads;

	private final List connectionCache = new LinkedList();

	private long operationCount = 0;
	private long lastOperation = 0;
	private boolean isRoundFinished;
	private int activeRoundThreads = 0;


	/** @return Example: "123.12 operations/second (12 threads)".
	*/
	public String getResult() {
		return toResultString(bestRoundOperationsPerSecond, bestRoundThreads);
	}


	public double getOperationsPerSecond() {
		return bestRoundOperationsPerSecond;
	}


	protected ScalabilityTestRun(ScalabilityTestSubject subject, int numberOfObjects, int minThreads, int maxThreads) {
		if (minThreads > maxThreads) throw new IllegalArgumentException("The minimum number of threads cannot be greater than the maximum number.");
		if (minThreads < 1) throw new IllegalArgumentException("The minimum number of threads cannot be smaller than one.");

		this.subject = subject;
		this.numberOfObjects = numberOfObjects;

		out("\n\n========= Running " + name() + " (" + (maxThreads - minThreads + 1) + " rounds). Subject: " + subject.name() + "...");
		prepare();

		out("Each round will take approx. " + ROUND_DURATION_MILLIS / 1000 + " seconds to run...");
		performTest(minThreads, maxThreads);
		out("\n----------- BEST ROUND: " + getResult());
	}

	protected void prepare() {
		subject.replaceAllRecords(numberOfObjects);
		System.gc();
	}


	/** @return The name of the test to be executed. Example: "Prevayler Query Test".
	*/
	protected abstract String name();


	private void performTest(int minThreads, int maxThreads) {

		int threads = minThreads;
		while (threads <= maxThreads) {
			double operationsPerSecond = performRound(threads);

			if (operationsPerSecond > bestRoundOperationsPerSecond) {
				bestRoundOperationsPerSecond = operationsPerSecond;
				bestRoundThreads = threads;
			}

			threads++;
		}
	}


	/** @return The number of operations the test managed to execute per second with the given number of threads.
	*/
	private double performRound(int threads) {
		long initialOperationCount = operationCount;
		StopWatch stopWatch = StopWatch.start();

		startThreads(threads);
		sleep();
		stopThreads();

		double secondsEllapsed = stopWatch.secondsEllapsed();
		double operationsPerSecond = (operationCount - initialOperationCount) / secondsEllapsed;

		out("\nMemory used: " + Runtime.getRuntime().totalMemory());
		subject.reportResourcesUsed(System.out);
		out("Seconds ellapsed: " + secondsEllapsed);
		out("--------- Round Result: " + toResultString(operationsPerSecond, threads));

		return operationsPerSecond;
	}


	private void startThreads(int threads) {
		isRoundFinished = false;

		int i = 1;
		while(i <= threads) {
			startThread(lastOperation + i, threads);
			i++;
		}
	}


	private void startThread(final long startingOperation, final int operationIncrement) {
		(new Thread() {
			public void run() {
				try {
					Object connection = acquireConnection();

					long operation = startingOperation;
					while (!isRoundFinished) {
						executeOperation(connection, operation);
						operation += operationIncrement;
					}

					synchronized (connectionCache) {
						connectionCache.add(connection);
						operationCount += (operation - startingOperation) / operationIncrement;
						if (lastOperation < operation) lastOperation = operation;
						activeRoundThreads--;
					}

				} catch (OutOfMemoryError err) {
					outOfMemory();
				}
			}
		}).start();

		activeRoundThreads++;
	}


	protected abstract void executeOperation(Object connection, long operation);


	private Object acquireConnection() {
		synchronized (connectionCache) {
			return connectionCache.isEmpty()
				? subject.createTestConnection()
				: connectionCache.remove(0);
		}
	}


	private void stopThreads() {
		isRoundFinished = true;
		while (activeRoundThreads != 0) {
			Thread.yield();
		}
	}


	static private String toResultString(double operationsPerSecond, int threads) {
		String operations = new DecimalFormat("0.00").format(operationsPerSecond);
		return "" + operations + " operations/second (" + threads + " threads)";
	}

	static void outOfMemory() {
		System.gc();
		out(
			"\n\nOutOfMemoryError.\n" +
			"===========================================================\n" +
			"The VM must be started with a sufficient maximum heap size.\n" +
			"Example for Linux and Windows:  java -Xmx512000000 ...\n\n"
		);
	}

	static private void sleep() {
		Cool.sleep(ROUND_DURATION_MILLIS);
	}

	
	static private void out(Object obj) {
		System.out.println(obj);
	}
}
