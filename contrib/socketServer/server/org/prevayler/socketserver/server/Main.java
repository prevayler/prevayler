package org.prevayler.socketserver.server;

import java.io.IOException;
import java.net.ServerSocket;

import org.prevayler.implementation.SnapshotPrevayler;
import org.prevayler.socketserver.util.Log;

/**
 * This is where the server starts everything
 * @author DaveO
 */
public class Main {

    private static SnapshotThread snapshotThread;
    private static SnapshotPrevayler prevayler;

    private static int port;

    // Init the Prevayler persistence engine and fork snapshot thread
    private static void initPrevayler() throws Exception {
        // Set up the repository location
        //String prevalenceBase = System.getProperty("user.dir") + "/prevalenceBase";
        String prevalenceBase = (String) ServerConfig.properties.get("Repository");
        Log.message("Snapshot/log file dir: " + prevalenceBase);

        // Set up the default port
        port = Integer.parseInt((String) ServerConfig.properties.get("BasePort"));
        
        // Set up the root object class
        String rootObjectClassName = (String) ServerConfig.properties.get("RootObjectClass");
        Class rootObjectClass = Class.forName(rootObjectClassName);

        // Create an instance of the root object class and start the server
        prevayler = new SnapshotPrevayler(rootObjectClass.newInstance(), prevalenceBase);
        snapshotThread = new SnapshotThread(prevayler);
        snapshotThread.start();
    }

    private static void runNotificationServer() {
        new Notification(port+1).start();
    }

    // Command-processing socket server is here    
    private static void runCommandServer() throws Exception {
        ServerSocket ss = null;
        boolean listening = true;

        // Listen dynamically
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
        	Log.error(e, "Couldn't open command server port: " + port);
            System.exit(-1);
        }

        while (listening)
            new CommandThread(prevayler, ss.accept()).start();

        ss.close();
    }

    // Everything starts here
	public static void main(String[] args) {
        try {
            new ServerConfig();
            initPrevayler();
            runNotificationServer();
            runCommandServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
    
}

