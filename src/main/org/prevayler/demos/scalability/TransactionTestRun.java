package org.prevayler.demos.scalability;


/** Tests insert, update and delete scalability.
*/
public class TransactionTestRun extends ScalabilityTestRun {

	private int halfTheObjects;


	public TransactionTestRun(ScalabilityTestSubject subject, int numberOfObjects, int minThreads, int maxThreads) {
		super(subject, numberOfObjects, minThreads, maxThreads);
	}

	protected String name() {
		return "Transaction Test";
	}

	protected void prepare() {
		super.prepare();
		halfTheObjects = numberOfObjects / 2;
	}

	/**
	* Deletes records from id zero            to id halfTheObjects - 1.
	* Updates records from id halfTheObjects  to id numberOfObjects - 1.
	* Inserts records from id numberOfObjects to id numberOfObjects + halfTheObjects - 1.
	* Every time halfTheObjects operations have completed, all ranges are shifted up by halfTheObjects.
	* Example for one million objects:
	* Deletes records from id 0000000 to id 0499999.
	* Updates records from id 0500000 to id 0999999.
	* Inserts records from id 1000000 to id 1499999.
	* Every time 500000 operations have completed, all ranges are shifted up by 500000.
	*/
	protected void executeOperation(Object connection, long operationSequence) {
		Record recordToInsert = new Record(numberOfObjects + operationSequence);
		long idToDelete = spreadId(operationSequence);
		Record recordToUpdate = new Record(halfTheObjects + idToDelete);

		((TransactionConnection)connection).performTransaction(recordToInsert, recordToUpdate, idToDelete);
	}


	/** Spreads out the id values so that deletes and updates are not done contiguously.
	*/
	private long spreadId(long id) {
		return (id / halfTheObjects) * halfTheObjects   //Step function.
			+ ((id * 16807) % halfTheObjects);   //16807 == 7 * 7 * 7 * 7 * 7. 16807 is relatively prime to 50000, 500000 and 5000000. This guarantees that all ids in the range will be covered.
	}
}
