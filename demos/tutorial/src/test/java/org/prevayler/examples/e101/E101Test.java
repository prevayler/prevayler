package org.prevayler.examples.e101;

import org.junit.Test;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.Query;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;

public class E101Test {

  @Test
  public void test() throws Exception {

    // Create or load existing prevalence layer from journal and/or snapshot.
    Prevayler<Root> prevayler = PrevaylerFactory.createPrevayler(new Root(), "target/PrevalenceBase_" + System.currentTimeMillis());
    try {

      final Person person = prevayler.execute(new CreatePersonTransaction(UUID.randomUUID().toString()));

      final String nameOfPerson = "John Doe";

      prevayler.execute(new UpdatePersonNameTransaction(person.getIdentity(), nameOfPerson));
      assertEquals(nameOfPerson, person.getName());

      Person queryResponse = prevayler.execute(new GetPerson(person.getIdentity()));
      assertSame("person and queryResponse are supposed to be the same object instance!", person, queryResponse);

      Person removed = prevayler.execute(new DeletePersonTransaction(person.getIdentity()));
      assertSame("person and removed are supposed to be the same object instance!", person, removed);

      assertTrue("There are not supposed to be any persons in the root at this point!",
          prevayler.execute(new Query<Root, Boolean>() {
            private static final long serialVersionUID = -96319481126700055L;

            public Boolean query(Root prevalentSystem, Date executionTime) throws Exception {
              return prevalentSystem.getPersons().isEmpty();
            }
          }));

    } finally {
      prevayler.close();

    }

  }

}
