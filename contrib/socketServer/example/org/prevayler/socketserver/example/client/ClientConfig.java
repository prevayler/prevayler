package org.prevayler.socketserver.example.client;

import java.util.Properties;

import org.prevayler.socketserver.util.Config;

/**
 * Manages the todo client configuration file
 * 
 * @author djo
 */
public class ClientConfig extends Config {

	/**
	 * @see org.prevayler.socketserver.util.Config#getConfigHeader()
	 */
	public String getConfigHeader() {
		return "Todo client config";
	}

	/**
	 * @see org.prevayler.socketserver.util.Config#getDefaultProps()
	 */
	public Properties getDefaultProps() {
		// Init the default property values
		Properties props = new Properties();
		props.put("BasePort", "7000");
        props.put("RemoteHost", "localhost");
		return props;
	}

}
