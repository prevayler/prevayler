package org.prevayler.implementation;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

public class RollbackTest extends PrevalenceTest {

	private Prevayler _prevayler;

    public void testRollback() throws Exception {
		testRollback(PrevaylerFactory.createPrevayler(new AppendingSystem(), _testDirectory));
		testRollback(PrevaylerFactory.createTransientPrevayler(new AppendingSystem()));
    }

	private void testRollback(Prevayler prevayler) throws Exception {
		_prevayler = prevayler;

		append("a", "a");
		
		try {
			append("rollback", "ignored");
			fail("RuntimeException expected and not thrown.");
		} catch (RuntimeException rx) {
		}

		append("b", "ab");

		_prevayler = null;
	}

	private void append(String appendix, String expectedResult) throws Exception {
        _prevayler.execute(new Appendix(appendix));
        assertEquals(expectedResult, system().value());
    }

    private AppendingSystem system() {
        return (AppendingSystem) _prevayler.prevalentSystem();
    }
}
