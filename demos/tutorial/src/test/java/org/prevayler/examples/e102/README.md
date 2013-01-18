Prevayler 102
=============

This is a caveat. It demonstrates the problems with non deterministic features
in transactions, i.e. things that occur within a transaction should evaluate
to the same value every time the transaction is executed.

This is exemplified by assigning the identity of a business object using
a random UUID created within the transaction. That cause the identity of the
business object to change every time the journal is replayed, which usually
would be be considered a rather problematic feature.

Amongst many other possible sources of non deterministic errors it's worth
mentioning new Date(), currentTimeMillis(), nanoTime(), hashCode(), etc.

See e103 for how to use timestamps.