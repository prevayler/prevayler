package org.prevayler.socketserver.util;

/*
 * prevayler.socketServer, a socket-based server (and client library)
 * to help create client-server Prevayler applications
 * 
 * Copyright (C) 2003 Advanced Systems Concepts, Inc.
 * 
 * Written by David Orme <daveo@swtworkbench.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import java.util.*;
import java.io.*;


/**
 * <h3>Handle centralized configuration data</h3>
 *
 * <h4><b>Abstract</b> class -- clients must override.</h4>
 * 
 * This class encapsulates a java.util.Properties object in order to 
 * make it easy to keep track of program-level configuration strings.<p> 
 *
 * The config file is called "Config.ini" by default.  This can be changed
 * by changing the value of propertyFile before calling init() or by changing
 * this value in your own default constructor.  The config file is always
 * located in System.getProperty("user.dir").<p>
 * 
 * If this file is not found, Config will attempt to create it with default 
 * values.  The default values are obtained by calling the (overridden)
 * abstract method getDefaultProps()<p> 
 * 
 * You must create a Config object in order to create the Config.properties 
 * object.  After that, you can access system properties by calling
 * `YourConfigClass.properties.get("propertyname");` or other appropriate 
 * methods of the Properties object.<p>
 * 
 * You can store any changed properties by calling
 *   YourConfigClass.configFile.store();
 */
public abstract class Config {
    /**
     * Initialize the configuration file.  This must be called in order 
     * to create the "properties" object.
     */
	public Config() {
		init();
	}
	
	/**
	 * Initialize the configuration file objects.  This must be called in 
	 * order to create the "properties" object.
	 * @param propertyFile The name of the property file to use.  This sets the propertyFile field.
	 */
	public Config(String propertyFile) {
		Config.propertyFile = propertyFile;
		init();
	}
	
	
	private void init() {
		// Init the default property values
		Properties props = getDefaultProps();
			
	    // Create root app properties object
		properties = new Properties(props);
	
	    // Try to load it from disk
	    propertyFile = System.getProperty("user.dir") + "/" + propertyFile;
		try {
			properties.load(new FileInputStream(propertyFile));
		} catch (IOException e) {
			try {
				props.store(new FileOutputStream(propertyFile), getConfigHeader());
	            properties.load(new FileInputStream(propertyFile));
			} catch (IOException e2) {
				Log.error(e, "Could not save default properties");
			}
		}
		
		configFile = this;
	}
	

	/**
	 * This method creates and initializes initializes a Properties object
	 * with the default values for all properties stored in the config file.
	 * It <b>must</b> be overriden in a derived class.
	 * 
	 * @return Properties the default properties
	 */
	public abstract Properties getDefaultProps();
	
	/**
	 * This method returns the string that is stored in the comment at the
	 * top of the config file.  It must be overridden in a derived class.
	 */
	public abstract String getConfigHeader();

	/**
	 * The last Config object created (probably the only one) is stored here 
	 * automatically for easy global access
	 */
	public static Config configFile;

	/**
	 * A "global" properties object containing the current
	 * application properties as read from the configuration file. 
	 */
	public static Properties properties;

    /**
     * The filename (not including path) of the application configuration 
     * file.<p>
     * 
     * The default value may be overridden:
     * <ul>
     *   <li> By changing this variable before constructing your Config 
     * 	      descendent class
     *   <li> By creating a non-default construtor accepting a String and 
     *        calling super(yourstring) in your constructor and using 
     *        that constructor instead of the default one.
     * </ul>
     */
	public static String propertyFile = "Config.ini";


	/**
	 * Store the current configuration properties in the configuration file
	 * @throws IOException if an error happened while storing
	 */
	public void store() throws IOException {
		properties.store(new FileOutputStream(propertyFile), getConfigHeader());
	}

}


