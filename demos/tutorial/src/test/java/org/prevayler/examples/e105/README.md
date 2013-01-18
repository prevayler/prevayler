Prevayler 105
=============

This is a caveat. It demonstrates that all changes to business objects
need to be encapsulated in a transaction and executed by the prevalence layer.
If not, then changes will be transient and thus lost as the system shuts down.

Changes made outside of a transaction will not be written to the journal.
This will cause a range of problems, mainly loss of data and inconsistency
when replaying transactions (exceptions) at startup.


FOR EXPERT EYES ONLY:

It is however true that changes made outside of a transaction followed with
a call to Prevaler#takeSnapshot() will be persistent. This behavior could be
considered a feature but is in no way endorsed nor recommended, but be aware
as here be dragons and several caveats not demonstrated by this example.
