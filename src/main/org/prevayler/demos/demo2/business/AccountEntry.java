package org.prevayler.demos.demo2.business;

import java.io.Serializable;
import java.util.Date;

public class AccountEntry implements Serializable {

		private static final long serialVersionUID = 8394742481391868028L;
		private long amount;
        private Date timestamp;

		private AccountEntry() {
		}
		
        AccountEntry(long amount, Date timestamp) {
            this.amount = amount;
            this.timestamp = timestamp;
        }

        public String toString() {
            return timestampString() + "      Amount: " + amount;
        }

        private String timestampString() {
            return new java.text.SimpleDateFormat("yyyy/MM/dd  hh:mm:ss.SSS").format(timestamp);
        }
}
