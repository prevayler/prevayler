package org.prevayler.examples.e103;

import org.junit.Test;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.Query;
import org.prevayler.TransactionWithQuery;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class E103Test {

  @Test(expected = java.lang.AssertionError.class)
  public void testFailing() throws Exception {
    runTest(new BadTimestampCreateEntityTransaction(UUID.randomUUID().toString()));
  }

  @Test
  public void testPassing() throws Exception {
    runTest(new CreateEntityTransaction(UUID.randomUUID().toString()));
  }

  private void runTest(TransactionWithQuery<Root, Entity> createEntityTransaction) throws Exception {

    // Create or load existing prevalence layer from journal and/or snapshot.
    String dataPath = "target/PrevalenceBase_" + System.currentTimeMillis();
    Prevayler<Root> prevayler = PrevaylerFactory.createPrevayler(new Root(), dataPath);

    try {
      final Entity entity = prevayler.execute(createEntityTransaction);
      final long timestampWhenInitiallyCreated = entity.getCreated();

      // close and reopen prevalence so the journal is replayed
      prevayler.close();
      prevayler = PrevaylerFactory.createPrevayler(new Root(), dataPath);

      long timestampAfterRestart = prevayler.execute(new Query<Root, Long>() {
        private static final long serialVersionUID = 2206397764164885775L;

        public Long query(Root prevalentSystem, Date executionTime) throws Exception {
          return prevalentSystem.getEntities().get(entity.getIdentity()).getCreated();
        }
      });

      assertEquals("timestamp should not have changed", timestampWhenInitiallyCreated, timestampAfterRestart);

    } finally {
      prevayler.close();

    }


  }

}
