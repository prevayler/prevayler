package org.prevayler.examples.e104;

import org.junit.Ignore;
import org.junit.Test;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

import java.util.UUID;

import static org.junit.Assert.*;

public class E104Test {

  @Test(expected = java.lang.AssertionError.class)
  public void testFailing() throws Exception {

    // this test will fail, and the reason is that
    // the aggregate entity passed down to the transaction is a business object
    // that will be serialized, i.e. the aggregate instance will become a deep clone of the business object
    // rather than referencing the business object in the prevalent system.

    new AbstractTest() {
      @Override
      protected void executeUpdateAggregateTransaction(Prevayler<Root> prevayler, Entity firstEntity, Entity secondEntity) {
        prevayler.execute(new PrevalentInitiationProblemUpdateEntityAggregate(firstEntity.getIdentity(), secondEntity));
      }
    }.runTest();
  }


  @Test
  public void testPassing() throws Exception {

    // this will not fail as we pass down the identities of the entities,
    // look them up in the root and then reference to the instance we found in root.

    new AbstractTest() {
      @Override
      protected void executeUpdateAggregateTransaction(Prevayler<Root> prevayler, Entity firstEntity, Entity secondEntity) {
        prevayler.execute(new UpdateEntityAggregate(firstEntity.getIdentity(), secondEntity.getIdentity()));
      }
    }.runTest();
  }

  private abstract class AbstractTest {

    protected void runTest() throws Exception {
      // Create or load existing prevalence layer from journal and/or snapshot.
      Prevayler<Root> prevayler = PrevaylerFactory.createPrevayler(new Root(), "target/PrevalenceBase_" + System.currentTimeMillis());
      try {
        Entity firstEntity = prevayler.execute(new CreateEntityTransaction(UUID.randomUUID().toString()));
        Entity secondEntity = prevayler.execute(new CreateEntityTransaction(UUID.randomUUID().toString()));

        assertNull("at this point no aggregate is supposed to be set in firstEntity", firstEntity.getAggregate());

        executeUpdateAggregateTransaction(prevayler, firstEntity, secondEntity);

        assertNotNull("at this point an aggregate is supposed to be set in firstEntity", firstEntity.getAggregate());

        assertSame("at this point we expect the aggregate in firstEntity to be the same instance as secondEntity", secondEntity, firstEntity.getAggregate());

      } finally {
        prevayler.close();

      }
    }

    protected abstract void executeUpdateAggregateTransaction(Prevayler<Root> prevayler, Entity firstEntity, Entity secondEntity);

  }
}
