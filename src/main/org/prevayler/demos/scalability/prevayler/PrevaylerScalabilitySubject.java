package org.prevayler.demos.scalability.prevayler;

import org.prevayler.Prevayler;
import org.prevayler.demos.scalability.ScalabilityTestSubject;

abstract class PrevaylerScalabilitySubject implements ScalabilityTestSubject {

	protected Prevayler prevayler;

	
	{System.gc();}


	public String name() { return "Prevayler"; }


	public void replaceAllRecords(int records) {
		try {

			prevayler.execute(new AllRecordsReplacement(records));

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Unexpected Exception: " + ex);
		}
	}

}
