package org.prevayler.implementation;

import java.io.IOException;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

public class ReplicationTest extends PrevalenceTest {

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
		try { Thread.sleep(10);	} catch (InterruptedException ignored) { }
		assertEquals(expectedResult, serverValue());
		assertEquals(expectedResult, clientValue());
	}

	private void serverCrashRecover(int portOffset) throws Exception {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configureReplicationServer(PrevaylerFactory.DEFAULT_REPLICATION_PORT + portOffset);
		factory.configurePrevalentSystem(new AppendingSystem());
		factory.configurePrevalenceBase(_testDirectory + "\\server");
		factory.configureTransientMode(true);
		_server = factory.create();
	}
	
	private void clientCrashRecover(int portOffset) throws Exception {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configureReplicationClient("localhost", PrevaylerFactory.DEFAULT_REPLICATION_PORT + portOffset);
		factory.configurePrevalentSystem(new AppendingSystem());
		factory.configurePrevalenceBase(_testDirectory + "\\client");
		_client = factory.create();
	}

	private void clientWithServerCrashRecover(int remoteServerPortOffset, int serverPortOffset) throws IOException, ClassNotFoundException {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configureReplicationClient("localhost", PrevaylerFactory.DEFAULT_REPLICATION_PORT + remoteServerPortOffset);
		factory.configureReplicationServer(PrevaylerFactory.DEFAULT_REPLICATION_PORT + serverPortOffset);
		factory.configurePrevalentSystem(new AppendingSystem());
		factory.configurePrevalenceBase(_testDirectory + "\\clientWithServer");
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
		return ((AppendingSystem)_client.prevalentSystem()).value();
	}

}
