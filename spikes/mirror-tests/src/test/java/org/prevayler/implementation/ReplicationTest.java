//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import java.io.File;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.Cool;
import org.prevayler.foundation.FileIOTest;

public class ReplicationTest extends FileIOTest {

	private Prevayler<AppendingSystem> _server;
	private Prevayler<AppendingSystem> _client;

	public void testServerFirst() throws Exception {
		serverCrashRecover(0);
		clientCrashRecover(0);
		serverAppend("a", "a");
		serverAppend("b", "ab");
		clientAppend("c", "abc");
		clientAppend("d", "abcd");
		serverAppend("e", "abcde");

		clientCrashRecover(0);
		assertEquals("abcde", clientValue());

		clientAppend("f", "abcdef");
		serverAppend("g", "abcdefg");

		networkCrash();
		threadToRestartNetworkAfterAWhile().start();
		_server.execute(new Appendix("h"));
		clientAppend("i", "abcdefghi");  //Blocks until the network is restarted.
		
		serverAppend("j", "abcdefghij");
		clientAppend("k", "abcdefghijk");
	}


	private void networkCrash() {
		//TODO NETWORK_MOCK.crash();    Implement a FaultTolerantNetwork so Prevayler doesn't have to worry about it. (!!!!)
	}


	private Thread threadToRestartNetworkAfterAWhile() {
		return new Thread() {
			public void run() {
				Cool.sleep(300);
				//TODO NETWORK_MOCK.recover();
			}
		};
	}

	public void testClientFirst() throws Exception {
		serverCrashRecover(1);
		clientCrashRecover(1);
		clientAppend("a", "a");
		clientAppend("b", "ab");
		serverAppend("c", "abc");
	}

/* //TODO Test replication chaining.
	public void testChaining() throws Exception {
		serverCrashRecover(2);
		clientWithServerCrashRecover(2, 3);
		clientCrashRecover(3);
		clientAppend("a", "a");
		clientAppend("b", "ab");
		serverAppend("c", "abc");
	}
*/
	
//	 TODO Test Prevayler.execute() on the "clientWithServer" (middle of the replication chain).

	
	private void serverAppend(String appendix, String expectedResult) {
		append(_server, appendix, expectedResult);
	}

	private void clientAppend(String appendix, String expectedResult) {
		append(_client, appendix, expectedResult);
	}

	private void append(Prevayler<AppendingSystem> prevayler, String appendix, String expectedResult) {
		prevayler.execute(new Appendix(appendix));
		Cool.sleep(10);
		assertEquals(expectedResult, serverValue());
		assertEquals(expectedResult, clientValue());
	}

	private void serverCrashRecover(int portOffset) throws Exception {
		PrevaylerFactory<AppendingSystem> factory = factory("server");
		factory.configureReplicationServer(PrevaylerFactory.DEFAULT_REPLICATION_PORT + portOffset);
		factory.configureTransientMode(true);
		_server = factory.create();
	}
	
	private void clientCrashRecover(int portOffset) throws Exception {
		PrevaylerFactory<AppendingSystem> factory = factory("client");
		factory.configureReplicationClient("localhost", PrevaylerFactory.DEFAULT_REPLICATION_PORT + portOffset);
		_client = factory.create();
	}

    private PrevaylerFactory<AppendingSystem> factory(String directory) {
		PrevaylerFactory<AppendingSystem> factory = new PrevaylerFactory<AppendingSystem>();
		factory.configurePrevalentSystem(new AppendingSystem());
		factory.configurePrevalenceDirectory(_testDirectory + File.separator + directory);
		return factory;
	}


	protected void tearDown() throws Exception {
		_server = null;
		_client = null;
		super.tearDown();
    }

	private String serverValue() {
		return _server.prevalentSystem().value();
	}

	private String clientValue() {
		Cool.sleep(100);  //The client is notified asynchronously.
		return _client.prevalentSystem().value();
	}

}
