// Prevayler(TM) - The Open-Source Prevalence Layer.
// Copyright (C) 2001 Klaus Wuestefeld.
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License version 2.1 as published by the Free Software Foundation. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.

package org.prevayler.demos.scalability;

import java.io.Serializable;
import java.util.*;
import java.math.BigDecimal;

public class Record implements Serializable {

	static private final Random random = new Random();
	static private final String largeString = generateLargeString();

	private final long id;
	private final String name;
	private final String string1;
	private final BigDecimal bigDecimal1;
	private final BigDecimal bigDecimal2;
	private final long date1;
	private final long date2;


	public Record(long id, String name, String string1, BigDecimal bigDecimal1, BigDecimal bigDecimal2, Date date1, Date date2) {
		this.id = id;
		this.name = name;
		this.string1 = string1;
		this.bigDecimal1 = bigDecimal1;
		this.bigDecimal2 = bigDecimal2;
		this.date1 = date1.getTime();
		this.date2 = date2.getTime();
	}

	Record(long id) {
		this.id = id;
		name = "NAME" + (id % 10000);

		string1 = (id % 10000) == 0
			? largeString + id
			: null;

		bigDecimal1 = randomBigDecimal();
		bigDecimal2 = randomBigDecimal();
		date1 = randomDate();
		date2 = randomDate();
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getString1() {
		return string1;
	}

	public BigDecimal getBigDecimal1() {
		return bigDecimal1;
	}

	public BigDecimal getBigDecimal2() {
		return bigDecimal2;
	}

	public Date getDate1() {
		return new Date(date1);
	}

	public Date getDate2() {
		return new Date(date2);
	}

	static private String generateLargeString() {
		char[] chars = new char[980];
		Arrays.fill(chars, 'A'); 
		return new String(chars);
	}

	static private BigDecimal randomBigDecimal() {
		return new BigDecimal(random.nextInt());
	}

	static private long randomDate() {
		return random.nextInt(10000000);
	}
}
