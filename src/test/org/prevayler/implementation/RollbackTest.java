package org.prevayler.implementation;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

public class RollbackTest extends PrevalenceTest {

	private Prevayler _prevayler;

    public void testRollback() throws Exception {
        _prevayler = PrevaylerFactory.createPrevayler(new AppendingSystem(), _testDirectory);

		append("a", "a");
		
		try {
			append("rollback", "a");
			throw new Exception("RuntimeException expected and not thrown.");
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
