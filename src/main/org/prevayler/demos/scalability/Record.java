package org.prevayler.demos.scalability;

import java.io.Serializable;
import java.util.*;
import java.math.BigDecimal;

public class Record implements Serializable {

	private static final long serialVersionUID = 7098880197177237832L;

	private static final String largeString = generateLargeString();

	private final long id;
	private final String name;
	private final String string1;
	private final BigDecimal bigDecimal1;
	private final BigDecimal bigDecimal2;
	private final long date1;
	private final long date2;

	private static final Random RANDOM = new Random();


	public Record(long id) {
		this(id, RANDOM);
	}

	
	public Record(long id, Random random) {
		this(
				id,
				"NAME" + (id % 10000),
				(id % 10000) == 0 ? largeString + id : null,
				new BigDecimal(random.nextInt()),
				new BigDecimal(random.nextInt()),
				new Date(random.nextInt(10000000)),
				new Date(random.nextInt(10000000))
			);
	}

	public Record(long id, String name, String string1, BigDecimal bigDecimal1, BigDecimal bigDecimal2, Date date1, Date date2) {
		this.id = id;
		this.name = name;
		this.string1 = string1;
		this.bigDecimal1 = bigDecimal1;
		this.bigDecimal2 = bigDecimal2;
		this.date1 = date1.getTime();
		this.date2 = date2.getTime();
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

	public int hashCode() {
		return (int)(id
			+ name.hashCode()
			+ ("" + string1).hashCode()
			+ bigDecimal1.hashCode()
			+ bigDecimal2.hashCode()
			+ date1
			+ date2
		);
	}


	static private String generateLargeString() {
		char[] chars = new char[980];
		Arrays.fill(chars, 'A'); 
		return new String(chars);
	}

}
