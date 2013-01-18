Prevayler 103
=============

This is a feature. It demonstrates how to use timestamps in transactions.

This problem is a variant of non deterministic features described as in e102,
it's just that this specific feature is built in to Prevayler.

A transaction was initially executed at a given time. This value is passed down
to the execute method and should be used for your own timestamps rather than
using System.currentTimeMillis(), new Date() or similar within the transaction.

The effect of not using the executionTime parameter will be that your timestamps
will end up being the time that the transaction last was executed,
i.e. if using System.currentTimeMills() etc your timestamps will be set to when
Prevayler replays your journal rather than the time of initially executing it.

This is of course not a problem if you rather than using the executionTime
pass down your own timestamp as an attribute to your transaction, since such
attributes will be serialized and thus end up deterministic.