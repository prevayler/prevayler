package org.prevayler.socketserver.server;

import java.util.Properties;

import org.prevayler.socketserver.util.Config;

/**
 * Manages the Prevayler server configuration file
 * 
 * @author djo
 */
public class ServerConfig extends Config {

	/**
	 * @see org.prevayler.socketserver.util.Config#getConfigHeader()
	 */
	public String getConfigHeader() {
		return "Prevayler Server Config";
	}

	/**
	 * @see org.prevayler.socketserver.util.Config#getDefaultProps()
	 */
	public Properties getDefaultProps() {
		// Init the default property values
		Properties props = new Properties();
		props.put("BasePort", "6000");
		props.put("Repository", System.getProperty("user.dir") + "/prevalenceBase");
		props.put("RootObjectClass", "");
		return props;
	}

}
