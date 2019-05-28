package org.prevayler.examples.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Club implements Serializable {

  private static final long serialVersionUID = 1L;

  List<Member> members = new ArrayList<Member>();

  public Member member(int number) {
    return members.get(number);
  }

  public Member createMember(String name) {
    int number = members.size();
    Member ret = new Member(number, name);
    members.add(ret);
    return ret;
  }

}
