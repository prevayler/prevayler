//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.Cool;
import org.prevayler.foundation.FileIOTest;

import java.io.File;
import java.io.IOException;

public class ReplicationTest extends FileIOTest {

	private Prevayler _server;
	private Prevayler _client;
	private Prevayler _clientWithServer;

	public void testServerFirst() throws Exception {
		serverCrashRecover(0);
		clientCrashRecover(0);
		serverAppend("a", "a");
		serverAppend("b", "ab");
		clientAppend("c", "abc");
		clientAppend("d", "abcd");
		serverAppend("e", "abcde");

		clientCrashRecover(0);
		clientAppend("f", "abcdef");
		serverAppend("g", "abcdefg");
	}

	public void testClientFirst() throws Exception {
		serverCrashRecover(1);
		clientCrashRecover(1);
		clientAppend("a", "a");
		clientAppend("b", "ab");
		serverAppend("c", "abc");
	}

/* TODO Test replication chaining.
	public void testChaining() throws Exception {
		serverCrashRecover(2);
		clientWithServerCrashRecover(2, 3);
		clientCrashRecover(3);
		clientAppend("a", "a");
		clientAppend("b", "ab");
		serverAppend("c", "abc");
	}
*/


	private void serverAppend(String appendix, String expectedResult) {
		append(_server, appendix, expectedResult);
	}

	private void clientAppend(String appendix, String expectedResult) {
		append(_client, appendix, expectedResult);
	}

	private void append(Prevayler prevayler, String appendix, String expectedResult) {
		prevayler.execute(new Appendix(appendix));
		Cool.sleep(10);
		assertEquals(expectedResult, serverValue());
		assertEquals(expectedResult, clientValue());
	}

	private void serverCrashRecover(int portOffset) throws Exception {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configureReplicationServer(PrevaylerFactory.DEFAULT_REPLICATION_PORT + portOffset);
		factory.configurePrevalentSystem(new AppendingSystem());
		factory.configurePrevalenceDirectory(_testDirectory + File.separator + "server");
		factory.configureTransientMode(true);
		_server = factory.create();
	}
	
	private void clientCrashRecover(int portOffset) throws Exception {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configureReplicationClient("localhost", PrevaylerFactory.DEFAULT_REPLICATION_PORT + portOffset);
		factory.configurePrevalentSystem(new AppendingSystem());
		factory.configurePrevalenceDirectory(_testDirectory + File.separator + "client");
		_client = factory.create();
	}

	private void clientWithServerCrashRecover(int remoteServerPortOffset, int serverPortOffset) throws IOException, ClassNotFoundException {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configureReplicationClient("localhost", PrevaylerFactory.DEFAULT_REPLICATION_PORT + remoteServerPortOffset);
		factory.configureReplicationServer(PrevaylerFactory.DEFAULT_REPLICATION_PORT + serverPortOffset);
		factory.configurePrevalentSystem(new AppendingSystem());
		factory.configurePrevalenceDirectory(_testDirectory + File.separator + "clientWithServer");
		_clientWithServer = factory.create();
	}

    protected void tearDown() throws Exception {
		_server = null;
		_client = null;
		_clientWithServer = null;
		super.tearDown();
    }

	private String serverValue() {
		return ((AppendingSystem)_server.prevalentSystem()).value();
	}

	private String clientValue() {
		Cool.sleep(100);  //The client is notified assynchronously.
		return ((AppendingSystem)_client.prevalentSystem()).value();
	}

}
