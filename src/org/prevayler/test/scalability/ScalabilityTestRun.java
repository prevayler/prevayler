// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.test.scalability;

import java.util.*;
import java.text.DecimalFormat;
import org.prevayler.util.*;

/** Represents a single run of a scalability test. To understand the implementation of this class, you must be familiar with Prevayler's Scalability Test (run org.prevayler.test.scalability.ScalabilityTest).
*/
abstract class ScalabilityTestRun {

	static private final long ROUND_DURATION_MILLIS = 1000 * 60;

	private ScalabilityTestSubject subject;
	private double bestRoundOperationsPerSecond;
	private int bestRoundThreads;

	private final List connectionCache = new LinkedList();

	private long operationCount = 0;
	private final Object roundMonitor = new Object();
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


	protected ScalabilityTestRun(ScalabilityTestSubject subject, int minThreads, int maxThreads) {
		if (minThreads > maxThreads) throw new IllegalArgumentException("The minimum number of threads cannot be greater than the maximum number.");
		if (minThreads < 1) throw new IllegalArgumentException("The minimum number of threads cannot be smaller than one.");

		out("\nRunning " + name() + " (Subject: " + subject.name() + ").");

		this.subject = subject;
		subject.replaceAllRecords(new RecordIterator());

		System.gc();
		out("Each round will take approx. " + ROUND_DURATION_MILLIS / 1000 + " seconds to run...");
		performTest(minThreads, maxThreads);

		out("BEST ROUND: " + getResult());
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

			out(toResultString(operationsPerSecond, threads));
			out("Total memory: " + Runtime.getRuntime().totalMemory());

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
		
		return (operationCount - initialOperationCount) / stopWatch.secondsEllapsed();
	}


	private void startThreads(int threads) {
		isRoundFinished = false;

		for(int i = 1; i <= threads; i++) {
			startThread();
		}
	}


	private void startThread() {
		(new Thread() {
			public void run() {
				Object connection = acquireConnection();

				while (!isRoundFinished) {
					long operation;
					synchronized (roundMonitor) {
						operation = operationCount;
						operationCount++;
					}
					executeOperation(connection, operation);
				}

				synchronized (roundMonitor) {
					connectionCache.add(connection);
					activeRoundThreads--;
				}
			}
		}).start();

		activeRoundThreads++;
	}


	protected abstract void executeOperation(Object connection, long operation);


	private Object acquireConnection() {
		synchronized (roundMonitor) {
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


	static private void sleep() {
		try {
			Thread.sleep(ROUND_DURATION_MILLIS);
		} catch (InterruptedException ix) {
			throw new RuntimeException("Unexpected InterruptedException.");
		}
	}

	
	static private void out(Object obj) {
		System.out.println(obj);
	}
}
