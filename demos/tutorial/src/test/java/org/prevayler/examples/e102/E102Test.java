package org.prevayler.examples.e102;

import org.junit.Test;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.Query;
import org.prevayler.TransactionWithQuery;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;


public class E102Test {

  @Test(expected = java.lang.AssertionError.class)
  public void testFailing() throws Exception {

    // this will fail as a new random UUID will be assigned to the entity within the transaction
    // and will thus change every time journal is replayed!
    runTest(new NonDeterministicCreateEntityTransaction());

  }

  @Test
  public void testPassing() throws Exception {
    // this will not fail as a new random UUID is passed down to and is serialized with the transaction
    // and will thus be used every time the journal is replayed.
    runTest(new DeterministicCreateEntityTransaction(UUID.randomUUID().toString()));

  }


  private void runTest(TransactionWithQuery<Root, Entity> createEntityTransaction) throws Exception {

    // Create or load existing prevalence layer from journal and/or snapshot.
    String dataPath = "target/PrevalenceBase_" + System.currentTimeMillis();
    Prevayler<Root> prevayler = PrevaylerFactory.createPrevayler(new Root(), dataPath);

    try {
      Entity entity = prevayler.execute(createEntityTransaction);
      final String identityOfEntityWhenInitiallyCreated = entity.getIdentity();

      assertSame("entity and query response is supposed to be the same instance", entity, prevayler.execute(new Query<Root, Entity>() {
        private static final long serialVersionUID = 2345967443241959260L;

        public Entity query(Root prevalentSystem, Date executionTime) throws Exception {
          return prevalentSystem.getEntities().get(identityOfEntityWhenInitiallyCreated);
        }
      }));

      assertEquals("only one entity is supposed to exist in the root", 1, (int) prevayler.execute(new Query<Root, Integer>() {
        private static final long serialVersionUID = -3193756627974872039L;

        public Integer query(Root prevalentSystem, Date executionTime) throws Exception {
          return prevalentSystem.getEntities().size();
        }
      }));

      // close and reopen prevalence so the journal is replayed
      prevayler.close();
      prevayler = PrevaylerFactory.createPrevayler(new Root(), dataPath);

      assertEquals("only one entity is supposed to exist in the root", 1, (int) prevayler.execute(new Query<Root, Integer>() {
        private static final long serialVersionUID = -4739548462309688139L;

        public Integer query(Root prevalentSystem, Date executionTime) throws Exception {
          return prevalentSystem.getEntities().size();
        }
      }));

      String identityOfTheOnlyEntityInRoot = prevayler.execute(new Query<Root, String>() {
        private static final long serialVersionUID = -449826861014013447L;

        public String query(Root prevalentSystem, Date executionTime) throws Exception {
          return prevalentSystem.getEntities().values().iterator().next().getIdentity();
        }
      });

      // this will fail if the setting of identity is non deterministic
      assertEquals(identityOfEntityWhenInitiallyCreated, identityOfTheOnlyEntityInRoot);

    } finally {
      prevayler.close();

    }


  }


}
