package org.prevayler.socketserver.example.client;

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

import org.prevayler.socketserver.client.IModelCallback;
import org.prevayler.socketserver.client.Remote;
import org.prevayler.socketserver.example.server.Todo;
import org.prevayler.socketserver.example.server.TodoList;
import org.prevayler.socketserver.example.transactions.CreateTodoBean;
import org.prevayler.socketserver.example.transactions.ListTodos;
import org.prevayler.socketserver.util.Config;
import org.prevayler.socketserver.util.Log;

/**
 * A simple prevayler.socketServer client implementation
 * 
 * Note that although we deal with business objects here, we are always 
 * working with *copies* of the prevalent ones.  They *must* be considered
 * read-only.
 * 
 * Also, a real application would likely cache the business object copies
 * in the clients and rely on callbacks to know when to flush those caches.
 * 
 * @author djo
 */
public class Main {

    public static synchronized void printList(Object todoList) {
        TodoList theList = (TodoList) todoList;
        Todo[] items = theList.toArray();
        for (int i = 0; i < items.length; i++) {
			System.out.println(items[i].getId() + "\t" + items[i].getDesc());
		}
        System.out.println();
    }
    
    private static char[] responses = {'A', 'E', 'L'};
    
    private static char toUpperCase(char ch) {
        String result = "";
        result += ch;
        result = result.toUpperCase();
        return result.toCharArray()[0];
    }
    
    private static boolean invalidResponse(char response) {
        boolean responseIsInvalid = true;
        for (int i = 0; i < responses.length && responseIsInvalid; i++) {
			if (responses[i] == response) {
                responseIsInvalid = false;
            }
		}
        return responseIsInvalid;
    }

    public static void printMenu() {
        System.out.println("L)ist todos;  A)dd todo;  E)xit");
    }
    
    public static void addTodo() {
        System.out.println("Please enter the todo item and press enter");
        String response = "";
        try {
            // Throw away any pending newline characters
            char nextChar;
            do {
                nextChar = (char) System.in.read();
            } while (nextChar == '\n' || nextChar == '\r');
            response += nextChar;
            
            // Read the rest of the input line
            while (true) {
                nextChar = (char) System.in.read();
                if (nextChar == '\n' || nextChar == '\r') break;
                response += nextChar;
            }
        } catch (Exception e) {
            Log.error(e, "Unable to read user input");
        }
        
        // Add the new todo item
        Remote.conn.execl(new CreateTodoBean(response), "Unable to add Todo");
    }
    
    public static void mainMenu() {
        try {
            // Print the current todo list
            System.out.println();
            printList(Remote.conn.execl(new ListTodos(), "Unable to list todos"));
            
            // Display the menu the first time
            printMenu();
                
            while (true) {
                // Get the response
                char response;
                do {
                    response = (char) System.in.read();

                    if (response == '\n') {
                        printMenu();
                    }

                    response = toUpperCase(response);
                } while (invalidResponse(response));
                          
                // Process it
                switch (response) {
                    case 'L':
                        printList(Remote.conn.execl(new ListTodos(), "Unable to list todos"));
                        break;
                    case 'A':
                        addTodo();
                        break;
                    case 'E':
                        return;
                }
            }
        } catch (Exception e) {
            Log.error(e, "Error in main menu loop");
        }
    }

	public static void main(String[] args) {
        Config.propertyFile = "Todo.ini";
        new ClientConfig();
        
        // Get the config options
        int basePort = Integer.parseInt(Config.properties.getProperty("BasePort"));
        String remoteHost = Config.properties.getProperty("RemoteHost");
        
        // Connect to the server
        try {
            Remote.connect(remoteHost, basePort);
        } catch (Exception e) {
            Log.error(e, "Error connecting to remote host: " + remoteHost);
            return;
        }
        
        // Listen to interesting events
        try {
            Remote.conn.registerCallback("ListChanged", new IModelCallback() {
    			public void happened(Long connectionID, String name, Object obj) {
                    // We'll just re-print the list when a change happens.
                    // Note that this will happen in a background thread.
                    System.out.println();
                    Main.printList(obj);
                    Main.printMenu();
    			}
            });
        } catch (Exception e) {
            Log.error(e, "Unable to register callback");
            return;
        }
        
        // Run the main loop
        mainMenu();
        
        // Close the connection
        try {
            Remote.conn.close();
        } catch (Exception e) {
            Log.error(e, "Unable to close client connection");
        }
	}
}
