package org.prevayler.examples.common;

import org.prevayler.Transaction;

import java.util.Date;

public class NameChange implements Transaction<Club> {

  private static final long serialVersionUID = 1L;

  private final int number;
  private final String newName;

  public NameChange(Member member, String newName) {
    this.number = member.number();
    this.newName = newName;
  }

  @Override
  public void executeOn(Club club, Date executionTime) {
    club.member(number).setName(newName);
  }

}
