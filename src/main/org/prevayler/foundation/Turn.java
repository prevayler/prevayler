package org.prevayler.foundation;


/** Used to sequence operations that have to be performed in order by several concurrent threads. 
 */
public class Turn {

	private Turn _next;
	private int _tickets = 0;
	private boolean _isAlwaysSkipped;


	public static Turn first() { return new Turn(1000000); }  //Arbitrarily large number.

	private Turn(int tickets) { _tickets = tickets; }

	public Turn next() {
		if (_next == null) _next = new Turn(0);
		return _next;
	}

	public synchronized void start() {
		if (_tickets == 0) Cool.wait(this);
		_tickets--;
	}

	public void end() {
		next().haveSomeTickets(1);
	}
	
	private synchronized void haveSomeTickets(int tickets) {
		if (_isAlwaysSkipped) {
			next().haveSomeTickets(tickets);
			return;
		}
		_tickets += tickets;
		notify();
	}

	public synchronized void alwaysSkip() {
		end();
		_isAlwaysSkipped = true;
		next().haveSomeTickets(_tickets);
	}

}
