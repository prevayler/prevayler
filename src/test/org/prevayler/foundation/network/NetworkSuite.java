//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.foundation.network;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class NetworkSuite extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(NetworkClientReceiverTest.class);
		suite.addTestSuite(NetworkProxyTest.class);
		suite.addTestSuite(StubbornNetworkReceiverTest.class);
		suite.addTestSuite(StubbornNetworkServerTest.class);
		suite.addTestSuite(NetworkTest.class);
		return suite;
	}
}
