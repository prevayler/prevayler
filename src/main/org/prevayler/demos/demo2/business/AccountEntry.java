package org.prevayler.demos.demo2.business;

import java.io.Serializable;
import java.util.Date;

import org.prevayler.util.clock.Clock;

public class AccountEntry implements Serializable {

        private long amount;
        private Date timestamp;

		private AccountEntry() {
		}
		
        AccountEntry(long amount, Clock clock) {
            this.amount = amount;
            this.timestamp = clock.time();
        }

        public String toString() {
            return timestampString() + "      Amount: " + amount;
        }

        private String timestampString() {
            return new java.text.SimpleDateFormat("yyyy/MM/dd  hh:mm:ss.SSS").format(timestamp);
        }
}
