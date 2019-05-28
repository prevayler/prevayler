package org.prevayler.examples.e104;

import org.prevayler.Transaction;
import org.prevayler.examples.common.Club;
import org.prevayler.examples.common.Member;

import java.util.Date;

public class NameChangeWithProblem implements Transaction<Club> {

  private static final long serialVersionUID = 1L;

  private final Member member;
  private final String newName;


  NameChangeWithProblem(Member member, String newName) {
    this.member = member;
    this.newName = newName;
  }

  @Override
  public void executeOn(Club club, Date executionTime) {
    //The member field is now a deep clone because this transaction as serialized and deserialized by Prevayler.
    member.setName(newName); //The clone is being modified instead of the actual member inside the club.
  }

}
