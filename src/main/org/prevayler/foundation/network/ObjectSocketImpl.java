//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the license distributed along with this file for more details.
//Contributions: Alexandre Nodari.

package org.prevayler.foundation.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ObjectSocketImpl implements ObjectSocket {

	private final Socket _socket;
	private final ObjectOutputStream _outputStream;
	private final ObjectInputStream _inputStream;

	
	public ObjectSocketImpl(String serverIpAddress, int serverPort) throws IOException {
		this(new Socket(serverIpAddress, serverPort));
	}

	public ObjectSocketImpl(Socket socket) throws IOException {
		_socket = socket;
		_outputStream = new ObjectOutputStream(_socket.getOutputStream());   // Get the OUTPUT stream first. JDK 1.3.1_01 for Windows will lock up if you get the INPUT stream first.
		_inputStream = new ObjectInputStream(_socket.getInputStream());
	}

	public void writeObject(Object object) throws IOException {
		//TODO Consider eliminating the POBox and putting a buffer here.
		_outputStream.writeObject(object);
		_outputStream.reset();
		_outputStream.flush();
		
	}

	public Object readObject() throws IOException, ClassNotFoundException {
		return _inputStream.readObject();
	}

	public void close() throws IOException {
		_outputStream.close();
		_inputStream.close();
		_socket.close();
	}
}
