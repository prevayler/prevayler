package org.prevayler.examples.e105;

import org.junit.Test;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.Query;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class E105Test {

  @Test(expected = java.lang.AssertionError.class)
  public void testFailing() throws Exception {
    // This will update Entity#text outside of a transaction,
    // hence the change will NOT be written to the journal and is lost when closing Prevayler.
    new AbstractTest() {
      @Override
      protected void updateEntity(Prevayler<Root> prevayler, Entity entity, String value) throws Exception {
        entity.setText(value);
      }
    }.runTest();
  }

  @Test
  public void testPassing() throws Exception {
    // This will update Entity#text from within a transaction,
    // i.e. the change will be written to the journal and thus available after closing and restarting Prevayler.
    new AbstractTest() {
      @Override
      protected void updateEntity(Prevayler<Root> prevayler, Entity entity, String value) throws Exception {
        prevayler.execute(new UpdateEntityTextTransaction(entity.getIdentity(), value));
      }
    }.runTest();
  }

  @Test
  public void testPassingDueToSnapshot() throws Exception {
    // This will update Entity#text outside of a transaction
    // and then take a snapshot of the complete prevalent system.
    // Hence changes will NOT be written the the journal but still available in the latest snapshot
    // and thus still available after closing Prevayler.
    //
    // THIS IS NEITHER ENDORSED NOR RECOMMENDED USE OF PREVAYLER
    //
    // DON'T EVEN CONSIDER DOING THIS UNLESS YOU KNOW WHAT YOU ARE DOING,
    // IT IS HERE SIMPLY TO DEMONSTRATE THAT IT'S POSSIBLE.
    new AbstractTest() {
      @Override
      protected void updateEntity(Prevayler<Root> prevayler, Entity entity, String value) throws Exception {
        entity.setText(value);
        prevayler.takeSnapshot();
      }
    }.runTest();
  }


  private abstract class AbstractTest {

    protected void runTest() throws Exception {

      // Create or load existing prevalence layer from journal and/or snapshot.
      String dataPath = "target/PrevalenceBase_" + System.currentTimeMillis();
      Prevayler<Root> prevayler = PrevaylerFactory.createPrevayler(new Root(), dataPath);

      try {
        final String identity = UUID.randomUUID().toString();
        Entity entity = prevayler.execute(new CreateEntityTransaction(identity));

        assertNull("entity text values is supposed to be null after creation", entity.getText());

        String value = "A text value";
        updateEntity(prevayler, entity, value);

        assertEquals("entity text value is supposed to have been updated", value, entity.getText());

        // close and reopen prevalence so the journal is replayed
        prevayler.close();
        prevayler = PrevaylerFactory.createPrevayler(new Root(), dataPath);

        entity = prevayler.execute(new Query<Root, Entity>() {
          private static final long serialVersionUID = -4272231312465955589L;

          public Entity query(Root prevalentSystem, Date executionTime) throws Exception {
            return prevalentSystem.getEntities().get(identity);
          }
        });
        assertEquals("entity text value is supposed to have been updated after restart", value, entity.getText());

      } finally {
        prevayler.close();

      }

    }

    protected abstract void updateEntity(Prevayler<Root> prevayler, Entity entity, String value) throws Exception;

  }


}
