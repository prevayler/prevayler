Prevayler 104
=============

This is a caveat. It demonstrates a problem so common amongst people new to
the system prevalence pattern that it's been titled "the initiation problem".

Transactions shouldn't contain actual business objects, instead they should
contain information about the business object so they can be found in the
prevalent system root using a query, a unique identity for example.

The problem is that transactions will be serialized and any business object in
a transaction will thus become a deep clone of that business object rather than
just pointing at the actual business object in the prevalent root.

This is exemplified by setting an aggregate of an business object to another
business object. By passing down the actual aggregate rather than the identity
of the aggregate, the business object will now be pointing at a deep clone of
the aggregate business object rather than reference the actual aggregate.
