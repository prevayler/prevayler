package org.prevayler.examples.common;

import java.util.Date;

import org.prevayler.TransactionWithQuery;

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
