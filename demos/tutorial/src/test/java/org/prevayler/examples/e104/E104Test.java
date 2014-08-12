package org.prevayler.examples.e104;

import org.junit.Test;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.examples.common.Club;
import org.prevayler.examples.common.Member;
import org.prevayler.examples.common.MemberCreation;
import org.prevayler.examples.common.NameChange;

import static org.junit.Assert.assertEquals;

public class E104Test {

  Prevayler<Club> prevayler = initPrevayler();


  @Test
  public void testInitiationProblem() throws Exception {
    Member member = createMember("John");

    prevayler.execute(new NameChange(member, "John S"));
    assertEquals("John S", member.name());

    prevayler.execute(new NameChangeWithProblem(member, "John Smith"));
    assertEquals("John S", member.name()); //The name change did not work because transactions are serialized and deserialized by Prevayler, producing a deep clone. The person object in the transaction is no longer the object we passed in but a clone!

    prevayler.close();
  }


  private Member createMember(String name) throws Exception {
    return prevayler.execute(new MemberCreation(name));
  }


  private static Prevayler<Club> initPrevayler() {
    String dataPath = "target/PrevalenceBase_" + System.currentTimeMillis();
    try {
      return PrevaylerFactory.createPrevayler(new Club(), dataPath);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

}


