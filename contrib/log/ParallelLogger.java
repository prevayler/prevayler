
	synchronized void advanceClockTo(Date newTime) {   //Synchronized so that a snapshot cannot occur during this method and block the transaction queue.
		_realTime = newTime;
		synchronized (_queueMonitor) {
			if (_lastExecuterInQueue != null) return;   // The transactions in the queue will advance the clock at their own pace.
			super.advanceClockTo(newTime);
		}
	}

	private class TransactionExecuter {

		private final Transaction _transaction;
		private final TransactionLogger _transactionLogger;
		private TransactionExecuter _previousExecuter;
		private Date _time;
		private long _sequence;

		TransactionExecuter(Transaction transaction) throws IOException {
			_transaction = transaction;
			_transactionLogger = _transactionLogManager.availableTransactionLogger();
		}

		synchronized Object execute() throws Exception {
			_transactionLogger.write(_transaction);
			queueUp();
			_transactionLogger.stamp(_time.getTime(), _sequence);

			synchronized (executerToWaitFor()) {
				_transactionLogger.sync();   // sync() is done as the last thing before execute(Transaction) for greater performance scalability. This allows other transactions to be written to the same file and be sync'd with a single sync() call.
				if (_sequence != _systemVersion + 1) throw new RuntimeException("Unexpected state. Transaction sequence is " + _sequence + " but should be " + (_systemVersion + 1));   // After J2SE 1.4 this would be an assert.

				AcidPrevayler.super.advanceClockTo(_time);
				try {
					return AcidPrevayler.super.execute(_transaction);
				} finally {
					endMyTurn();
				}
			}
		}

		private void queueUp() {
			synchronized (_queueMonitor) {
				_previousExecuter = _lastExecuterInQueue;
				_lastExecuterInQueue = this;

				_time = _realTime;
				_sequence = _previousExecuter == null
					? _systemVersion + 1
					: _previousExecuter._sequence + 1;
			}
		}

		private Object executerToWaitFor() {
			return _previousExecuter == null
				? this   // This means I don't have to wait.
				: _previousExecuter;
		}

		private void endMyTurn() {
			synchronized (_queueMonitor) {
				if (_lastExecuterInQueue == this) _lastExecuterInQueue = null;   // The clock can now advance freely.
			}
			_previousExecuter = null;   // Free for Garbage Collection.
		}

	}

}
