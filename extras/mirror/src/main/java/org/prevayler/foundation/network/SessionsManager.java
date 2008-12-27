/*
 * StubbornService.java
 *
 * Copyright (c) 2005 MoneySwitch Ltd.
 * Level 5, 55 Lavender St, Milsons Point 2061.
 * All rights reserved.
 *
 */
package org.prevayler.foundation.network;

/**
 * Useful class comments should go here
 *
 * $Revision: 1.1 $
 * $Date: 2005/03/02 06:04:17 $
 * $Author: peter_mxgroup $
 */
public interface SessionsManager {

    StubbornNetworkProxy find(NetworkSessionId sessionId);

    NetworkSessionId add(StubbornNetworkProxy receiver);

    void remove(NetworkSessionId sessionId);
}