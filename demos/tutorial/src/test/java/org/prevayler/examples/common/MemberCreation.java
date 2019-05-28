package org.prevayler.examples.common;

import org.prevayler.TransactionWithQuery;

import java.util.Date;

public class MemberCreation implements TransactionWithQuery<Club, Member> {

  private static final long serialVersionUID = 1L;

  private final String name;

  public MemberCreation(String name) {
    this.name = name;
  }


  @Override
  public Member executeAndQuery(Club club, Date executionTime) {
    return club.createMember(name);
  }

}
